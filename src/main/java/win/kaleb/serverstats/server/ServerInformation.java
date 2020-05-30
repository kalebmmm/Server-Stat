package win.kaleb.serverstats.server;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.influxdb.dto.Point;

@Data
public class ServerInformation {

	private final String network, server;

	public static ServerInformation get(FileConfiguration config) {
		String network = config.getString("server-information.network");
		String server = config.getString("server-information.server");
		return new ServerInformation(network, server);
	}

	public Point.Builder attachTags(Point.Builder point) {
		return point
				.tag("network", network)
				.tag("server", server);
	}

}
