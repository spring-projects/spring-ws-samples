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

package org.springframework.ws.samples.airline.web;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ws.samples.airline.domain.ServiceClass;
import org.springframework.ws.samples.airline.service.AirlineService;
import org.springframework.ws.samples.airline.service.NoSuchFlightException;

/** @author Arjen Poutsma */
@Controller
@RequestMapping("/flights")
public class FlightsController {

	private AirlineService airlineService;

	public FlightsController(AirlineService airlineService) {

		Assert.notNull(airlineService, "'airlineService' must not be null");
		this.airlineService = airlineService;
	}

	@GetMapping
	public String flightList(@RequestParam(value = "from", required = false) String fromAirportCode,
			@RequestParam(value = "to", required = false) String toAirportCode,
			@RequestParam(value = "departureDate", required = false) String departureDateString,
			@RequestParam(value = "serviceClass", required = false) String serviceClassString, ModelMap model) {

		if (ObjectUtils.isEmpty(departureDateString)) {
			departureDateString = LocalDate.now().toString();
		}
		if (ObjectUtils.isEmpty(serviceClassString)) {
			serviceClassString = "ECONOMY";
		}
		ServiceClass serviceClass = ServiceClass.valueOf(serviceClassString);
        ZonedDateTime departureDate = ZonedDateTime.parse(departureDateString);

		if (StringUtils.hasLength(fromAirportCode) && StringUtils.hasLength(toAirportCode)) {
			model.addAttribute("from", fromAirportCode);
			model.addAttribute("to", toAirportCode);
			model.addAttribute("departureDate", departureDateString);
			model.addAttribute("serviceClass", serviceClassString);
			model.addAttribute("flights",
					airlineService.getFlights(fromAirportCode, toAirportCode, departureDate, serviceClass));
		}
		return "flights";
	}

	@GetMapping(value = "{id}")
	public String singleFlight(@PathVariable("id") long id, ModelMap model) throws NoSuchFlightException {

		model.addAttribute(airlineService.getFlight(id));
		return "flight";
	}

}
