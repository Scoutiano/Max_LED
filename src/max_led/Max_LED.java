/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package max_led;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Mohanad
 */
public class Max_LED extends Application {
    
    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/FXMLMain.fxml"));
  
        Scene scene = new Scene(root);
        
        stage.setTitle("Max LED");
        stage.getIcons().add(new Image(Max_LED.class.getResourceAsStream("resources/images/icon.jpg")));
        scene.getStylesheets().add("max_led/resources/css/Style.css");
        stage.setResizable(false);
        
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
