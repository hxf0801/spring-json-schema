package com.shu.json.schema.examples.scf;

import static com.shu.json.schema.examples.SchemaLocations.FEECODE_SPEC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.shu.json.schema.examples.myjson.MyJsonController;

@RestController
@RequestMapping("/scf")
public class FeeCodeSpecController {
	private static final Logger logger = LoggerFactory.getLogger(MyJsonController.class);

	@PostMapping("/feecodeSpec")
	public ResponseEntity<String> validateEvent(@JsonValidation(FEECODE_SPEC) JsonNode jsonNode) {
		logger.debug("Validated request body: " + jsonNode);
		return ResponseEntity.ok().header("feecodeOk", "Successfully validated").body(" " + jsonNode);
	}
}
