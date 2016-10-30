package io.github.xianzhuliu.anumbrella.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.github.xianzhuliu.anumbrella.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public static void sendOkHttp(String url, final HttpCallbackListener listener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    e.printStackTrace();
                    listener.onError(e);
                } else {
                    new RuntimeException("HttpCallbackListener is null");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (listener != null) {
                    listener.onFinish(response.body().string());
                } else {
                    new RuntimeException("HttpCallbackListener is null");
                }
            }
        });
    }

    public static void getCitiesFromFile(final Context context, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = context.getResources().openRawResource(R.raw.city_id);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder response = new StringBuilder();
                try {
                    while ((line = reader.readLine()) != null) {
                        response.append(line + ";");
                    }
                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
}
