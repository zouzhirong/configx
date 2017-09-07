/**
 * $Id$
 * Copyright(C) 2012-2016 qiyun.com. All rights reserved.
 */
package com.configx.web.service.app;


import com.configx.web.model.Profile;

import java.util.Comparator;

/**
 * Profile排序
 *
 * @author Zhirong Zou
 */
public class ProfileComparator implements Comparator<Profile> {

    @Override
    public int compare(Profile o1, Profile o2) {
        return o1.getOrder().compareTo(o2.getOrder());
    }

}
