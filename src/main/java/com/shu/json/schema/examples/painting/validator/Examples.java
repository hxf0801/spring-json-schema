package com.shu.json.schema.examples.painting.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.Formats;
import com.networknt.schema.JsonMetaSchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.NonValidationKeyword;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.SpecVersionDetector;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.ValidatorTypeCode;
import com.networknt.schema.format.PatternFormat;
import com.networknt.schema.serialization.JsonMapperFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Examples {

	public JsonSchema getJsonSchemaSample1() throws IOException {
		String schemaId = "https://schemas.amazon.com/selling-partners/definitions/product-types/meta-schema/v1";
		
		// Standard JSON Schema 2019-09
		JsonMetaSchema standardMetaSchema = JsonMetaSchema.getV201909();

		// Keywords that are informational only and do not require validation.
		Collection<String> NON_VALIDATING_KEYWORDS = Arrays.asList("$schema", "$id", "title", "description", "default", "definitions", "$defs", "editable", "enumNames");
		
		// Register custom keyword validation classes (see below).
		JsonMetaSchema metaSchema = JsonMetaSchema.builder(schemaId, standardMetaSchema)
		    .keywords(NON_VALIDATING_KEYWORDS.stream().map(NonValidationKeyword::new)
		        .collect(Collectors.toSet()))
		    .build();
		
		/* below block is equivalent to the above */
		/*
		JsonMetaSchema metaSchema = new JsonMetaSchema.Builder(schemaId)
	            .idKeyword("$id")
	            .formats(Formats.DEFAULT)
	            .keywords(ValidatorTypeCode.getKeywords(SpecVersion.VersionFlag.V201909))
	            // keywords that may validly exist, but have no validation aspect to them
	            .keywords(Arrays.asList(
	                    new NonValidationKeyword("$schema"),
	                    new NonValidationKeyword("$id"),
	                    new NonValidationKeyword("title"),
	                    new NonValidationKeyword("description"),
	                    new NonValidationKeyword("default"),
	                    new NonValidationKeyword("definitions"),
	                    new NonValidationKeyword("$defs")
	            ))
	            // add your custom keyword
	            .keyword(new GroovyKeyword())
	            .build();
		*/

		// Build the JsonSchemaFactory.
		JsonSchemaFactory schemaFactory = new JsonSchemaFactory.Builder()
		    .defaultMetaSchemaIri(schemaId)
		    .metaSchema(standardMetaSchema)
		    .metaSchema(metaSchema)
		    .build();
		    
		// Create the JsonSchema instance.
		JsonSchema luggageSchema = schemaFactory.getSchema(new String(Files.readAllBytes(Paths.get("luggage.json"))));
		return luggageSchema;
	}
	
	public void Snippet2() {
		Function<ObjectNode, Set<JsonSchemaException>> validateAgainstMetaSchema = schema -> {
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
			JsonSchema metaSchema = factory.getSchema("https://json-schema.org/draft/2019-09/schema");
			return metaSchema.validate(schema).stream()
					.map((validation) -> new JsonSchemaException(validation.getMessage()))
					.collect(Collectors.toSet());
		};
	}

	public class OneTestExample1 {
		private final ObjectMapper mapper = JsonMapperFactory.getInstance();

		void test() throws IOException {
			JsonSchema schema = getJsonSchemaFromStringContent(
					"{\"enum\":[1, 2, 3, 4],\"enumErrorCode\":\"Not in the list\"}");
			JsonNode node = getJsonNodeFromStringContent("7");
			Set<ValidationMessage> errors = schema.validate(node);
			//assertThat(errors.size(), is(1));

			// With automatic version detection
			JsonNode schemaNode = getJsonNodeFromStringContent(
					"{\"$schema\": \"http://json-schema.org/draft-06/schema#\", \"properties\": { \"id\": {\"type\": \"number\"}}}");
			JsonSchema schema1 = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode);

			schema.initializeValidators(); // by default all schemas are loaded lazily. You can load them eagerly via
											// initializeValidators()

			JsonNode node1 = getJsonNodeFromStringContent("{\"id\": \"2\"}");
			Set<ValidationMessage> error1s = schema.validate(node);
			//assertThat(error1s.size(), is(1));
		}

		public JsonSchema getJsonSchemaFromStringContent(String schemaContent) {
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
			return factory.getSchema(schemaContent);
		}

		public JsonNode getJsonNodeFromStringContent(String content) throws IOException {
			return mapper.readTree(content);
		}
		
		// Automatically detect version for given JsonNode
	    public JsonSchema getJsonSchemaFromJsonNodeAutomaticVersion(JsonNode jsonNode) {
	        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonNode));
	        return factory.getSchema(jsonNode);
	    }
	}
	
	public class OverrideEmailValidator {
		/**
		 * Override Email/UUID/DateTime Validator: if the format keyword is "email",
		 * "uuid", "date", "date-time", default validator provided by the library will
		 * be used. If you want to override this behavior, do as below.
		 */
		public JsonSchemaFactory mySchemaFactory() {
			// base on JsonMetaSchema.V201909 copy code below
			String URI = "https://json-schema.org/draft/2019-09/schema";
			String ID = "$id";

			JsonMetaSchema overrideEmailValidatorMetaSchema = new JsonMetaSchema.Builder(URI).idKeyword(ID)
					// Override EmailValidator
					.format(PatternFormat.of("email",
							"^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$", null))
					.build();

			return new JsonSchemaFactory.Builder().defaultMetaSchemaIri(overrideEmailValidatorMetaSchema.getIri())
					.metaSchema(overrideEmailValidatorMetaSchema).build();
		}
	}

}
