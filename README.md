# Currency Gateway - WIP

Implementation of gateway application providing currency data to different type of customers.

Architecture 

<img width="729" alt="Screenshot 2024-07-28 at 20 25 50" src="https://github.com/user-attachments/assets/a1c0762e-5f95-4c36-85ce-0a4ae83e577c">


Functional requirements

1. Collects current currency data provided by https://fixer.io/ to store in relational database. Data refresh should be configure to happen on predefined time interval.

2. Provides two public REST APIs for external services EXT_SERVICE_1 and EXT_SERVICE_2 running with Content-type application/json and application/xml respectively. The returned information contains data from fixer.io, with the structure of the response according to the Content-type maintained by the external service.
   
   Examples:

   JSON API: has 2 endpoints

         -  /json_api/current
              {
                "requestId": "b89577fe-8c37-4962-8af3-7cb89a245160",
                "timestamp": 1586335186721, // UTC
                "client": "1234",
                “currency”:”EUR”
              }
   
          Note: Processing this request requires a duplicate request check with the given id. In case on duplication the system returns an error.
                The client field identifies an end client.
                It normal execution of the request, the response contains the last data received in the system or the respective currency

         -  /json_api/history
            {
              "requestId": "b89577fe-8c37-4962-8af3-7cb89a24q909",
              "timestamp": 1586335186721,
              "client": "1234",
              “currency”:”EUR”,
              "period": 24
            }

          Note: This request requires a duplicate request check again. The response contains a list of the accumulated data for the respective currency during the set period.
                The time interval interprets as hours and is an integer.

   XML API: has 1 endpoint in one of the following formats

        /xml_api/command

         - for current data
            <command id="1234" >
              <get consumer="13617162" >
                <currency>EUR</currency>
              </get>
            </command>

        - statistics for a given period
           <command id="1234-8785" >
              <history consumer="13617162" currency=“EUR” period=”24” />
          </command>

        Note: The id attribute in the command tag uniquely identifies the request. The consumer attribute, interprets as end user id.
              A check for duplicate requests should also be implemented.

3. Collects unified statistical information (service name/id - EXT_SERVICE_Х, request id, time (UTC), end client id) in a relational database, regarding received requests from EXT_SERVICE_1 and EXT_SERVICE_2
   
4. Forwards through a web socket (RabbitMQ), the unified information about incoming requests. Exchange name is predefined in configuration. 

The implementation should consider that this is a heavily loaded system in terms of requests per time unit coming from EXT_SERVICE_X.
