/*
 * Copyright 2006-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.ws.samples.airline.dao;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.ws.samples.airline.domain.Flight;
import org.springframework.ws.samples.airline.domain.ServiceClass;

public interface FlightDao extends CrudRepository<Flight, Long> {

	@Query("SELECT f FROM Flight f WHERE f.from.code = :fromAirportCode "
			+ "AND f.to.code = :toAirportCode AND f.departureTime >= :#{#date} AND f.departureTime <= :#{#date.plusDays(1)} AND "
			+ "f.serviceClass = :class")
	List<Flight> findFlights(@Param("fromAirportCode") String fromAirportCode, //
			@Param("toAirportCode") String toAirportCode, //
			@Param("date") ZonedDateTime date, //
			@Param("class") ServiceClass serviceClass);

	Flight findFlightByNumberAndDepartureTime(String flightNumber, ZonedDateTime departureTime);

}
