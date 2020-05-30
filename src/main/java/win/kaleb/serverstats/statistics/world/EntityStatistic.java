package win.kaleb.serverstats.statistics.world;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import win.kaleb.serverstats.server.ServerInformation;

import java.util.concurrent.TimeUnit;

public class EntityStatistic extends WorldStatistic {

	public EntityStatistic(ConfigurationSection config) {
		super(config);
	}

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		for (World world : getUsableWorlds()) {
			int entities = world.getEntities().size();
			Point.Builder builder = Point
					.measurement("entities")
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("entities", entities)
					.tag("world", world.getName());
			influx.write(serverInfo.attachTags(builder).build());
		}
	}

}
