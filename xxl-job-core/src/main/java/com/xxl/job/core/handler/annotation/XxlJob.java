package com.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

/**
 * annotation for method jobhandler
 *
 * @author xuxueli 2019-12-11 20:50:13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlJob {

    /**
     * jobhandler name
     */
    String value();

    /**
     * init handler, invoked when JobThread init
     */
    // 声明 xxlJob 任务的 init 的初始方法，和 bean 对象的初始方法不是同一个意思
    String init() default "";

    /**
     * destroy handler, invoked when JobThread destroy
     */
    // 声明 xxlJob 任务的 destroy 方法
    String destroy() default "";

}
