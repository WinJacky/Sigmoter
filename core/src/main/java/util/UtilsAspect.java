package main.java.util;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/30 10:36
 */

import io.appium.java_client.MobileElement;
import main.java.dataType.KeyText;
import main.java.dataType.ViewTreeInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static main.java.util.UtilsParser.gson;

/**
 * This class refers to the class in Vista
 */
public class UtilsAspect {

    private static Logger logger = LoggerFactory.getLogger(UtilsAspect.class);

    /**
     * 创建文件夹
     * @param path
     */
    public static void createTestFolder(String path) {
        File theDir = new File(path);
        if(!theDir.exists()){
            boolean result = theDir.mkdirs();
            if(result){
                logger.info("Directory is created!");
            } else {
                logger.info("Directory failed to create!");
            }
        }
    }

    /**
     * 保存目标元素的视图树信息
     * @param me
     * @param viewTreeInfoJsonFile
     */
    public static void saveViewTreeInformation(MobileElement me, String hierarchyLayoutXmlFile, String viewTreeInfoJsonFile) {
        ViewTreeInfo mobileEleWithViewTreeInfo = new ViewTreeInfo(me, hierarchyLayoutXmlFile);

        try {
            FileUtils.writeStringToFile(new File(viewTreeInfoJsonFile), gson.toJson(mobileEleWithViewTreeInfo, ViewTreeInfo.class), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取目标元素包裹的text内容，作为关键词进行保存
     * @param joinPoint
     * @return
     */
    public static String getKeyText(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String textualContent = "";
        // 如果是点击事件
        // 则可能是跳转、弹出事件（对话框、表单、目录等）、表单提交
        // 点击事件是关键事件
        if (methodName.equals("click")) {
            MobileElement me = (MobileElement) joinPoint.getTarget();
            textualContent = me.getAttribute("text");
            if (StringUtils.isBlank(textualContent)) {
                textualContent = me.getAttribute("contentDescription");
                if (StringUtils.isBlank(textualContent) && joinPoint.getStaticPart().toString().contains("findElementById")) {
                    textualContent = me.getAttribute("resourceId");
                    if (StringUtils.isNotBlank(textualContent) && textualContent.contains(":id/")) {
                            textualContent = textualContent.substring(textualContent.indexOf("/") + 1);
                    }
                }
            }
        }

        return StringUtils.isBlank(textualContent) ? "" : textualContent.trim();
    }

    public static void saveKeyTextInfo(List<KeyText> keyTextList, String keyTextInfoJsonFile) {
        try {
            FileUtils.writeStringToFile(new File(keyTextInfoJsonFile), gson.toJson(keyTextList, List.class), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
