package org.springframework.ws.samples.airline.security;

import org.apache.wss4j.common.principal.UsernameTokenPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.samples.airline.dao.FrequentFlyerDao;
import org.springframework.ws.samples.airline.domain.FrequentFlyer;
import org.springframework.ws.samples.airline.service.NoSuchFrequentFlyerException;

/**
 * Implementation of the {@link FrequentFlyerSecurityService} that uses Spring Security.
 *
 * @author Arjen Poutsma
 */
public class SpringSecurityFrequentFlyerService implements FrequentFlyerSecurityService, UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(SpringSecurityFrequentFlyerService.class);

	private final FrequentFlyerDao frequentFlyerDao;

	public SpringSecurityFrequentFlyerService(FrequentFlyerDao frequentFlyerDao) {
		this.frequentFlyerDao = frequentFlyerDao;
	}

	@Override
	@Transactional
	public FrequentFlyer getFrequentFlyer(String username) throws NoSuchFrequentFlyerException {
		return frequentFlyerDao.findByUsername(username) //
				.orElseThrow(() -> new NoSuchFrequentFlyerException(username));
	}

	@Override
	@Transactional
	public FrequentFlyer getCurrentlyAuthenticatedFrequentFlyer() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			UsernameTokenPrincipal principal = (UsernameTokenPrincipal) authentication.getPrincipal();
			FrequentFlyerDetails frequentFlyerDetails = (FrequentFlyerDetails) loadUserByUsername(principal.getName());
			return frequentFlyerDetails.getFrequentFlyer();
		}

		return null;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Looking up " + username);

		FrequentFlyerDetails details = frequentFlyerDao.findByUsername(username) //
				.map(FrequentFlyerDetails::new) //
				.orElseThrow(() -> new UsernameNotFoundException("Frequent flyer '" + username + "' not found"));

		log.debug("Found " + details);

		return details;
	}
}
