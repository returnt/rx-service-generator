/**
 * File AbstractWebSocket
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 28.05.18 13:46
 */

package returnt.ru.rxservicegenerator.service;

import android.support.annotation.Nullable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.*;
import okio.ByteString;

/**
 * Class AbstractWebSocket
 *
 * JDK version 8
 *
 * @author d.a.ganzha
 * @category rx-service-generator
 * @package returnt.ru.rxservicegenerator.service
 * @copyright 2017-2018 returnt (http://returnt.ru). All rights reserved.
 * @link http://returnt.ru
 * @created by 28.05.18 13:46
 */
public abstract class AbstractWebSocket {

    public interface Action {

        String onOpenSendMessage();

        /**
         * onReceiveMessage
         * the method is started in a new thread
         *
         * @param single
         */
        void onReceiveMessage(Single<String> single);

        /**
         * onClosed
         * the method is started in a new thread
         *
         * @param single
         */
        void onClosed(Single<String> single);

        /**
         * onFailure
         * the method is started in a new thread
         *
         * @param single
         */
        void onFailure(Single<Throwable> single);
    }

    public abstract String baseHost();

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private OkHttpClient mOkHttpClient;

    private Request mRequest;

    private WebSocket mWebSocket;

    private AbstractWebSocket.Action mAbstractWebSocket;

    private void init() {
        if (mWebSocket != null) return;
        mOkHttpClient = new OkHttpClient();
        mRequest = new Request.Builder().url(baseHost()).build();
        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(okhttp3.WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                if (mAbstractWebSocket != null)
                    webSocket.send(mAbstractWebSocket.onOpenSendMessage());
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if (mAbstractWebSocket != null)
                    mAbstractWebSocket.onReceiveMessage(Single.just(text));
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                if (mAbstractWebSocket != null)
                    mAbstractWebSocket.onClosed(Single.just(reason)
                        .observeOn(AndroidSchedulers.mainThread()));
            }

            @Override
            public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                webSocket.close(NORMAL_CLOSURE_STATUS, null);
                if (mAbstractWebSocket != null)
                    mAbstractWebSocket.onClosed(Single.just(reason));
            }

            @Override
            public void onFailure(okhttp3.WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                if (mAbstractWebSocket != null)
                    mAbstractWebSocket.onFailure(Single.just(t));
            }
        };
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, webSocketListener);
        mOkHttpClient.dispatcher().executorService().shutdown();
    }

    public void openSocket() {
        init();
    }

    public void closedSocket() {
        mAbstractWebSocket = null;
        mWebSocket.close(NORMAL_CLOSURE_STATUS, null);
        mWebSocket = null;
    }

    public WebSocket getWebSocket() {
        if (mWebSocket == null) init();
        return mWebSocket;
    }

    public void setAbstractWebSocketListener(Action abstractWebSocket) {
        mAbstractWebSocket = abstractWebSocket;
    }
}
