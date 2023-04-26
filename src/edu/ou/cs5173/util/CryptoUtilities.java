package edu.ou.cs5173.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtilities {
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * The 'key' we've usually been thinking of in the messaging protocol is more accurately
     * a password that we use to generate the key that is used in the AES-256 encryption.
     * This method actually generates a {@link SecretKey} that is used to encrypt and
     * decrypt messages.
     *
     * @param password the password/psuedokey from the {@link User} class
     * @param salt a random salt
     * @return the secret key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        return secret;
    }

    public static byte[] generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static IvParameterSpec ivFromBytes(byte[] iv) {
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String plaintext, SecretKey key, String iv) throws Exception {
        IvParameterSpec spec = ivFromBytes(Base64.getDecoder().decode(iv));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public static String decrypt(String ciphertext, SecretKey key, String iv) throws Exception {
        IvParameterSpec spec = ivFromBytes(Base64.getDecoder().decode(iv));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(plaintext);
    }
}
