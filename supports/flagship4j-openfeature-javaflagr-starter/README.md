# OpenFeature JavaFlagr Starter

## Getting Started

### Step 1: Install SDK

#### Using Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.linecorp.flagship4j</groupId>
        <artifactId>flagship4j-openfeature-javaflagr-starter</artifactId>
    </dependency>
</dependencies>
```

#### Using Gradle

```gradle
dependencies {
    implementation 'com.linecorp.flagship4j:flagship4j-openfeature-javaflagr-starter'
}
```

### Step 2: Create a OpenFlagr instance

```java
OpenFlagrConfig openFlagrConfig = OpenFlagrConfig.builder()
                .endpoint("http://localhost:18000")
                .callTimeoutSeconds(10)
                .connectionTimeoutSeconds(10)
                .readTimeoutSeconds(10)
                .writeTimeoutSeconds(10)
                .build();
OpenFlagr openFlagr = new DefaultOpenFlagr(openFlagrConfig);
```

### Step 3: Setup OpenFlagrProvider to OpenFeature instance

```java
OpenFeatureAPI api = OpenFeatureAPI.getInstance();
api.setProvider(new OpenFlagrProvider(openFlagr));
```
