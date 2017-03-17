package eu.organicity.data.controller;

import org.springframework.web.bind.annotation.RequestBody;

public interface ContextControllerInterface {

    String notifyContext(@RequestBody final String update);
}
