package com.configx.client.jmx;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ImportBeanDefinitionRegistrar} for export spring beans as jmx mbean..
 *
 * @author Zhirong Zou
 */
public class ConfigJmxRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * ConfigManagement MBean
     */
    private static final String CONFIG_MANAGEMENT_BEAN_NAME = ConfigManagement.class.getName();

    /**
     * MBeanExporter
     */
    private static final String EXPORTER_BEAN_NAME = CONFIG_MANAGEMENT_BEAN_NAME + ".exporter";

    /**
     * MBeanServer
     */
    private static final String MBEAN_SERVER_BEAN_NAME = CONFIG_MANAGEMENT_BEAN_NAME + ".mbeanServer";

    /**
     * JMXConnectorServer
     */
    private static final String SERVER_CONNECTOR_BEAN_NAME = CONFIG_MANAGEMENT_BEAN_NAME + ".serverConnector";

    /**
     * Registry
     */
    private static final String RMI_REGISTRY_BEAN_NAME = CONFIG_MANAGEMENT_BEAN_NAME + ".rmiRegistry";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // ConfigManagement MBean
        if (!registry.containsBeanDefinition(CONFIG_MANAGEMENT_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(ConfigManagement.class);
            registry.registerBeanDefinition(CONFIG_MANAGEMENT_BEAN_NAME, meta.getBeanDefinition());
        }

        // MBeanExporter
        if (!registry.containsBeanDefinition(EXPORTER_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(MBeanExporter.class);

            Map<Object, Object> map = new HashMap<>();
            map.put("spring.beans:name=ConfigManagement", CONFIG_MANAGEMENT_BEAN_NAME);
            meta.addPropertyValue("beans", map);

            meta.addPropertyReference("server", MBEAN_SERVER_BEAN_NAME);

            registry.registerBeanDefinition(EXPORTER_BEAN_NAME, meta.getBeanDefinition());
        }

        // MBeanServer
        if (!registry.containsBeanDefinition(MBEAN_SERVER_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(MBeanServerFactoryBean.class);
            meta.addPropertyValue("locateExistingServerIfPossible", true);
            registry.registerBeanDefinition(MBEAN_SERVER_BEAN_NAME, meta.getBeanDefinition());
        }


        // 暴露Mbean，远程调用
        // JMXConnectorServer
        if (!registry.containsBeanDefinition(SERVER_CONNECTOR_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(ConnectorServerFactoryBean.class);
            meta.addDependsOn(RMI_REGISTRY_BEAN_NAME);
            meta.addPropertyValue("objectName", "connector:name=rmi");
            meta.addPropertyValue("serviceUrl", "service:jmx:rmi://localhost/jndi/rmi://localhost:1099/jmxconnector");
            registry.registerBeanDefinition(SERVER_CONNECTOR_BEAN_NAME, meta.getBeanDefinition());
        }

        // Registry
        if (!registry.containsBeanDefinition(RMI_REGISTRY_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(RmiRegistryFactoryBean.class);
            registry.registerBeanDefinition(RMI_REGISTRY_BEAN_NAME, meta.getBeanDefinition());
        }

    }

}
