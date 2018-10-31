package com.hh.api.tools;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * RSA安全编码组件
 */
public class ToolRSA {
    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "RSA";
    private static final String CIPHER_ALGORITHM = "RSA";
    public static int KEY_SIZE=1024;

    /**
     * 创建公钥
     *
     * @param key 二进制密钥
     * @throws Exception
     */
    private static Key genPublicKey(byte[] key) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * 创建私钥
     *
     * @param key 二进制密钥
     * @throws Exception
     */
    private static Key genPrivateKey(byte[] key) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * 解密
     *
     * @param data 待解密字符串（经Base64编码后）
     * @param key  经过Base64编码后的密钥
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String key) {
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            byte[] res = decrypt(decoder.decode(data), decoder.decode(key));
            return new String(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = genPrivateKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    /**
     * 加密
     *
     * @param data 待加密原始字符串,字节长度只能为 KEY_SIZE/8-11 个字节
     * @param key  经过Base64编码后的密钥
     * @return (Base64编码)
     * @throws Exception
     */
    public static String encrypt(String data, String key){
        try {
            byte[] res = encrypt(data.getBytes(), Base64.getDecoder().decode(key.getBytes()));
            return Base64.getEncoder().encodeToString(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = genPublicKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    /**
     * 生成密钥 <br>
     *
     * @return byte[] 二进制密钥
     * @throws Exception
     */
    public static String[] initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String pubKey = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        String priKey = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
        return new String[]{pubKey, priKey};
    }

    public static void main(String[] args) {
        try {
            String[] keys = new String[2];//initKey();
            keys[0]="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUMc5EBbl0ZDj3jqPPSPDfFAa8Ce1i1njTuQMDFLB9+QVN4fwRQHmFRSMBTxQKwnFLExLiIASapfMG3g8dmCqCD90uf9jkG5vcBSu7fFOdurLZVn9zTeSolIdylvsU7eJzb1qQkf1CPDdUWSm7+zzd5kFIQDL4baHoINc3KXqGrwIDAQAB";
            keys[1]="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJQxzkQFuXRkOPeOo89I8N8UBrwJ7WLWeNO5AwMUsH35BU3h/BFAeYVFIwFPFArCcUsTEuIgBJql8wbeDx2YKoIP3S5/2OQbm9wFK7t8U526stlWf3NN5KiUh3KW+xTt4nNvWpCR/UI8N1RZKbv7PN3mQUhAMvhtoegg1zcpeoavAgMBAAECgYBI0YxkOlfdjF29rzZo5ZOOWh3RCiO+XFq01oVXXzQ2QkXL9k5Q/9h2lvlTbFMR0mevFylBObBpeT/I0RamWR4RV8JP4pcSgEaDPDqn40B8DR+sVPGjLH3S2JTbjF39Ot4j+004li7JDgqPFAloevvqY0gUjUG9N1YzymgdU2nHyQJBAN33H1SnEepLDs36WfudLq11ScV4BT0vhzJYZwxNLCtbE/Mq23QcOSGw4nTcAAP2ydrxdHn0dP2NFI7P5RgbZDsCQQCq6u9nLKk70ymdkX2AfLGTq33lzbD3wwYIpm5B9+IXYSsmSiYNlFgbmffUUja0Q3/rFJ7DlCqRqDHOx6ulucQdAkA//PbWQ5ehkGPDM5HvPF/xd2d9+tyfx+zUsbF7VWAPW6FDr642KdC6irCt+aIzglisuoDWi4eSdLJhfUNREMxzAkB0ZfsUcL3wGCbHQEYQMttcjfX3gtV6Zj69tVpxYrd+5JXWRbkUlmpKodi9qB/JNA7xseFNoKI9ACarhB/JhXGhAkBaUxlapdQU7j8PaRcCuAW6MAQcHgLY872rrrzPOrC68wx+K7qGWt3GS5iiguuH7REQtRETaN8uwz1Zn92pv37s";
            System.out.println("公钥(Base64)：" + keys[0]);
            System.out.println("私钥(Base64)：" + keys[1]);

            String src = "456123";
            System.out.println("len:"+src.getBytes().length);
            String res = encrypt(src, keys[0]);
            System.out.println("公钥加密后：" + res);
            String orgi = decrypt(res, keys[1]);
            System.out.println("私钥解密后：" + orgi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
