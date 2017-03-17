package eu.organicity.data.controller;

import eu.organicity.data.dto.HistoricDataDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


public interface HistoryControllerInterface {
    public HistoricDataDTO readings(@PathVariable("entity_id") final String entityId, @RequestParam(value = "attribute_id") final String attributeId, @RequestParam(value = "from") final String from, @RequestParam(value = "to") final String to, @RequestParam(value = "all_intervals", required = false, defaultValue = "true") final boolean allIntervals, @RequestParam(value = "rollup", required = false, defaultValue = "") final String rollup, @RequestParam(value = "function", required = false, defaultValue = "avg") final String function);
}
