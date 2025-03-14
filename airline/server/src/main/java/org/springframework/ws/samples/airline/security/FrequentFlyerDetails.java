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

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ws.samples.airline.domain.FrequentFlyer;

public class FrequentFlyerDetails implements UserDetails {

	private static final List<GrantedAuthority> GRANTED_AUTHORITIES = List
		.of(new SimpleGrantedAuthority("ROLE_FREQUENT_FLYER"));

	private FrequentFlyer frequentFlyer;

	public FrequentFlyerDetails(FrequentFlyer frequentFlyer) {
		this.frequentFlyer = frequentFlyer;
	}

	public FrequentFlyer getFrequentFlyer() {
		return frequentFlyer;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return GRANTED_AUTHORITIES;
	}

	@Override
	public String getUsername() {
		return frequentFlyer.getUsername();
	}

	@Override
	public String getPassword() {
		return frequentFlyer.getPassword();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "FrequentFlyerDetails{" + "frequentFlyer=" + frequentFlyer + '}';
	}

}
