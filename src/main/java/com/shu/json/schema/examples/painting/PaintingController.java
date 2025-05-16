package com.shu.json.schema.examples.painting;

import static com.shu.json.schema.examples.SchemaLocations.PAINTING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shu.json.schema.examples.painting.vo.Painting;

@RestController
public class PaintingController {
	private static final Logger logger = LoggerFactory.getLogger(PaintingController.class);

	@PostMapping("/paintings")
	public ResponseEntity<String> createPainting(@ValidJson(PAINTING) Painting painting) {
		logger.debug("Validated painting: " + painting);
		return ResponseEntity.ok().header("PaintingOK", "Successfully painted").body("Hello " + painting);
	}
}
