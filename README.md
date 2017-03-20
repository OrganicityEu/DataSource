#  Datasource Service

The Datasource Service provides historical measurements for OrganiCity assets, providing the appropriate API for accessing them throught the OrganiCity UDO. 

It can also be used as a quick-start solution for integrating with legacy Data Stores available in OrganiCity Sites and Experiments to provide historical data in an appropriate format.

The API of the Datasource service follows the standard used for the OC Data Service Proxy available [here](https://organicityeu.github.io/api/DataSource.html).

## Prerequisites

Given the version of the Datasource Service they choose to use they may need to configure an external data store to persist the data. The currently available options are: 

1. A server with a public IP and access to the 443 (HTTPS) port.
1. Java 1.8+ installed on your server.
1. `No-Database` using the `service-hsql` version of the Datasource that keeps historical data in memory. This has the drawback that every time the service is restared all stored data are lost.
1. `InfluxDB` using the `service-influx` version of the Datasource that keeps historical data in an instance of InfluxDB. For more information check out [InfluxDB](https://www.influxdata.com/).

## Installing and Running the Datasource Service

To run the Datasource Service on you machine you need to clone this repository and run `mvn clean install` on the base directoy.

Once Maven process is completed, you can run the `datasource-service-1.0-????-SNAPSHOT.jar` jar file from the module you want to use. All jar files created are directly executables and can also be used as `init` script on linux machines to start the service on boot.

## Pointing to your Datasource Service

For OrganiCity to access your asset's historical data you need to provide a `datasource` attribute in each one of them. This datasource attribute is structured as following :

    "datasource": {
        "type": "urn:oc:attributeType:datasource"
        "value": "https://datasource.mysite.eu/v1/entities/"
    }

## Usage

### Usage for OC Sites

To use the Datasource Service for OC Sites, site managers need to deploy the service on one of their own servers and make sure they have HTTPS access to it.

The next step is to create a Subscription from their Local Orion Instance to the Datasource Service, pointing to the `/v1/notifyContext` endpoint, and wait for their assets to populate the internal data store.


### Usage for OC Experiments

Currently there is no way to subscribe to data of OrganiCity through the Experimenter's Site, but to use the Datasource Service in your experiment you simply need to post the same OrganiCity assets you post to the Experimenter's Site to the Datasource Service as well, in the `ContextController` and the [`notifyContext`](https://github.com/amaxilat/DataSource/blob/master/service-hsql/src/main/java/eu/organicity/data/controller/ContextController.java#L29) API call.

## Extending the Datasource Service to use your own data storage

If you want or need to extend the Datasource Service to use an existing backend to retrieve historical data for your assets you need to do the following: 

1. Create a new module (i.e., called `service-legacy`) by copying one of the existing ones that fits best to your case. The `service-hsql` could be the best option in most cases.
1. Remove the `ContextController.java` file if your service needs to get no notifications about data updates as the data are stored in the database through another mechanism.
1. Adapt the `HistoryController.java` to access and retrieve data from your own storage and transform them appropriately to the format used by OrganiCity.

if you think that your version of the Datasource Service could be used by others feel free to provide us with a pull request.
