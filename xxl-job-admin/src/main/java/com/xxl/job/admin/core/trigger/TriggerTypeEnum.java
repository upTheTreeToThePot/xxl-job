package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * trigger type enum
 *
 * @author xuxueli 2018-09-16 04:56:41
 */
public enum TriggerTypeEnum {

    // 手动触发
    MANUAL(I18nUtil.getString("jobconf_trigger_type_manual")),
    // corn 规则触发
    CRON(I18nUtil.getString("jobconf_trigger_type_cron")),
    // 重试触发
    RETRY(I18nUtil.getString("jobconf_trigger_type_retry")),
    // 父任务触发
    PARENT(I18nUtil.getString("jobconf_trigger_type_parent")),
    // api触发
    API(I18nUtil.getString("jobconf_trigger_type_api")),
    // 任务超时触发
    MISFIRE(I18nUtil.getString("jobconf_trigger_type_misfire"));

    private TriggerTypeEnum(String title){
        this.title = title;
    }
    private String title;
    public String getTitle() {
        return title;
    }

}
