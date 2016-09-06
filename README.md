# spring-pubsub
Spring Boot Google Pubsub performance test

This is a simple demo of trying google pubsub with different clients

## Running:

`mvn package` 

From either the `rpc` or `rest` module just run `java -jar target/{artifact}.jar`

Make sure you export `GOOGLE_CLOUD_JSON_CRED` variable with the full contents of the json credential
from your google project. The applications will look for that to bootstrap the clients (gRPC and REST)

## Testing

Both apps will expose an endpoint at `/pubsub/{topic}` if you post to that enpoint they will send messages
to Pubsub on the chosen `topic`.

The payload of the message should be:

```javascript

{
"size":128,
"messages":100,
"batchSize" : 1

}
```

The output is the time taken to send the messages


