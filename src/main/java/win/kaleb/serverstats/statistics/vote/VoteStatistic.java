package win.kaleb.serverstats.statistics.vote;

import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import win.kaleb.serverstats.server.ServerInformation;
import win.kaleb.serverstats.statistics.StatisticsRecorder;
import win.kaleb.serverstats.util.Depend;

import java.util.concurrent.TimeUnit;

@Depend("Votifier")
public class VoteStatistic implements StatisticsRecorder, Listener {

	private int votes = 0;

	@Override
	public void record(InfluxDB influx, ServerInformation serverInfo) {
		Point.Builder builder = Point.measurement("votes")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("votes", votes);
		influx.write(serverInfo.attachTags(builder).build());
		votes = 0;
	}

	@EventHandler
	public void on(VotifierEvent event) {
		votes++;
	}

}
