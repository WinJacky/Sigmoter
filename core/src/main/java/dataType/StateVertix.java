package main.java.dataType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StateVertix {
    private final String xmlFilePath;
    private List<EnhancedMobileElement> elesToBeClicked;

    public StateVertix(String filePath) {
        this.xmlFilePath = filePath;
        elesToBeClicked = new ArrayList<>();
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public List<EnhancedMobileElement> getElesToBeClicked() {
        return elesToBeClicked;
    }

    public void setElesToBeClicked(List<EnhancedMobileElement> elesToBeClicked) {
        this.elesToBeClicked = elesToBeClicked;
    }

    public void addElement(EnhancedMobileElement element) {
        this.elesToBeClicked.add(element);
    }

    // 状态节点比较逻辑：两个状态的层次布局文件内容完全一样
    // TODO：比较方法待改进
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StateVertix) {
            // 判断两个文件内容是否一样
            FileInputStream fis1 = null;
            FileInputStream fis2 = null;
            try {
                fis1 = new FileInputStream(xmlFilePath);
                fis2 = new FileInputStream(((StateVertix)obj).getXmlFilePath());

                int len1 = fis1.available();//返回总的字节数
                int len2 = fis2.available();

                if (len1 == len2) {//长度相同，则比较具体内容
                    //建立两个字节缓冲区
                    byte[] data1 = new byte[len1];
                    byte[] data2 = new byte[len2];

                    //分别将两个文件的内容读入缓冲区
                    fis1.read(data1);
                    fis2.read(data2);

                    //依次比较文件中的每一个字节
                    for (int i=0; i<len1; i++) {
                        //只要有一个字节不同，两个文件就不一样
                        if (data1[i] != data2[i]) {
                            return false;
                        }
                    }
                    return true;
                }
                //长度不一样，文件肯定不同
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {//关闭文件流，防止内存泄漏
                if (fis1 != null) {
                    try {
                        fis1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis2 != null) {
                    try {
                        fis2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
        return false;
    }
}
