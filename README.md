#cf-service-broker

This is a stub Cloud Foundry Service Broker implementation to mamage a pseudo service. The purpose of this project is to understand the service concept of Cloud Foundry (CF) a little bit better while playing with this broker.

The broker implements the [CF Service Broker API](http://docs.cloudfoundry.org/services/api.html) and returns pseudo values.

##Preconditions

Have a running CF and CF tools installed. This project was developed using [Bosh Lite](https://github.com/cloudfoundry/bosh-lite) with recommended standard settings on a Mac computer.

    bosh vms
    Deployment `cf-warden'
    
    Director task 14
    
    Task 14 done
    
    +------------------------------------+---------+---------------+--------------+
    | Job/index                          | State   | Resource Pool | IPs          |
    +------------------------------------+---------+---------------+--------------+
    | unknown/unknown                    | running | small_errand  | 10.244.0.178 |
    | unknown/unknown                    | running | small_errand  | 10.244.0.182 |
    | api_z1/0                           | running | large_z1      | 10.244.0.138 |
    | etcd_z1/0                          | running | medium_z1     | 10.244.0.42  |
    | ha_proxy_z1/0                      | running | router_z1     | 10.244.0.34  |
    | hm9000_z1/0                        | running | medium_z1     | 10.244.0.142 |
    | loggregator_trafficcontroller_z1/0 | running | small_z1      | 10.244.0.150 |
    | loggregator_z1/0                   | running | medium_z1     | 10.244.0.146 |
    | login_z1/0                         | running | medium_z1     | 10.244.0.134 |
    | nats_z1/0                          | running | medium_z1     | 10.244.0.6   |
    | postgres_z1/0                      | running | medium_z1     | 10.244.0.30  |
    | router_z1/0                        | running | router_z1     | 10.244.0.22  |
    | runner_z1/0                        | running | runner_z1     | 10.244.0.26  |
    | uaa_z1/0                           | running | medium_z1     | 10.244.0.130 |
    +------------------------------------+---------+---------------+--------------+
    
    VMs total: 14
    
Java installed and a Maven build environment.

Set cf api to: `cf api --skip-ssl-validation https://api.10.244.0.34.xip.io` *)

##Run It

Clone repository, build project, initialize org and space for CF, deploy broker and service consumer application to CF, bind app to service, curl results.

    git clone https://github.com/sklevenz/cf-service-broker.git
    cd cf-service-broker
    
    mvn clean install
    
    ./script/init.sh
    ./script/deploy.sh
    ./script/bind.sh
    
    curl http://cf-service-app.10.244.0.34.xip.io/
    curl http://cf-service-broker.10.244.0.34.xip.io/
    
Instead of curl view both urls in a web browser. The app url should display the value of $VCAP_SERVICES environment variable. All values are pseudo values.

    {
      "mock service": [
        {
          "name": "largeServiceInstance",
          "label": "mock service",
          "tags": [
            "tag1",
            "tag2"
          ],
          "plan": "large",
          "credentials": {
            "uri": "http://cf-service-broker.10.244.0.34.xip.io:80/service-broker/",
            "username": "myuser",
            "password": "mypass",
            "host": "cf-service-broker.10.244.0.34.xip.io",
            "port": "80",
            "database": "dummy"
          }
        }
      ]
    }

The broker url displays an instance registry and a binding registry. These are values passed by CF to the broker while service instantiation and binding.

Service Instance: 

    {
      "7dc62dd8-97ba-4123-b787-3f96abe030df": {
        "service_id": "9af92c14-c7d6-4c8b-ab32-b0f0e95a5a7c",
        "plan_id": "4e23c75e-799c-4a99-ab0b-e921a92c4314",
        "organization_guid": "5e25953b-56a5-47c4-a78c-3a42447e2ca1",
        "space_guid": "0cf81fd3-0fcb-463d-aa0d-f8a77aa8b079"
      }
    }
    
Service Binding:

    {
      "0e7e651c-bc38-45bb-8735-64ca354d98e1": {
        "service_id": "9af92c14-c7d6-4c8b-ab32-b0f0e95a5a7c",
        "plan_id": "4e23c75e-799c-4a99-ab0b-e921a92c4314",
        "app_guid": "488c59b5-adbb-4fa5-8cb2-4850cdef60f1"
      }
    }
    
The following url displays the service catalog of the broker:

    http://cf-service-broker.10.244.0.34.xip.io/v2/catalog

##In Detail

Provided shell scripts are for convenience only. Try out CF commands manually and play around with this service broker.

###script/init.sh

Set CF api, authenticate and create org and space. 

    #/bin/bash!
    
    cf api --skip-ssl-validation https://api.10.244.0.34.xip.io
    cf auth admin admin
    cf create-org me
    cf target -o me
    cf create-space development
    cf target -s development
    
###script/deploy.sh

Deploy apps build by Maven first. Delete them if they exist already.

    #/bin/bash!
    
    cf delete cf-service-app -f
    cf delete cf-service-broker -f
    
    cf push cf-service-app -p ./app/target/service-app-1.0-SNAPSHOT.war 
    cf push cf-service-broker -p ./broker/target/service-broker-1.0-SNAPSHOT.war
    
###script/bind.sh

Cleanup from previous state. Create a service broker and enable service access. Create then a service instance and bind it to an application.

    #/bin/bash!
    
    cf restart cf-service-broker    
    cf purge-service-offering "mock service" -f
    cf delete-service-broker mybroker -f
    
    cf create-service-broker mybroker admin admin http://cf-service-broker.10.244.0.34.xip.io
    cf enable-service-access "mock service"
    
    cf create-service "mock service" large largeServiceInstance
    cf bind-service cf-service-app largeServiceInstance
    
    cf restart cf-service-app

## curl testing

service catalog

    curl http://cf-service-broker.10.244.0.34.xip.io/v2/catalog
    
create service instance

    curl http://cf-service-broker.10.244.0.34.xip.io/v2/service_instances/123 -X PUT -d '{ "service_id": "1", "plan_id": "2", "organization_guid": "3", "space_guid": "5" }'
    curl http://cf-service-broker.10.244.0.34.xip.io/v2/service_instances/123 -X DELETE -d '{ "service_id": "1", "plan_id": "2", "organization_guid": "3", "space_guid": "5" }'

create/delete service binding

    curl http://cf-service-broker.10.244.0.34.xip.io/v2/service_instances/123/service_bindings/abc -X PUT -d '{ "plan_id": "1", "service_id": "2", "app_guid": "3" }'
    curl http://cf-service-broker.10.244.0.34.xip.io/v2/service_instances/123/service_bindings/abc -X DELETE -d '{ "plan_id": "1", "service_id": "2", "app_guid": "3" }'
