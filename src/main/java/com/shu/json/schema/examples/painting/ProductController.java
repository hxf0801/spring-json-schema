package com.shu.json.schema.examples.painting;

import static com.shu.json.schema.examples.SchemaLocations.PRODUCT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/painting")
public class ProductController {
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@PostMapping("/product")
	public ResponseEntity<String> validateEvent(@ValidJson(PRODUCT) JsonNode jsonNode) {
		logger.debug("Validated request body: " + jsonNode);
		return ResponseEntity.ok().header("productOK", "Successfully validated").body(" " + jsonNode);
	}
}
