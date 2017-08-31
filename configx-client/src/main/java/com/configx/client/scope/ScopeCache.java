/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.scope;

import java.util.Collection;

/**
 * A special purpose cache interface specifically for the {@link GenericScope} to use to manage cached bean instances.
 * Implementations generally fall into two categories: those that store values "globally" (i.e. one instance per key),
 * and those that store potentially multiple instances per key based on context (e.g. via a thread local). All
 * implementations should be thread safe.
 *
 * @author Zhirong Zou
 */
public interface ScopeCache {

    /**
     * Remove the object with this name from the cache.
     *
     * @param name the object name
     * @return the object removed or null if there was none
     */
    Object remove(String name);

    /**
     * Clear the cache and return all objects in an unmodifiable collection.
     *
     * @return all objects stored in the cache
     */
    Collection<Object> clear();

    /**
     * Get the named object from the cache.
     *
     * @param name the name of the object
     * @return the object with that name or null if there is none
     */
    Object get(String name);

    /**
     * Put a value in the cache if the key is not already used. If one is already present with the name provided, it is
     * not replaced, but is returned to the caller.
     *
     * @param name  the key
     * @param value the new candidate value
     * @return the value that is in the cache at the end of the operation
     */
    Object put(String name, Object value);

}
