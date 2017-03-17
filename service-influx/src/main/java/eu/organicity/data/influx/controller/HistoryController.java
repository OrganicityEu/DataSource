package eu.organicity.data.influx.controller;

import eu.organicity.data.controller.HistoryControllerInterface;
import eu.organicity.data.dto.HistoricDataDTO;
import org.apache.log4j.Logger;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class HistoryController implements HistoryControllerInterface {

    protected static final Logger LOGGER = Logger.getLogger(HistoryController.class);

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    private SimpleDateFormat dateFormatHours = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private SimpleDateFormat dateFormatSecondsInflux = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private SimpleDateFormat dateFormatMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
    private SimpleDateFormat dateFormatSeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @PostConstruct
    public void init() {

        final TimeZone tz = TimeZone.getTimeZone("UTC");

        dateFormatSecondsInflux.setTimeZone(tz);

        dateFormatHours.setTimeZone(tz);
        dateFormatSeconds.setTimeZone(tz);
        dateFormatMillis.setTimeZone(tz);

    }

    @RequestMapping(value = {"/entities/{entity_id}/readings"}, method = RequestMethod.GET, produces = "application/json")
    public HistoricDataDTO readings(@PathVariable("entity_id") final String entityId, @RequestParam(value = "attribute_id") final String attributeId, @RequestParam(value = "from") final String from, @RequestParam(value = "to") final String to, @RequestParam(value = "all_intervals", required = false, defaultValue = "true") final boolean allIntervals, @RequestParam(value = "rollup", required = false, defaultValue = "") final String rollup, @RequestParam(value = "function", required = false, defaultValue = "avg") final String function) {

        final HistoricDataDTO data = new HistoricDataDTO();
        data.setEntity_id(entityId);
        data.setAttribute_id(attributeId);
        data.setFrom(from);
        data.setTo(to);
        data.setReadings(new ArrayList<>());

        try {
            final Date fromTime = dateFormatSeconds.parse(from);
            final Date toTime = dateFormatSeconds.parse(to);

            final String queryString = String.format("select \"time\",\"%s\" from \"%s\" WHERE time > '%s' and time < '%s'", attributeId, entityId, dateFormatSecondsInflux.format(fromTime), dateFormatSecondsInflux.format(toTime));
            LOGGER.info(queryString);
            final Query q = new Query(queryString, "patras");
            final QueryResult result = influxDBTemplate.query(q);
            final List<QueryResult.Series> series = result.getResults().get(0).getSeries();
            LOGGER.info(series);

            for (final QueryResult.Series ser : series) {
                data.setReadings(ser.getValues());
            }
            for (final List<Object> objects : data.getReadings()) {
                final Date date = dateFormatMillis.parse(String.valueOf(objects.get(0)));
                objects.set(0, dateFormatSeconds.format(date));
            }
            return data;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
