package win.kaleb.serverstats.statistics;

import org.influxdb.InfluxDB;
import win.kaleb.serverstats.server.ServerInformation;

public interface StatisticsRecorder {

	void record(InfluxDB influx, ServerInformation serverInfo);

}
