package podcast.models.utils;

import podcast.utils.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class Crypto {

  /* Singletons */
  private static Cipher encryptionCipher = null;
  private static Cipher decryptionCipher = null;
  /* ^ helps these */
  private static Key aesKey = new SecretKeySpec(Constants.SECRET_KEY.getBytes(), "AES");

  /**
   * Cipher singleton getter helper (parameterized by mode, ENCYPT_MODE
   * or DECRYPT_MODE)
   * @param mode - int
   * @return - Cipher
   */
  private static Cipher getCipher(Cipher cipher, int mode) {
    if (cipher == null) {
      try {
        Cipher newCipher = Cipher.getInstance("AES");
        newCipher.init(mode, Crypto.aesKey);
        return newCipher;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return cipher;
    }
  }

  /**
   * Cipher singleton getter for encryption cipher
   * @return - Cipher
   */
  private static Cipher getEncryptionCipher () {
    Crypto.encryptionCipher = Crypto.getCipher(Crypto.encryptionCipher, Cipher.ENCRYPT_MODE);
    return Crypto.encryptionCipher;
  }

  /**
   * Cipher singleton getter for decryption cipher
   * @return - Cipher
   */
  private static Cipher getDecryptionCipher () {
    Crypto.decryptionCipher = Crypto.getCipher(Crypto.decryptionCipher, Cipher.DECRYPT_MODE);
    return Crypto.decryptionCipher;
  }

  /**
   * Encrypt
   * @param s - String
   * @return - String
   */
  public static String encrypt(String s) {
    try {
      byte[] encrypted = getEncryptionCipher().doFinal(s.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Decrypt
   * @param s - String
   * @return - String
   */
  public static String decrypt(String s) {
    try {
      byte[] base64crypt = Base64.getDecoder().decode(s);
      return new String(getDecryptionCipher().doFinal(base64crypt));
    } catch (Exception e) {
      return null;
    }
  }

}