/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.lenovo.project.utils;

import com.alibaba.fastjson.JSON;
import com.zhy.http.okhttp.callback.Callback;
import okhttp3.Response;
public abstract class TransCallBack extends Callback<TransResult> {
    @Override
    public TransResult parseNetworkResponse(Response response, int id) throws Exception {
        String s = response.body().string();
        TransResult result = JSON.parseObject(s, TransResult.class);
        return result;
    }
}
