package eu.organicity.data.repository;

import eu.organicity.data.model.Measurement;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeasurementRepository extends CrudRepository<Measurement, Long> {

    List<Measurement> findByAssetUrn(final String assetUrn);

    List<Measurement> findByAssetUrnAndAttribute(final String assetUrn, final String attribute);

    List<Measurement> findByAssetUrnAndAttributeAndTimestampBetween(final String assetUrn, final String attribute, final long from, final long to);
}
