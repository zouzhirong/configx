/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.scope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A simple cache implementation backed by a concurrent map.
 *
 * @author Zhirong Zou
 */
public class StandardScopeCache implements ScopeCache {

    private final ConcurrentMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

    public Object remove(String name) {
        return cache.remove(name);
    }

    public Collection<Object> clear() {
        Collection<Object> values = new ArrayList<Object>(cache.values());
        cache.clear();
        return values;
    }

    public Object get(String name) {
        return cache.get(name);
    }

    public Object put(String name, Object value) {
        Object result = cache.putIfAbsent(name, value);
        if (result != null) {
            return result;
        }
        return value;
    }

}
