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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Persistent;

@Entity
@Table(name = "TICKET")
public class Ticket implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ISSUE_DATE")
	@Persistent
	private LocalDate issueDate;

	@ManyToOne
	@JoinColumn(name = "FLIGHT_ID", nullable = false)
	private Flight flight;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "PASSENGER_TICKET", joinColumns = @JoinColumn(name = "TICKET_ID"),
			inverseJoinColumns = @JoinColumn(name = "PASSENGER_ID"))
	private Set<Passenger> passengers = new HashSet<Passenger>();

	public Ticket() {
	}

	public Ticket(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public Set<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<Passenger> passengers) {
		this.passengers = passengers;
	}

	public void addPassenger(Passenger passenger) {
		passengers.add(passenger);
	}

}
