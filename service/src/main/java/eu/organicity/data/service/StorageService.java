package eu.organicity.data.service;

import com.amaxilatis.orion.model.Attribute;
import com.amaxilatis.orion.model.OrionContextElement;
import eu.organicity.data.controller.HistoryController;
import eu.organicity.data.model.Measurement;
import eu.organicity.data.repository.MeasurementRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    protected static final Logger LOGGER = Logger.getLogger(HistoryController.class);

    @Autowired
    MeasurementRepository measurementRepository;

    public void storeUpdate(OrionContextElement element) {

        LOGGER.info(element);

        for (final Attribute attribute : element.getAttributes()) {

            final Measurement m = new Measurement();
            m.setAssetUrn(element.getId());
            m.setAttribute(attribute.getName());
            m.setValue(attribute.getValue());
            m.setTimestamp(System.currentTimeMillis());

            measurementRepository.save(m);
        }

    }
}
