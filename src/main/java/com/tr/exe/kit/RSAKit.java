package com.tr.exe.kit;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 工具类
 */
public class RSAKit {

    /**
     * RSA 公钥加密
     *
     * @param string    加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String string, String publicKey) {
        try {
            // base64 编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            // RSA 加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            String ciphertext = Base64.encodeBase64String(cipher.doFinal(string.getBytes("UTF-8")));
            return ciphertext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
