package test.java;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/6 17:00
 */
public class TempTest {
    public static void main(String[] args) {
        String tt = "[27,349][170,506]";
        String[] as = tt.substring(1).split("[,\\[\\]]+");

        System.out.println(as.length);
        for (String s : as) {
            System.out.println(s);
            if(s.equals("")){
                System.out.print(true);
            }
        }
    }
}
