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
