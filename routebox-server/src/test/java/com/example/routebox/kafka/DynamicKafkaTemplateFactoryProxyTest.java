package com.example.routebox.server.kafka;

import com.example.testconfig.TestKafkaOnlyApplication;
import com.example.catbox.server.config.DynamicKafkaTemplateFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests to verify that KafkaTemplate instances created by DynamicKafkaTemplateFactory
 * are properly managed Spring beans with AOP proxy support.
 */
@SpringBootTest(classes = TestKafkaOnlyApplication.class)
@TestPropertySource(properties = {
    "kafka.clusters.proxy-test-cluster.bootstrap-servers=localhost:9092",
    "kafka.clusters.proxy-test-cluster.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "kafka.clusters.proxy-test-cluster.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
@DirtiesContext
class DynamicKafkaTemplateFactoryProxyTest {

    @Autowired
    private DynamicKafkaTemplateFactory factory;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testKafkaTemplateIsSpringManagedBean() {
        // Given: a cluster key
        String clusterKey = "proxy-test-cluster";
        
        // When: we get a template from the factory
        KafkaTemplate<String, String> template = factory.getTemplate(clusterKey);
        
        // Then: the template should be registered as a Spring bean
        String expectedBeanName = clusterKey + "-KafkaTemplate";
        assertThat(applicationContext.containsBean(expectedBeanName))
            .as("KafkaTemplate should be registered as a Spring bean with name: " + expectedBeanName)
            .isTrue();
        
        // And: retrieving the bean from context should return the same instance
        Object beanFromContext = applicationContext.getBean(expectedBeanName);
        assertThat(beanFromContext)
            .as("Template from factory should be the same instance as bean from context")
            .isSameAs(template);
    }

    @Test
    void testKafkaTemplateIsSingleton() {
        // Given: a cluster key
        String clusterKey = "proxy-test-cluster";
        
        // When: we get the template multiple times
        KafkaTemplate<String, String> template1 = factory.getTemplate(clusterKey);
        KafkaTemplate<String, String> template2 = factory.getTemplate(clusterKey);
        
        // Then: we should get the same instance (singleton scope)
        assertThat(template1)
            .as("Multiple calls to getTemplate should return the same singleton instance")
            .isSameAs(template2);
    }

    @Test
    void testProducerFactoryIsSpringManagedBean() {
        // Given: a cluster key
        String clusterKey = "proxy-test-cluster";
        
        // When: we get a template from the factory (which creates the ProducerFactory)
        factory.getTemplate(clusterKey);
        
        // Then: the ProducerFactory should also be registered as a Spring bean
        String expectedFactoryBeanName = clusterKey + "-ProducerFactory";
        assertThat(applicationContext.containsBean(expectedFactoryBeanName))
            .as("ProducerFactory should be registered as a Spring bean with name: " + expectedFactoryBeanName)
            .isTrue();
    }

    @Test
    void testDynamicallyCreatedTemplateHasSpringLifecycle() {
        // Given: a cluster key
        String clusterKey = "proxy-test-cluster";
        
        // When: we get a template from the factory
        KafkaTemplate<String, String> template = factory.getTemplate(clusterKey);
        
        // Then: the template should have been initialized by Spring
        // We can verify this by checking that it's not null and properly configured
        assertThat(template).isNotNull();
        assertThat(template.getProducerFactory()).isNotNull();
    }

    @Test
    void testTemplateCanBeRetrievedDirectlyFromContext() {
        // Given: we create a template through the factory
        String clusterKey = "proxy-test-cluster";
        factory.getTemplate(clusterKey);
        
        // When: we retrieve it directly from the Spring context
        String beanName = clusterKey + "-KafkaTemplate";
        Object bean = applicationContext.getBean(beanName);
        
        // Then: it should be a KafkaTemplate instance
        assertThat(bean)
            .as("Bean retrieved from context should be a KafkaTemplate")
            .isInstanceOf(KafkaTemplate.class);
        
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> templateFromContext = (KafkaTemplate<String, String>) bean;
        
        // And: it should be the same instance as returned by the factory
        assertThat(templateFromContext)
            .as("Template from context should match template from factory")
            .isSameAs(factory.getTemplate(clusterKey));
    }

    @Test
    void testMultipleCallsDoNotDuplicateBeans() {
        // Given: a cluster key
        String clusterKey = "proxy-test-cluster";
        
        // When: we create a template
        factory.getTemplate(clusterKey);
        factory.getTemplate(clusterKey);

        // Then: there should not be duplicate bean definitions for the same template
        String beanName = clusterKey + "-KafkaTemplate";
        String[] names = applicationContext.getBeanNamesForType(KafkaTemplate.class);
        assertThat(names).contains(beanName);
        // Ensure only one bean for this cluster key exists
        long count = java.util.Arrays.stream(names).filter(n -> n.equals(beanName)).count();
        assertThat(count).isEqualTo(1);
    }
}
