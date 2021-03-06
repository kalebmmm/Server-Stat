package win.kaleb.serverstats.statistics.world;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import win.kaleb.serverstats.server.ServerInformation;

import java.util.concurrent.TimeUnit;

public class ChunkStatistic extends WorldStatistic {

	public ChunkStatistic(ConfigurationSection config) {
		super(config);
	}

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		for (World world : getUsableWorlds()) {
			int chunks = world.getLoadedChunks().length;
			Point.Builder builder = Point
					.measurement("chunks")
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("chunks", chunks)
					.tag("world", world.getName());
			influx.write(serverInfo.attachTags(builder).build());
		}
	}

}
