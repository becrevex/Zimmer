/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zimmer;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files.*;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.misc.IOUtils;
import static zimmer.FileProtect.archiveCBCFiles;
import static zimmer.FileProtect.copyFileUsingStream;
import static zimmer.FileProtect.decryptFile;
import static zimmer.FileProtect.decryptIndependentFile;
import static zimmer.FileProtect.encryptFile;
import static zimmer.FileProtect.encryptIndependentFile;
import static zimmer.FileProtect.generateNewCBC;
import static zimmer.FileProtect.convertBytesToHex;
import static zimmer.FileProtect.getIVFileHash;
import static zimmer.FileProtect.getSaltFileHash;

/**
 *
 * @author becatasu
 */
public class ZimmerController implements Initializable {
    @FXML private Button browseButton;
    @FXML private ChoiceBox operationChoice;
    @FXML private TextField phasePhraseField;
    @FXML private ChoiceBox cipherChoice;
    @FXML private TextField outputFileName;
    @FXML private Button goButton;
    @FXML private Label selectedFileLabel;
    @FXML private Label outputCompleteLabel;
    @FXML private MenuItem menuFileNew;
    @FXML private Label ivHashLabel;
    @FXML private Label saltHashLabel;
    @FXML private Button exportCBCButton;
    private Stage thisStage;
    private File currentFile;
    private FileInputStream fileStream;

    @FXML private void displayAboutAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About ");
        alert.setHeaderText("Zimmer File Encryption Application (ZFE) ");
        alert.setContentText("ZFE version 1.0 release 0x01 \nWritten by Brent 'becrevex' Chambers\nCygiene Solutions © 2020");
        alert.showAndWait();   
    }    
    
    
    @FXML private void clearAllFields() throws IOException {
        selectedFileLabel.setText(null);
        operationChoice.setValue(null);
        phasePhraseField.clear();
        cipherChoice.setValue(null);
        outputFileName.clear();
        outputCompleteLabel.setText(null);
        
        //zimmer.Zimmer.main(null);
        
        //outputFileName.setText("");
    }
    
    
    @FXML private void exitApplication() {
        Platform.exit();
    }      
    
    
    /***
    * Name: addNewDocument()
    * Description: Selects a local file for a profile pic
    * @param  - Action Event
    * @return - None
    */     
    public void addNewDocument(ActionEvent event) throws IOException {
        FileChooser chooser = new FileChooser();
        this.currentFile = chooser.showOpenDialog(thisStage);
        this.fileStream = new FileInputStream(this.currentFile);
        if (this.currentFile != null) {
            String filePath = this.currentFile.toURI().toString();
            String fileName = this.currentFile.getName();
            selectedFileLabel.setText(fileName);
        }
    }  
    

    
    
    /***
    * Name: addNewDocument()
    * Description: Selects a local file for a profile pic
    * @param  - Action Event
    * @return - None
    */     
    public void importNewSaltFile(ActionEvent event) throws IOException {
        // Select the new SaltFile
        FileChooser chooser = new FileChooser();
        File newSalt = chooser.showOpenDialog(thisStage);
        FileInputStream saltStream = new FileInputStream(newSalt);
        File targetFile = new File("salt.enc");

    }
 
    
    @FXML private void protect(ActionEvent event) throws Exception {
        if (operationChoice.getValue() == "Encrypt") {
            outputCompleteLabel.setText("Ewoncrypting target file...");
            outputCompleteLabel.setText(encryptFile(phasePhraseField.getText(), this.fileStream, outputFileName.getText()+".zim"));
            outputCompleteLabel.setText("File encryption complete.  Opening path...");
        } 
        if (operationChoice.getValue() == "Decrypt") {
            outputCompleteLabel.setText("Decrypting target file...");
            outputCompleteLabel.setText(decryptFile(phasePhraseField.getText(), this.fileStream, outputFileName.getText()));
            outputCompleteLabel.setText("File decryption complete.  Opening path...");
        }
        if (operationChoice.getValue() == "Decrypt Independent") {
            outputCompleteLabel.setText("Decrypting Independent file...");
            outputCompleteLabel.setText(decryptIndependentFile(phasePhraseField.getText(), this.fileStream, outputFileName.getText()));
            outputCompleteLabel.setText("Independent File encryption complete.  Opening path...");
        }        
        if (operationChoice.getValue() == "Encrypt Independent") {
            outputCompleteLabel.setText("Encrypting target file...");
            outputCompleteLabel.setText(encryptIndependentFile(phasePhraseField.getText(), this.fileStream, outputFileName.getText()+".zfe"));
            outputCompleteLabel.setText("Independent File encryption complete.  Opening path...");
        }
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        try {
            Desktop.getDesktop().open(new File(s));
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    
    
    @FXML private void generateNewCBC_call(ActionEvent event) throws Exception {
        generateNewCBC("supportfreeinfo");
    }
    
    @FXML private void archiveCBCFiles_call(ActionEvent event) {
        archiveCBCFiles();
        outputCompleteLabel.setText("Salt and IV files archived successfully.");
    }
    
    @FXML private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
    }
    
    @FXML public void newClearAllFields(ActionEvent event) {
        
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //this.thisStage.setTitle("ZFE version 1.1");
        operationChoice.setItems(FXCollections.observableArrayList("Encrypt", "Encrypt Independent", "Decrypt", "Decrypt Independent"));
        cipherChoice.setItems(FXCollections.observableArrayList("AES"));
        try {
            ivHashLabel.setText(convertBytesToHex(getIVFileHash()));
            saltHashLabel.setText(convertBytesToHex(getSaltFileHash()));
        } catch (IOException ex) {
            Logger.getLogger(ZimmerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ZimmerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        File dir = new File(s + "\\Archives");
        // Create archives directory if it doesn't exist.
        File engagementDir = new File(dir.getAbsolutePath());
        if (!engagementDir.exists()) {
            engagementDir.mkdir();
        } 
        
    }    
    
}
