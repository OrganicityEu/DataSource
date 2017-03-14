package eu.organicity.data.controller;

import com.fasterxml.jackson.databind.deser.Deserializers;
import eu.organicity.data.dto.HistoricDataDTO;
import eu.organicity.data.model.Measurement;
import eu.organicity.data.repository.MeasurementRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static eu.organicity.data.controller.BaseController.APPLICATION_JSON;

@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class HistoryController extends Deserializers.Base {

    protected static final Logger LOGGER = Logger.getLogger(HistoryController.class);

    @Autowired
    MeasurementRepository measurementRepository;

    private SimpleDateFormat dateFormatHours;
    private SimpleDateFormat dateFormatSeconds;

    @PostConstruct
    public void init() {

        final TimeZone tz = TimeZone.getTimeZone("UTC");
        dateFormatHours = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        dateFormatHours.setTimeZone(tz);
        dateFormatSeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormatSeconds.setTimeZone(tz);

    }

    @RequestMapping(value = {"/entities/{entity_id}/readings"}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    public HistoricDataDTO experimentView(@PathVariable("entity_id") final String entityId, @RequestParam(value = "attribute_id") final String attributeId, @RequestParam(value = "from") final String from, @RequestParam(value = "to") final String to, @RequestParam(value = "all_intervals", required = false, defaultValue = "true") final boolean allIntervals, @RequestParam(value = "rollup", required = false, defaultValue = "") final String rollup, @RequestParam(value = "function", required = false, defaultValue = "avg") final String function) {

        final HistoricDataDTO data = new HistoricDataDTO();
        data.setEntity_id(entityId);
        data.setAttribute_id(attributeId);
        data.setFrom(from);
        data.setTo(to);
        data.setReadings(new ArrayList<>());

        final List<Measurement> measurements = measurementRepository.findByAssetUrn(entityId);
        for (final Measurement measurement : measurements) {
            final List<Object> dataList = new ArrayList<>();
            dataList.add(dateFormatSeconds.format(new Date(measurement.getTimestamp())));
            dataList.add(measurement.getValue());
            data.getReadings().add(dataList);
        }

        return data;
    }

}
