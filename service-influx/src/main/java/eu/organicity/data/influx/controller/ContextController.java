package eu.organicity.data.influx.controller;

import eu.organicity.data.controller.ContextControllerInterface;
import eu.organicity.data.influx.service.InfluxStorageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;


@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class ContextController implements ContextControllerInterface {

    protected static final Logger LOGGER = Logger.getLogger(ContextController.class);

    @Autowired
    InfluxStorageService storageService;

    @PostConstruct
    public void init() {
    }

    @ResponseBody
    @RequestMapping(value = {"/notifyContext"}, method = RequestMethod.POST, produces = "application/json")
    public String notifyContext(@RequestBody final String update) {
        LOGGER.info("[/notifyContext]");

        storageService.storeUpdate(update);

        return update;
    }

}
