/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.form;

import com.configx.web.model.ReleaseForm;

import java.util.Comparator;

/**
 * @author Zhirong Zou
 */
public class ReleaseFormComparator implements Comparator<ReleaseForm> {

    @Override
    public int compare(ReleaseForm o1, ReleaseForm o2) {
        return Long.valueOf(o2.getId()).compareTo(o1.getId());
    }

}
