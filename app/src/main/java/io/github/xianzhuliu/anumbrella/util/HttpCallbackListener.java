package io.github.xianzhuliu.anumbrella.util;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
