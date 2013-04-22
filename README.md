Rhombus
===========================

An time-series object store for Cassandra that handles all the complexity of building wide row indexes.

to install:

(1) Clone the repository

	git clone git@github.com:Pardot/Rhombus.git

(2) install maven

	brew install maven

(3) Build and Run

To build:

	mvn package

To run:

	java -jar target/analytics-1.0-SNAPSHOT.jar server target/classes/service-dev.js


To run/debug in IntelliJ IDEA

(1) Open the cloned repository as a project.

(2) Create a run configuration

    Add a new configuration with the plus button of type Application
        Name: Service
        Main class: com.pardot.analyticsservice.AnalyticsService
        Program arguments: server target/classes/service-dev.js

(3) Run the server

	Select Service from the run configuration dropdown in the toolbar
	Run or debug


Configuring Cassandra

Analytics Service requires a connection to Cassandra 1.2 or greater.

After installing cassandra, you must modify cassandra.yml and set start_native_transport: true

Modify the hosts file of the machine running the analytics service and point cdev.localhost.com to the Cassandra server
