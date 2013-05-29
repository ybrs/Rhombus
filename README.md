Rhombus
===========================

An time-series object store for Cassandra that handles all the complexity of building wide row indexes.

To use as a dependency:

(1) Add the proper repository to pom.xml (snapshots or releases)

    <repositories>
        <repository>
            <id>pardot-snapshot-repository</id>
            <url>scp://test.pardot.com/var/www/chef-repos/maven/snapshots</url>
        </repository>
    </repositories>

    OR

    <repositories>
        <repository>
            <id>pardot-release-repository</id>
            <url>scp://test.pardot.com/var/www/chef-repos/maven/releases</url>
        </repository>
    </repositories>

(2) Add the dependency

    <dependency>
        <groupId>com.pardot</groupId>
        <artifactId>rhombus</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    OR

    <dependency>
        <groupId>com.pardot</groupId>
        <artifactId>rhombus</artifactId>
        <version>1.0</version>
    </dependency>


(3) Configure server information in maven settings.xml (~/.m2/settings.xml) - may need to create.

    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          http://maven.apache.org/xsd/settings-1.0.0.xsd">
        <localRepository/>
        <interactiveMode/>
        <usePluginRegistry/>
        <offline/>
        <pluginGroups/>
        <servers>
            <server>
                <id>pardot-snapshot-repository</id>
                <username>xx</username>
                <password>XXXXXXXXXX</password>
                <filePermissions>664</filePermissions>
                <directoryPermissions>775</directoryPermissions>
            </server>
            <server>
                <id>pardot-release-repository</id>
                <username>xx</username>
                <password>XXXXXXXXXX</password>
                <filePermissions>664</filePermissions>
                <directoryPermissions>775</directoryPermissions>
            </server>
        </servers>
        <mirrors/>
        <proxies/>
        <profiles/>
        <activeProfiles/>
    </settings>

To deploy:

(1) Maven deploy to the releases or snapshots directory of the local pardot-maven-artifacts repository.  This is determined by the presence or absence of SNAPSHOT in the version number.

    mvn deploy
