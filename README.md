# webclient-integrationtest
Example of end to end integration test with WebClient and MockWebServer

The WebClientMockWebServerIntegrationTest is the focus of this repo.

A better way of testing is using the @DynamicPropertySource to insert the MockWebServer url into the properties at runtime.  
This allows you to test the WebClient configuration as well.
If you look in the [commit history](https://github.com/tbatty7/webclient-integrationtest/commit/3725378af614a0b434c9e420ff724b43e628bd8c), you can see where I swapped the overriding of the WebClient with overriding the url property with @DynamicPropertySource.

