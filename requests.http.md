## Postman 

1.Correct request for  _"/paintings"_

```
POST http://localhost:8080/paintings
Content-Type: application/json
```

*body:*

```
{
  "name": "Mona Lisa",
  "artist": "Leonardo da Vinci",
  "description": "This is a well-known painting",
  "dimension": {
    "height": 77.0,
    "width": 53.0
  },
  "tags": [
    "oil",
    "famous"
  ]
}
```

2.Bad request for  _"/paintings"_

```
POST http://localhost:8080/paintings
Content-Type: application/json

{
  "name": "Mona Lisa",
  "artist": null,
  "description": null,
  "dimension": {
    "height": -77,
    "width": 53
  },
  "tags": [
    "oil",
    "famous"
  ]
}
```

3.Request for  __"/painting/product"__

> json schema for the property "product_id" of one product as following. it requires the value of "product_id" must be minimum 2 bytes and maximum 10 bytes

```
"products": {
	"type": "array",
	"items": {
		"additionalProperties": true,
		"required": [
			"product_id",
			"price"
		],
		"minItems": 1,
		"properties": {
			"product_id": {
				"type": "string",
				"maxUtf8ByteLength":10,
				"minUtf8ByteLength":2
			},
			"price": {
				"type": "number",
				"minimum": 0
			},
			"quantity": {
				"type": "integer",
				"equals": "2"
			}
		}
	}
}
```

One request sample:

```
POST http://localhost:8080/painting/product
Content-Type: application/json

{
	"order_id": "order1234",
	"event": "PLACED",
	"products": [
		{
			"product_id": "1",
			"quantity": 2,
            "price": 20.5
		},
        {
			"product_id": "product_2000",
			"quantity": 3,
            "price": 5
		}
	],
	"total-price": 41
}
```

and its response as below:

```
{
    "details": [
        "Value '1' must be greater than or equal 2 bytes in length.",
        "Value 'product_2000' must be less than or equal 10 bytes in length.",
        "$.products[1].quantity: must be equal to '2'"
    ],
    "message": "Json validation failed"
}
```


4.Request for  __"/scf/feecode"__ or  __"/scf/feecodeSpec"__

the first is using a proprietary json file, while the latter is using a spec compliant json schema file.

```
{
    "batch": {
        "batchRef": "BATCH25050101"
    },
    "items": [
        {
            "seq": 1,
            "feeType": "TEST-FEE",
            "feeCodeName": "SHUTESTFEE",
            "feeAmt": 10.99
        }
    ]
}
```

5.Request for  __"/myjson"__

Demostrate how to utilize environment variable to pass the path of the schema file to the controller class.

```
# environment variables
json.schema.location=myjsonValidation.json
```

```
POST http://localhost:8080/myjson
Content-Type: application/json

{
	"order_id": "order1234",
	"event": "PLACED",
	"products": [
		{
			"product_id": "1",
			"quantity": 2,
            "price": 20.5
		},
        {
			"product_id": "product_2000",
			"quantity": 3,
            "price": 5
		}
	],
	"total-price": 25.5
}
```