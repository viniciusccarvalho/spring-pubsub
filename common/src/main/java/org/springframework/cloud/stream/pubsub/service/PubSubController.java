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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Vinicius Carvalho
 */
@RequestMapping(value = "/pubsub")
public class PubSubController {

	private PubSubService pubSubService;

	public PubSubController(PubSubService pubSubService) {
		this.pubSubService = pubSubService;
	}


	@RequestMapping(method = RequestMethod.POST, value = "/{topic}/")
	public ResponseEntity<Map<String,Object>> test(@RequestBody MessageRequest request, @PathVariable("topic") String topic) throws Exception {

		pubSubService.createTopic(topic);
		Long start = System.currentTimeMillis();
		byte[] payload = createPayload(request.getSize());
		for (int i = 0; i < request.getMessages() / request.getBatchSize(); i++) {
			List<byte[]> messages = new ArrayList<>(request.getBatchSize());
			for(int j=0;j<request.getBatchSize();j++){
				messages.add(payload);
			}
			pubSubService.publish(topic,messages);
		}
		Long end = System.currentTimeMillis();
		pubSubService.destroyTopic(topic);
		Map<String,Object> result = new HashMap<>();
		result.put("executionTime",end-start);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}


	private byte[] createPayload(int size){
		byte[] result = new byte[size];
		Arrays.fill(result,(byte)1);
		return result;
	}

}
