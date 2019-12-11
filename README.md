# elastic-indexer

**elastic-indexer** is a high-level Java API for indexing data into [Elastic](https://www.elastic.co/products/elasticsearch).
It is compatabile with all versions of Elasticsearch starting with version 5.2. This library is currently being used in a 
production environment where hundreds of billions of records are re-indexed several times each year into an Elastic cluster
with hundreds of nodes. In other words, this library is battle tested at scale.

When considering a library like **elastic-indexer**, the first question you may ask is why wouldn't you want to use the offical 
[Low Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low.html), or 
better yet the new [High Level Rest Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html).

With regards to the former, the answer is easy: Indexing data at scale involves many tasks that the low-level API simply does not provide
support for. This includes:
* Building Elastic documents for indexing that are sourced from large, hierarchical JSON structure which must be transformed, massaged
  or redacted in some way;
* Handling the inevitable 'Server too busy' conditions that occur environments with hundreds of nodes, and retrying index operations 
  with backoff so that clusters can recover; 
* Sending data to Elastic in an asynchronous manner so that your application can continue processing, while allowing callers to 
  register callbacks so that if indexing fails (despite retries), the application can schedule a retry at a future date.

With regards to the latter option, there are at least two things you should consider:
* As of this writing (late February, 2018) the High Level Client remains in beta;
* The High Level API only supports Elastic 6.1+ and does not strive to maintain backwards compatibility with previous versions of Elastic.

> from Elastic High-Level Client: "The 6.0 client is able to communicate with any 6.x Elasticsearch node, while the 6.1 client is 
> for sure able to communicate with 6.1, 6.2 and any later 6.x version, but *there may be incompatibility issues( when communicating 
> with a previous Elasticsearch node version, for instance between 6.1 and 6.0, in case the 6.1 client supports 
> new request body fields for some APIs that are not known by the 6.0 node(s)." 

**elastic-indexer** is compatible with every version of Elastic from version 5.2. As new features are added to our bulk indexer,
or search APIs, we strive to maintain backwards compatiblity with old versions of Elastic, since we recognize that enterprise
deployments of Elastic (like the ones this library operates in) often operate on much longer upgrade cycles than smaller installations
of Elastic.

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
    <version>7.2.0</version>
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
  testCompile 'com.arakelian:elastic-indexer:7.2.0'
}
```

## Licence

Apache Version 2.0
