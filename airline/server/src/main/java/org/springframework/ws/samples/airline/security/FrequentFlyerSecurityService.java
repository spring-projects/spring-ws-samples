package org.springframework.ws.samples.airline.security;

import org.springframework.ws.samples.airline.domain.FrequentFlyer;
import org.springframework.ws.samples.airline.service.NoSuchFrequentFlyerException;

/**
 * Defines the business logic for handling frequent flyers.
 *
 * @author Arjen Poutsma
 */
public interface FrequentFlyerSecurityService {

	/**
	 * Returns the <code>FrequentFlyer</code> with the given username.
	 *
	 * @param username the username
	 * @return the frequent flyer with the given username, or <code>null</code> if not found
	 * @throws NoSuchFrequentFlyerException when the frequent flyer cannot be found
	 */
	FrequentFlyer getFrequentFlyer(String username) throws NoSuchFrequentFlyerException;

	/**
	 * Returns the <code>FrequentFlyer</code> that is currently logged in.
	 *
	 * @return the frequent flyer that is currently logged in, or <code>null</code> if not found
	 */
	FrequentFlyer getCurrentlyAuthenticatedFrequentFlyer();

}
