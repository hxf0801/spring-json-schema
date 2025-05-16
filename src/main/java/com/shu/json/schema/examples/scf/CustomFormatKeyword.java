package com.shu.json.schema.examples.scf;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.CustomErrorMessageType;
import com.networknt.schema.ErrorMessageType;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.Format;
import com.networknt.schema.FormatKeyword;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.ValidationContext;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.format.BaseFormatJsonValidator;

public class CustomFormatKeyword extends FormatKeyword {

	public CustomFormatKeyword(Map<String, Format> formats) {
		super(formats);
	}

	@Override
	public JsonValidator newValidator(SchemaLocation schemaLocation, JsonNodePath evaluationPath, JsonNode schemaNode,
			JsonSchema parentSchema, ValidationContext validationContext) {
		ErrorMessageType errorMessageType = CustomErrorMessageType.of("customFormatKeyword");
		return new BaseFormatJsonValidator(schemaLocation, evaluationPath, schemaNode, parentSchema, errorMessageType,
				this, validationContext) {

			@Override
			public Set<ValidationMessage> validate(ExecutionContext executionContext, JsonNode node, JsonNode rootNode,
					JsonNodePath instanceLocation) {
				return Collections.emptySet();
			}

		};
	}

}
