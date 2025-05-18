public class Encryptor {
    public static String encrypt(String message, int key) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            encrypted.append((char)(c ^ key));
        }
        return encrypted.toString();
    }

    public static String decrypt(String encryptedMessage, int key) {
        return encrypt(encryptedMessage, key); // XOR is symmetric
    }
}
