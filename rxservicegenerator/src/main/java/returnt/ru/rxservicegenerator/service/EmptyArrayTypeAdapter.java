/**
 * File EmptyArrayTypeAdapter
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

import android.util.Log;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Class EmptyArrayTypeAdapter
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
class EmptyArrayTypeAdapter implements TypeAdapterFactory {

    private static final String TAG = "EmptyArrayTypeAdapter";

    /**
     * Replaces empty arrays with nulls.
     * Prevents class cast exceptions
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = elementAdapter.read(in);
                if(jsonElement.isJsonArray()) {
                    JsonArray jsonArray= jsonElement.getAsJsonArray();
                    if(jsonArray.size() == 0) {
                        return null;
                    }

                }

                try {
                    return delegate.fromJsonTree(jsonElement);
                } catch (JsonSyntaxException | IllegalStateException e) {
                    Log.e(TAG, "read: ", e);
                    return null;
                }
            }
        }.nullSafe();
    }

}
