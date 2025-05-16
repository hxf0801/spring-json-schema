package com.shu.json.schema.examples.scf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.networknt.schema.AnnotationKeyword;
import com.networknt.schema.InputFormat;
import com.networknt.schema.ItemsValidator202012;
import com.networknt.schema.JsonMetaSchema;
import com.networknt.schema.JsonNodePath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.NonValidationKeyword;
import com.networknt.schema.OutputFormat;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.ValidationResult;
import com.networknt.schema.ValidatorTypeCode;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.walk.JsonSchemaWalkListener;
import com.networknt.schema.walk.WalkEvent;
import com.networknt.schema.walk.WalkFlow;

public class MyJsonSchemaWalkListernerTest {
	
    private final class MyJsonSchemaWalkListerner implements JsonSchemaWalkListener {
		@Override
		public WalkFlow onWalkStart(WalkEvent walkEvent) {
		    @SuppressWarnings("unchecked")
		    List<WalkEvent> items = (List<WalkEvent>) walkEvent.getExecutionContext()
		            .getCollectorContext()
		            .getCollectorMap()
		            .computeIfAbsent("items", key -> new ArrayList<>());
		    items.add(walkEvent);
		    items.stream().forEach(System.out::println);
		    return WalkFlow.CONTINUE;
		}

		@Override
		public void onWalkEnd(WalkEvent walkEvent, Set<ValidationMessage> validationMessages) {
		}
	}

//	@Test
//    void items202012Listener() {
//        String schemaData = "{\r\n"
//                + "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\r\n"
//                + "  \"type\": \"object\",\r\n"
//                + "  \"properties\": {\r\n"
//                + "    \"tags\": {\r\n"
//                + "      \"type\": \"array\",\r\n"
//                + "      \"items\": {\r\n"
//                + "        \"$ref\": \"#/definitions/tag\"\r\n"
//                + "      }\r\n"
//                + "    }\r\n"
//                + "  },\r\n"
//                + "  \"definitions\": {\r\n"
//                + "    \"tag\": {\r\n"
//                + "      \"properties\": {\r\n"
//                + "        \"name\": {\r\n"
//                + "          \"type\": \"string\"\r\n"
//                + "        },\r\n"
//                + "        \"description\": {\r\n"
//                + "          \"type\": \"string\"\r\n"
//                + "        }\r\n"
//                + "      }\r\n"
//                + "    }\r\n"
//                + "  }\r\n"
//                + "}";
//
//        JsonSchemaWalkListener itemWalkListener = new MyJsonSchemaWalkListerner();
//		SchemaValidatorsConfig config = SchemaValidatorsConfig.builder().itemWalkListener(itemWalkListener).build();
//		
//        JsonSchema schema = JsonSchemaFactory.getInstance(VersionFlag.V202012).getSchema(schemaData, config);
//        
//        String inputData = "{\r\n"
//                + "  \"tags\": [\r\n"
//                + "    {\r\n"
//                + "      \"name\": \"image\",\r\n"
//                + "      \"description\": \"An image\"\r\n"
//                + "    },\r\n"
//                + "    {\r\n"
//                + "      \"name\": \"link\",\r\n"
//                + "      \"description\": \"A link\"\r\n"
//                + "    }\r\n"
//                + "  ]\r\n"
//                + "}";
//        ValidationResult result = schema.walk(inputData, InputFormat.JSON, true);
//        assertTrue(result.getValidationMessages().isEmpty());
//
//        @SuppressWarnings("unchecked")
//        List<WalkEvent> items = (List<WalkEvent>) result.getExecutionContext().getCollectorContext().get("items");
//        assertEquals(2, items.size());
//        assertEquals("items", items.get(0).getValidator().getKeyword());
//	    assertInstanceOf(ItemsValidator202012.class, items.get(0).getValidator());
//
//        assertEquals("/tags/0", items.get(0).getInstanceLocation().toString());
//        assertEquals("/properties/tags/items", items.get(0).getSchema().getEvaluationPath().toString());
//
//        assertEquals("/tags/1", items.get(1).getInstanceLocation().toString());
//        assertEquals("/properties/tags/items", items.get(1).getSchema().getEvaluationPath().toString());
//    }
    
