package com.shu.json.schema.examples.painting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonMetaSchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.shu.json.schema.examples.exception.JsonSchemaLoadingFailedException;
import com.shu.json.schema.examples.exception.JsonValidationFailedException;
import com.shu.json.schema.examples.painting.validator.EqualsKeyword;
import com.shu.json.schema.examples.painting.validator.MaxUniqueItemsKeyword;
import com.shu.json.schema.examples.painting.validator.MaxUtf8ByteLengthKeyword;
import com.shu.json.schema.examples.painting.validator.MinUtf8ByteLengthKeyword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JsonSchemaValidatingArgumentResolver implements HandlerMethodArgumentResolver {
	private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidatingArgumentResolver.class);

	private final ObjectMapper objectMapper;
	private final ResourcePatternResolver resourcePatternResolver;
	private final Map<String, JsonSchema> schemaCache;

	public JsonSchemaValidatingArgumentResolver(ObjectMapper objectMapper,
			ResourcePatternResolver resourcePatternResolver) {
		this.objectMapper = objectMapper;
		this.resourcePatternResolver = resourcePatternResolver;
		this.schemaCache = new ConcurrentHashMap<>();
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		ValidJson parameterAnnotation = methodParameter.getParameterAnnotation(ValidJson.class);
		logger.debug("supportsParameter method parameterAnnotation="+parameterAnnotation);
		return parameterAnnotation != null;
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		// get schema path from ValidJson annotation
		ValidJson parameterAnnotation = methodParameter.getParameterAnnotation(ValidJson.class);
		logger.debug("resolveArgument method parameterAnnotation="+parameterAnnotation);
		String schemaPath = parameterAnnotation.value();
		
		logger.debug("Before resolving, Internal schemaCache ==> " + this.schemaCache);

		// get JsonSchema from schemaPath
		JsonSchema schema = getJsonSchema(schemaPath);
		
		logger.debug("After resolving, Internal schemaCache ==> " + this.schemaCache);

		// parse json payload
		JsonNode json = objectMapper.readTree(getJsonPayload(nativeWebRequest));

		// Do actual validation
		Set<ValidationMessage> validationResult = schema.validate(json);

		if (validationResult.isEmpty()) {
			// No validation errors, convert JsonNode to method parameter type and return it
			return objectMapper.treeToValue(json, methodParameter.getParameterType());
		}

		// throw exception if validation failed
		throw new JsonValidationFailedException(validationResult);
	}

	private String getJsonPayload(NativeWebRequest nativeWebRequest) throws IOException {
		HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
		return StreamUtils.copyToString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8);
	}

	private JsonSchema getJsonSchema(String schemaPath) {
		logger.debug("schemaPath ==>" + schemaPath);
		return schemaCache.computeIfAbsent(schemaPath, path -> {
			Resource resource = resourcePatternResolver.getResource(path);
			if (!resource.exists()) {
				throw new JsonSchemaLoadingFailedException("Schema file does not exist, path: " + path);
			}

			// Register custom keyword validation classes
			JsonMetaSchema metaSchema = JsonMetaSchema.builder(JsonMetaSchema.getV202012())
			        .keyword(new EqualsKeyword())
			        .keyword(new MaxUniqueItemsKeyword())
			        .keyword(new MinUtf8ByteLengthKeyword())
			        .keyword(new MaxUtf8ByteLengthKeyword())
			        .build();
			JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012,
					builder -> builder.metaSchema(metaSchema));
			
			try (InputStream schemaStream = resource.getInputStream()) {
				return schemaFactory.getSchema(schemaStream);
			} catch (Exception e) {
				throw new JsonSchemaLoadingFailedException("An error occurred while loading JSON Schema, path: " + path,
						e);
			}
		});
	}
}
