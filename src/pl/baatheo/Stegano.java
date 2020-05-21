package pl.baatheo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.pow;

public class Stegano {

    private BufferedImage imageIn;
    private BufferedImage imageOut;
    private String messageToEncrypt;
    private String messageInBytes;
    private String imageName;
    private int width;
    private int heigth;
    private String encryptedMessage;
    private String binaryEncryptedMessage;
    private int lengthInBytes;
    private int length;

    public Stegano( String imageName, String messageToEncrypt, int length) throws IOException {
        this.imageIn = readImage(imageName);
        this.imageOut = readImage(imageName);
        this.messageToEncrypt = messageToEncrypt;
        this.messageInBytes = getMessageInBytes(messageToEncrypt);
        this.imageName = imageName;
        this.width = this.imageIn.getWidth();
        this.heigth = this.imageIn.getHeight();
        this.encryptedMessage = "";
        this.binaryEncryptedMessage = "";
        this.lengthInBytes= length;
        this.length = 123235346;
    }

    public String getMessageInBytes() {
        return messageInBytes;
    }

    private char binaryToChar(String binary){
        int ascii = Integer.parseInt(binary, 2 );
        char charToReturn = (char)ascii;
        return charToReturn;
    }

    private String getBinaryFromAscii(int ascii, int numberOfPlaces){
        String asciiBinary = Integer.toBinaryString(ascii);
        if(asciiBinary.length()!=8){
            int bytesToFill = 8 - asciiBinary.length();
            String correctAsciiBinary = "";
            for(int j=0; j<bytesToFill; j++){
                correctAsciiBinary+="0";
            }
            correctAsciiBinary += asciiBinary;
            asciiBinary = correctAsciiBinary;
        }
        return asciiBinary;
    }

    public String fillWithZero(String message, int numberOfPlaces){
        if(message.length()!=8){
            int bytesToFill = 8 - message.length();
            String tempMessage="";
            for(int i=0; i<bytesToFill; i++){
                tempMessage+="0";
            }
            tempMessage+=message;
            message=tempMessage;
        }
        return message;
    }

    private String getLastchars(int[] rgbValues){
        int red = rgbValues[0];
        int green = rgbValues[1];
        int blue = rgbValues[2];
        //System.out.println(rgbValues[0] + " " + rgbValues[1] + " " + rgbValues[2]);
        String binaryRed = fillWithZero(Integer.toBinaryString(red),8);
        String binaryGreen = fillWithZero(Integer.toBinaryString(green),8);
        String binaryBlue = fillWithZero(Integer.toBinaryString(blue),8);
        //System.out.println(binaryRed + " " + binaryGreen + " " + binaryBlue);
        char tempRed = binaryRed.charAt(7);
        char tempGreen = binaryGreen.charAt(7);
        char tempBlue = binaryBlue.charAt(7);

        String partOfBinaryMessage = Character.toString(tempRed) + Character.toString(tempGreen) + Character.toString(tempBlue);
        //System.out.println(partOfBinaryMessage);
        return partOfBinaryMessage;
    }

    public int[] getRgbValuesIn(int x, int y){
        int pixel = this.imageIn.getRGB(x,y);
        int[] rgbValues = new int[3];
        rgbValues[0] = (pixel>>16) & 0xff;
        rgbValues[1] = (pixel>>8) & 0xff;
        rgbValues[2] = (pixel) & 0xff;
        return rgbValues;
    }

    public int[] getRgbValuesOut(int x, int y){
        int pixel = this.imageOut.getRGB(x,y);
        int[] rgbValues = new int[3];
        rgbValues[0] = (pixel>>16) & 0xff;
        rgbValues[1] = (pixel>>8) & 0xff;
        rgbValues[2] = (pixel) & 0xff;
        return rgbValues;
    }

