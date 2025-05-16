package com.shu.json.schema.examples.scf;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.networknt.schema.*;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shu.json.schema.examples.exception.JsonSchemaLoadingFailedException;
import com.shu.json.schema.examples.exception.JsonValidationFailedException;

public class ScfJsonValidationArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger logger = LoggerFactory.getLogger(ScfJsonValidationArgumentResolver.class);

    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourcePatternResolver;


    public ScfJsonValidationArgumentResolver(ObjectMapper objectMapper,
                                             ResourcePatternResolver resourcePatternResolver) {
        super();
        this.objectMapper = objectMapper;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        JsonValidation parameterAnnotation = parameter.getParameterAnnotation(JsonValidation.class);
        logger.debug("supportsParameter method parameterAnnotation=" + parameterAnnotation);
        return parameterAnnotation != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonValidation parameterAnnotation = parameter.getParameterAnnotation(JsonValidation.class);
        String schemaPath = parameterAnnotation.value();

        // get JsonSchema from schemaPath
        JsonSchema schema = getJsonSchema(schemaPath);
        logger.debug("JsonSchema is ==>"+ schema);

        // parse requestJson payload
        JsonNode requestJson = objectMapper.readTree(getJsonPayload(webRequest));
        logger.debug("The request to be validated is ==>"+ requestJson);

        // Do actual validation
        Set<ValidationMessage> validationResult = schema.validate(requestJson);

        if (validationResult.isEmpty()) {
            // No validation errors, convert JsonNode to method parameter type and return it
            return objectMapper.treeToValue(requestJson, parameter.getParameterType());
        }

        // throw exception if validation failed
        throw new JsonValidationFailedException(validationResult);
    }

    private String getJsonPayload(NativeWebRequest webRequest) throws IOException {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return StreamUtils.copyToString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * get JsonSchema from the passed meta schema file
     *
     * @param metaSchemaFile the path of the passed meta schema file
     * @return JsonSchema
     * @throws IOException
     */
    private JsonSchema getJsonSchema(String metaSchemaFile) throws IOException {
        logger.debug("The given json schema file ==>" + metaSchemaFile);
        Resource resource = resourcePatternResolver.getResource(metaSchemaFile);
        logger.debug("The given json schema file ==>" + resource.getFile().getAbsolutePath());

        // Standard JSON Schema 2020-12
        JsonMetaSchema standardMetaSchema = JsonMetaSchema.getV202012();

        // Customize own meta-schema based on the standard dialect 2020-12 version with custom keywords, custom formats
        JsonMetaSchema scfMetaSchema = JsonMetaSchema.builder(resource.getFile().getAbsolutePath(), standardMetaSchema)
                // Keywords that are informational only and do not require validation
                .keyword(new RulesKeyword("rules"))
                .format(new MatchNumberFormat(new BigDecimal("12345")))
                .formatKeywordFactory(CustomFormatKeyword::new)
                .build();
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(standardMetaSchema.getSpecification(),
                builder -> builder.metaSchema(scfMetaSchema));

        try (InputStream schemaStream = resource.getInputStream()) {
        	SchemaValidatorsConfig config = SchemaValidatorsConfig.builder().typeLoose(false).build();
        	JsonSchema schema = schemaFactory.getSchema(schemaStream, config);
            return schema;
        } catch (Exception e) {
            throw new JsonSchemaLoadingFailedException("An error occurred while loading JSON Schema, path: " + metaSchemaFile,
                    e);
        }
    }

}
