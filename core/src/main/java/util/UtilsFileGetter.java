package main.java.util;

import java.io.File;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/13 11:51
 */
public class UtilsFileGetter {
    /**
     * @param name 测试用例名称
     * @param pathToTestSuite 通向测试用例集的路径
     * @return 测试用例的绝对路径
     */
    public static String getTestFile(String name, String pathToTestSuite) {
        File[] files = new File(pathToTestSuite).listFiles(FileFilter.javaFileFilter);
        for (File file: files) {
            if(file.getName().contains(name)) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
}
