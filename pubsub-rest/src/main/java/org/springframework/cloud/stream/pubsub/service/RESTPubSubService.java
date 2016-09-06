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

package org.springframework.cloud.stream.pubsub.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.api.services.pubsub.model.Topic;

/**
 * @author Vinicius Carvalho
 */
public class RESTPubSubService implements PubSubService {

	private Pubsub client;
	final String TOPICS_ENDPOINT = "projects/%s/topics";
	private final String projectName;

	public RESTPubSubService(Pubsub client, String projectName) {
		this.client = client;
		this.projectName = projectName;
	}

	@Override
	public void publish(String topic, byte[] payload) {

	}

	@Override
	public void publish(String topic, List<byte[]> payloads) {
		PublishRequest publishRequest = new PublishRequest();
		String topicName = String.format(TOPICS_ENDPOINT,projectName)+"/"+topic;
		List<PubsubMessage> messages = payloads.stream().map(bytes -> {
			PubsubMessage message = new PubsubMessage();
			message.encodeData(bytes);
			return message;
		}).collect(Collectors.toList());
		publishRequest.setMessages(messages);
		try {
			client.projects().topics().publish(topicName,publishRequest).execute();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createTopic(String topic) {
		String topicName = String.format(TOPICS_ENDPOINT,projectName)+"/"+topic;
		try {
			client.projects().topics().create(topicName,new Topic()).execute();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroyTopic(String topic) {
		String topicName = String.format(TOPICS_ENDPOINT,projectName)+"/"+topic;
		try {
			client.projects().topics().delete(topicName);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
