# RxServiceGenerator
The generator can create services based on your Retrofit Interfaces.

The generator contains caching strategies {@link StrategyCache}: 

- FULL_CACHE
- ONLY_OFFLINE_CACHE
- ONLY_SHORT_LIVING_CACHE
- NO_CACHE.

The service can add and modify the request mash according to your custom Interceptor. And also add a request authentication header

## Usage

**Gradle**

- **Project level `build.gradle`**
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
- **App level `build.gradle`**
```gradle
dependencies {
    implementation 'com.github.returnt:rx-service-generator:1.0.1'
}
```

**Maven**

```xml
<!-- <repositories> section of pom.xml -->
<repository>
    <id>jitpack.io</id>
   <url>https://jitpack.io</url>
</repository>

<!-- <dependencies> section of pom.xml -->
<dependency>
    <groupId>com.github.returnt</groupId>
    <artifactId>rx-service-generator</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Example Code 

`MyServiceGenerator` - To begin, extends from ServiceGenerator, and use all methods

```java
public final class RxServiceGenerator extends ServiceGenerator {

    private static final String HEADER_KEY_LANGUAGE = "Language";
    private static final String HEADER_KEY_REGION = "Region";

    private Application mApplication;

    @Override
    public Application appApplication() {
        return mApplication;
    }

    public void setApplication(Application application) {
        mApplication = application;
    }

    @Override
    public String authorizationToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    }

    @Override
    public String baseHost() {
        return "https://api.example.com";
    }

    @Override
    public File cacheDir() {
        return mApplication.getCacheDir();
    }

    @Override
    public boolean isConnectNetwork() {
        return ConnectionUtils.hasConnection((ConnectivityManager) Objects.requireNonNull(mRatesApplication.getSystemService(Context.CONNECTIVITY_SERVICE)));
    }

    @Override
    public boolean buildConfig() {
        return false;
    }

    @Override
    public List<Interceptor> getInterceptors() {
        return Collections.singletonList(chain -> {
            Request newRequest = chain.request().newBuilder()
                .addHeader(HEADER_KEY_LANGUAGE, "ua")
                .addHeader(HEADER_KEY_REGION, "ua")
                .build();
            return chain.proceed(newRequest);
        });
    }

    @Override
    @NonNull
    public Converter.Factory getConverterFactory() {
        return JacksonConverterFactory.create();
    }
}
```
