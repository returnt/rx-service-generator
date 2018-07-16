/**
 * File ServiceGenerator
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 07.11.2017 13:56
 */

package returnt.ru.rxservicegenerator.service;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.gson.GsonBuilder;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import returnt.ru.rxservicegenerator.utils.CacheUtils;
import returnt.ru.rxservicegenerator.utils.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Class ServiceGenerator
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @package returnt.ru.rxservicegenerator.service
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 07.11.2017 13:56
 */
public abstract class ServiceGenerator implements IServiceGenerator {

    private static final String TAG = "ServiceGenerator";

    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String CACHE_CONTROL_NO_CACHE = "Cache-Control: no-cache";
    private static final int MEMORY_CACHE_LENGTH_MINS = 2;
    private static final int DISC_CACHE_LENGTH_DAYS = 7;
    private static final int TIMEOUT = 20;

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> S createRxService(Class<S> serviceClass) {
        return createRxService(getConverterFactory(), serviceClass, StrategyCache.FULL_CACHE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> S createRxService(Class<S> serviceClass, StrategyCache strategyCache) {
        return createRxService(getConverterFactory(), serviceClass, strategyCache);
    }

    /**
     * Creates service for specifies base URL
     *
     * @param <S>
     * @param serviceClass
     * @param strategyCache One of {@link StrategyCache}.
     *
     * @return generated service
     */
    <S> S createRxService(Converter.Factory factory, Class<S> serviceClass, StrategyCache strategyCache) {
        setRxErrorHandlerGlobal();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (buildConfig()) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            // Can be Level.BASIC, Level.HEADERS, or Level.BODY
            // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        } else {
            setCacheStrategy(builder, strategyCache);
        }

        builder.addInterceptor(provideAccessTokenInterceptor());
        builder.addInterceptor(errorHandlerInterceptor());

        if (!TextUtils.isEmpty(getInterceptors()))
            builder.interceptors().addAll(getInterceptors());

        setConnectionTimeouts(builder);

        OkHttpClient okHttpClient = builder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(baseHost())
            .addConverterFactory(factory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(okHttpClient);

        return retrofitBuilder.build().create(serviceClass);
    }

    /**
     * setRxErrorHandlerGlobal
     */
    void setRxErrorHandlerGlobal() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, "RxJavaPlugins setErrorHandler: ", throwable);
            }
        });
    }

    /**
     * set Connection Timeouts
     *
     * @param builder {@link OkHttpClient.Builder}
     */
    void setConnectionTimeouts(OkHttpClient.Builder builder) {
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * set Cache Strategy
     *
     * @param builder
     * @param strategyCache One of {@link StrategyCache}.
     */
    private void setCacheStrategy(OkHttpClient.Builder builder, StrategyCache strategyCache) {
        File cacheDir = cacheDir();
        if (cacheDir != null) {
            switch (strategyCache) {
                case FULL_CACHE:
                    builder.addNetworkInterceptor(provideCacheInterceptor());
                    builder.addInterceptor(provideOfflineCacheInterceptor());
                    builder.cache(CacheUtils.provideCache(cacheDir()));
                    break;
                case ONLY_OFFLINE_CACHE:
                    builder.addNetworkInterceptor(provideDumbCacheInterceptor());
                    builder.addInterceptor(provideOfflineCacheInterceptor());
                    builder.cache(CacheUtils.provideCache(cacheDir()));
                    break;
                case ONLY_SHORT_LIVING_CACHE:
                    builder.addNetworkInterceptor(provideCacheInterceptor());
                    builder.cache(CacheUtils.provideCache(cacheDir()));
                    break;
                default:
            }
        } else {
            Log.e(TAG, "setCacheStrategy: cache Dir null");
        }
    }

    /**
     * handling errors from response and replace default message from Throwable
     *
     * @return {@link Interceptor}
     */
    private Interceptor errorHandlerInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                ResponseBody responseBody = response.body();

                if (400 <= response.code() && response.code() < 500 && responseBody != null) {
                    response = response.newBuilder()
                        .message("||".concat(TextUtils.charStreamToString(responseBody.charStream())))
                        .build();
                }
                return response;
            }
        };
    }

    /**
     * Creates memory cache interceptor
     *
     * @return {@link Interceptor}
     */
    private Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(MEMORY_CACHE_LENGTH_MINS, TimeUnit.MINUTES)
                    .build();

                return response.newBuilder()
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
            }
        };
    }

    /**
     * Do not use cache, just adds cache headers that are needed offline cache to work
     *
     * @return {@link Interceptor}
     */
    private Interceptor provideDumbCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                    .build();

                return response.newBuilder()
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
            }
        };
    }

    /**
     * Creates disc cache interceptor
     *
     * @return {@link Interceptor}
     */
    private Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();

                if (!isConnectNetwork()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(DISC_CACHE_LENGTH_DAYS, TimeUnit.DAYS)
                        .build();

                    request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
                }
                return chain.proceed(request);
            }
        };
    }

    /**
     * Creates auth token interceptor
     *
     * @return {@link Interceptor}
     */
    private Interceptor provideAccessTokenInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request.Builder newRequestBuilder = chain.request().newBuilder();
                if (!TextUtils.isEmpty(authorizationToken())) {
                    Log.i(TAG, "Authorization intercept: Bearer " + authorizationToken());
                    newRequestBuilder.addHeader("Authorization", "Bearer " + authorizationToken());
                }
                return chain.proceed(newRequestBuilder.build());
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Converter.Factory getConverterFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapterFactory(new EmptyObjectTypeAdapter());
        gsonBuilder.registerTypeAdapterFactory(new EmptyArrayTypeAdapter());
        return GsonConverterFactory.create(gsonBuilder.create());
    }
}
