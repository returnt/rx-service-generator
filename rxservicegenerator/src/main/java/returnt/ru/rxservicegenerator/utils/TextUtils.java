/**
 * File TextUtils
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

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.Reader;

/**
 * Class TextUtils
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
public class TextUtils {

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isEmpty(@Nullable Object str) {
        return str == null;
    }

    public static String charStreamToString(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int charsRead;
        char[] chars = new char[100];
        do {
            charsRead = reader.read(chars, 0, chars.length);
            //if we have valid chars, append them to end of string.
            if (charsRead > 0)
                builder.append(chars, 0, charsRead);
        } while (charsRead > 0);
        return builder.toString();
    }
}
