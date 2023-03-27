# Server Sent Events
This application is a POC to implement SSE into a blocking (servlet) and a non-blocking (reactive) way.

## Start the application
To run the application into a Reactive mode:
```shell
gradle bootRun -PspringProfile=reactive 
```
and into a Servlet mode:
```shell
gradle bootRun -PsprignProfile=servlet
```

## Reactive mode
To create the stream session between an HTTP client and the Event producer in a Reactive mode we can use an HTTP client like curl to call the **authorities** endpoint chich deliver a Flux, and the *done* endpoint which deliver a **Mono**: 
```shell
curl -X GET http://localhost:8080/api/sse/reactive/authorities -R -u user:user
curl -X GET http://localhost:8080/api/sse/reactive/session -R -u user:user
```
or httpie:
```shell
http GET http://localhost:8080/api/sse/reactive/authorities -u user:user
http GET http://localhost:8080/api/sse/reactive/session -u user:user
```

## Servlet mode
To test the same thing with the Servlet mode the *remaining* endpoint can be called:
```shell
curl -X GET http://localhost:8080/api/sse/servlet/session -R -u user:user
```
or httpie:
```shell
http GET http://localhost:8080/api/sse/servlet/session -u user:user
```
