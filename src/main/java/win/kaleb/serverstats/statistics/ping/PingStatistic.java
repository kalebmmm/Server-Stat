package win.kaleb.serverstats.statistics.ping;

import org.bukkit.Bukkit;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;

import java.util.concurrent.TimeUnit;

public class PingStatistic implements StatisticsRecorder {

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		double ping = Bukkit.getOnlinePlayers().stream().mapToDouble(p -> p.spigot().getPing())
				.average().orElse(0);
		Point.Builder builder = Point.measurement("ping")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("ping", ping);
		influx.write(serverInfo.attachTags(builder).build());
	}

}
