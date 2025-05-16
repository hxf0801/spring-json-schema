package com.shu.json.schema.examples.myjson;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;

/**
 * MyJsonController is a Spring Boot REST controller that handles JSON
 * validation requests. It uses the MyJsonSchemaValidationService to validate
 * incoming JSON data against a predefined schema.
 */
@RestController
public class MyJsonController {
	private static final Logger logger = LoggerFactory.getLogger(MyJsonController.class);

	@Autowired
	private MyJsonSchemaValidationService service;
	@Autowired
    private ObjectMapper objectMapper;
	
	@PostMapping("/myjson")
    public ResponseEntity<String> validateEvent(@RequestBody String jsonData) {
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonData);
			Set<ValidationMessage> errors = service.validateJson(jsonNode);
			if (errors.isEmpty()) {
				logger.debug("json data is valid");
				return ResponseEntity.ok("Successfully ");
			} else {
				logger.debug("json data is invalid");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON data " + errors);
			}
		} catch(IOException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error parsing JSON data");
		}
    }
}
