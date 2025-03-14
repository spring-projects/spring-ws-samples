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

package org.springframework.ws.samples.airline.ws;

import static org.springframework.ws.samples.airline.ws.AirlineWebServiceConstants.*;

import jakarta.xml.bind.JAXBElement;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.ws.samples.airline.domain.FrequentFlyer;
import org.springframework.ws.samples.airline.domain.Passenger;
import org.springframework.ws.samples.airline.domain.ServiceClass;
import org.springframework.ws.samples.airline.schema.*;
import org.springframework.ws.samples.airline.schema.support.SchemaConversionUtils;
import org.springframework.ws.samples.airline.service.AirlineService;
import org.springframework.ws.samples.airline.service.NoSeatAvailableException;
import org.springframework.ws.samples.airline.service.NoSuchFlightException;
import org.springframework.ws.samples.airline.service.NoSuchFrequentFlyerException;
import org.springframework.ws.server.endpoint.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Endpoint that handles the Airline Web Service messages using a combination of JAXB2
 * marshalling and XPath expressions.
 *
 * @author Arjen Poutsma
 */
@Endpoint
public class AirlineEndpoint {

	private static final Logger logger = LoggerFactory.getLogger(AirlineEndpoint.class);

	private final ObjectFactory objectFactory = new ObjectFactory();

	private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

	private final AirlineService airlineService;

	@Autowired
	public AirlineEndpoint(AirlineService airlineService) {
		this.airlineService = airlineService;
	}

	/**
	 * This endpoint method uses a combination of XPath expressions and marshalling to
	 * handle message with a <code>&lt;GetFlightsRequest&gt;</code> payload.
	 * @param from the from airport
	 * @param to the to airport
	 * @param departureDateString the string representation of the departure date
	 * @param serviceClassString the string representation of the service class
	 * @return the JAXB2 representation of a <code>&lt;GetFlightsResponse&gt;</code>
	 */
	@PayloadRoot(localPart = GET_FLIGHTS_REQUEST, namespace = MESSAGES_NAMESPACE)
	@Namespace(prefix = "m", uri = MESSAGES_NAMESPACE)
	@ResponsePayload
	public GetFlightsResponse getFlights(@XPathParam("//m:from") String from, @XPathParam("//m:to") String to,
			@XPathParam("//m:departureDate") String departureDateString,
			@XPathParam("//m:serviceClass") String serviceClassString) throws DatatypeConfigurationException {

		if (logger.isDebugEnabled()) {
			logger.debug("Received GetFlightsRequest '" + from + "' to '" + to + "' on " + departureDateString);
		}

		ZonedDateTime departureDate = LocalDate.parse(departureDateString).atStartOfDay(ZoneId.systemDefault());
		ServiceClass serviceClass = null;

		if (StringUtils.hasLength(serviceClassString)) {
			serviceClass = ServiceClass.valueOf(serviceClassString.toUpperCase());
		}
		List<org.springframework.ws.samples.airline.domain.Flight> flights = airlineService.getFlights(from, to,
				departureDate, serviceClass);

		GetFlightsResponse response = objectFactory.createGetFlightsResponse();
		for (org.springframework.ws.samples.airline.domain.Flight domainFlight : flights) {
			response.getFlight().add(SchemaConversionUtils.toSchemaType(domainFlight));
		}

		return response;
	}

	/**
	 * This endpoint method uses marshalling to handle message with a
	 * <code>&lt;BookFlightRequest&gt;</code> payload.
	 * @param request the JAXB2 representation of a <code>&lt;BookFlightRequest&gt;</code>
	 * @return the JAXB2 representation of a <code>&lt;BookFlightResponse&gt;</code>
	 */
	@PayloadRoot(localPart = BOOK_FLIGHT_REQUEST, namespace = MESSAGES_NAMESPACE)
	@ResponsePayload
	public JAXBElement<Ticket> bookFlight(@RequestPayload BookFlightRequest request) throws NoSeatAvailableException,
			DatatypeConfigurationException, NoSuchFlightException, NoSuchFrequentFlyerException {

		if (logger.isDebugEnabled()) {
			logger.debug("Received BookingFlightRequest '" + request.getFlightNumber() + "' on '"
					+ request.getDepartureTime() + "' for " + request.getPassengers().getPassengerOrUsername());
		}

		Ticket ticket = bookSchemaFlight(request.getFlightNumber(), request.getDepartureTime(),
				request.getPassengers().getPassengerOrUsername());

		return objectFactory.createBookFlightResponse(ticket);
	}

	/**
	 * Converts between the domain and schema types.
	 */
	private Ticket bookSchemaFlight(String flightNumber, XMLGregorianCalendar xmlDepartureTime,
			List<Object> passengerOrUsernameList) throws NoSeatAvailableException, NoSuchFlightException,
			NoSuchFrequentFlyerException, DatatypeConfigurationException {

		ZonedDateTime departureTime = SchemaConversionUtils.toDateTime(xmlDepartureTime);
		List<Passenger> passengers = new ArrayList<>(passengerOrUsernameList.size());

		for (Object passengerOrUsername : passengerOrUsernameList) {
			if (passengerOrUsername instanceof Name passengerName) {
				Passenger passenger = new Passenger(passengerName.getFirst(), passengerName.getLast());
				passengers.add(passenger);
			}
			else if (passengerOrUsername instanceof String frequentFlyerUsername) {
				FrequentFlyer frequentFlyer = new FrequentFlyer(frequentFlyerUsername);
				passengers.add(frequentFlyer);
			}
		}

		org.springframework.ws.samples.airline.domain.Ticket domainTicket = airlineService.bookFlight(flightNumber,
				departureTime, passengers);

		return SchemaConversionUtils.toSchemaType(domainTicket);
	}

	@PayloadRoot(localPart = GET_FREQUENT_FLYER_MILEAGE_REQUEST, namespace = MESSAGES_NAMESPACE)
	@ResponsePayload
	public Element getFrequentFlyerMileage() throws ParserConfigurationException {

		if (logger.isDebugEnabled()) {
			logger.debug("Received GetFrequentFlyerMileageRequest");
		}

		int mileage = airlineService.getFrequentFlyerMileage();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element response = document.createElementNS(MESSAGES_NAMESPACE, GET_FREQUENT_FLYER_MILEAGE_RESPONSE);
		response.setTextContent(Integer.toString(mileage));

		return response;
	}

}
