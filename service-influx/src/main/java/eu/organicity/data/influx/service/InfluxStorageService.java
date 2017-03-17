package eu.organicity.data.influx.service;

import eu.organicity.data.service.StorageServiceInterface;
import org.apache.log4j.Logger;
import org.influxdb.dto.Point;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
public class InfluxStorageService implements StorageServiceInterface {

    protected static final Logger LOGGER = Logger.getLogger(InfluxStorageService.class);

    private static final String DATA_KEY = "data";
    private static final String ID_KEY = "id";
    private static final String VALUE_TYPE = "type";
    private static final String VALUE_KEY = "value";
    private static final String TIMEINSTANT_KEY = "TimeInstant";

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    private final Set<String> unusedKeys = new HashSet<>();
    private SimpleDateFormat dateFormatHours = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private SimpleDateFormat dateFormatSeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private SimpleDateFormat dateFormatMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");


    @PostConstruct
    public void init() {
        unusedKeys.add("id");
        unusedKeys.add("type");
        unusedKeys.add("TimeInstant");
        unusedKeys.add("location");
        unusedKeys.add("datasource");

        final TimeZone tz = TimeZone.getTimeZone("UTC");
        dateFormatHours.setTimeZone(tz);
        dateFormatSeconds.setTimeZone(tz);
        dateFormatMillis.setTimeZone(tz);

        influxDBTemplate.createDatabase();
    }

    @Async
    public void storeUpdate(final String update) {

        final List<Point> measurementList = new ArrayList<>();

        final JSONObject jobj = new JSONObject(update);

        LOGGER.info(jobj.keySet());

        final JSONArray data = jobj.getJSONArray(DATA_KEY);
        for (int i = 0; i < data.length(); i++) {
            final JSONObject obj = data.getJSONObject(i);
            final List<Point> objectMeasurementList = parseContext(obj);
            measurementList.addAll(objectMeasurementList);
        }

        store(measurementList);
    }

    private List<Point> parseContext(final JSONObject obj) {
        final List<Point> pointList = new ArrayList<>();

        if (obj.keySet() == null || !obj.has(ID_KEY) || !obj.has(TIMEINSTANT_KEY)) {
            return pointList;
        }

        final String id = obj.getString(ID_KEY);
        final JSONObject timeInstant = obj.getJSONObject(TIMEINSTANT_KEY);
        Date date;
        try {
            date = dateFormatHours.parse(timeInstant.getString(VALUE_KEY));
        } catch (ParseException e1) {
            try {
                date = dateFormatSeconds.parse(timeInstant.getString(VALUE_KEY));
            } catch (ParseException e2) {
                try {
                    date = dateFormatMillis.parse(timeInstant.getString(VALUE_KEY));
                } catch (ParseException e3) {
                    return pointList;
                }
            }
        }

        final Set<String> attributes = new HashSet<>(obj.keySet());
        attributes.removeAll(unusedKeys);

        for (final String attribute : attributes) {
            final Point p = parseAttribute(id, date, attribute, obj);
            if (p != null) {
                pointList.add(p);
            }
        }

        return pointList;
    }

    private Point parseAttribute(final String id, Date date, final String attribute, final JSONObject obj) {
        final JSONObject attributeObj = obj.getJSONObject(attribute);

        if (!attributeObj.has(VALUE_KEY)) {
            return null;
        }

        final String type = attributeObj.getString(VALUE_TYPE);
        final String value = attributeObj.getString(VALUE_KEY);
        return buildMeasurement(id, type, value, date.getTime());
    }

    private Point buildMeasurement(final String id, final String attribute, final String value, final long l) {
        final Point p = Point.measurement(id).time(l, TimeUnit.MILLISECONDS).addField(attribute, value).build();
        LOGGER.info("point :" + p);
        return p;
    }

    private void store(List<Point> measurementList) {
        LOGGER.info("Stroing :" + measurementList);
        influxDBTemplate.write(measurementList);
    }
}
