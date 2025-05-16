package com.shu.json.schema.examples.painting.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.google.common.collect.Streams;
import com.networknt.schema.AbstractJsonValidator;
import com.networknt.schema.AbstractKeyword;
import com.networknt.schema.CustomErrorMessageType;
import com.networknt.schema.ErrorMessageType;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.FailFastAssertionException;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.MessageSourceValidationMessage;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.ValidationContext;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.i18n.DefaultMessageSource;
import com.networknt.schema.utils.StringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example validator for the "maxUniqueItems" keyword.
 */
public class MaxUniqueItemsKeyword extends AbstractKeyword {
	private static final Logger logger = LoggerFactory.getLogger(MaxUniqueItemsKeyword.class);

    private static final String PATTERN = "Each combination of selector "
            + "values may only occur {1} times. The following selector value combination occurs too many times: {2}";

    private static final String KEYWORD = "maxUniqueItems";
    private static final String SELECTORS = "required";

    public MaxUniqueItemsKeyword() {
        super(KEYWORD);
    }

    @Override
    public JsonValidator newValidator(SchemaLocation schemaLocation, JsonNodePath evaluationPath, JsonNode schemaNode,
            JsonSchema parentSchema, ValidationContext validationContext) {
            
        // Only process if the provided schema value is a number.
        if (!JsonNodeType.NUMBER.equals(schemaNode.getNodeType())) {
            return null;
        }

        int maxUniqueItems = schemaNode.asInt();

        // Get the selector properties configured on the scheme element, if they exist. Otherwise, this validator
        // defaults to using all properties.
        logger.debug("parentSchema===>{}", parentSchema);
        Set<String> selectors = getSelectorProperties(parentSchema);
        logger.debug("selectors===>{}", selectors);

        return new AbstractJsonValidator(schemaLocation, evaluationPath, this, schemaNode) {
        
            @Override
            public Set<ValidationMessage> validate(ExecutionContext executionContext, JsonNode node, JsonNode rootNode,
					JsonNodePath evaluationPath) {
            	logger.debug("current node={}, nodeType={}, asText={}, textValue={}", node, node.getNodeType(), node.asText(), node.textValue());

                // Only process if the node is an array, as selectors and unique items do not apply to other data
                // types.
                if (node.isArray()) {
                
                    // Create a property-value map of each items properties (selectors) and count the number of
                    // occurrences for each combination.
                    Map<Map<String, String>, Integer> uniqueItemCounts = Maps.newHashMap();
                    
                    node.forEach(instance -> {
                    
                        // Only process instances that are objects.
                        if (instance.isObject()) {
                            Map<String, String> uniqueKeys = Maps.newHashMap();

                            Iterator<Map.Entry<String, JsonNode>> fieldIterator = instance.fields();
                            while (fieldIterator.hasNext()) {
                                Map.Entry<String, JsonNode> entry = fieldIterator.next();
                                logger.debug("loop: entry=[{}], key=[{}], value=[{}]", entry, entry.getKey(), entry.getValue());
                                // If no selectors are configured, always add. Otherwise only add if the property is
                                // a selector.
                                if (selectors.isEmpty() || selectors.contains(entry.getKey())) {
                                    uniqueKeys.put(entry.getKey(), entry.getValue().asText());
                                }
                            }

                            // Iterate count and put in counts map.
                            int count = uniqueItemCounts.getOrDefault(uniqueKeys, 0) + 1;
                            uniqueItemCounts.put(uniqueKeys, count);
                        }
                    });

                    // Find first selector combination with too many instances.
                    Optional<Map<String, String>> uniqueKeysWithTooManyItems = uniqueItemCounts.entrySet()
                            .stream().filter(entry -> entry.getValue() > maxUniqueItems).map(Map.Entry::getKey)
                            .findFirst();
                    logger.debug("uniqueKeysWithTooManyItems===>{}", uniqueKeysWithTooManyItems);

                    // Return a failed validation if a selector combination has too many instances.
                    if (uniqueKeysWithTooManyItems.isPresent()) {
                        return fail(CustomErrorMessageType.of(KEYWORD), node,
                                Integer.toString(maxUniqueItems), uniqueKeysWithTooManyItems.get().toString());
                    }
                }

                return pass();
            }
           
		    private Set<ValidationMessage> fail(ErrorMessageType errorMessageType, JsonNode node, String maxItems, String details) {
		    	Map<String, String> errorMessages = new LinkedHashMap<>();
		    	errorMessages.put(errorMessageType.getErrorCode(), errorMessageType.getErrorCodeValue());
		    	
		    	MessageSourceValidationMessage.Builder builder = MessageSourceValidationMessage.builder((validationContext != null && validationContext.getConfig() != null)
                        ? validationContext.getConfig().getMessageSource()
                        : DefaultMessageSource.getInstance(), errorMessages, (message, failFast) -> {
		            if (failFast) {
		                throw new FailFastAssertionException(message);
		            }
		        }).code(errorMessageType.getErrorCode()).schemaLocation(schemaLocation)
		                .evaluationPath(evaluationPath).type(KEYWORD).instanceNode(node)
		                .messageKey(errorMessageType.getErrorCodeValue());
		    	
		    	return Collections
                        .singleton(builder.message(PATTERN)
                        .arguments(maxItems, details)
                        .instanceLocation(evaluationPath).instanceNode(schemaNode).build());
		    }
    
		    private Set<ValidationMessage> pass() {
		    	return Collections.emptySet();
		    }            
            
        };
    }

    private Set<String> getSelectorProperties(JsonSchema parentSchema) {
    	logger.debug("parentSchema.getSchemaNode()===>{}", parentSchema.getSchemaNode());
        if (parentSchema.getSchemaNode().has(SELECTORS) && parentSchema.getSchemaNode().get(SELECTORS).isArray()) {
            return Streams.stream(parentSchema.getSchemaNode().get(SELECTORS)).map(JsonNode::asText)
                    .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        }
        return Sets.newHashSet();
    }
    
}