package com.configx.client.version;

/**
 * 版本移除监听器
 * 当一个版本成为了旧版本，并且没有线程引用这个版本，则会触发移除这个版本的监听器
 * <p>
 * Created by zouzhirong on 2017/9/15.
 */
public interface VersionRemovalListener {

    /**
     * 在移除某个版本号时，执行操作
     *
     * @param version
     */
    void onRemoval(long version);

}
