package com.shu.json.schema.examples.painting.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.Keyword;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.ValidationContext;

public class EqualsKeyword implements Keyword {
    @Override
    public String getValue() {
        return "equals";
    }
    @Override
    public JsonValidator newValidator(SchemaLocation schemaLocation, JsonNodePath evaluationPath,
            JsonNode schemaNode, JsonSchema parentSchema, ValidationContext validationContext)
            throws JsonSchemaException, Exception {
        return new EqualsValidator(schemaLocation, evaluationPath, schemaNode, parentSchema, this, validationContext, false);
    }
}
