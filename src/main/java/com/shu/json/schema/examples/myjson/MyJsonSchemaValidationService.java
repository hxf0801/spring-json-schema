package com.shu.json.schema.examples.myjson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

/**
 * This service is responsible for loading the JSON schema from an environment
 * variable in <code>application.properties</code> and validating JSON data
 * against it.
 */
@Service
public class MyJsonSchemaValidationService {
	private static final Logger logger = LoggerFactory.getLogger(MyJsonSchemaValidationService.class);
	private JsonSchema jsonSchema;

	public MyJsonSchemaValidationService(@Value("${json.schema.location}") String schemaLocation) throws IOException {
		Resource resource = new ClassPathResource(schemaLocation);
		String schemaJson = new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
		JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
		this.jsonSchema = factory.getSchema(schemaJson);
	}

	public Set<ValidationMessage> validateJson(JsonNode jsonNode) {
		Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
		logger.debug(" === validateJson === jsonNode={}", jsonNode);
		return errors;
	}
}
