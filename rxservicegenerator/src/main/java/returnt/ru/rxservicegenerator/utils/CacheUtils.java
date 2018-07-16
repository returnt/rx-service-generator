/**
 * File CacheUtils
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 07.11.2017 13:56
 */

package returnt.ru.rxservicegenerator.utils;

import android.util.Log;
import okhttp3.Cache;

import java.io.File;

/**
 * Class CacheUtils
 *
 * Author d.a.ganzha
 * Package returnt.rxmodule.utils
 * Created by 07.11.2017
 */

/**
 * Class CacheUtils
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @package returnt.ru.rxservicegenerator.utils
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 07.11.2017 13:56
 */
public class CacheUtils {

    private static final String TAG = "CacheUtils";

    private static final long CACHE_FILE_SIZE_MB = 10 * 1024 * 1024; // 10 MB
    private static final String CHILD_FOLDER = "http-cache";

    /**
     * Creates cahce file
     *
     * @return {@link Cache}
     */
    public static Cache provideCache(File cacheDir) {
        Cache cache = null;
        try {
            cache = new Cache(new File(cacheDir, CHILD_FOLDER),
                    CACHE_FILE_SIZE_MB); // 10 MB
        } catch (Exception e) {
            Log.e(TAG, "provideCache: ", e);
        }
        return cache;
    }

}
