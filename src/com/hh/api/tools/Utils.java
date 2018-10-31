package com.hh.api.tools;

import java.util.Random;

/**
 * Created by Administrator on 2016/12/6.
 */
public class Utils {
    private static final char[] confoundLetters = {'I', '1', 'O', '0'};

    /**
     * 创建指定长度的随机码
     * <p>
     * ASCII 0-9:48-57  A-Z:65-90
     *
     * @param length         长度
     * @param ignoreConfound 是否去掉易混淆字符
     * @return
     */
    public static String createRandomCode(int length, boolean ignoreConfound) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < length; ) {
            int s = rand.nextInt(43) + 48;//生成48到91之间的数
            if (s > 57 && s <= 60) {
                s = s - rand.nextInt(8) - 3;
            } else if (s > 60 && s < 65) {
                s = s + rand.nextInt(22) + 4;
            }
            char lt = (char) s;
            boolean isConfound = false;
            if (ignoreConfound) {
                for (char confoundLetter : confoundLetters) {
                    if (lt == confoundLetter) {
                        isConfound = true;
                        break;
                    }
                }
            }
            if (!isConfound) {
                sb.append((char) s);
                i++;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        String code = createRandomCode(100000, true);
        long t2 = System.currentTimeMillis();
        System.out.println(code);
        System.out.println("\n" + (t2 - t1));
    }
}
