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

package com.mycompany.hr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

/**
 * @author Arjen Poutsma
 */
@Configuration
public class HRConfiguration {

	@Bean
	public DefaultWsdl11Definition holiday() {

		DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
		definition.setPortTypeName("HumanResource");
		definition.setLocationUri("/holidayService/");
		definition.setTargetNamespace("http://mycompany.com/hr/definitions");
		definition.setSchemaCollection(holidayXsd());
		return definition;
	}

	@Bean
	public CommonsXsdSchemaCollection holidayXsd() {

		CommonsXsdSchemaCollection collection = new CommonsXsdSchemaCollection(new ClassPathResource("/hr.xsd"));
		collection.setInline(true);
		return collection;
	}

}
