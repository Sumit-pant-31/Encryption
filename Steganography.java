import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Steganography {

    public static void embedText(String message, BufferedImage image, String outputPath) throws IOException {
        byte[] msgBytes = message.getBytes();
        int msgLength = msgBytes.length;

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        if ((msgLength * 8) + 32 > pixels.length * 3) {
            throw new IllegalArgumentException("Message too long to hide in this image.");
        }

        int bitIndex = 0;

        // First 32 bits: message length
        for (int i = 0; i < 32; i++) {
            int bit = (msgLength >> (31 - i)) & 1;
            pixels[bitIndex / 3] = setBit(pixels[bitIndex / 3], bitIndex % 3, bit);
            bitIndex++;
        }

        // Then the message bits
        for (byte b : msgBytes) {
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                pixels[bitIndex / 3] = setBit(pixels[bitIndex / 3], bitIndex % 3, bit);
                bitIndex++;
            }
        }

        image.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ImageIO.write(image, "png", new File(outputPath));
    }

    private static int setBit(int rgb, int colorIndex, int bit) {
        int shift = 16 - (colorIndex * 8);
        int mask = ~(1);
        int color = (rgb >> shift) & 0xFF;
        color = (color & mask) | bit;
        rgb &= ~(0xFF << shift);
        rgb |= (color << shift);
        return rgb;
    }

    public static String extractText(BufferedImage image) {
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        int bitIndex = 0;

        // First 32 bits = length
        int msgLength = 0;
        for (int i = 0; i < 32; i++) {
            int bit = getBit(pixels[bitIndex / 3], bitIndex % 3);
            msgLength = (msgLength << 1) | bit;
            bitIndex++;
        }

        byte[] msgBytes = new byte[msgLength];
        for (int i = 0; i < msgLength; i++) {
            int b = 0;
            for (int j = 0; j < 8; j++) {
                int bit = getBit(pixels[bitIndex / 3], bitIndex % 3);
                b = (b << 1) | bit;
                bitIndex++;
            }
            msgBytes[i] = (byte) b;
        }

        return new String(msgBytes);
    }

    private static int getBit(int rgb, int colorIndex) {
        int shift = 16 - (colorIndex * 8);
        return (rgb >> shift) & 1;
    }
}
