package org.springframework.ws.samples.airline.dao;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.samples.airline.domain.Airport;
import org.springframework.ws.samples.airline.domain.Flight;
import org.springframework.ws.samples.airline.domain.FrequentFlyer;
import org.springframework.ws.samples.airline.domain.ServiceClass;

@Configuration
public class Databaseinit {

	@Bean
	CommandLineRunner initializeDatabase(AirportDao airportDao, FlightDao flightDao, FrequentFlyerDao frequentFlyerDao) {
		return args -> {
			Airport amsterdam = airportDao.save(new Airport("AMS", "Schiphol Airport", "Amsterdam"));
			Airport venice = airportDao.save(new Airport("VCE", "Marco Polo Airport", "Venice"));
			Airport rotterdam = airportDao.save(new Airport("RTM", "Rotterdam Airport", "Rotterdam"));
			Airport gardermoen = airportDao.save(new Airport("OSL", "Gardermoen", "Oslo"));

			Flight flight = new Flight();
			flight.setNumber("KL1653");
			flight.setDepartureTime(ZonedDateTime.of(2006, 1, 31, 10, 5, 0, 0, ZoneId.systemDefault()));
			flight.setFrom(amsterdam);
			flight.setArrivalTime(ZonedDateTime.of(2006, 1, 31, 12, 25, 0, 0, ZoneId.systemDefault()));
			flight.setTo(venice);
			flight.setServiceClass(ServiceClass.ECONOMY);
			flight.setSeatsAvailable(5);
			flight.setMiles(200);

			flightDao.save(flight);

			flight = new Flight();
			flight.setNumber("KL1654");
			flight.setDepartureTime(ZonedDateTime.of(2006, 2, 5, 12, 40, 0, 0, ZoneId.systemDefault()));
			flight.setFrom(venice);
			flight.setArrivalTime(ZonedDateTime.of(2006, 2, 5, 14, 15, 0, 0, ZoneId.systemDefault()));
			flight.setTo(amsterdam);
			flight.setServiceClass(ServiceClass.ECONOMY);
			flight.setSeatsAvailable(5);
			flight.setMiles(200);

			flightDao.save(flight);

			frequentFlyerDao.save(new FrequentFlyer("John", "Doe", "john", "changeme"));
		};
	}
}
