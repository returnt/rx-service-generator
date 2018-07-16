/**
 * File IServiceGenerator
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 16.07.18 13:56
 */

package returnt.ru.rxservicegenerator.service;

import android.app.Application;
import okhttp3.Interceptor;
import retrofit2.Converter;

import java.io.File;
import java.util.List;

/**
 * Interface IServiceGenerator
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @package returnt.ru.rxservicegenerator.service
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 16.07.18 13:56
 */
public interface IServiceGenerator {

    /**
     * Application class
     *
     * @return {@link Application}
     */
    Application appApplication();

    /**
     * Base api host
     *
     * @return string uri
     */
    String baseHost();

    /**
     * authorization Token
     * Example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmV...
     *
     * @return String
     */
    String authorizationToken();

    /**
     * get cache dir
     *
     * @return {@link File}
     */
    File cacheDir();

    boolean isConnectNetwork();

    boolean buildConfig();

    /**
     * Custom Interceptor
     *
     * @return {@link Interceptor}
     */
    List<Interceptor> getInterceptors();

    /**
     * Creates service for mobile API
     * default full cache
     *
     * @param <S>
     *
     * @return generated service
     */
    <S> S createRxService(Class<S> serviceClass);

    /**
     * Creates service for mobile API
     *
     * @param <S>
     * @param serviceClass One of {@link StrategyCache}
     *
     * @return generated service
     */
    <S> S createRxService(Class<S> serviceClass, StrategyCache strategyCache);

    /**
     * get json converter factory
     *
     * @return {@link Converter.Factory}
     */
    Converter.Factory getConverterFactory();
}
