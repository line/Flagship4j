# Flagship4j

---

Flagship4j is a Java library that provides multiple API client SDKs to integrate with toggle systems (for now, we support [Openflagr](https://github.com/openflagr/flagr)) and follows the [Open-Feature](https://openfeature.dev/) specification.
There are different libraries for different use-cases, e.g. flagship4j-openfeature-javaflagr-starter for pure Java application, flagship4j-openfeature-spring-boot-starter for a Spring Boot application. 
 
The libraries are listed below:
## Libraries

| Name                                                                                                                                                                   | Description                                                                      |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| [flagship4j-javaflagr-core](/libs/flagship4j-javaflagr-core)                                           | Native OpenFlagr SDK for Java                                                    |
| [flagship4j-openfeature-provider-javaflagr](/libs/flagship4j-openfeature-provider-javaflagr)           | OpenFlagr Provider for OpenFeature                                               |
| [flagship4j-openfeature-javaflagr-starter](/supports/flagship4j-openfeature-javaflagr-starter)         | A convenient dependency descriptors for using OpenFeature SDK with OpenFlagr SDK |
| [flagship4j-javaflagr-spring-boot-web-starter](/supports/flagship4j-javaflagr-spring-boot-web-starter) | (Deprecated) OpenFlagr implementation for Spring Boot Web Starter                |
| [flagship4j-openfeature-spring-boot-starter](/supports/flagship4j-javaflagr-spring-boot-web-starter)   | Auto configuration with OpenFeature and OpenFlagr SDK for Spring Boot Starter    |

## Prerequisites

- Java 8 or later if you are a user.

## Getting started

There are many libraries we provide. Among them, we mostly recommend to use the flagship4j-openfeature-spring-boot-starter.
For other libraries and use cases, please refer to the [Examples](#Examples) and [Libraries](#Libraries)

### Use feature toggle via OpenFeature interface

#### maven
```xml
<dependency>
  <groupId>com.linecorp.flagship4j</groupId>
  <artifactId>flagship4j-openfeature-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

#### gradle
```groovy
dependencies {
    implementation "org.springframework.boot:spring-boot-starter"
    implementation "com.linecorp.flagship4j:flagship4j-openfeature-spring-boot-starter"
}
```

#### Config
please refer to [Configuration options](#Configuration-options)
```yaml
flagship4j:
  toggle:
    flagr:
      baseUrl: http://localhost:18000
      connectionTimeout: 30
      readTimeout: 30
      callTimeout: 30
      writeTimeout: 30
```

#### Code
```java

    private final Client client; // dependency injected by spring 
            
    ... other code
            
    ... someMethod(...) {
            Boolean isHelloWorldEnabled = client.getBooleanValue("hello-world-enabled", false);
            if (isHelloWorldEnabled) {
            System.out.println("Hello World");
        }   
    }
```

## Install to local Maven repository

- If you are developing flagship4j for your other application in local
- Perform following command to deploy to local maven repository (~/.m2/repository)

```sh
./gradlew clean build publishToMavenLocal
```

## Configuration options

| Name                | Description                                                                                            | Required | Default value |
| ------------------- | ------------------------------------------------------------------------------------------------------ | -------- | ------------- |
| `endpoint`          | The URL of the OpenFlagr evaluator.                                                                    | Yes      |               |
| `connectionTimeout` | A time period for establish a connection to the OpenFlagr host                                         | Yes      |               |
| `readTimeout`       | The maximum time of inactivity between two data packets when waiting for the OpenFlagr host response   | Yes      |               |
| `writeTimeout`      | The maximum time of inactivity between two data packets when sending the request to the OpenFlagr host | Yes      |               |
| `callTimeout`       | The time limit for a complete HTTP call to the OpenFlagr host                                          | Yes      |               |

## Examples

| Name                                                                                                                             | Description                                                     |
| -------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| [On / Off](/examples/openfeature-example)                      | An simple example of OpenFeature with OpenFlagr provider        |
| [A/B Testing](/examples/openfeature-abtest-example)            | An A/B testing example of OpenFeature with OpenFlagr provider   |
| [Canary Release](/examples/openfeature-canary-release-example) | A canary release example of OpenFeature with OpenFlagr provider |
| [White List](/examples/openfeature-white-list-example)         | A white list example of OpenFeature with OpenFlagr provider     |

## Contributing

If you believe you found a vulnerability or you have an issue related to security, please **DO NOT** open a public issue. Instead, send us an email at [dl_oss_dev@linecorp.com](mailto:dl_oss_dev@linecorp.com).

Before contributing to this project, please read the [CONTRIBUTING](/CONTRIBUTING.md).