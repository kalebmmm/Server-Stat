package win.kaleb.serverstats.statistics.staff;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;

import java.util.concurrent.TimeUnit;

public class StaffOnlineStatistic implements StatisticsRecorder {

	private String permission;

	public StaffOnlineStatistic(ConfigurationSection config) {
		this.permission = config.getString("permission");
	}

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		int online = (int) Bukkit.getOnlinePlayers().stream()
				.filter(pl -> pl.hasPermission(this.permission))
				.count();

		Point.Builder builder = Point.measurement("staff_online")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("staff_online", online);
		influx.write(serverInfo.attachTags(builder).build());
	}

}
