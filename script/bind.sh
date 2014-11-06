#/bin/bash!

cf restart cf-service-broker

cf purge-service-offering "mock service" -f
cf delete-service-broker mybroker -f

cf create-service-broker mybroker admin admin http://cf-service-broker.10.244.0.34.xip.io
cf enable-service-access "mock service"

cf create-service "mock service" large largeServiceInstance
cf bind-service cf-service-app largeServiceInstance

cf restart cf-service-app
