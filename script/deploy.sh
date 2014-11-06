#/bin/bash!

cf delete cf-service-app -f
cf delete cf-service-broker -f

cf push cf-service-app -p ./app/target/service-app-1.0-SNAPSHOT.war 
cf push cf-service-broker -p ./broker/target/service-broker-1.0-SNAPSHOT.war

