/*
 *  Copyright 2016 original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.springframework.cloud.stream.pubsub;

import java.io.ByteArrayInputStream;

import com.google.cloud.AuthCredentials;
import com.google.cloud.pubsub.PubSubOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.pubsub.service.GRPCPubSubService;
import org.springframework.cloud.stream.pubsub.service.PubSubController;
import org.springframework.cloud.stream.pubsub.service.PubSubService;
import org.springframework.context.annotation.Bean;

/**
 * @author Vinicius Carvalho
 */
@SpringBootApplication
public class GRPCPubSubApplication {


	public static void main(String[] args) {
		SpringApplication.run(GRPCPubSubApplication.class,args);
	}

	@Bean
	public PubSubService pubSubService(@Value("${google.cloud.json.cred}") String creds) throws Exception{
		return new GRPCPubSubService(PubSubOptions.builder().authCredentials(AuthCredentials.createForJson(new ByteArrayInputStream(creds.getBytes()))).build().service());
	}

	@Bean
	public PubSubController pubSubController(PubSubService service){
		return new PubSubController(service);
	}

}
