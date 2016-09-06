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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.stream.pubsub.service.PubSubController;
import org.springframework.cloud.stream.pubsub.service.PubSubService;
import org.springframework.cloud.stream.pubsub.service.RESTPubSubService;
import org.springframework.context.annotation.Bean;

/**
 * @author Vinicius Carvalho
 */
@SpringBootApplication
public class RESTPubSubApplication {

	public static void main(String[] args) {
		SpringApplication.run(RESTPubSubApplication.class, args);
	}

	@Value("${spring.application.name}")
	private String APP_NAME;

	@Value("${google.cloud.json.cred}")
	private String creds;

	@Bean
	@ConditionalOnMissingBean(HttpTransport.class)
	public HttpTransport httpTransport(){
		return Utils.getDefaultTransport();
	}

	@Bean
	@ConditionalOnMissingBean(JsonFactory.class)
	public JsonFactory jsonFactory(){
		return Utils.getDefaultJsonFactory();
	}


	@Bean
	public GoogleCredential credentials(HttpTransport httpTransport, JsonFactory jsonFactory) throws Exception {
		GoogleCredential credential =
				GoogleCredential.fromStream(new ByteArrayInputStream(creds.getBytes()),httpTransport,jsonFactory);
		if (credential.createScopedRequired()) {
			credential = credential.createScoped(PubsubScopes.all());
		}
		return  credential;
	}

	@Bean
	public Pubsub pubSub(HttpTransport httpTransport, JsonFactory jsonFactory, GoogleCredential credential) throws Exception{


		HttpRequestInitializer initializer =
				new RetryHttpInitializerWrapper(credential);

		return new Pubsub.Builder(httpTransport, jsonFactory, initializer)
				.setApplicationName(APP_NAME)

				.build();
	}

	@Bean
	public PubSubService pubSubService(Pubsub pubsub) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readValue(creds,JsonNode.class);
		String projectName = node.get("project_id").asText();
		PubSubService pubSubService = new RESTPubSubService(pubsub,projectName);
		return pubSubService;
	}

	@Bean
	public PubSubController pubSubController(PubSubService pubSubService){
		return new PubSubController(pubSubService);
	}

}
