# rpc-service

Due to technical issues in the company, the HTTP protocol that has been used to implement RPC has been having performance problems. Out of interest in possible future technical upgrades and personal interest in technology, a simple RPC framework was implemented.



### Rpc-service-core
The core code of rpc

### Rpc-service-support
Public dependencies, tool classes

### Rpc-test-api
Test package that can be used as a two-sided package for the caller to facilitate coding

### Rpc-test-consumer
Serving Consumers, Direct Start;
You can use postman to call the controller service to test the rcp provided by the provider

### Rpc-test-provider
Service provider, just start it directly

### START
start zookeeper:```./zkCli.sh -server```

start rpc-test-consumer

start rpc-test-privider

invoke controller in rpc-test-consumer by postman or other api test tool


### todo
build a simple config service can config like load balance way, serialization way ，maybe can add s simple function about search service and test it
