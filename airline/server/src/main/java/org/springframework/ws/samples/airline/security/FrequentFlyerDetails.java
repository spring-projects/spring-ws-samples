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
