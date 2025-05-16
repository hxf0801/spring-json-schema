1. schme file is not complaint to json schema spec. In order to leverage json schema api, we need to change our schema file to follow the spec:

> add "$schema" keyword and "properties" keyward
			
			{ "$schema": "https://json-schema.org/draft/2020-12/schema" }


			"properties" keyword to list what fields are expected and how they are organized
				and use "type" keyword to describe its data type 
				


## todo
1.custom error message with possible arguments, how to integrate with java i18L  
2.walk semantics, how to implement our own walker for our json schema  