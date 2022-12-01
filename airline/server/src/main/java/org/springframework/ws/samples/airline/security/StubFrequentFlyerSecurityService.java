package org.springframework.ws.samples.airline.security;

import org.springframework.ws.samples.airline.domain.FrequentFlyer;
import org.springframework.ws.samples.airline.service.NoSuchFrequentFlyerException;

/**
 * Stub implementation of <code>FrequentFlyerSecurityService</code>. This implementation is used by default by
 * {@link org.springframework.ws.samples.airline.service.impl.AirlineServiceImpl}, to allow it to run without depending
 * on Spring Security.
 *
 * @author Arjen Poutsma
 */
public class StubFrequentFlyerSecurityService implements FrequentFlyerSecurityService {

	private FrequentFlyer john;

	public StubFrequentFlyerSecurityService() {
		this.john = new FrequentFlyer("John", "Doe", "john", "changeme");
		john.setMiles(10);
	}

	@Override
	public FrequentFlyer getFrequentFlyer(String username) throws NoSuchFrequentFlyerException {
		if (john.getUsername().equals(username)) {
			return john;
		} else {
			throw new NoSuchFrequentFlyerException(username);
		}
	}

	@Override
	public FrequentFlyer getCurrentlyAuthenticatedFrequentFlyer() {
		return john;
	}
}
