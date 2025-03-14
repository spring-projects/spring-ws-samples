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

package org.springframework.ws.samples.airline.service;

import java.time.ZonedDateTime;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception thrown when a specified flight cannot be found.
 *
 * @author Arjen Poutsma
 */
@SoapFault(faultCode = FaultCode.CLIENT)
public class NoSuchFlightException extends Exception {

	private String flightNumber;

	private ZonedDateTime departureTime;

	public NoSuchFlightException(String flightNumber, ZonedDateTime departureTime) {
		super("No flight with number [" + flightNumber + "] and departure time [" + departureTime + "]");
		this.flightNumber = flightNumber;
		this.departureTime = departureTime;
	}

	public NoSuchFlightException(Long id) {
		super("No flight with id [" + id + "]");
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public ZonedDateTime getDepartureTime() {
		return departureTime;
	}

}
