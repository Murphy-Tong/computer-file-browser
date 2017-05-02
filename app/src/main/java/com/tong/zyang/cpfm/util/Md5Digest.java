package com.tong.zyang.cpfm.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by TONG on 2017/2/6.
 */
public class Md5Digest {

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private Object salt;
    private String algorithm;

    public Md5Digest(Object salt, String algorithm) {
        this.salt = salt;
        this.algorithm = algorithm;
    }

    public String encode(String pwd) {
        String result = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            result = byteArrayToHex(messageDigest.digest(mergePwdAndSalt(pwd).getBytes("GBK")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String byteArrayToHex(byte[] b) {
        if (b == null) return null;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            stringBuffer.append(byteToHex(b[i]));
        }
        return stringBuffer.toString();
    }

    private static String byteToHex(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public boolean isPwdVaild(String encPass, String scrPwd) {
        String p1 = "" + encPass;
        String p2 = encode(scrPwd);
        return p1.equals(p2);
    }

    private String mergePwdAndSalt(String pwd) {
        if (pwd == null) pwd = "";

        if (salt == null || "".equals(salt)) {
            return pwd;
        }
        return pwd + "{" + salt.toString() + "}";
    }

}
