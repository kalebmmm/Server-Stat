package win.kaleb.serverstats;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import win.kaleb.serverstats.influx.InfluxManager;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;
import win.kaleb.serverstats.statistics.ping.PingStatistic;
import win.kaleb.serverstats.statistics.players.PlayerOnlineStatistic;
import win.kaleb.serverstats.statistics.staff.StaffOnlineStatistic;
import win.kaleb.serverstats.statistics.tps.TPSStatistic;
import win.kaleb.serverstats.statistics.version.PlayerVersionStatistic;
import win.kaleb.serverstats.statistics.vote.VoteStatistic;
import win.kaleb.serverstats.statistics.world.ChunkStatistic;
import win.kaleb.serverstats.statistics.world.EntityStatistic;
import win.kaleb.serverstats.util.Depend;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public final class ServerStats extends JavaPlugin implements Runnable {

	private static final Map<String, Class<? extends StatisticsRecorder>> configClassMap = new HashMap<>();

	private final Set<StatisticsRecorder> recorders = new HashSet<>();
	private InfluxManager influxManager;
	private ServerInformation serverInformation;

	static {
		configClassMap.put("players", PlayerOnlineStatistic.class);
		configClassMap.put("world-entities", EntityStatistic.class);
		configClassMap.put("world-chunks", ChunkStatistic.class);
		configClassMap.put("staff-online", StaffOnlineStatistic.class);
		configClassMap.put("tps", TPSStatistic.class);
		configClassMap.put("player-versions", PlayerVersionStatistic.class);
		configClassMap.put("ping", PingStatistic.class);
		configClassMap.put("votes", VoteStatistic.class);
	}

	@SneakyThrows
	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.influxManager = new InfluxManager(getConfig());
		this.serverInformation = ServerInformation.get(getConfig());
		this.recorders.clear();

		ConfigurationSection stats = getConfig().getConfigurationSection("stats");
		mapLoop: for (Map.Entry<String, Class<? extends StatisticsRecorder>> entry : configClassMap.entrySet()) {
			ConfigurationSection statConf = stats.getConfigurationSection(entry.getKey());
			Class<? extends StatisticsRecorder> recorder = entry.getValue();

			if (!statConf.getBoolean("enabled"))
				continue;

			for (Depend depend : recorder.getAnnotationsByType(Depend.class)) {
				for (String plugin : depend.value()) {
					if (!Bukkit.getPluginManager().isPluginEnabled(plugin))
						continue mapLoop;
				}
			}

			StatisticsRecorder instance = null;

			for (Constructor<?> constructor : recorder.getConstructors()) {
				Class<?>[] parameters = constructor.getParameterTypes();
				if (parameters.length == 1 && parameters[0] == ConfigurationSection.class) {
					instance = (StatisticsRecorder) constructor.newInstance(statConf);
					break;
				}

				if (parameters.length == 0) {
					instance = (StatisticsRecorder) constructor.newInstance();
					break;
				}
			}

			if (instance != null) {
				if (instance instanceof Listener)
					Bukkit.getPluginManager().registerEvents((Listener) instance, this);
				recorders.add(instance);
				getLogger().info(String.format("Enabled Statistic: %s", recorder.getSimpleName()));
			} else {
				getLogger().warning(String.format("Couldn't construct %s", recorder.getSimpleName()));
			}
		}

		Bukkit.getScheduler().runTaskTimerAsynchronously(this, this, 20 * 30, 20 * 30);
	}

	@Override
	public void onDisable() {
		this.influxManager.disconnect();
	}

	@Override
	public void run() {
		getRecorders().forEach(recorder -> recorder.record(getInfluxManager().getInflux(), getServerInformation()));
	}

}
