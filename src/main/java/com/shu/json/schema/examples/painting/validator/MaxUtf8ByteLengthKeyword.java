package com.shu.json.schema.examples.painting.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.networknt.schema.AbstractJsonValidator;
import com.networknt.schema.AbstractKeyword;
import com.networknt.schema.CustomErrorMessageType;
import com.networknt.schema.ErrorMessageType;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.FailFastAssertionException;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.MessageSourceValidationMessage;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.ValidationContext;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.i18n.DefaultMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MaxUtf8ByteLengthKeyword extends AbstractKeyword {

	private static final String PATTERN = "Value ''{1}'' must be less than or equal {2} bytes in length.";

    private static final String KEYWORD = "maxUtf8ByteLength";

    public MaxUtf8ByteLengthKeyword() {
        super(KEYWORD);
    }

	@Override
	public JsonValidator newValidator(SchemaLocation schemaLocation, JsonNodePath evaluationPath, JsonNode schemaNode,
			JsonSchema parentSchema, ValidationContext validationContext) throws JsonSchemaException, Exception {
		
		// Only process if the provided schema value is a number.
        if (!JsonNodeType.NUMBER.equals(schemaNode.getNodeType())) {
            return null;
        }

        int maxUtf8ByteLength = schemaNode.asInt();
        
        return new AbstractJsonValidator(schemaLocation, evaluationPath, this, schemaNode) {
            
            @Override
            public Set<ValidationMessage> validate(ExecutionContext executionContext, JsonNode node, JsonNode rootNode,
					JsonNodePath evaluationPath) {
            
                // Get the value as a string and evaluate its length in bytes.
                String value = node.asText();
                if (value.getBytes(StandardCharsets.UTF_8).length > maxUtf8ByteLength) {
                    return fail(CustomErrorMessageType.of(KEYWORD), node,
                            Integer.toString(maxUtf8ByteLength));
                }
                return pass();
            }
            
            private Set<ValidationMessage> fail(ErrorMessageType errorMessageType, JsonNode node, String length) {
		    	Map<String, String> errorMessages = new LinkedHashMap<>();
		    	errorMessages.put(errorMessageType.getErrorCode(), errorMessageType.getErrorCodeValue());
		    	
				MessageSourceValidationMessage.Builder builder = MessageSourceValidationMessage
						.builder((validationContext != null && validationContext.getConfig() != null)
								? validationContext.getConfig().getMessageSource()
								: DefaultMessageSource.getInstance(), errorMessages, (message, failFast) -> {
									if (failFast) {
										throw new FailFastAssertionException(message);
									}
								});
//						.code(errorMessageType.getErrorCode()).schemaLocation(schemaLocation)
//						.evaluationPath(evaluationPath).type(KEYWORD).instanceNode(node)
//						.messageKey(errorMessageType.getErrorCodeValue());
		    	
		    	return Collections
                        .singleton(builder.message(PATTERN)
                        .arguments(node.textValue(), length)
                        .build());
		    }
    
		    private Set<ValidationMessage> pass() {
		    	return Collections.emptySet();
		    } 
        };
	}	

}
