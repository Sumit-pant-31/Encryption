import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
public class ImageStegoGUI extends JFrame 
{
    private JTextField messageField;
    private JTextField keyField;
    private JLabel imageLabel;
    private BufferedImage loadedImage;
    public ImageStegoGUI() 
    {
        setTitle("Image Steganography Encryptor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        topPanel.add(new JLabel("Message:"));
        messageField = new JTextField();
        topPanel.add(messageField);
        topPanel.add(new JLabel("Key (int):"));
        keyField = new JTextField();
        topPanel.add(keyField);
        JButton loadButton = new JButton("Load Image");
        loadButton.addActionListener(e -> loadImage());
        topPanel.add(loadButton);
        JButton saveButton = new JButton("Encrypt & Embed");
        saveButton.addActionListener(e -> encryptAndEmbed());
        topPanel.add(saveButton);
        add(topPanel, BorderLayout.NORTH);
        imageLabel = new JLabel("Image Preview", JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);
        JButton extractButton = new JButton("Extract & Decrypt");
        extractButton.addActionListener(e -> extractAndDecrypt());
        add(extractButton, BorderLayout.SOUTH);
        setVisible(true);
    }
    private void loadImage() 
    {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
        {
            try 
            {
                loadedImage = ImageIO.read(chooser.getSelectedFile());
                imageLabel.setIcon(new ImageIcon(loadedImage.getScaledInstance(300, -1, Image.SCALE_SMOOTH)));
            } 
            catch (Exception ex) 
            {
                showMessage("Failed to load image.");
            }
        }
    }
    private void encryptAndEmbed() 
    {
        if (loadedImage == null) 
        {
            showMessage("Load an image first.");
            return;
        }
        String message = messageField.getText();
        int key;
        try 
        {
            key = Integer.parseInt(keyField.getText());
        } 
        catch (NumberFormatException ex) 
        {
            showMessage("Invalid key. Use an integer.");
            return;
        }
        String encrypted = Encryptor.encrypt(message, key);
        try 
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Encrypted Image");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
            {
                Steganography.embedText(encrypted, loadedImage, chooser.getSelectedFile().getAbsolutePath() + ".png");
                showMessage("Message encrypted and embedded successfully!");
            }
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
            showMessage("Error embedding message.");
        }
    }
    private void extractAndDecrypt() 
    {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
        {
            try 
            {
                BufferedImage image = ImageIO.read(chooser.getSelectedFile());
                String extracted = Steganography.extractText(image);
                int key = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter decryption key (int):"));
                String decrypted = Encryptor.decrypt(extracted, key);
                showMessage("Decrypted Message: " + decrypted);
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
                showMessage("Failed to extract or decrypt.");
            }
        }
    }
    private void showMessage(String msg) 
    {
        JOptionPane.showMessageDialog(this, msg);
    }
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new ImageStegoGUI());
    }
}