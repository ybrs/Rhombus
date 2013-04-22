Rhombus
===========================

An time-series object store for Cassandra that handles all the complexity of building wide row indexes.

To use as a dependency:

(1) Add the proper repository to pom.xml (snapshots or releases)

    <repository>
        <id>pardot-snapshots</id>
        <url>ssh://git@github.com/pardot/pardot-maven-artifacts/raw/master/snapshots</url>
    </repository>

(2) Add the dependency

    <dependency>
        <groupId>com.pardot</groupId>
        <artifactId>rhombus</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>


To deploy:

(1) Maven deploy to the releases or snapshots directory of the local pardot-maven-artifacts repository

    (ex): mvn -DaltDeploymentRepository=snapshot-repo::default::file:../pardot-maven-artifacts/snapshots clean deploy

(2) Add and push the new deployment to github

    cd ../pardot-maven-artifacts
    git add .
    git commit -m "<message>"
    git push