package org.springframework.ws.samples.airline.security;

import java.util.HashMap;
import java.util.Map;

import net.bytebuddy.build.Plugin;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ws.samples.airline.dao.FrequentFlyerDao;

@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration /*extends WebSecurityConfigurerAdapter*/ {

	@Bean
	SpringFrequentFlyerSecurityService securityService(FrequentFlyerDao frequentFlyerDao) {
		return new SpringFrequentFlyerSecurityService(frequentFlyerDao);
	}

	@Bean
	AuthenticationManager authenticationManager(SpringFrequentFlyerSecurityService securityService) {

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(securityService);
		return new ProviderManager(authenticationProvider);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
}
