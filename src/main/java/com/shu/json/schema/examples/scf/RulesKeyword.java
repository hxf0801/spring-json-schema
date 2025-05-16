package com.shu.json.schema.examples.scf;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.AbstractJsonValidator;
import com.networknt.schema.AbstractKeyword;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.Keyword;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.ValidationContext;
import com.networknt.schema.ValidationMessage;

/**
 * This has no validation aspect.
 */
public class RulesKeyword extends AbstractKeyword {
	private static final Logger logger = LoggerFactory.getLogger(RulesKeyword.class);
	
	public RulesKeyword(String keyword) {
		super(keyword);
	}

	private static final class Validator extends AbstractJsonValidator {
        public Validator(SchemaLocation schemaLocation, JsonNodePath evaluationPath, JsonNode schemaNode,
                JsonSchema parentSchema, ValidationContext validationContext, Keyword keyword) {
            super(schemaLocation, evaluationPath, keyword, schemaNode);
        }

        @Override
        public Set<ValidationMessage> validate(ExecutionContext executionContext, JsonNode node, JsonNode rootNode, JsonNodePath instanceLocation) {
			logger.debug("schemaNode=" + getSchemaNode() + ", value=" + getNodeValue(getSchemaNode()));
			return Collections.emptySet();
        }

        private Object getNodeValue(JsonNode schemaNode) {
            if (schemaNode.isTextual()) {
                return schemaNode.textValue(); 
            } else if (schemaNode.isNumber()) {
                return schemaNode.numberValue();
            } else if (schemaNode.isObject()) {
                return schemaNode;
            }
            return null;
        }
    }
	
	@Override
	public JsonValidator newValidator(SchemaLocation schemaLocation, JsonNodePath evaluationPath, JsonNode schemaNode,
			JsonSchema parentSchema, ValidationContext validationContext) throws JsonSchemaException, Exception {
		return new Validator(schemaLocation, evaluationPath, schemaNode, parentSchema, validationContext, this);
	}

}
