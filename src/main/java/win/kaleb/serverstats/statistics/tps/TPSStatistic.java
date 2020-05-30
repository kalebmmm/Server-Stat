package win.kaleb.serverstats.statistics.tps;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;

import java.util.concurrent.TimeUnit;

public class TPSStatistic implements StatisticsRecorder {

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		Point.Builder builder = Point.measurement("tps")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("tps", TPSUtil.getRecentTps()[0]);
		influx.write(serverInfo.attachTags(builder).build());
	}

}
