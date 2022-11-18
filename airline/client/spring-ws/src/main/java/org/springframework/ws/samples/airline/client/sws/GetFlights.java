/*
 * Copyright 2005-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.samples.airline.client.sws;

import jakarta.xml.bind.JAXBElement;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.springWs.samples.airline.schemas.*;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class GetFlights extends WebServiceGatewaySupport {

	public GetFlights(WebServiceMessageFactory messageFactory) {
		super(messageFactory);
	}

	public void getFlights() {

		GetFlightsRequest getFlightsRequest = new GetFlightsRequest();
		getFlightsRequest.setFrom("AMS");
		getFlightsRequest.setTo("VCE");
		XMLGregorianCalendar departureDate = null;
		try {
			departureDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2006, 1, 31,
					DatatypeConstants.FIELD_UNDEFINED);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}

		getFlightsRequest.setDepartureDate(departureDate);

		System.out.println("Requesting flights on " + departureDate);
		GetFlightsResponse response = (GetFlightsResponse) getWebServiceTemplate().marshalSendAndReceive(getFlightsRequest);
		System.out.println("Got " + response.getFlight().size() + " results");
		if (response.getFlight().size() > 0) {
			// Book the first flight using John Doe as a frequent flyer
			BookFlightRequest bookFlightRequest = new BookFlightRequest();
			bookFlightRequest.setFlightNumber(response.getFlight().get(0).getNumber());
			bookFlightRequest.setDepartureTime(response.getFlight().get(0).getDepartureTime());
			BookFlightRequest.Passengers passengers = new BookFlightRequest.Passengers();
			passengers.getPassengerOrUsername().add("john");
			bookFlightRequest.setPassengers(passengers);

			JAXBElement<Ticket> ticket = (JAXBElement<Ticket>) getWebServiceTemplate()
					.marshalSendAndReceive(bookFlightRequest);

			writeTicket(ticket.getValue());
		}
	}

	private void writeTicket(Ticket ticket) {

		System.out.println("Ticket " + ticket.getId());
		System.out.println("Ticket issue date:\t" + ticket.getIssueDate());
		ticket.getPassengers().getPassenger().forEach(this::writeName);
		writeFlight(ticket.getFlight());
	}

	private void writeName(Name name) {

		System.out.println("Passenger Name:");
		System.out.println(name.getFirst() + " " + name.getLast());
		System.out.println("------------");
	}

	private void writeFlight(Flight flight) {

		System.out.println(flight.getDepartureTime());
		System.out.println(flight.getNumber() + "\t" + flight.getServiceClass());
		System.out.println("------------");
		System.out.println(
				"Depart:\t" + flight.getFrom().getCode() + "-" + flight.getFrom().getName() + "\t" + flight.getDepartureTime());
		System.out.println("\t" + flight.getFrom().getCity());
		System.out.println(
				"Arrive:\t" + flight.getTo().getCode() + "-" + flight.getTo().getName() + "\t" + flight.getArrivalTime());
		System.out.println("\t" + flight.getTo().getCity());
	}
}
