package org.feosd.admin.system.web.schedule;

import org.feosd.common.crontask.web.schedule.JpJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 测试任务2
 */
@Component
@EnableScheduling
public class TestTask2 extends JpJob implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(org.feosd.common.crontask.web.task.ScheduleTaskTestJob.class);

    /**
     * 执行任务
     *
     * @throws RuntimeException
     */
    @Override
    public void runJob() throws RuntimeException {
        //在这里写定时任务执行的内容
        logger.info("==== 定时任务 TestTask2 ====> 执行中...... ");
    }
}
