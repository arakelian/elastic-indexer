# elastic-indexer

High-speed indexing into Elastic.

## Background

**elastic-indexer** is a high-level Java API for indexing data into [Elastic](https://www.elastic.co/products/elasticsearch).
It is compatabile with all versions of Elasticsearch starting with version 5.2. This library is currently being used in a 
production environment where hundreds of billions of records are re-indexed several times per year into an Elastic cluster
with hundreds of nodes. In other words, it is battle tested at scale.

When considering a library like **elastic-indexer**, the first question you may ask me is why wouldn't you want to use the offical 
[Low Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low.html), or 
better yet,[High Level Rest Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html).

With regards to the former, the answer is easy: Indexing data at scale involves tasks that the low-level API does not provide
support for. This includes:
* Building the document that Elastic will index from a larger, hierarchical JSON structure that needs to be transformed in some way
* Handling the inevitable 'Server to busy' conditions that will occur in production environments with hundreds or thousands of nodes
* Sending data to Elastic in an asynchronous manner so that the application can continue processing, and registering callbacks so that
  when indexing failures occur the application can retry at some future date.

## Installation

The library is available on [Maven Central](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.arakelian%22%20AND%20a%3A%22elastic-indexer%22).

### Maven

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>http://repo.maven.apache.org/maven2</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>

...

<dependency>
    <groupId>com.arakelian</groupId>
    <artifactId>elastic-indexer</artifactId>
    <version>2.0.3</version>
    <scope>test</scope>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  testCompile 'com.arakelian:elastic-indexer:2.0.3'
}
```

## Licence

Apache Version 2.0
