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
     * Expression in <import>, which is expected to be resolved via a property
     * in a property file, does not work. As the property file only loads when
     * all XML files have been loaded, the expression cannot be resolved in
     * time.
     */
    @Test
    public void testUsePropsFilePropertyExpressionInImportFails() {
        try {
            load("testUsePropertyExpressionInImportFails.xml");
            fail("Expected BeanDefinitionParsingException");
        } catch (BeanDefinitionParsingException e) {
            FileNotFoundException fnfe = getCause(e, FileNotFoundException.class);
            assertNotNull("Expected to see a FileNotFoundException as root cause", fnfe);
        }
    }

    /**
     * Expression in <import>, which is a system property expression, does work.
     */
    @Test
    public void testUseSysPropPropertyExpressionInImportSucceeds() {
        System.setProperty("fileName", "file1.xml");
        load("testUseSysPropPropertyExpressionInImport.xml");
        // loading file1.xml should cause bean1 to be present.
        Bean bean = (Bean) ctx.getBean("bean1");
        assertNotNull(bean);
        assertEquals("bean1", bean.getName());
        // loading file1.xml should cause bean2 to NOT be present.
        assertFalse("bean2 should not exist", ctx.containsBean("bean2"));
        assertEquals("Should only be one Bean created", 1, Bean.beans.size());
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
        // loading both file1.xml and file2.xml should cause bean1 and bean2 to
        // be present as definitions.
        assertTrue("bean2 should exist as a definition", ctx.containsBean("bean2"));
        assertEquals("Should only be one Bean created", 1, Bean.beans.size());
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