    public int[] changePixelValues(int[] rgbValues, String partOfMessage){
        int red = rgbValues[0];
        int green = rgbValues[1];
        int blue = rgbValues[2];

        String binaryRed = fillWithZero(Integer.toBinaryString(red),8);
        String binaryGreen = fillWithZero(Integer.toBinaryString(green),8);
        String binaryBlue = fillWithZero(Integer.toBinaryString(blue),8);

        String newRed = binaryRed.substring(0,7) + partOfMessage.charAt(0);
        String newGreen = binaryGreen.substring(0,7) + partOfMessage.charAt(1);
        String newBlue = binaryBlue.substring(0,7) + partOfMessage.charAt(2);

        int[] newValues = new int[3];
        newValues[0] = Integer.parseInt(newRed, 2);
        newValues[1] = Integer.parseInt(newGreen, 2);
        newValues[2] = Integer.parseInt(newBlue, 2);

        return newValues;

    }

    private BufferedImage readImage(String imageName) throws IOException {
        File imageFile = new File(imageName);
        BufferedImage image = ImageIO.read(imageFile);
        return image;
    }

    public void displayOriginalImage(){
        ImageIcon icon1 = new ImageIcon(this.imageIn);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(2 * this.imageIn.getWidth() + 50, this.imageIn.getHeight() + 50);
        frame.setTitle(this.imageName);
        JLabel label1 = new JLabel();
        label1.setIcon(icon1);
        frame.add(label1);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void displayEncryptImage(){
        ImageIcon icon1 = new ImageIcon(this.imageOut);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(2 * this.imageOut.getWidth() + 50, this.imageOut.getHeight() + 50);
        frame.setTitle(this.imageName);
        JLabel label1 = new JLabel();
        label1.setIcon(icon1);
        frame.add(label1);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private String getMessageInBytes(String messageToEncrypt) {
        int messageLength = messageToEncrypt.length();
        String messageInBytesWithoutLength="";
        for(int i=0; i<messageLength; i++){
            int ascii = (char)messageToEncrypt.charAt(i);
            messageInBytesWithoutLength+=getBinaryFromAscii(ascii, 8);
            messageInBytesWithoutLength+="0";
        }
        int messageLengthWithoutLength =  messageInBytesWithoutLength.length();
        int fill = Integer.toBinaryString(messageLengthWithoutLength).length() % 3;
        String messageInBytes= "";
        if (fill==2) messageInBytes+="0";
        else if (fill==1) messageInBytes+="00";
        messageInBytes +=Integer.toBinaryString(messageLengthWithoutLength);
        messageInBytes += messageInBytesWithoutLength;
        return messageInBytes;
    }


    public void encrypt(){
        int counter=0;
        //System.out.println(this.messageInBytes);
        //System.out.println(this.messageInBytes.length());
        for(int i=0; i<this.heigth; i++){
            for(int j=0; j<this.width; j++){
                if(counter==this.messageInBytes.length()) break;
                int[] rgbValues = changePixelValues(getRgbValuesIn(i,j), this.messageInBytes.substring(counter,counter+3));
                int rgb = (rgbValues[0]<<16) + (rgbValues[1]<<8) + rgbValues[2];
                this.imageOut.setRGB(i,j,rgb);
                //System.out.println(getLastchars(getRgbValuesOut(i,j)));
                counter+=3;
            }
        }
        //System.out.println("udalo sie zaszyfrowac");
    }

    public void decrypt(){
        int counter = 0;
        int counterOfSize = 0;
        boolean checked = false;
        for(int i=0; i<this.heigth; i++){
            for(int j=0; j<this.width; j++){
                if(counter==this.length+3) break;
                this.binaryEncryptedMessage += getLastchars(getRgbValuesOut(i,j));
                if(this.binaryEncryptedMessage.length()>=this.lengthInBytes && !checked){
                    this.length = Integer.parseInt(this.binaryEncryptedMessage.substring(0,9), 2);
                    checked = true;
                    counter=0;
                }
                counter+=3;
            }
        }
        this.binaryEncryptedMessage = this.binaryEncryptedMessage.substring(9);
        for(int i=0; i<this.binaryEncryptedMessage.length(); i+=9){
            char temp = binaryToChar(this.binaryEncryptedMessage.substring(i, i+8));
            this.encryptedMessage += Character.toString(temp);
        }
        System.out.println(this.encryptedMessage);
    }



}
