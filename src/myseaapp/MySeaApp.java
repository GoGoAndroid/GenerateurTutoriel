/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myseaapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
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
public class MySeaApp extends Application {
    
    public List<String> textes = new ArrayList<String>();
    public List<String> nomPhotoOuVideos = new ArrayList<String>();
    public List<String> typeTags = new ArrayList<String>();
    
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
        
        sea0 = new ImageView(new Image(MySeaApp.class.getResourceAsStream("images/Nintendo.png")));
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
        ImageView monImage = new ImageView(new Image(MySeaApp.class.getResourceAsStream("images/glyphicons-202-upload.png")));
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
        quit = new ImageView(new Image(MySeaApp.class.getResourceAsStream("images/glyphicons-193-circle-remove.png")));
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
	if (remainingDocumentValue.indexOf("<photo>") > -1) {
		int posDeb = remainingDocumentValue.indexOf("<photo>")+7;
		int posFin = remainingDocumentValue.indexOf("</photo>");	
		String nomPhoto = remainingDocumentValue.substring(posDeb, posFin);
		String texte = remainingDocumentValue.substring(0, posDeb-7);
		callBuilder(texte, nomPhoto,remainingDocumentValue.substring(posDeb-7, posDeb));
		return(remainingDocumentValue.substring(posFin+8,remainingDocumentValue.length()));
	}
	else if (remainingDocumentValue.indexOf("<video>") > -1) {
		int posDeb = remainingDocumentValue.indexOf("<video>")+7;
		int posFin = remainingDocumentValue.indexOf("</video>");
                System.out.println("Video tag : pos deb " + posDeb + "pos fin : " + posFin);
		String nomVideo = remainingDocumentValue.substring(posDeb, posFin);
		String texte = remainingDocumentValue.substring(0, posDeb-7);
		callBuilder(texte, nomVideo,remainingDocumentValue.substring(posDeb-7, posDeb));
		return(remainingDocumentValue.substring(posFin+8,remainingDocumentValue.length()));
	}
	else {
		String texte = remainingDocumentValue.substring(0, remainingDocumentValue.length());
		callBuilder(texte, "rien", "rien");
		return("");
	}
    }
    
    private void callBuilder(String texte, String nomPhotoOuVideo, String typeTag) {
        
        textes.add(texte);
        nomPhotoOuVideos.add(nomPhotoOuVideo);
        typeTags.add(typeTag);
        
	System.out.println("Le callBuilder marche --> texte : " + texte + ", chemin : " + nomPhotoOuVideo + ", tag : " + typeTag);
                
        try {
            String url = "https://selfsolve.apple.com/wcResults.do";
            URL obj = new URL(url);
            HttpURLConnection connexion = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            connexion.setRequestMethod("POST");        
            connexion.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

            // Send post request
            connexion.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = connexion.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connexion.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());            
        }
        
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
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
