package win.kaleb.serverstats.statistics.version;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;
import win.kaleb.serverstats.util.Depend;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Depend("ViaVersion")
public class PlayerVersionStatistic implements StatisticsRecorder {

	private static final Map<Integer, String> versions = new HashMap<>();

	static {
		versions.put(578, "1.15.2");
		versions.put(575, "1.15.1");
		versions.put(573, "1.15");
		versions.put(498, "1.14.4");
		versions.put(490, "1.14.3");
		versions.put(485, "1.14.2");
		versions.put(480, "1.14.1");
		versions.put(477, "1.14");
		versions.put(404, "1.13.2");
		versions.put(401, "1.13.1");
		versions.put(393, "1.13");
		versions.put(340, "1.12.2");
		versions.put(338, "1.12.1");
		versions.put(335, "1.12");
		versions.put(316, "1.11.2");
		versions.put(315, "1.11");
		versions.put(210, "1.10.2");
		versions.put(110, "1.9.4");
		versions.put(109, "1.9.2");
		versions.put(108, "1.9.1");
		versions.put(107, "1.9");
		versions.put(47, "1.8.x");
		versions.put(5, "1.7.6+");
		versions.put(4, "1.7.2+");
	}

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		ViaAPI<?> viaAPI = Via.getAPI();
		Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId)
				.collect(Collectors.groupingBy(viaAPI::getPlayerVersion))
				.forEach((ver, coll) -> {
					String version = versions.getOrDefault(ver, "Unknown");
					int amount = coll.size();
					Point.Builder builder = Point.measurement("versions")
							.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
							.addField("players", amount)
							.tag("version", version);
					influx.write(serverInfo.attachTags(builder).build());
				});
	}

}
