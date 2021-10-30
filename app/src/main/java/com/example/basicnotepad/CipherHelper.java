package com.example.basicnotepad;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper class that encrypts and decrypts bytes. Generates random key of 256 bytes by calling
 * function 6500 times, using a 32 byte Salt.
 */
public class CipherHelper {

    /** It should be 10000 https://cryptosense.com/blog/parameter-choice-for-pbkdf2 */
    private static final int PBKDF2_ITERATION_COUNT = 10000;

    /** It should at least be 16 bytes long https://hexdocs.pm/pbkdf2_elixir/Pbkdf2.html */
    private static final int PBKDF2_SALT_LENGTH = 16;

    /** Recommended AES key length
     * https://www.ibm.com/docs/hr/sgklm/4.1?topic=overview-cryptographic-algorithm-key-length */
    private static final int AES_KEY_LENGTH = 256;

    /** It should be 12 bytes https://developer.mozilla.org/en-US/docs/Web/API/AesGcmParams */
    private static final int INITIALIZATION_VECTOR_LENGTH = 12;

    /** Recommended GCM tag length */
    private static final int TAG_LENGTH = 64;

    /**
     * Helper class that encrypts and decrypts bytes.
     */
    private CipherHelper() {
    }

    /**
     * Encrypts data with given file name and password.
     * @param data The data.
     * @param password The password.
     * @return The encrypted data.
     */
    public static byte[] encrypt(byte[] data, String password) {

        try {

            // Get the Secure Random number generator
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();

            // Get the algorithm for the hash
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            // Create the byte array of salt and generate a random value for salt
            byte[] salt = new byte[PBKDF2_SALT_LENGTH];
            secureRandom.nextBytes(salt);

            // The Keyspec specifications for hashing
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, AES_KEY_LENGTH);

            // Generate the hash using the specifications above
            byte[] secret = factory.generateSecret(keySpec).getEncoded();

            // Use the secret hash generated above as the key for the encryption algorithm
            SecretKey key = new SecretKeySpec(secret, "AES");

            // Get the instance of the encryption algorithm
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Create the byte array of initializationVector and generate a random value for initializationVector
            byte[] initializationVector = new byte[INITIALIZATION_VECTOR_LENGTH];
            secureRandom.nextBytes(initializationVector);

            // Create the specification using GCM and initializationVector
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, initializationVector);

            // Set up the encryption using the hash key and the GCM specification
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

            // Encrypt the input data
            byte[] encrypted = cipher.doFinal(data);

            // Create a byteBuffer for the byte array and create enough space for salt, initializationVector, and encrypted
            ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + initializationVector.length + encrypted.length);

            // Place salt, initializationVector, and encrypted into the byte buffer
            byteBuffer.put(salt);
            byteBuffer.put(initializationVector);
            byteBuffer.put(encrypted);

            // Return the byte array from the byte buffer
            return byteBuffer.array();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypt data using password.
     * @param data The data.
     * @param password The password to use to decrypt.
     */
    public static byte[] decrypt(byte[] data, String password) {

        try {

            // Wrap the byte array of data into a byte buffer
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);

            // Extract the byte array of salt from the byte buffer
            byte[] salt = new byte[PBKDF2_SALT_LENGTH];
            byteBuffer.get(salt);

            // Extract the byte array of initializationVector from the byte buffer
            byte[] initializationVector = new byte[INITIALIZATION_VECTOR_LENGTH];
            byteBuffer.get(initializationVector);

            // Extract the remaining byte array from the byte buffer into cypherBytes array
            byte[] cipherBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherBytes);

            // Get the algorithm for the hash
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            // The Keyspec specifications for hashing
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, AES_KEY_LENGTH);

            // Generate the hash using the specifications above
            byte[] secret = factory.generateSecret(keySpec).getEncoded();

            // Use the secret hash generated above as the key for the encryption algorithm
            SecretKey key = new SecretKeySpec(secret, "AES");

            // Get the instance of the encryption algorithm
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Create the specification using GCM and initializationVector
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, initializationVector);

            // Set up the decryption using the hash key and the GCM specification
            cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

            // Return the decrypted byte array of cipherBytes
            return cipher.doFinal(cipherBytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
