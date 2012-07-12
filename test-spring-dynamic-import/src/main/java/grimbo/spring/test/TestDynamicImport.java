package grimbo.spring.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDynamicImport {
    ApplicationContext ctx = null;

    @Before
    public void before() {
        // clear the beans list
        Bean.beans = new ArrayList<Bean>();
    }

    /**
     * Expression in <import> does not work.
     */
    @Test
    public void testUsePropertyExpressionInImportFails() {
        try {
            load("testUsePropertyExpressionInImportFails.xml");
            fail("Expected BeanDefinitionParsingException");
        } catch (BeanDefinitionParsingException e) {
            FileNotFoundException fnfe = getCause(e, FileNotFoundException.class);
            assertNotNull("Expected to see a FileNotFoundException as root cause", fnfe);
        }
    }

    /**
     * Properties loaded from properties file.
     * 
     * Expression should only cause the referenced bean to load. Not the second
     * bean. Assuming beans are lazy-init.
     */
    @Test
    public void testUsePropsFilePropertyExpressionInBeanRef() {
        load("testUsePropsFilePropertyExpressionInBeanRef.xml");
        Bean bean = (Bean) ctx.getBean("bean");
        assertNotNull(bean);
        assertEquals("bean1", bean.getName());
        assertEquals(1, Bean.beans.size());
    }

    /**
     * Properties loaded from system properties.
     * 
     * Expression should only cause the referenced bean to load. Not the second
     * bean. Assuming beans are lazy-init.
     */
    @Test
    public void testUseSysPropPropertyExpressionInBeanRef() {
        System.setProperty("beanName", "bean1");
        load("testUseSysPropPropertyExpressionInBeanRef.xml");
        Bean bean = (Bean) ctx.getBean("bean");
        assertNotNull(bean);
        assertEquals("bean1", bean.getName());
        assertEquals(1, Bean.beans.size());
    }

    private <T> T getCause(Throwable throwable, Class<T> clazz) {
        Throwable t = throwable.getCause();
        while (null != t) {
            if (clazz.isAssignableFrom(t.getClass())) {
                return (T) t;
            }
            t = t.getCause();
        }
        return null;
    }

    private void load(String resource) {
        ctx = new ClassPathXmlApplicationContext(resource);
    }
}
