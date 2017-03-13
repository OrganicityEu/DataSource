package eu.organicity.data.controller;

import com.fasterxml.jackson.databind.deser.Deserializers;
import eu.organicity.data.dto.HistoricDataDTO;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static eu.organicity.data.controller.BaseController.APPLICATION_JSON;

@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class HistoryController extends Deserializers.Base{

    /**
     * a log4j logger to print messages.
     */
    protected static final Logger LOGGER = Logger.getLogger(HistoryController.class);
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
        return new HistoricDataDTO();
    }

}
