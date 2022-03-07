package main.java.runner;

import main.java.config.AppEnum;
import main.java.config.Settings;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/24 17:00
 */
public class InfoExtractionRunner {

    private static Logger logger = LoggerFactory.getLogger(InfoExtractionRunner.class);

    public static void main(String[] args){

        // enable the AspectJ module
        Settings.aspectActive = true;

        Settings extractorSetting = new Settings(AppEnum.LarkPlayer);

        runTest(extractorSetting.testSuite, "EqualizerTest");
    }


    /**
     * Run a single test case when a runner class is specified.
     * @param testSuite
     * @param testCase
     */
    public static void runTest(String testSuite, String testCase){

        long startTime = 0;
        long finishTime;
        long elapsedTime;

        // build the class runner
        String testCaseToRun = testSuite + "." + testCase;

        // run the test programmatically
        Result result = null;
        try {
            logger.info("Running test " + testCaseToRun);
            startTime = System.currentTimeMillis();

            result = JUnitCore.runClasses(Class.forName(testCaseToRun));
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        // If the test failed, save the exception.
        if(!result.wasSuccessful()){
            logger.info("Test " + testCaseToRun + " failed, saving the exception.");

            // For each breakage, record it.
            for (Failure failure : result.getFailures()) {
                logger.error(String.valueOf(failure));
            }
        } else {
            logger.info("Test " + testCaseToRun + " passed!");
        }

        finishTime = System.currentTimeMillis();
        elapsedTime = finishTime - startTime;
        logger.info("Info extraction collected in {} s", String.format("%.3f", elapsedTime / 1000.0f));

    }

}
