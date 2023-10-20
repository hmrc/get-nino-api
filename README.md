
# Get NINO API

This is the code repository for the Get NINO API. This repository contains:

* code used to serve requests issued by approved other government departments via the HMRC API Platform.
* content used to serve the documentation related to the endpoints exposed via the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api).

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

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
