JSON Schema validation in Spring
=============

Example Spring project for my Blog post [Integrating JSON Schema validation in Spring using a custom HandlerMethodArgumentResolver][1].

[1]: https://www.mscharhag.com/spring/json-schema-validation-handlermethodargumentresolver

go to postman to initiate post request **[http://localhost:8080/paintings](http://localhost:8080/paintings)** with below content:

```code
{
  "name": "Mona Lisa",
  "artist": "Artist",
  "description": null,
  "dimension": {
    "height": 77,
    "width": 53
  },
  "tags": [
    "oil",
    "famous"
  ]
}
```