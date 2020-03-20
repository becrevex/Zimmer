/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zimmer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.IOUtils;
/**
 *
 * @author becatasu
 */

public class FileProtect {
    
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    /***
    * Name: generateNewCBC()
    * Description: Generates a new Cipher Block with a provided passPhrase
    * @param  - String passPhase
    * @return - None
    */ 
    public static void generateNewCBC(String passPhrase) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("New CBC Salt and IV");
        alert.setHeaderText("Generate New CBC Encryption Elements?");
        alert.setContentText("Please confirm");

        byte[] salt = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        FileOutputStream saltOutFile = new FileOutputStream("salt.enc");
        saltOutFile.write(salt);
        saltOutFile.close();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, 65536, 256);
        SecretKey secretKey = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        ivOutFile.write(iv);
        ivOutFile.close();
    }
    
    /***
    * Name: copyFileUsingStream()
    * Description: Copies File source to File destination
    * @param  - File source, File dest
    * @return - None
    */      
    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }      
    
    public static byte[] convertToByte(File file) throws FileNotFoundException, IOException {
        byte[] bytesArray = new byte[(int) file.length()];
        FileInputStream out = new FileInputStream(file);
        out.read(bytesArray);
        out.close();
        return bytesArray;        
    }
    
    public static String convertBytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    

    public static byte[] readIVFromFile(File file) throws FileNotFoundException, IOException {
        return null;
        // load the ZFE and convert to bytes
        
        // access the 16 bytes at the start of the file
        
        // return the IV bytes
        
    }

    public static void getBytes(byte[] source, int srcBegin, int srcEnd, byte[] destination,
        int dstBegin) {
      System.arraycopy(source, srcBegin, destination, dstBegin, srcEnd - srcBegin);
    }
    
    public static byte[] subbytes(byte[] source, int srcBegin, int srcEnd) {
      byte destination[];

      destination = new byte[srcEnd - srcBegin];
      getBytes(source, srcBegin, srcEnd, destination, 0);

      return destination;
    }    
    
    public static byte[] subbytes(byte[] source, int srcBegin) {
      return subbytes(source, srcBegin, source.length);
    }    
    
    /***
    * Name: encapsulateFile()
    * Description: Save the current salt and inv to the Archives folder
    * @param  - Action Event
    * @return - None
    */  
    public static void encapsulateFile(FileInputStream inputFile){
 
    }
    

    /***
    * Name: archiveCBCFiles()()
    * Description: Save the current salt and inv to the Archives folder
    * @param  - Action Event
    * @return - None
    */     
    public static void archiveCBCFiles() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Path currentRelativePath = Paths.get("Archives");
        String s = currentRelativePath.toAbsolutePath().toString();
        File dir = new File(s + "\\" + timeStamp);
        File archiveDir = new File(dir.getAbsolutePath());
        if (!archiveDir.exists()) {
        archiveDir.mkdir();
        } 
        File currentSalt = new File("salt.enc");
        File currentIV = new File("iv.enc");
        File destSalt = new File(dir.getAbsolutePath()+"\\salt.enc");
        File destIV = new File(dir.getAbsolutePath()+"\\iv.enc");
        try {
            copyFileUsingStream(currentSalt, destSalt);
            copyFileUsingStream(currentIV, destIV);

        } catch (Exception e) {
            e.printStackTrace();    
        }
    }

    
    public static byte[] getIVFileHash() throws IOException, NoSuchAlgorithmException {
        byte[] ib = Files.readAllBytes(Paths.get("iv.enc"));
        byte[] iv_hash = MessageDigest.getInstance("MD5").digest(ib);
        return iv_hash;
    }
    
    public static byte[] getSaltFileHash() throws IOException, NoSuchAlgorithmException {        
        byte[] sb = Files.readAllBytes(Paths.get("salt.enc"));
        byte[] salt_hash = MessageDigest.getInstance("MD5").digest(sb);
        return salt_hash;
    }
    

    public static String encryptFile (String passPhrase, FileInputStream inputFile, String outputFile) throws Exception {
            FileInputStream inFile = inputFile;
            FileOutputStream outFile = new FileOutputStream(outputFile);
            String password = passPhrase;

            byte[] salt = new byte[8]; 
            byte[] iv = new byte[16];
            
            File saltcheck = new File("salt.enc");
            File IVcheck = new File("iv.enc");
            if ((saltcheck.isFile() && saltcheck.canRead()) && IVcheck.isFile() && IVcheck.canRead()) {
                //use the existing salt... don't judge me
                FileInputStream saltFis = new FileInputStream("salt.enc");
                //byte[] salt = new byte[8];
                saltFis.read(salt);
                saltFis.close();
                
                // use the existing IV... quit judging
                FileInputStream ivFis = new FileInputStream("iv.enc");
                //byte[] iv = new byte[16];
                ivFis.read(iv);
                ivFis.close();                
            } else {
                generateNewCBC(passPhrase);
                encryptFile (passPhrase, inputFile, outputFile);
            }
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey secretKey = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            ivOutFile.write(iv);
            ivOutFile.close();
            byte[] input = new byte[64];
            int bytesRead;
            while ((bytesRead = inFile.read(input)) != -1) {
                    byte[] output = cipher.update(input, 0, bytesRead);
                    if (output != null)
                            outFile.write(output);
            }
            byte[] output = cipher.doFinal();             //-->  previous method prior to independence implemented at write
            if (output != null)
                    outFile.write(output);

            String out = outFile.toString();
            inFile.close();
            outFile.flush();
            outFile.close();
            return out;
        }

    public static String encryptIndependentFile (String passPhrase, FileInputStream inputFile, String outputFile) throws Exception {
            FileInputStream inFile = inputFile;
            FileOutputStream outFile = new FileOutputStream(outputFile);
            String password = passPhrase;

            byte[] salt = new byte[8]; 
            byte[] iv = new byte[16];
            
            File saltcheck = new File("salt.enc");
            File IVcheck = new File("iv.enc");
            if ((saltcheck.isFile() && saltcheck.canRead()) && IVcheck.isFile() && IVcheck.canRead()) {
                //use the existing salt... don't judge me
                FileInputStream saltFis = new FileInputStream("salt.enc");
                //byte[] salt = new byte[8];
                saltFis.read(salt);
                saltFis.close();
                // use the existing IV... quit judging
                FileInputStream ivFis = new FileInputStream("iv.enc");
                //byte[] iv = new byte[16];
                ivFis.read(iv);
                ivFis.close();                
            } else {
                generateNewCBC(passPhrase);
                encryptFile (passPhrase, inputFile, outputFile);
            }
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey secretKey = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            ivOutFile.write(iv);
            ivOutFile.close();
            byte[] input = new byte[64];
            int bytesRead;
            while ((bytesRead = inFile.read(input)) != -1) {
                    byte[] output = cipher.update(input, 0, bytesRead);
                    if (output != null)
                            outFile.write(output);
            }

            byte[] ciphertext = cipher.doFinal();
            //Create the byte array for the independently encrypted file.  
            byte[] independentOut = new byte[iv.length + cipher.doFinal().length + salt.length];
            ByteBuffer buff = ByteBuffer.wrap(independentOut);
            
            //Get the file hashes
            byte[] ib = Files.readAllBytes(Paths.get("iv.enc"));
            byte[] iv_hash = MessageDigest.getInstance("MD5").digest(ib);
            byte[] sb = Files.readAllBytes(Paths.get("salt.enc"));
            byte[] salt_hash = MessageDigest.getInstance("MD5").digest(sb);

            //Retrive the encrypted goo
            //byte[] ciphertext = subbytes(buffer, 16, initialStream.available()-8);
            System.out.println("****************Encryption Details*******************");
            System.out.println("Salt File Checksum:         " + convertBytesToHex(salt_hash));
            System.out.println("IV File Checksum:           " + convertBytesToHex(iv_hash));            
            // Encrypt file with iv+cipher+salt, to include necessary encryption elements
            System.out.println("Loaded IV file:             " + convertBytesToHex(iv));
            buff.put(iv);               //            byte[] salt = new byte[8]; 
            //System.out.println("Ciphertext:            " + Arrays.toString(ciphertext));
            buff.put(ciphertext);

            System.out.println("Loaded Salt file:           " + convertBytesToHex(salt));
            buff.put(salt);             //            byte[] iv = new byte[16];
            System.out.println("Combined independent:       " + convertBytesToHex(iv) +  "(" +
                                                             Integer.toString(ciphertext.length) + ")" +
                                                             convertBytesToHex(salt));
            byte[] output = buff.array();   
            //byte[] output = cipher.doFinal();             -->  previous method prior to independence implemented at write
            if (output != null)
                    outFile.write(output);

            String out = outFile.toString();
            inFile.close();
            outFile.flush();
            outFile.close();
            return out;
        }
    

    public static String decryptFile(String passPhrase, FileInputStream inputFile, String outputFile) throws Exception {
            // how to get the salt byte from the file itself
            String password = passPhrase;
            FileInputStream saltFis = new FileInputStream("salt.enc");
            byte[] salt = new byte[8];
            saltFis.read(salt);
            saltFis.close();
            
            // reading the iv
            FileInputStream ivFis = new FileInputStream("iv.enc");
            byte[] iv = new byte[16];
            ivFis.read(iv);
            ivFis.close();

            SecretKeyFactory factory = SecretKeyFactory
                            .getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                            256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // file decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            FileInputStream fis = inputFile;
            FileOutputStream fos = new FileOutputStream(outputFile);
            byte[] in = new byte[64];
            int read;
            while ((read = fis.read(in)) != -1) {
                    byte[] output = cipher.update(in, 0, read);
                    if (output != null)
                            fos.write(output);
            }
            

            byte[] output = cipher.doFinal();
            if (output != null)
                    fos.write(output);
            String out = fos.toString();
            fis.close();
            fos.flush();
            fos.close();
            return out;
    }

    public static String decryptIndependentFile(String passPhrase, FileInputStream inputFile, String outputFile) throws Exception {
            //Convert inputfile to bytestream
            InputStream initialStream = inputFile;
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);
            
            // See if this carve action works
            byte[] ciphertext = Arrays.copyOfRange(buffer, 16, buffer.length-8);
            
            //Retrive the IV from bytestream
                //byte[] iv = new byte[16];
                //initialStream.read(iv);
            long starter = 0;
            inputFile.getChannel().position(starter);
            byte[] iv = new byte[16];
            inputFile.read(iv);              
            
            
            //Retrieve the Salt from the inputFile
            inputFile.getChannel().position(inputFile.getChannel().size() - 8);
            byte[] salt= new byte[8];
            inputFile.read(salt);    
            
            
            // Run checksums


            //Retrive the encrypted goo
            //byte[] ciphertext = subbytes(buffer, 16, initialStream.available()-8);
            System.out.println("****************Decryption Details*******************");
            System.out.println("IV File Checksum:         ");
            System.out.println("Salt File Checksum:       ");
            System.out.println("Original File Size:       " + Integer.toString(buffer.length));
            System.out.println("Carved Ciphertext Size:   " + ciphertext.length);            
            System.out.println("Retrieved Salt:           " + convertBytesToHex(salt));
            System.out.println("Retrieved IV:             " + convertBytesToHex(iv));
            System.out.println();
            System.out.println("Decrypted independent:    " + convertBytesToHex(iv) +  "( )" + convertBytesToHex(salt));            
            
            String password = passPhrase;
            /*
            FileInputStream saltFis = new FileInputStream("salt.enc");
            byte[] salt = new byte[8];
            saltFis.read(salt);
            saltFis.close();
            
            // reading the iv
            FileInputStream ivFis = new FileInputStream("iv.enc");
            byte[] iv = new byte[16];
            ivFis.read(iv);
            ivFis.close();
            
            FileInputStream inde = inputFile;
            byte[] iv_b = new byte[16];
            byte[] salt_b = new byte[8];
            convertBytesToHex(inde.read());
            
            
            inputFile.getChannel().position(inputFile.getChannel().size() - 8);
            byte[] salt_i= new byte[8];
            inputFile.read(salt);            

            // reading the iv
            //FileInputStream ivFis = new FileInputStream("iv.enc");
            //byte[] iv = new byte[16];
            //ivFis.read(iv);
            //ivFis.close();
            */

            SecretKeyFactory factory = SecretKeyFactory
                            .getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                            256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // file decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            
            // Stream the cipher text to decrypt it with the unpacked salt and IV
            InputStream cipherstream = new ByteArrayInputStream(buffer);
            FileOutputStream fos = new FileOutputStream(outputFile);
            byte[] in = new byte[16];
            int read;
            while ((read = cipherstream.read(in)) != -1) {
                    byte[] output = cipher.update(in, 0, read);
                    if (output != null)
                            fos.write(output);
            }
            
            byte[] output = null;
            output = cipher.doFinal();    
            if (output != null)
                    fos.write(output);
            String out = fos.toString();
            cipherstream.close();
            fos.flush();
            fos.close();
            return out;
    }


}
 