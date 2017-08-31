/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.app;

import com.configx.web.dao.TagMapper;
import com.configx.web.model.Tag;
import com.configx.web.service.user.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 标签 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取Tag
     *
     * @param id
     * @return
     */
    public Tag getTag(int id) {
        return tagMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取Tag
     *
     * @param appId
     * @param id
     * @return
     */
    public Tag getTag(int appId, int id) {
        Tag tag = tagMapper.selectByPrimaryKey(id);
        if (tag == null || tag.getAppId() != appId) {
            return null;
        } else {
            return tag;
        }
    }

    /**
     * 获取Tag，根据名称
     *
     * @param appId
     * @param name
     * @return
     */
    public Tag getTag(int appId, String name) {
        List<Tag> tagList = getTagList(appId);
        if (tagList == null || tagList.isEmpty()) {
            return null;
        }
        for (Tag tag : tagList) {
            if (tag.getName().equalsIgnoreCase(name)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * 获取Tag列表
     *
     * @param appId
     * @return
     */
    public List<Tag> getTagList(int appId) {
        return tagMapper.selectByAppId(appId);
    }

    /**
     * 获取Tag ID列表
     *
     * @param tagList
     * @return
     */
    public List<Integer> getTagIdList(List<Tag> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            return Collections.emptyList();
        }

        List<Integer> tagIdList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagIdList.add(tag.getId());
        }
        return tagIdList;
    }

    /**
     * 创建Tag列表，如果不存在的话
     *
     * @param appId
     * @param tagNameList
     * @return
     */
    public List<Tag> createIfAbsent(int appId, List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            return Collections.emptyList();
        }

        List<Tag> resultList = new ArrayList<>();
        for (String tagName : tagNameList) {
            Tag tag = createIfAbsent(appId, tagName, "");
            resultList.add(tag);
        }
        return resultList;
    }

    /**
     * 创建Tag，如果不存在的话
     *
     * @param appId
     * @param name
     * @param description
     * @return
     */
    public Tag createIfAbsent(int appId, String name, String description) {
        Tag tag = getTag(appId, name);
        if (tag != null) {
            return tag;
        }

        return createTag(appId, name, description);
    }


    /**
     * 创建新Tag
     *
     * @param appId
     * @param name
     * @param description
     * @return
     */
    public Tag createTag(int appId, String name, String description) {
        Tag tag = newTag(appId, name, description);
        return createTag(tag);
    }

    /**
     * 创建新Tag对象
     *
     * @param appId
     * @param name
     * @param description
     * @return
     */
    private Tag newTag(int appId, String name, String description) {
        Date now = new Date();

        Tag tag = new Tag();
        tag.setAppId(appId);
        tag.setName(name);
        tag.setDescription(description);

        tag.setCreator(UserContext.name());
        tag.setCreateTime(now);
        return tag;
    }

    /**
     * 创建新Tag
     *
     * @param tag
     * @return
     */
    public Tag createTag(Tag tag) {
        tagMapper.insertSelective(tag);
        return tag;
    }

    /**
     * 修改Tag信息
     *
     * @param id
     * @param name
     * @param description
     */
    public void modifyTag(int id, String name, String description) {
        Tag tag = getTag(id);

        Tag newTag = new Tag();
        newTag.setId(tag.getId());
        newTag.setName(name);
        newTag.setDescription(description);
        newTag.setUpdater(UserContext.name());
        newTag.setUpdateTime(new Date());

        updateTag(newTag);
    }

    /**
     * 更新Tag信息
     *
     * @param tag
     * @return
     */
    public Tag updateTag(Tag tag) {
        tagMapper.updateByPrimaryKeySelective(tag);
        return tag;
    }

    /**
     * 删除Tag
     *
     * @param appId
     * @param id
     */
    public void delete(int appId, int id) {
        Tag tag = getTag(appId, id);
        if (tag != null) {
            tagMapper.deleteByPrimaryKey(id);
        }
    }

}
