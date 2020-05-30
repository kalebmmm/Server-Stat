package win.kaleb.serverstats.statistics.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.influxdb.InfluxDB;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;

import java.util.HashSet;
import java.util.Set;

public abstract class WorldStatistic implements StatisticsRecorder {

	private final Set<String> worlds = new HashSet<>();

	public WorldStatistic(ConfigurationSection config) {
		this.worlds.addAll(config.getStringList("worlds"));
	}

	protected Set<World> getUsableWorlds() {
		Set<World> worlds = new HashSet<>(Bukkit.getWorlds());
		if (!this.worlds.isEmpty())
			worlds.removeIf(world -> !this.worlds.contains(world.getName()));
		return worlds;
	}

	public abstract void record(InfluxDB influx, ServerInformation serverInfo);

}
