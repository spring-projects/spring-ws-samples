package com.mycompany.hr.config;

import org.springframework.ws.transport.http.support.AbstractAnnotationConfigMessageDispatcherServletInitializer;

/**
 * @author Arjen Poutsma
 */
public class HRServletInitializer
		extends AbstractAnnotationConfigMessageDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{HRConfiguration.class};
	}

	@Override
	public boolean isTransformWsdlLocations() {
		return true;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/*"};
	}
}
