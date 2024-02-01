# Get NINO API

This is the code repository for the Get NINO API. This repository contains:

* code used to serve requests issued by approved other government departments via the HMRC API Platform.
* content used to serve the documentation related to the endpoints exposed via the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api).

## Running tests
To compile and run all tests locally run `./run_all_tests.sh`

## Viewing Documentation
### Locally
- Run Get NINO API and other required services with the script:

    ```bash
     ./run_local_preview_documentation.sh
    ```

- Navigate to the preview page at http://localhost:9680/api-documentation/docs/openapi/preview
- Enter the full URL path of the OpenAPI specification file with the appropriate port and version:

    ```
     http://localhost:9750/api/conf/1.0/application.yaml
    ```
- Ensure to uncomment the lines [here](https://github.com/hmrc/get-nino-api/blob/main/conf/application.conf#L30-L33) in case of CORS errors

### On Developer Hub
Full documentation can be found on the [Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/get-nino-api).

## Local Testing
This API can be run in a local environment connected to dependent services,
with [NINO Automation Stub](https://github.com/hmrc/nino-automation-stub) stubbing the behaviour of DES.

The `./run_local_with_dependencies.sh` script uses Service Manager 2 to start up
the dependent services before running Get NINO API on port `9750`.

Make a call to the Auth endpoint to obtain a bearer token:
* `POST http://localhost:8500/auth/sessions`
* set `Content-Type` header to `application/json`
* set request body to [Auth Request Body Json](public/jsons/authRequestBody.json)

or via a curl request replacing the content `PUT JSON BODY HERE` with [Auth Request Body Json](public/jsons/authRequestBody.json):
```
curl --location --request POST 'http://localhost:8500/auth/sessions' \
--header 'Content-Type: application/json' \
--data 'PUT JSON BODY HERE'
```

A `201 Created` response is returned with some response headers. Copy the value of the response header `Authorization`.
This is the bearer token to use when calling the API endpoint.

Make a call to the Get NINO API endpoint:
* `POST http://localhost:9750/process-nino`
* set `Authorization` header to the bearer token obtained from the Auth endpoint call
* set `Accept` header to `application/vnd.hmrc.1.0+json`
* set `Content-Type` header to `application/json`
* set `CorrelationId` header to `c75f40a6-a3df-4429-a697-471eeec46435`
* set `OriginatorId` header to `DA2_DWP_REG`
* set request body to [Register NINO Request Body Json](public/jsons/registerNinoRequestBody.json)

or via a curl request replacing the contents `PUT JSON BODY HERE` with [Register NINO Request Body Json](public/jsons/registerNinoRequestBody.json)
and `PUT BEARER TOKEN HERE` with bearer token obtained from the Auth endpoint call which MUST start with the prefix `Bearer`:
```
curl --location --request POST 'http://localhost:9750/process-nino' \
--header 'Content-Type: application/json' \
--header 'Accept: application/vnd.hmrc.1.0+json' \
--header 'CorrelationId: c75f40a6-a3df-4429-a697-471eeec46435' \
--header 'Authorization: PUT BEARER TOKEN HERE' \
--header 'OriginatorId: DA2_DWP_REG' \
--data 'PUT JSON BODY HERE'
```

This will return a `503 Service Unavailable` response because no test data has been set up.

To set up test data, see the [NINO Automation Stub README](https://github.com/hmrc/nino-automation-stub/blob/main/README.md#local-testing).

Once test data has been set up, call the API endpoint again. This should return a `202 Accepted` response indicating a successful NINO registration.

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

