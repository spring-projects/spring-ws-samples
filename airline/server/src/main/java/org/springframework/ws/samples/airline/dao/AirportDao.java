package org.springframework.ws.samples.airline.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.ws.samples.airline.domain.Airport;

public interface AirportDao extends CrudRepository<Airport, Long> {

}
