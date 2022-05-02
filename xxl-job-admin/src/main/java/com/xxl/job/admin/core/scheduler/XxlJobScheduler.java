package com.xxl.job.admin.core.scheduler;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.thread.*;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli 2018-10-28 00:18:17
 */

public class XxlJobScheduler  {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobScheduler.class);

    // init 方法，在实例化的时候，被外部手动调用
    public void init() throws Exception {
        // init i18n
        initI18n();

        // admin trigger pool start
        // 初始化 scheduler 的两个线程池，一个是快线程池 fastThreadPool，一个是慢线程池 slowThreadPool
        JobTriggerPoolHelper.toStart();

        // admin registry monitor run
        // 初始化一个任务注册或移除的线程池 并 启动一个注册管理线程
        JobRegistryHelper.getInstance().start();

        // admin fail-monitor run
        // 启动一条管理线程，对失败任务进行重试，并进行邮件报警
        JobFailMonitorHelper.getInstance().start();

        // admin lose-monitor run ( depend on JobTriggerPoolHelper )
        // 创建一个回调线程池，并且创建一条管理长时间执行并且任务注册已经消息的任务，
        JobCompleteHelper.getInstance().start();

        // admin log report start
        // 1。创建一个线程对过去3天的任务执行状况进行统计，并对过期的 jobLog 进行删除
        JobLogReportHelper.getInstance().start();

        // start-schedule  ( depend on JobTriggerPoolHelper )
        // 启动线程去执行定时任务
        JobScheduleHelper.getInstance().start();

        logger.info(">>>>>>>>> init xxl-job admin success.");
    }

    
    public void destroy() throws Exception {

        // stop-schedule
        // 关闭任务扫描执行线程
        JobScheduleHelper.getInstance().toStop();

        // admin log report stop
        // 关闭logReport统计线程
        JobLogReportHelper.getInstance().toStop();

        // admin lose-monitor stop
        // 销毁 callback 线程池
        JobCompleteHelper.getInstance().toStop();

        // admin fail-monitor stop
        // 关闭失败重试-警告线程
        JobFailMonitorHelper.getInstance().toStop();

        // admin registry stop
        // 销毁任务注册线程池
        JobRegistryHelper.getInstance().toStop();

        // admin trigger pool stop
        // 销毁 fast线程池和slow线程池
        JobTriggerPoolHelper.toStop();

    }

    // ---------------------- I18n ----------------------

    private void initI18n(){
        for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }

    // ---------------------- executor-client ----------------------
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();
    // 根据地址做一个 ExecutorBiz 的缓存，如果没有将会生成一个 ExecutorBizClient
    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        // valid
        if (address==null || address.trim().length()==0) {
            return null;
        }

        // load-cache
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // set-cache
        executorBiz = new ExecutorBizClient(address, XxlJobAdminConfig.getAdminConfig().getAccessToken());

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }

}
