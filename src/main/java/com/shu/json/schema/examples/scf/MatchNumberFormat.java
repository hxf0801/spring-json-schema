package com.shu.json.schema.examples.scf;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.Format;
import com.networknt.schema.FormatValidator;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonType;
import com.networknt.schema.MessageSourceValidationMessage.Builder;
import com.networknt.schema.TypeFactory;
import com.networknt.schema.ValidationContext;
import com.networknt.schema.ValidationMessage;

public class MatchNumberFormat implements Format {
	private static final Logger logger = LoggerFactory.getLogger(MatchNumberFormat.class);
	
	private final BigDecimal compare;

	public MatchNumberFormat(BigDecimal compare) {
		this.compare = compare;
	}

	@Override
	public Set<ValidationMessage> validate(ExecutionContext executionContext, ValidationContext validationContext,
			JsonNode node, JsonNode rootNode, JsonNodePath instanceLocation, boolean assertionsEnabled,
			Supplier<Builder> message, FormatValidator formatValidator) {
		
		logger.debug("~~~ Location of=" +node);
		JsonType nodeType = TypeFactory.getValueNodeType(node, validationContext.getConfig());
		if (nodeType != JsonType.NUMBER && nodeType != JsonType.INTEGER) {
			return Collections.emptySet();
		}
		BigDecimal number = node.isBigDecimal() ? node.decimalValue() : BigDecimal.valueOf(node.doubleValue());
		number = new BigDecimal(number.toPlainString());
		logger.debug("~~~ value to be validated=" + number);
		logger.debug("~~~ compare=" + compare);
		
		 if (!(number.compareTo(compare) == 0)) {
             return Collections
                     .singleton(message.get()
                             .arguments(this.getName(), this.getMessageKey(), node.asText()).build());
         }
		 return Collections.emptySet();
	}

	@Override
	public String getName() {
		return "matchnumber";
	}

	@Override
	public String getMessageKey() {
		return Format.super.getMessageKey();
	}

}
