/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item.locator;

import com.configx.client.env.VersionPropertySource;
import com.configx.client.item.ConfigItemList;

/**
 * Strategy for locating (possibly remote) property sources for the Environment.
 *
 * @author zhirong.zou
 */
public interface PropertySourceLocator {

    VersionPropertySource<ConfigItemList> update(long version);

}
