/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GenerateurTutoriel;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author fgourdea
 */
public class GenerateurTutoriel extends Application {    
    public List<String> textes = new ArrayList<String>();
    public List<String> nomPhotoOuVideos = new ArrayList<String>();
    public List<String> typeTags = new ArrayList<String>();
    public List<String> resteDocuments = new ArrayList<String>();
    
    private ImageView sea0;
    private ImageView quit;
    private Rectangle sea0Clip;
    
    private double sX = 0;
    private DoubleProperty coordXReal = new SimpleDoubleProperty(0);
    
    private double sY = 0;
    private DoubleProperty coordYReal = new SimpleDoubleProperty(0);
    
    private Button charger = new Button();
    private File sourceFile;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.TRANSPARENT);      
        
        charger();
        
        sea0 = new ImageView(new Image(GenerateurTutoriel.class.getResourceAsStream("images/Nintendo.png")));
        sea0Clip = new Rectangle(300, 220);        
        sea0Clip.setArcHeight(70);
        sea0Clip.setArcWidth(70);
        
        setDrag();        
        setQuit();
        
        sea0.setClip(sea0Clip);
        Pane root = new Pane();
        root.getChildren().addAll(sea0, charger, quit);
        
        Scene myScene = new Scene(root, 300, 250);
        myScene.setFill(null);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    private void charger() {
        ImageView monImage = new ImageView(new Image(GenerateurTutoriel.class.getResourceAsStream("images/glyphicons-202-upload.png")));
        charger.setText("Charger");
        charger.setLayoutX(125);
        charger.setLayoutY(115);
        charger.setGraphic(monImage);
        charger.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent t) {
                System.out.println("C'est chargé !");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisissez le fichier à parser");
                sourceFile = fileChooser.showOpenDialog(new Stage());
                
                if (sourceFile != null){
                    try {
                        List<String> fileContentAsLine = Files.readAllLines(sourceFile.toPath(), StandardCharsets.UTF_8);
                        StringBuffer bf = new StringBuffer();
                        for (String line : fileContentAsLine) {
                            bf.append(line);
                        }
                        String fileContent = bf.toString();
                        parseDocument(fileContent);
                    }
                    catch (IOException ex) {
                        System.err.println("Exception when reading the file " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void setDrag() {
        sea0.setOnMousePressed(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent t) {
                sX = t.getSceneX() - coordXReal.getValue();
                sY = t.getSceneY() - coordYReal.getValue();
            }
        });
        
        sea0.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                coordXReal.set(t.getSceneX() - sX);
                coordYReal.set(t.getSceneY() - sY);
            }
        });
        
        sea0.xProperty().bind(coordXReal);
        sea0.yProperty().bind(coordYReal);
    }

    private void setQuit() {
        quit = new ImageView(new Image(GenerateurTutoriel.class.getResourceAsStream("images/glyphicons-193-circle-remove.png")));
        quit.setFitHeight(25);
        quit.setFitWidth(25);
        quit.setX(270);
        quit.setY(10);        
        
        quit.setOnMouseClicked(new EventHandler<MouseEvent>() {
           
            @Override
            public void handle(MouseEvent t) {
                System.exit(0);
            }        
        });
    }
    
    public void parseDocument(String fileContent) {      
        while (!fileContent.equals("")) {
            fileContent = parseItem(fileContent);		
	}
    }
    
    private String parseItem(String remainingDocumentValue) {
        int posPhoto = remainingDocumentValue.indexOf("<photo>");
        int posVideo = remainingDocumentValue.indexOf("<video>");
        
	if (posPhoto > -1 && (posVideo == -1 || posPhoto < posVideo)) {
                System.out.println("Photo found");
		int posDeb = remainingDocumentValue.indexOf("<photo>")+7;
		int posFin = remainingDocumentValue.indexOf("</photo>");	
		String nomPhoto = remainingDocumentValue.substring(posDeb, posFin);
		String texte = remainingDocumentValue.substring(0, posDeb-7);
		callBuilder(texte, nomPhoto,remainingDocumentValue.substring(posDeb-7, posDeb));
                String finDocument = remainingDocumentValue.substring(posFin+8,remainingDocumentValue.length());
                resteDocuments.add(finDocument);
		return(finDocument);
	}
	else if (posVideo > -1 && (posPhoto == -1 || posVideo < posPhoto)) {
                System.out.println("Video found");
		int posDeb = remainingDocumentValue.indexOf("<video>")+7;
		int posFin = remainingDocumentValue.indexOf("</video>");
		String nomVideo = remainingDocumentValue.substring(posDeb, posFin);
		String texte = remainingDocumentValue.substring(0, posDeb-7);
		callBuilder(texte, nomVideo,remainingDocumentValue.substring(posDeb-7, posDeb));
                String finDocument = remainingDocumentValue.substring(posFin+8,remainingDocumentValue.length());
                resteDocuments.add(finDocument);
		return(finDocument);
	}
	else {
                System.out.println("No tag found");
		String texte = remainingDocumentValue.substring(0, remainingDocumentValue.length());
		callBuilder(texte, "", "");
                resteDocuments.add("");
		return("");
	}
    }
    
    private void callBuilder(String texte, String nomPhotoOuVideo, String typeTag) {        
            textes.add(texte);
            nomPhotoOuVideos.add(nomPhotoOuVideo);
            typeTags.add(typeTag);
            
            String response;

            System.out.println("Le callBuilder marche --> texte : " + texte + ", chemin : " + nomPhotoOuVideo + ", tag : " + typeTag);

        try {
            response = sendPost(texte, nomPhotoOuVideo, typeTag);
            System.out.println(response);
            } 
            
        catch (Exception ex) {
            Logger.getLogger(GenerateurTutoriel.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }

    }

//    // HTTP GET request
//    public void sendGet() throws Exception { 
//        String url = "http://localhost/TutoSQLite/ok.php";
//
//        URL obj = new URL(url);
//        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//        // optional default is GET
//        con.setRequestMethod("GET");
//
//        int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//        }
//        in.close();
//
//        //print result
//        System.out.println(response.toString()); 
//    }
        
    public String sendPost(String texte, String nomPhotoOuVideo, String typeTag) throws Exception {
        String url = "http://localhost/TutoSQLite/ok.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        StringBuffer urlParameters = new StringBuffer();
        urlParameters.append("texte = ").append(URLEncoder.encode(texte));
        urlParameters.append(" & mediaContent = ").append(URLEncoder.encode(getBytesAsBase64(getFileContentAsByteArray(nomPhotoOuVideo))));
        urlParameters.append(" & type = ").append(URLEncoder.encode(typeTag));
        
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        
        con.setDoOutput(true);
        DataOutputStream writer = new DataOutputStream(con.getOutputStream());
        writer.writeBytes(urlParameters.toString());
        writer.flush();
        writer.close();
        
        /*
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        */
        
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }
    
    private byte[] getFileContentAsByteArray(String path) {
        File file = new File(path);
        byte[] bytes = new byte[(int)file.length()];
        
        FileInputStream fileToConvert = new FileInputStream(file);
        fileToConvert.read(bytes);
        fileToConvert.close();
        return bytes;
    }
        
    private String getBytesAsBase64(byte[] bytes){
        Base64.Encoder encoder = new Base64.Encoder();
        return encoder.encode(bytes);
    }
    
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}