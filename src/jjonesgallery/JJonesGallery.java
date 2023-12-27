/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jjonesgallery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Thema
 */
public class JJonesGallery extends Application {
    
    //Initializes the variables related to the index of the arrays
    private final short galleryLength = 10; //Works on all numbers up to 10
    private short index;
    private Random random;
    
    //Initializes the scanners
    private Scanner namesReader;
    private Scanner sourcesReader;
    
    //Initializes the variables used in the layout of the application
    private GridPane layout;
    private Scene scene;
    private VBox imageDetails;
    private HBox imageChangers;
    private HBox loopButtons;
    
    //Initializes the arrays
    private final Image[] imageGallery = new Image[galleryLength];
    private final String[] imageNames = new String[galleryLength];
    private final String[] imageSources = new String[galleryLength];
    
    //Initializes the ImageView
    private ImageView galleryView;
    
    //Initializes the labels
    private Label nameText;
    private Label sourceText;
    private Label imageText;
    
    //Initializes the text field
    private TextField startField;
    
    //Initializes the buttons and checkbox
    private Button prevButton;
    private Button nextButton;
    private Button startButton;
    private Button resetButton;
    private CheckBox isRandom;
    
    //Initializes the variables relating to transitions
    private Timeline autoMove;
    private FadeTransition fadeOut;
    private FadeTransition fadeIn;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException, IOException {
        
        //Initializes the path of the images
        //Note for the future: When calling for the images folder, also call the src folder since that contains it
        File folder = new File("JJonesGallery\\src\\images");
        System.out.println(folder.getCanonicalPath()); //Used for debugging the path
        
        //Sets up the files to read for the names and sources
        //If more images are added, then you can just add the name and source in the files
        namesReader = new Scanner(new File("JJonesGallery\\src\\resources\\names.txt"));
        sourcesReader = new Scanner(new File("JJonesGallery\\src\\resources\\sources.txt"));
        
        //Puts all of the images in the folder path into the imageGallery array
        String[] fileList = folder.list();
        for(short i = 0; i < fileList.length; i++){
            if(i < imageGallery.length){ //Makes sure not too many images are put into the imageGallery array
                imageGallery[i] = new Image("images/" + fileList[i]);
                if(namesReader.hasNext()){
                    imageNames[i] = namesReader.nextLine();
                    imageSources[i] = sourcesReader.nextLine();
                }
            } else {
                i = (short) fileList.length;
            }
        }
        
        //Closes the scanners as they are no longer needed
        namesReader.close();
        sourcesReader.close();
        
        //Sets up the index and random variables
        index = 0;
        random = new Random();
        
        //Sets up the autoMove timeline
        autoMove = new Timeline(new KeyFrame(Duration.millis(3000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ae) {
                //Checks if the isRandom check box is selected
                if(!isRandom.isSelected()){ //If the box is not selected, perform this code
                    if(index + 1 == galleryLength || imageGallery[index+1] == null){
                        index = 0;
                    } else if(index < galleryLength-1){
                        index++;
                    }
                } else { //If the box is selected, make the order random
                    index = (short) random.nextInt(galleryLength);
                }
                update();
            }
        }));
        autoMove.setCycleCount(Animation.INDEFINITE);
        
        //Sets up the layout
        layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        
        //Sets up the scene
        scene = new Scene(layout, 500, 400);
        primaryStage.setTitle("Video Game Art Gallery");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //Sets up the galleryView variable
        galleryView = new ImageView(imageGallery[index]);
        galleryView.setImage(imageGallery[index]);
        galleryView.setPreserveRatio(true);
        galleryView.setFitHeight(250);
        layout.add(galleryView, 0, 0);
        
        //Sets up the fadeOut transition
        fadeOut = new FadeTransition(new Duration(1000), galleryView);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Performs the needed updates after the transition is done
                galleryView.setImage(imageGallery[index]);
                nameText.setText("Name: " + imageNames[index]);
                sourceText.setText("Source: " + imageSources[index]);
                imageText.setText((index + 1) + " / " + galleryLength);
                fadeIn.play(); //Plays this transition after the updates are done
            }
        });
        
        //Sets up the fadeIn transition
        fadeIn = new FadeTransition(new Duration(1000), galleryView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        //Sets up the name and source texts
        nameText = new Label("Name: " + imageNames[index]);
        sourceText = new Label ("Source: " + imageSources[index]);
        
        //Sets up the prevButton
        prevButton = new Button("<");
        prevButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(index > 0){ //Makes sure the index doesn't go below zero
                    index--;
                    update();
                }
            }
        });
        
        //Sets up the text that shows where the user is at in the gallery
        imageText = new Label((index + 1) + " / " + galleryLength);
        
        //Sets up the nextButton
        nextButton = new Button();
        nextButton.setText(">");
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                //Rolls over to the beginning if the next index would equal the gallery's length or if the next position would be null
                if(index +1 == galleryLength || imageGallery[index+1] == null){ 
                    index = 0;
                } else if(index < galleryLength-1){ //Increments as normal if the condition is false
                    index++;
                }
                update();
            }
        });
        
        //Sets up the text field for user input
        startField = new TextField();
        startField.setMaxSize(35, 10);
        
        //Sets up the startButton
        startButton = new Button("Start");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    String text = startField.getText(); //Gets the user's input and puts it into a string variable
                    if(!text.isEmpty() && text.matches("^[0-9\\.]*$")){ //Checks if the text is empty or is a full number
                        Short num = (short) (Short.parseShort(text) - 1); //Converts the user's input into a short
                        if(num >= galleryLength){ //If the number would exceed the gallery's length, then set the indext to the length - 1
                            index = galleryLength-1;
                        } else if (num <= 0){ //If the number would be lower than 0, then set the index to 0
                            index = 0;
                        } else { //Assigns the num to the index if both conditions are false
                            index = num;
                        }

                    } else if (text.isEmpty()){ //Sets the index to 0 if the input is invalid
                        index = (short) random.nextInt(galleryLength);
                    } else {
                        index = 0;
                    }
                
                    //Begins the autoMove animation after the first index is set
                    autoMove.play();
                
                    update();
                
            }
        });
        
        //Sets up the resetButton
        resetButton = new Button("Reset");
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                autoMove.stop(); //Stops the autoMove animation
                index = 0; //Resets the index to 0, the start
                update();
            }
        });
        
        //Sets up the isRandom check box
        isRandom = new CheckBox("Randomize Order?");
        
        //Sets up the HBox that holds the buttons that move the user through the gallery
        imageChangers = new HBox(prevButton, imageText, nextButton);
        imageChangers.setAlignment(Pos.CENTER);
        
        //Sets up the HBox that holds the buttons relating to the loop
        loopButtons = new HBox(startField, startButton, resetButton);
        loopButtons.setAlignment(Pos.CENTER);
        
        //Sets up the VBox that holds all of the text, buttons, and check box
        imageDetails = new VBox(nameText, sourceText, imageChangers, loopButtons, isRandom);
        imageDetails.setAlignment(Pos.CENTER);
        layout.add(imageDetails, 0, 1);
        
    }
    /**
     * Simply put, this method will update the needed fields once it is called
     * However, due to limitations, the resetting code was put into the .setOnFinished() 
     * method of the fadeOut transition.
     */
    public void update(){
        fadeOut.play();
    }
}
