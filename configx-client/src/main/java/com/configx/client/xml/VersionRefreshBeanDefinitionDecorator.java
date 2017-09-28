package com.configx.client.xml;

import com.configx.client.dependency.DependencyConfigUtils;
import com.configx.client.scope.refresh.VersionRefreshScope;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 处理configx:version-refresh标签
 * <p>
 * Created by zouzhirong on 2017/9/17.
 */
public class VersionRefreshBeanDefinitionDecorator implements BeanDefinitionDecorator {

    private static final String DEPENDS_ON = "dependsOn";

    @Override
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {

        // 设置bean scope为version-refresh
        definition.getBeanDefinition().setScope(VersionRefreshScope.SCOPE_NAME);

        String[] dependsOn = null;
        if (node instanceof Element) {
            Element ele = (Element) node;
            if (ele.hasAttribute(DEPENDS_ON)) {
                dependsOn = StringUtils.tokenizeToStringArray(ele.getAttribute(DEPENDS_ON), ",");
            }
        }

        // 注册属性依赖
        DependencyConfigUtils.registerDependency(parserContext.getRegistry(), definition.getBeanName(), dependsOn);

        return definition;
    }

}
