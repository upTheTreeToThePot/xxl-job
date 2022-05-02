package com.xxl.job.admin.util;

import com.xxl.job.admin.core.cron.CronExpression;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;

/**
 * @author glg
 * @date 2022/04/12
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoreExpressionTest {

    private static final String CORN = "* * * * 1 ?";

    @Test
    public void testConstructor() {
        try {
            CronExpression cronExpression = new CronExpression(CORN);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
