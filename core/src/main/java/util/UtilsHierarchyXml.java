package main.java.util;

import com.android.ddmlib.IDevice;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/12/31 15:43
 */
public class UtilsHierarchyXml {

    private static final String UIAUTOMATOR = "/system/bin/uiautomator";
    private static final String UIAUTOMATOR_DUMP_COMMAND = "dump";
    private static final String UIDUMP_DEVICE_PATH = "/data/local/tmp/uidump.xml";
    private static final int XML_CAPTURE_TIMEOUT_SEC = 40;


    /**
     * @return 通过调试桥获取当前安卓设备
     */
//    public static IDevice pickDevice() {
//        return DebugBridge.getDevices().get(0);
//    }
//
//    /**
//     * 获取当前Android APP界面 XML 层次布局文件
//     * @param filePath 文件存放路径
//     */
//    public static void getHierarchyXmlFile(String filePath) {
//        final IDevice device = pickDevice();
//
//        takeXmlSnapshot(device, filePath);
//
//    }
//
//    public static void takeXmlSnapshot(IDevice device, String filePath) {
//
//    }


}
