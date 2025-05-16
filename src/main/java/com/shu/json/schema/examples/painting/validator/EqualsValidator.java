package com.shu.json.schema.examples.painting.validator;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.BaseJsonValidator;
import com.networknt.schema.ErrorMessageType;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.Keyword;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.ValidationContext;
import com.networknt.schema.ValidationMessage;

public class EqualsValidator extends BaseJsonValidator {
	private static final Logger logger = LoggerFactory.getLogger(EqualsValidator.class);

	private static ErrorMessageType ERROR_MESSAGE_TYPE = new ErrorMessageType() {
		@Override
		public String getErrorCode() {
			return "equals";
		}
	};

	private final String value;

	public EqualsValidator(SchemaLocation schemaLocation, JsonNodePath evaluationPath, JsonNode schemaNode,
			JsonSchema parentSchema, Keyword keyword, ValidationContext validationContext,
			boolean suppressSubSchemaRetrieval) {
		super(schemaLocation, evaluationPath, schemaNode, parentSchema, ERROR_MESSAGE_TYPE, keyword, validationContext,
				suppressSubSchemaRetrieval);
		this.value = schemaNode.textValue();
	}

	@Override
	public Set<ValidationMessage> validate(ExecutionContext executionContext, JsonNode node, JsonNode rootNode,
			JsonNodePath instanceLocation) {
		logger.debug("validate current node:[{}], path:[{}], root node:[{}]", node, instanceLocation, rootNode);
		if (!node.asText().equals(value)) {
			return Collections.singleton(message().message("{0}: must be equal to ''{1}''").arguments(value)
					.instanceLocation(instanceLocation).instanceNode(node).build());
		}
		return Collections.emptySet();
	}
}
