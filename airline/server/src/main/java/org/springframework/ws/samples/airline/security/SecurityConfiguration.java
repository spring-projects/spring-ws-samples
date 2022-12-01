package org.springframework.ws.samples.airline.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.ws.samples.airline.dao.FrequentFlyerDao;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfiguration {

	@Bean
	SpringSecurityFrequentFlyerService userDetailsService(FrequentFlyerDao frequentFlyerDao) {
		return new SpringSecurityFrequentFlyerService(frequentFlyerDao);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
//
//		security.authorizeHttpRequests() //
//				.requestMatchers("/login", "/error", "/logout").permitAll() //
//				.anyRequest().authenticated() //
//				.and() //
//				.csrf().disable();
//
//		return security.build();
//	}
}
