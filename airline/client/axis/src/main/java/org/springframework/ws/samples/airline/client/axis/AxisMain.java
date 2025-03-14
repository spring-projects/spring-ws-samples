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

package org.springframework.ws.samples.airline.client.axis;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.AirportCode;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.BookFlightRequest;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.BookFlightResponse;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.Flight;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.FrequentFlyerUsername;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.GetFlightsRequest;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.GetFlightsResponse;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.Name;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.PassengersChoice;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.Passengers_type0;
import org.springframework.ws.samples.airline.client.axis.AirlineServiceStub.Ticket;

/**
 * Simple client that calls the <code>GetFlights</code> and <code>BookFlight</code>
 * operations using Axis 2 and HTTP.
 *
 * @author Arjen Poutsma
 * @author Stephane Nicoll
 */
public class AxisMain {

	public static void main(String[] args) throws RemoteException {
		AirlineServiceStub airline = (args.length > 0) ? new AirlineServiceStub(args[0]) : new AirlineServiceStub();
		GetFlightsRequest request = new GetFlightsRequest();
		request.setFrom(createAirportCode("AMS"));
		request.setTo(createAirportCode("VCE"));
		Calendar departureCalendar = Calendar.getInstance();
		departureCalendar.set(Calendar.YEAR, 2006);
		departureCalendar.set(Calendar.MONTH, Calendar.JANUARY);
		departureCalendar.set(Calendar.DATE, 31);
		Date departureDate = departureCalendar.getTime();
		request.setDepartureDate(departureDate);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("Requesting flights on " + dateFormat.format(departureDate));

		GetFlightsResponse flightsResponse = airline.getFlights(request);
		Flight[] flights = flightsResponse.getFlight();

		System.out.println("Got " + flights.length + " results");

		if (flights.length > 0) {
			// Book the first flight using John Doe as a frequent flyer
			BookFlightRequest bookFlightRequest = new BookFlightRequest();
			bookFlightRequest.setFlightNumber(flights[0].getNumber());
			bookFlightRequest.setDepartureTime(flights[0].getDepartureTime());
			Passengers_type0 passengers = new Passengers_type0();
			passengers.addPassengersChoice(createPassenger("john"));
			bookFlightRequest.setPassengers(passengers);
			BookFlightResponse bookFlightResponse = airline.bookFlight(bookFlightRequest);
			Ticket ticket = bookFlightResponse.getBookFlightResponse();

			writeTicket(ticket);
		}
	}

	private static void writeTicket(Ticket ticket) {
		System.out.println("Ticket " + ticket.getId());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("Ticket issue date:\t" + dateFormat.format(ticket.getIssueDate()));

		Name[] passengers = ticket.getPassengers().getPassenger();
		for (Name passenger : passengers) {
			writeName(passenger);
		}
		writeFlight(ticket.getFlight());
	}

	private static void writeName(Name name) {

		System.out.println("Passenger Name:");
		System.out.println(name.getFirst() + " " + name.getLast());
		System.out.println("------------");
	}

	private static void writeFlight(Flight flight) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		System.out.println(dateFormat.format(flight.getDepartureTime().getTime()));
		System.out.println(flight.getNumber() + "\t" + flight.getServiceClass());
		System.out.println("------------");
		System.out.println("Depart:\t" + flight.getFrom().getCode() + "-" + flight.getFrom().getName() + "\t"
				+ dateFormat.format(flight.getDepartureTime().getTime()));
		System.out.println("\t" + flight.getFrom().getCity());
		System.out.println("Arrive:\t" + flight.getTo().getCode() + "-" + flight.getTo().getName() + "\t"
				+ dateFormat.format(flight.getArrivalTime().getTime()));
		System.out.println("\t" + flight.getTo().getCity());
	}

	private static AirportCode createAirportCode(String code) {
		AirportCode airportCode = new AirportCode();
		airportCode.setAirportCode(code);
		return airportCode;
	}

	private static PassengersChoice createPassenger(String username) {
		PassengersChoice passenger = new PassengersChoice();
		FrequentFlyerUsername frequentFlyerUsername = new FrequentFlyerUsername();
		frequentFlyerUsername.setFrequentFlyerUsername(username);
		passenger.setUsername(frequentFlyerUsername);
		return passenger;
	}

}
