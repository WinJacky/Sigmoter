package main.java.util;

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import main.java.config.Settings;
import main.java.dataType.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/14 16:31
 */
public class ParseTest {
    private static Logger logger = LoggerFactory.getLogger(ParseTest.class);

    private static EnhancedTestCase tc;
    // folder 表示提取得到的元素及关键词信息存放文件
    private static String folder;
    private DesiredCapabilities capabilities;

    public ParseTest(String folder) {
        ParseTest.folder = folder;
        capabilities = new DesiredCapabilities();
    }

    public static String getFolder() {
        return folder;
    }

    public DesiredCapabilities getCapabilities() {
        return capabilities;
    }

    // Function
    // 对崩溃测试用例进行解析并以 JSON 形式存储到 folder 中
    public EnhancedTestCase parseAndSerialize(String pathToTestCase) {
        CompilationUnit cu = null;

        try {
            cu = JavaParser.parse(new File(pathToTestCase));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        new MethodVisitor().visit(cu, pathToTestCase);
        // 以 JSON 格式保存测试用例（TODO: 可能可以删掉）
        UtilsParser.serializeTestCase(tc, pathToTestCase, folder);

        return tc;
    }

    /**
     * 生成新的修复后的测试用例
     * @param newTest 已修复测试用例
     * @param oldPath 旧测试用例绝对路径
     * 新测试用例路径，示例：output\RepairedTC\LarkPlayer\TC1.java
     */
    public void parseAndSaveToJava(EnhancedTestCase newTest, String oldPath) throws IOException {
        tc = newTest;
        CompilationUnit cu = null;

        try {
            cu = JavaParser.parse(new File(oldPath));
        } catch (ParseException | IOException e) {
            logger.error("An error occurred while parsing a java file!");
            e.printStackTrace();
        }

        // 获取并修改包名，例：LarkPlayer
        String newPath = newTest.getPath();
        String packageName = newPath.substring(0, newPath.lastIndexOf("\\"));
        packageName = packageName.substring(packageName.lastIndexOf("\\") + 1);
        // 修改包的声明
        new PackageVisitor().visit(cu, packageName);

        // 修改语句
        new ChangeMethodVisitor().visit(cu, newPath);

        // 持久化新测试用例到文件
        String source = cu.toString();
        File file = new File(newPath);
        FileUtils.touch(file);
        FileUtils.write(file, source, Charset.defaultCharset());
    }

    // Class
    // MethodVisitor for parseAndSerialize()
    // PackageVisitor and ChangeMethodVisitor for parseAndSaveToJava()
    private class MethodVisitor extends VoidVisitorAdapter<Object> {
        @Override
        public void visit(MethodDeclaration m, Object arg) {
            // 提取 DesiredCapabilities
            if (m.getAnnotations() != null && m.getAnnotations().get(0).getName().getName().equals("Before")) {
                for (Statement st: m.getBody().getStmts()) {
                    if (st.toString().contains("setCapability")) {
                        String tempStr = st.toString();
                        String[] keyValue = StringUtils.substringsBetween(tempStr, "(", ")")[0].split(", ");
                        String key = keyValue[0].replace("\"", "");
                        if ("true".equals(keyValue[1]) || "false".equals(keyValue[1])) {
                            capabilities.setCapability(key, Boolean.parseBoolean(keyValue[1]));
                        } else {
                            capabilities.setCapability(key, keyValue[1].replace("\"", ""));
                        }
                    }
                }
            }
            // 提取测试用例核心内容
            else if (m.getAnnotations() != null && m.getAnnotations().get(0).getName().getName().equals("Test")) {
                String fullPath = arg.toString();
                String testCaseName = fullPath.substring(fullPath.lastIndexOf("\\") + 1).replace(Settings.JAVA_EXT, "");
                tc = new EnhancedTestCase(testCaseName, fullPath);

                for (Statement st : m.getBody().getStmts()) {
                    // 记录该测试语句所在行号
                    int line = st.getBeginLine();

                    // 对界面元素操作
                    if (st.toString().contains("driver.findElement")) {
                        EnhancedMobileElement eme = new EnhancedMobileElement(line);

                        if (st.toString().contains("click()")) {
                            eme.setAction(AppiumAction.Click);
                            eme.setValue("");
                        } else if (st.toString().contains("sendKeys")) {
                            eme.setAction(AppiumAction.SendKeys);
                            try {
                                eme.setValue(UtilsParser.getValueFromSendKeys(st.toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (st.toString().contains("clear()")) {
                            eme.setAction(AppiumAction.Clear);
                            eme.setValue("");
                        }

                        try {
                            eme.setLocator(UtilsParser.getAppiumLocator(st.toString()));

                            ViewTreeInfo info = UtilsFileGetter.getViewTreeInfoFromJsonFile(testCaseName, line, "viewTreeInfo", getFolder());
                            eme.setXpath(info.getXpath());
                            eme.setClassName(info.getClassName());
                            eme.setResourceId(info.getResourceId());
                            eme.setContentDesc(info.getContentDesc());
                            eme.setText(info.getText());
                            eme.setCheckable(info.isCheckable());
                            eme.setClickable(info.isClickable());
                            eme.setScrollable(info.isScrollable());
                            eme.setFocusable(info.isFocusable());
                            eme.setLongClickable(info.isLongClickable());
                            eme.setCoordinate(new Point(info.getX(), info.getY()));
                            eme.setDimension(new Dimension(info.getWidth(), info.getHeight()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tc.addStatementAtPosition(line, eme);
                    }
                    // 界面跳转（回退、前进、刷新）
                    else if (st.toString().contains("driver.navigate()")) {
                        EnhancedNavigate nav = null;

                        try {
                            if (st.toString().contains("back()")) {
                                nav = new EnhancedNavigate(line, "back");
                            } else if (st.toString().contains("forward()")) {
                                nav = new EnhancedNavigate(line, "forward");
                            } else if (st.toString().contains("refresh()")) {
                                nav = new EnhancedNavigate(line, "refresh");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (nav != null) {
                            tc.addStatementAtPosition(line, nav);
                        }
                    }
                    // 界面滑动、点击
                    else if (st.toString().contains("new TouchAction(driver)")) {
                        EnhancedTouchAction touchAction = null;
                        String temp = st.toString().replace("new TouchAction(driver).", "");

                        if (temp.startsWith("press") && temp.contains("moveTo") && temp.contains("release().perform()")) {
                            touchAction = new EnhancedTouchAction(line, "swipe");
                        } else if (temp.startsWith("tap") && temp.contains("perform()")) {
                            touchAction = new EnhancedTouchAction(line, "tap");
                        }

                        if (touchAction != null) {
                            if (touchAction.getValue().equals("swipe")) {
                                try {
                                    // 获取界面滑动时的初始坐标点和结束坐标点
                                    touchAction = UtilsParser.getSwipePoints(touchAction, st.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            tc.addStatementAtPosition(line, touchAction);
                        }

                    }
                }
            }
        }
    }

    private class PackageVisitor extends VoidVisitorAdapter<Object> {
        @Override
        public void visit(PackageDeclaration n, Object arg) {
            n.setName(new NameExpr(arg.toString()));
        }
    }

    private class ChangeMethodVisitor extends VoidVisitorAdapter<Object> {
        @Override
        public void visit(MethodDeclaration n, Object arg) {
            if (n.getAnnotations() != null && n.getAnnotations().get(0).getName().getName().equals("Test")) {
                BlockStmt newBlockStmt = new BlockStmt();

                for (Integer i : tc.getStatements().keySet()) {
                    ASTHelper.addStmt(newBlockStmt, new NameExpr(tc.getStatements().get(i).toString()));
                }
                n.setBody(newBlockStmt);
            }
        }
    }


}
