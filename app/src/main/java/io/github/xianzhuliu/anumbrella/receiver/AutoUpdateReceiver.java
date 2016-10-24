package io.github.xianzhuliu.anumbrella.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.xianzhuliu.anumbrella.service.AutoUpdateService;

/**
 * Created by LiuXianzhu on 21/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AutoUpdateService.class);
        context.startService(service);
    }
}
