package eu.organicity.data.controller;

import com.amaxilatis.orion.model.SubscriptionUpdate;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class ContextController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    protected static final Logger LOGGER = Logger.getLogger(ContextController.class);

    @PostConstruct
    public void init() {
    }

    /**
     * A method that handles subscription updates from Orion.
     * <p>
     *
     * @param subscriptionUpdate the {@link SubscriptionUpdate} received from the Orion Context Broker.
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/v1/notifyContext"}, method = RequestMethod.POST, produces = APPLICATION_JSON)
    SubscriptionUpdate notifyContext(@RequestBody final SubscriptionUpdate subscriptionUpdate, @PathVariable("contextConnectionId") String contextConnectionId) {
        LOGGER.debug("[call] notifyContext " + subscriptionUpdate.getSubscriptionId());
        return subscriptionUpdate;
    }

}
