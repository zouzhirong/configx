/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.app;

import com.configx.web.dao.ProfileMapper;
import com.configx.web.exception.ConfigException;
import com.configx.web.exception.ErrorCode;
import com.configx.web.model.Profile;
import com.configx.web.service.user.UserContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Profile Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ProfileService {

    @Autowired
    private ProfileMapper profileMapper;

    /**
     * 获取Profile
     *
     * @param id
     * @return
     */
    public Profile getProfile(int id) {
        return profileMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取Profile
     *
     * @param appId
     * @param id
     * @return
     */
    public Profile getProfile(int appId, int id) {
        if (ProfileContants.DEFAULT_PROFILE_ID == id) {
            return defaultProfile(appId);
        }
        Profile profile = profileMapper.selectByPrimaryKey(id);
        if (profile == null || profile.getAppId() != appId) {
            return null;
        } else {
            return profile;
        }
    }

    /**
     * 获取Profile，根据名称
     *
     * @param appId
     * @param name
     * @return
     */
    public Profile getProfile(int appId, String name) {
        if (ProfileContants.DEFAULT_PROFILE_NAME.equalsIgnoreCase(name)) {
            return defaultProfile(appId);
        }
        List<Profile> profileList = getProfileList(appId);
        if (profileList == null || profileList.isEmpty()) {
            return null;
        }
        for (Profile profile : profileList) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }

    /**
     * 默认的Profile
     *
     * @param appId
     * @return
     */
    public Profile defaultProfile(int appId) {
        Profile profile = newProfile(appId, ProfileContants.DEFAULT_PROFILE_NAME, "", 0, "");
        profile.setId(ProfileContants.DEFAULT_PROFILE_ID);
        return profile;
    }

    /**
     * 获取Profile列表
     *
     * @param appId
     * @return
     */
    public List<Profile> getProfileList(int appId) {
        List<Profile> list = profileMapper.selectByAppId(appId);
        list.add(0, defaultProfile(appId));
        return list;
    }

    /**
     * 获取Profile列表，根据Profile名称列表
     *
     * @param appId
     * @param profileNameList
     * @return
     */
    public List<Profile> getOrderedProfileList(int appId, List<String> profileNameList) {
        List<Profile> resultList = new ArrayList<>();

        if (profileNameList == null || profileNameList.isEmpty()) {
            return resultList;
        }

        List<Profile> profileList = getProfileList(appId);
        if (profileList == null || profileList.isEmpty()) {
            return resultList;
        }

        Profile profile = null;
        for (String profileName : profileNameList) {
            profile = findProfile(profileList, profileName);
            if (profile != null) {
                resultList.add(profile);
            }
        }

        return resultList;
    }

    /**
     * 从Profile列表中找出指定名称的Profile
     *
     * @param profileList
     * @param profileName
     * @return
     */
    private Profile findProfile(List<Profile> profileList, String profileName) {
        if (profileList == null || profileList.isEmpty()) {
            return null;
        }
        for (Profile profile : profileList) {
            if (profile.getName().equalsIgnoreCase(profileName)) {
                return profile;
            }
        }
        return null;
    }

    /**
     * 获取Profile ID列表，根据Profile名称列表
     *
     * @param appId
     * @param profiles
     * @return
     */
    public List<Integer> getOrderedProfileIdList(int appId, String profiles) {
        List<String> profileNameList = StringUtils.isEmpty(profiles) ? null : Arrays.asList(StringUtils.split(profiles, ","));
        return getOrderedProfileIdList(appId, profileNameList);
    }

    /**
     * 获取Profile ID列表，根据Profile名称列表
     *
     * @param appId
     * @param profileNameList
     * @return
     */
    public List<Integer> getOrderedProfileIdList(int appId, List<String> profileNameList) {
        List<Integer> profileIdList = new ArrayList<>();

        List<Profile> profileList = getOrderedProfileList(appId, profileNameList);
        if (profileList == null || profileList.isEmpty()) {
            return profileIdList;
        }

        for (Profile profile : profileList) {
            profileIdList.add(profile.getId());
        }

        return profileIdList;
    }

    /**
     * 创建Profile，如果不存在的话
     *
     * @param appId
     * @param name
     * @param description
     * @param order
     * @param color
     * @return
     */
    public Profile createIfAbsent(int appId, String name, String description, int order, String color) {
        Profile profile = getProfile(appId, name);
        if (profile != null) {
            return profile;
        }

        return createProfile(appId, name, description, order, color);
    }

    /**
     * 创建新Profile
     *
     * @param appId
     * @param name
     * @param description
     * @param order
     * @param color
     * @return
     */
    public Profile createProfile(int appId, String name, String description, int order, String color) {
        if (ProfileContants.DEFAULT_PROFILE_NAME.equalsIgnoreCase(name)) {
            throw new ConfigException(ErrorCode.PROFILE_DEFAULT_NAME_CONFLICT);
        }
        Profile profile = newProfile(appId, name, description, order, color);
        return createProfile(profile);
    }

    /**
     * 创建新Profile对象
     *
     * @param appId
     * @param name
     * @param description
     * @param order
     * @param color
     * @return
     */
    private Profile newProfile(int appId, String name, String description, int order, String color) {
        Date now = new Date();

        Profile profile = new Profile();
        profile.setAppId(appId);
        profile.setName(name);
        profile.setDescription(description);
        profile.setOrder(order);
        profile.setColor(color);

        profile.setCreator(UserContext.name());
        profile.setCreateTime(now);
        return profile;
    }

    /**
     * 创建新Profile
     *
     * @param profile
     * @return
     */
    public Profile createProfile(Profile profile) {
        profileMapper.insertSelective(profile);
        return profile;
    }

    /**
     * 修改Profile信息
     *
     * @param appId
     * @param id
     * @param name
     * @param description
     * @param order
     * @param color
     */
    public void modifyProfile(int appId, int id, String name, String description, int order, String color) {
        Profile profile = getProfile(appId, id);

        Profile newProfile = new Profile();
        newProfile.setId(profile.getId());
        newProfile.setName(name);
        newProfile.setDescription(description);
        newProfile.setOrder(order);
        newProfile.setColor(color);
        newProfile.setUpdater(UserContext.name());
        newProfile.setUpdateTime(new Date());

        updateProfile(newProfile);
    }

    /**
     * 更新Profile信息
     *
     * @param profile
     * @return
     */
    public Profile updateProfile(Profile profile) {
        profileMapper.updateByPrimaryKeySelective(profile);
        return profile;
    }

    /**
     * 删除Profile
     *
     * @param appId
     * @param id
     */
    public void delete(int appId, int id) {
        Profile profile = getProfile(appId, id);
        if (profile != null) {
            profileMapper.deleteByPrimaryKey(id);
        }
    }

}
