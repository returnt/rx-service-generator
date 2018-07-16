/**
 * File ConnectionUtils
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

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class ConnectionUtils
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
public class ConnectionUtils {

    /**
     * check if device has internet connection
     *
     * @return boolean
     */
    @SuppressLint("MissingPermission")
    public static boolean hasConnection(ConnectivityManager cm) {
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }

}
