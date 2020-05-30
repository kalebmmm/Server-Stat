package win.kaleb.serverstats.influx;

import org.bukkit.configuration.file.FileConfiguration;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

public class InfluxManager {

	private final InfluxDB influx;

	public InfluxManager(FileConfiguration config) {
		String url = config.getString("influx.url");
		String database = config.getString("influx.database");
		String username = config.getString("influx.username");
		String password = config.getString("influx.password");
		this.influx = InfluxDBFactory.connect(url, username, password);
		this.influx.enableBatch(BatchOptions.DEFAULTS);
		this.influx.setDatabase(database);
	}

	public InfluxDB getInflux() {
		return influx;
	}

	public void disconnect() {
		this.influx.close();
	}

}
