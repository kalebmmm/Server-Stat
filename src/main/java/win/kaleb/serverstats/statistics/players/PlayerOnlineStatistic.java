package win.kaleb.serverstats.statistics.players;

import org.bukkit.Bukkit;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;

import java.util.concurrent.TimeUnit;

public class PlayerOnlineStatistic implements StatisticsRecorder {

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		Point.Builder builder = Point.measurement("players")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("players", Bukkit.getOnlinePlayers().size());
		influx.write(serverInfo.attachTags(builder).build());
	}

}