    @Test
    void itemListenerDraft202012() {
//        String schemaData = "        {\r\n"
//                + "          \"type\": \"object\",\r\n"
//                + "          \"properties\": {\r\n"
//                + "            \"name\": {\r\n"
//                + "              \"type\": \"string\"\r\n"
//                + "            },\r\n"
//                + "            \"children\": {\r\n"
//                + "              \"type\": \"array\",\r\n"
//                + "              \"items\": {\r\n"
//                + "                \"type\": \"object\",\r\n"
//                + "                \"properties\": {\r\n"
//                + "                  \"name\": {\r\n"
//                + "                    \"type\": \"string\"\r\n"
//                + "                  }\r\n"
//                + "                }\r\n"
//                + "              }\r\n"
//                + "            }\r\n"
//                + "          }\r\n"
//                + "        }";
        String schemaData = "{\r\n"
                + "  \"rules\": {\r\n"
                + "    \"feeType\": {\r\n"
                + "      \"ruleInfos\": [\r\n"
                + "        {\r\n"
                + "          \"ruleName\": \"Required\",\r\n"
                + "          \"errorArgs\": [\"label.feeCode.feeType\"],\r\n"
                + "          \"errorMsg\": \"validation.required\"\r\n"
                + "        }\r\n"
                + "      ]\r\n"
                + "    }\r\n"
                + "  }\r\n"
                + "}";
        JsonSchemaWalkListener listener = new JsonSchemaWalkListener() {
            @Override
            public WalkFlow onWalkStart(WalkEvent walkEvent) {
                return WalkFlow.CONTINUE;
            }

            @Override
            public void onWalkEnd(WalkEvent walkEvent, Set<ValidationMessage> validationMessages) {
                @SuppressWarnings("unchecked")
                List<WalkEvent> items = (List<WalkEvent>) walkEvent.getExecutionContext()
                        .getCollectorContext()
                        .getCollectorMap()
                        .computeIfAbsent("items", key -> new ArrayList<JsonNodePath>());
                items.add(walkEvent);
                items.stream().forEach(System.out::println);
            }
        };
        SchemaValidatorsConfig config = SchemaValidatorsConfig.builder()
                .itemWalkListener(listener)
//                .propertyWalkListener(listener)
//                .keywordWalkListener(listener)
                .build();
        
        List<ValidatorTypeCode> keywords = ValidatorTypeCode.getKeywords(SpecVersion.VersionFlag.V202012);
        keywords.stream().forEach(System.out::println);
        
        JsonMetaSchema standardMetaSchema = JsonMetaSchema.getV202012();
        JsonMetaSchema scfMetaSchema = JsonMetaSchema.builder(standardMetaSchema)
                // Keywords that are informational only and do not require validation
                .keyword(new AnnotationKeyword("rules"))
                .build();
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(
        		standardMetaSchema.getSpecification(),
                builder -> builder.metaSchema(scfMetaSchema));
        JsonSchema schema = schemaFactory.getSchema(schemaData, config);
//        ValidationResult result = schema.walk(null, true);
        ValidationResult result = schema.validate(null, OutputFormat.RESULT);
        @SuppressWarnings("unchecked")
        List<WalkEvent> items = (List<WalkEvent>) result.getExecutionContext().getCollectorContext().get("items");
        assertEquals(4, items.size());
        assertEquals("/name", items.get(0).getInstanceLocation().toString());
        assertEquals("properties", items.get(0).getKeyword());
        assertEquals("#/properties/name", items.get(0).getSchema().getSchemaLocation().toString());
        assertEquals("/children/0/name", items.get(1).getInstanceLocation().toString());
        assertEquals("properties", items.get(1).getKeyword());
        assertEquals("#/properties/children/items/properties/name", items.get(1).getSchema().getSchemaLocation().toString());
        assertEquals("/children/0", items.get(2).getInstanceLocation().toString());
        assertEquals("items", items.get(2).getKeyword());
        assertEquals("#/properties/children/items", items.get(2).getSchema().getSchemaLocation().toString());
        assertEquals("/children", items.get(3).getInstanceLocation().toString());
        assertEquals("properties", items.get(3).getKeyword());
        assertEquals("#/properties/children", items.get(3).getSchema().getSchemaLocation().toString());
    }
//
//	@Test
//	void keywordListener() {
//	    String schemaData = "{\r\n"
//	            + "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\r\n"
//	            + "  \"type\": \"object\",\r\n"
//	            + "  \"description\": \"Default Description\",\r\n"
//	            + "  \"properties\": {\r\n"
//	            + "    \"tags\": {\r\n"
//	            + "      \"type\": \"array\",\r\n"
//	            + "      \"items\": {\r\n"
//	            + "        \"$ref\": \"#/definitions/tag\"\r\n"
//	            + "      }\r\n"
//	            + "    }\r\n"
//	            + "  },\r\n"
//	            + "  \"definitions\": {\r\n"
//	            + "    \"tag\": {\r\n"
//	            + "      \"properties\": {\r\n"
//	            + "        \"name\": {\r\n"
//	            + "          \"type\": \"string\"\r\n"
//	            + "        },\r\n"
//	            + "        \"description\": {\r\n"
//	            + "          \"type\": \"string\"\r\n"
//	            + "        }\r\n"
//	            + "      }\r\n"
//	            + "    }\r\n"
//	            + "  }\r\n"
//	            + "}";
//	
//	    SchemaValidatorsConfig config = SchemaValidatorsConfig.builder()
//	            .keywordWalkListener(ValidatorTypeCode.PROPERTIES.getValue(), new JsonSchemaWalkListener() {
//	                @Override
//	                public WalkFlow onWalkStart(WalkEvent walkEvent) {
//	                    @SuppressWarnings("unchecked")
//	                    List<WalkEvent> propertyKeywords = (List<WalkEvent>) walkEvent.getExecutionContext()
//	                            .getCollectorContext()
//	                            .getCollectorMap()
//	                            .computeIfAbsent("propertyKeywords", key -> new ArrayList<>());
//	                    propertyKeywords.add(walkEvent);
//	                    return WalkFlow.CONTINUE;
//	                }
//	
//	                @Override
//	                public void onWalkEnd(WalkEvent walkEvent, Set<ValidationMessage> validationMessages) {
//	                }
//	            })
//	            .build();
//	    JsonSchema schema = JsonSchemaFactory.getInstance(VersionFlag.V7).getSchema(schemaData, config);
//	    String inputData = "{\r\n"
//	            + "  \"tags\": [\r\n"
//	            + "    {\r\n"
//	            + "      \"name\": \"image\",\r\n"
//	            + "      \"description\": \"An image\"\r\n"
//	            + "    },\r\n"
//	            + "    {\r\n"
//	            + "      \"name\": \"link\",\r\n"
//	            + "      \"description\": \"A link\"\r\n"
//	            + "    }\r\n"
//	            + "  ]\r\n"
//	            + "}";
//	    ValidationResult result = schema.walk(inputData, InputFormat.JSON, true);
//	    assertTrue(result.getValidationMessages().isEmpty());
//	    @SuppressWarnings("unchecked")
//	    List<WalkEvent> propertyKeywords = (List<WalkEvent>) result.getExecutionContext().getCollectorContext().get("propertyKeywords"); 
//	    assertEquals(3, propertyKeywords.size());
//	    assertEquals("properties", propertyKeywords.get(0).getValidator().getKeyword());
//	    assertEquals("", propertyKeywords.get(0).getInstanceLocation().toString());
//	    assertEquals("/properties", propertyKeywords.get(0).getSchema().getEvaluationPath()
//	            .append(propertyKeywords.get(0).getKeyword()).toString());
//	    assertEquals("/tags/0", propertyKeywords.get(1).getInstanceLocation().toString());
//	    assertEquals("image", propertyKeywords.get(1).getInstanceNode().get("name").asText());
//	    assertEquals("/properties/tags/items/$ref/properties",
//	            propertyKeywords.get(1).getValidator().getEvaluationPath().toString());
//	    assertEquals("/properties/tags/items/$ref/properties", propertyKeywords.get(1).getSchema().getEvaluationPath()
//	            .append(propertyKeywords.get(1).getKeyword()).toString());
//	    assertEquals("/tags/1", propertyKeywords.get(2).getInstanceLocation().toString());
//	    assertEquals("/properties/tags/items/$ref/properties", propertyKeywords.get(2).getSchema().getEvaluationPath()
//	            .append(propertyKeywords.get(2).getKeyword()).toString());
//	    assertEquals("link", propertyKeywords.get(2).getInstanceNode().get("name").asText());
//	}
}
