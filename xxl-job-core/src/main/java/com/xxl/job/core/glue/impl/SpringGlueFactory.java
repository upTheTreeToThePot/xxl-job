package com.xxl.job.core.glue.impl;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.glue.GlueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author xuxueli 2018-11-01
 */
public class SpringGlueFactory extends GlueFactory {
    private static Logger logger = LoggerFactory.getLogger(SpringGlueFactory.class);


    /**
     * inject action of spring
     * @param instance
     */
    // 注入字段属性
    @Override
    public void injectService(Object instance){
        if (instance==null) {
            return;
        }

        if (XxlJobSpringExecutor.getApplicationContext() == null) {
            return;
        }

        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Object fieldBean = null;
            // with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired

            if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
                try {
                    Resource resource = AnnotationUtils.getAnnotation(field, Resource.class);
                    // 根据 resource name 获取 bean
                    if (resource.name()!=null && resource.name().length()>0){
                        fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(resource.name());
                    } else {
                        // 如果 resource 没有指定名称，根据字段名称获取 bean
                        fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(field.getName());
                    }
                } catch (Exception e) {
                }
                if (fieldBean==null ) {
                    // 如果前面没有获取到bean，根据字段的类型获取bean
                    fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(field.getType());
                }
            } else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
                Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
                if (qualifier!=null && qualifier.value()!=null && qualifier.value().length()>0) {
                    // 根据Qualifier指定的name获取bean
                    fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(qualifier.value());
                } else {
                    // 如果没有Qualifier，根据字段的类型获取bean
                    fieldBean = XxlJobSpringExecutor.getApplicationContext().getBean(field.getType());
                }
            }

            if (fieldBean!=null) {
                field.setAccessible(true);
                try {
                    field.set(instance, fieldBean);
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
