package eu.organicity.data.service;

import eu.organicity.data.controller.HistoryController;
import eu.organicity.data.model.Measurement;
import eu.organicity.data.repository.MeasurementRepository;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class StorageService implements StorageServiceInterface {

    protected static final Logger LOGGER = Logger.getLogger(HistoryController.class);

    private static final String DATA_KEY = "data";
    private static final String ID_KEY = "id";
    private static final String VALUE_TYPE = "type";
    private static final String VALUE_KEY = "value";
    private static final String TIMEINSTANT_KEY = "TimeInstant";

    @Autowired
    MeasurementRepository measurementRepository;

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
    }

    @Async
    public void storeUpdate(final String update) {

        final List<Measurement> measurementList = new ArrayList<>();

        final JSONObject jobj = new JSONObject(update);

        LOGGER.info(jobj.keySet());

        final JSONArray data = jobj.getJSONArray(DATA_KEY);
        for (int i = 0; i < data.length(); i++) {
            final JSONObject obj = data.getJSONObject(i);
            final List<Measurement> objectMeasurementList = parseContext(obj);
            measurementList.addAll(objectMeasurementList);
        }

        store(measurementList);
    }

    private List<Measurement> parseContext(final JSONObject obj) {
        final List<Measurement> measurementList = new ArrayList<>();

        if (obj.keySet() == null || !obj.has(ID_KEY) || !obj.has(TIMEINSTANT_KEY)) {
            return measurementList;
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
                    return measurementList;
                }
            }
        }

        final Set<String> attributes = new HashSet<>(obj.keySet());
        attributes.removeAll(unusedKeys);

        for (final String attribute : attributes) {
            final Measurement m = parseAttribute(id, date, attribute, obj);
            if (m != null) {
                measurementList.add(m);
            }
        }

        return measurementList;
    }

    private Measurement parseAttribute(final String id, Date date, final String attribute, final JSONObject obj) {
        final JSONObject attributeObj = obj.getJSONObject(attribute);

        if (!attributeObj.has(VALUE_KEY)) {
            return null;
        }

        final String type = attributeObj.getString(VALUE_TYPE);
        final String value = attributeObj.getString(VALUE_KEY);
        return buildMeasurement(id, type, value, date.getTime());
    }

    private Measurement buildMeasurement(final String id, final String attribute, final String value, final long l) {
        final Measurement m = new Measurement();
        m.setAssetUrn(id);
        m.setAttribute(attribute);
        m.setValue(value);
        m.setTimestamp(l);
        return m;
    }

    private void store(List<Measurement> measurementList) {
        measurementRepository.save(measurementList);
    }
}