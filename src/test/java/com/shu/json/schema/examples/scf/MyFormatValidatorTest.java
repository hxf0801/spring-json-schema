package com.shu.json.schema.examples.scf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonMetaSchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.OutputFormat;
import com.networknt.schema.ValidationMessage;

public class MyFormatValidatorTest {
	@Test
    void durationFormatValidatorTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final String schema = "{\"type\": \"number\", \"format\": \"matchnumber\"}\n";
        final JsonNode validTargetNode = objectMapper.readTree("12345");
        final JsonNode invalidTargetNode = objectMapper.readTree("234");

        JsonMetaSchema standardMetaSchema = JsonMetaSchema.getV202012();

        // Customize own meta-schema based on the standard dialect 2020-12 version with custom keywords, custom formats
        JsonMetaSchema scfMetaSchema = JsonMetaSchema.builder(standardMetaSchema)
                .format(new MatchNumberFormat(new BigDecimal("12345")))
//                .formatKeywordFactory(CustomFormatKeyword::new)
                .build();
        JsonSchemaFactory validatorFactory = JsonSchemaFactory.getInstance(standardMetaSchema.getSpecification(),
                builder -> builder.metaSchema(scfMetaSchema));
        final JsonSchema validatorSchema = validatorFactory.getSchema(schema);

        Set<ValidationMessage> messages = validatorSchema.validate(validTargetNode, (executionContext, validationContext) -> {
            executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
        });
        assertEquals(0, messages.size());

        messages = validatorSchema.validate(invalidTargetNode, OutputFormat.DEFAULT, (executionContext, validationContext) -> {
            executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
        });
        messages.stream().forEach(System.out::println);
        assertEquals(1, messages.size());

    }
}
