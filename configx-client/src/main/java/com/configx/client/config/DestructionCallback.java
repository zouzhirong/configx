package com.configx.client.config;

/**
 * bean销毁回调
 * <p>
 * Created by zouzhirong on 2017/9/16.
 */
public interface DestructionCallback {

    boolean destroy(String name);

}
