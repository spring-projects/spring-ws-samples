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
package org.springframework.ws.samples.airline.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.springframework.data.annotation.Persistent;

@Entity
@Table(name = "FLIGHT")
public class Flight implements Serializable {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "NUMBER")
	private String number;

	@Column(name = "DEPARTURE_TIME")
	@Persistent
	private ZonedDateTime departureTime;

	@ManyToOne
	@JoinColumn(name = "FROM_AIRPORT_CODE", nullable = false)
	private Airport from;

	@Column(name = "ARRIVAL_TIME")
	@Persistent
	private ZonedDateTime arrivalTime;

	@ManyToOne
	@JoinColumn(name = "TO_AIRPORT_CODE", nullable = false)
	private Airport to;

	@Column(name = "SERVICE_CLASS")
	@Enumerated(EnumType.STRING)
	private ServiceClass serviceClass;

	@Column(name = "SEATS_AVAILABLE")
	private int seatsAvailable;

	@Column(name = "MILES")
	private int miles;

	public Flight() {
	}

	public Flight(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public ZonedDateTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(ZonedDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public ZonedDateTime getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(ZonedDateTime departureTime) {
		this.departureTime = departureTime;
	}

	public Airport getFrom() {
		return from;
	}

	public void setFrom(Airport from) {
		this.from = from;
	}

	public int getMiles() {
		return miles;
	}

	public void setMiles(int miles) {
		this.miles = miles;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getSeatsAvailable() {
		return seatsAvailable;
	}

	public void setSeatsAvailable(int seatsAvailable) {
		this.seatsAvailable = seatsAvailable;
	}

	public ServiceClass getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(ServiceClass serviceClass) {
		this.serviceClass = serviceClass;
	}

	public Airport getTo() {
		return to;
	}

	public void setTo(Airport to) {
		this.to = to;
	}

	public void substractSeats(int count) {
		seatsAvailable -= count;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Flight flight = (Flight) o;

		if (!departureTime.equals(flight.departureTime)) {
			return false;
		}
		return number.equals(flight.number);
	}

	public int hashCode() {
		int result = number.hashCode();
		result = 29 * result + departureTime.hashCode();
		return result;
	}

	public String toString() {
		return getNumber() + " " + getDepartureTime();
	}

}
