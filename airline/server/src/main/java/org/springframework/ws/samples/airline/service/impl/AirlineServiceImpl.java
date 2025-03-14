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
package org.springframework.ws.samples.airline.service.impl;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.ws.samples.airline.dao.FlightDao;
import org.springframework.ws.samples.airline.dao.TicketDao;
import org.springframework.ws.samples.airline.domain.*;
import org.springframework.ws.samples.airline.security.FrequentFlyerSecurityService;
import org.springframework.ws.samples.airline.security.StubFrequentFlyerSecurityService;
import org.springframework.ws.samples.airline.service.AirlineService;
import org.springframework.ws.samples.airline.service.NoSeatAvailableException;
import org.springframework.ws.samples.airline.service.NoSuchFlightException;
import org.springframework.ws.samples.airline.service.NoSuchFrequentFlyerException;

/**
 * Default implementation of the <code>AirlineService</code> interface.
 *
 * @author Arjen Poutsma
 */
@Service
@Transactional(readOnly = true)
public class AirlineServiceImpl implements AirlineService {

	private static final Log logger = LogFactory.getLog(AirlineServiceImpl.class);

	private FlightDao flightDao;

	private TicketDao ticketDao;

	private FrequentFlyerSecurityService frequentFlyerSecurityService = new StubFrequentFlyerSecurityService();

	public AirlineServiceImpl(FlightDao flightDao, TicketDao ticketDao) {

		this.flightDao = flightDao;
		this.ticketDao = ticketDao;
	}

	@Autowired(required = false)
	public void setFrequentFlyerSecurityService(FrequentFlyerSecurityService frequentFlyerSecurityService) {
		this.frequentFlyerSecurityService = frequentFlyerSecurityService;
	}

	@Transactional(readOnly = false, rollbackFor = { NoSuchFlightException.class, NoSeatAvailableException.class,
			NoSuchFrequentFlyerException.class })
	public Ticket bookFlight(String flightNumber, ZonedDateTime departureTime, List<Passenger> passengers)
			throws NoSuchFlightException, NoSeatAvailableException, NoSuchFrequentFlyerException {

		Assert.notEmpty(passengers, "No passengers given");

		if (logger.isDebugEnabled()) {
			logger.debug("Booking flight '" + flightNumber + "' on '" + departureTime + "' for " + passengers);
		}

		Flight flight = flightDao.findFlightByNumberAndDepartureTime(flightNumber, departureTime);

		if (flight == null) {
			throw new NoSuchFlightException(flightNumber, departureTime);
		}
		else if (flight.getSeatsAvailable() < passengers.size()) {
			throw new NoSeatAvailableException(flight);
		}

		Ticket ticket = new Ticket();
		ticket.setIssueDate(LocalDate.now());
		ticket.setFlight(flight);

		for (Passenger passenger : passengers) {
			if (passenger instanceof FrequentFlyer && frequentFlyerSecurityService != null) {
				String username = ((FrequentFlyer) passenger).getUsername();
				Assert.hasLength(username, "No username specified");
				FrequentFlyer frequentFlyer = frequentFlyerSecurityService.getFrequentFlyer(username);
				frequentFlyer.addMiles(flight.getMiles());
				ticket.addPassenger(frequentFlyer);
			}
			else {
				ticket.addPassenger(passenger);
			}
		}

		flight.substractSeats(passengers.size());

		flightDao.save(flight);
		ticketDao.save(ticket);

		return ticket;
	}

	public Flight getFlight(Long id) throws NoSuchFlightException {

		return flightDao.findById(id) //
			.orElseThrow(() -> new NoSuchFlightException(id));
	}

	public List<Flight> getFlights(String fromAirportCode, String toAirportCode, ZonedDateTime departureDate,
			ServiceClass serviceClass) {

		if (serviceClass == null) {
			serviceClass = ServiceClass.ECONOMY;
		}

		if (logger.isDebugEnabled()) {
			logger
				.debug("Getting flights from '" + fromAirportCode + "' to '" + toAirportCode + "' on " + departureDate);
		}

		List<Flight> flights = flightDao.findFlights(fromAirportCode, toAirportCode, departureDate, serviceClass);

		if (logger.isDebugEnabled()) {
			logger.debug("Returning " + flights.size() + " flights");
		}

		return flights;
	}

	@Override
	@PreAuthorize("hasRole('FREQUENT_FLYER')")
	public int getFrequentFlyerMileage() {

		if (logger.isDebugEnabled()) {
			logger.debug("Using " + frequentFlyerSecurityService + " for security");
		}

		return Optional.ofNullable(frequentFlyerSecurityService.getCurrentlyAuthenticatedFrequentFlyer())
			.map(FrequentFlyer::getMiles) //
			.orElse(0);
	}

}
