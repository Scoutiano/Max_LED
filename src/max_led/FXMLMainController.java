/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package max_led;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Mohanad
 */
public class FXMLMainController implements Initializable {

    //GridPane for cost table
    GridPane grid = new GridPane();

    //Array for LED order and an array for the status of each LED if it's entered or not, plus a counter to keep track of the number of LEDs
    static boolean[] status;
    static int[] LEDs;
    static int counter = -1;
    static int[][] cost;

    //TextArea to show LEDs not used yet
    @FXML
    TextArea LEDTA = new TextArea();

    //TextField to take number of LEDs from
    @FXML
    TextField numOfLEDTF = new TextField();

    //Buttons for control
    @FXML
    Button backBtn = new Button();
    @FXML
    Button enterBtn = new Button();
    @FXML
    Button resetBtn = new Button();
    @FXML
    Button randomBtn = new Button();

    //Label for instructions
    @FXML
    Label instructLbl = new Label();

    //FillTransition for sick title animation
    FillTransition fill = new FillTransition();

    //Text for Max LED title
    @FXML
    Text max_LED = new Text();

    //Stage for Alerts
    @FXML
    Stage alertStage = new Stage();
    @FXML
    Button yesBtn = new Button();
    @FXML
    Button noBtn = new Button();
    @FXML
    static int cheapCount = 0;

    //Notification Label
    @FXML
    Label notifLbl = new Label();
    FadeTransition ft;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Set backBtn to disabled when starting, same with resetBtn
        resetBtn.setDisable(true);
        backBtn.setDisable(true);

        //Setting up animation for max_LED title
        fill.setCycleCount(Timeline.INDEFINITE);
        fill.setDuration(Duration.millis(1000));
        fill.setAutoReverse(true);
        fill.setFromValue(Color.color(0.7, 0.7, 0));
        fill.setToValue(Color.color(0, 0, 0));
        fill.setShape(max_LED);
        fill.play();

        //LED number is entered when enter is pressed
        numOfLEDTF.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();
            if (key == KeyCode.ENTER) {
                enterBtnAction();
            }
        });

        //Fade transition for notifLbl
        ft = new FadeTransition(Duration.millis(500), notifLbl);
        ft.setDelay(Duration.millis(2500));
        ft.setFromValue(1);
        ft.setToValue(0);

    }

    //Actual arr1 and arr2 elements start at 1
    //This is so the code is easier to understand as LCS[j][i] array has initial states when j or i == 0
    public LinkedList<Integer> LCS(int[] ar1, int[] ar2) {

        //Define 2 dimensional array where previous costs will be stored.
        cost = new int[ar1.length][ar2.length];

        //Setting initial states
        for (int i = 0; i < cost.length; i++) {
            cost[0][i] = 0;
        }

        for (int i = 0; i < cost[0].length; i++) {
            cost[i][0] = 0;
        }

        for (int i = 0; i < ar1.length; i++) {
            System.out.println(ar1[i] + "  " + ar2[i]);
        }

        //Iterate over possible combination of elements from ar1 and ar2
        //and find their cost
        for (int i = 1; i < ar1.length; i++) {
            for (int j = 1; j < ar2.length; j++) {
                if (ar1[i] == ar2[j]) {
                    cost[i][j] = cost[i - 1][j - 1] + 1;
                } else {
                    cost[i][j] = max(cost[i - 1][j], cost[i][j - 1]);
                }
            }
        }

        for (int i = 0; i < cost.length; i++) {
            for (int j = 0; j < cost[0].length; j++) {
                System.out.print(cost[i][j] + " ");
            }
        }
        
        return generateLCS(cost, ar1, ar2);
    }

    //Method to generate LCS
    //Result of the method is reversed
    public LinkedList<Integer> generateLCS(int[][] cost, int[] ar1, int[] ar2) {

        LinkedList<Integer> LCS = new LinkedList<>();

        int i = ar1.length - 1;
        int j = ar2.length - 1;

        while (i > 0 && j > 0) {
            if (ar1[i] == ar2[j]) {
                LCS.add(ar1[i]);
                i--;
                j--;
            } else if (cost[i][j - 1] > cost[i - 1][j]) {
                j--;
            } else {
                i--;
            }
        }

        return LCS;
    }

    //Utility function used to find the max of 2 integers
    public int max(int x, int y) {
        if (y > x) {
            return y;
        }
        return x;
    }

    //Action for enterBtn, either enters number of LEDs or an LED or connects LEDs depending on the current state
    public void enterBtnAction() {

        if (counter == -1) {
            enterNumberOfLEDs();
            return;
        }

        if (counter < LEDs.length) {
            enterLED();

        } else {
            connectLEDs();
            resetBtnAction();
            notifLbl.setText("Done!");
        }
    }

    //Method to enter number of LEDs and initialize all necessary variables
    public void enterNumberOfLEDs() {
        String num = numOfLEDTF.getText();
        
        num = num.trim();

        if (num.compareTo("") == 0) {
            return;
        }

        //Error handling, the entered value can only be an integer
        for (int i = 0; i < num.length(); i++) {
            if (!Character.isDigit(num.charAt(i))) {
                System.out.println("Enter a valid integer.");

                ft.stop();
                notifLbl.setOpacity(1);
                notifLbl.setText("Not an integer");
                notifLbl.setTextFill(Color.web("#ff0000"));
                ft.play();

                return;
            }
        }

        int numOfLED = Integer.parseInt(num);

        //Initialize array and counter
        //Counter and LEDs[0] changed #Here
        counter = 1;
        LEDs = new int[numOfLED + 1];
        status = new boolean[numOfLED + 1];
        LEDs[0] = 0;

        //Change control labels and enable backBtn
        numOfLEDTF.clear();
        backBtn.setDisable(false);
        resetBtn.setDisable(false);
        randomBtn.setDisable(false);
        enterBtn.setText("Insert");
        instructLbl.setText("Now, enter each LED \nin the order you want");
        updateLEDList();

        System.out.println("Number of LEDs entered");
        ft.stop();
        notifLbl.setOpacity(1);
        notifLbl.setText("Number of LEDs entered");
        notifLbl.setTextFill(Color.web("#249d02"));
        ft.play();

    }

    //Method to load order of LEDs one by one
    public void enterLED() {

        String num = numOfLEDTF.getText();
        num = num.trim();
        if (num.compareTo("") == 0) {
            return;
        }
        //Error handling, the entered value can only be an integer
        for (int i = 0; i < num.length(); i++) {
            if (!Character.isDigit(num.charAt(i))) {
                System.out.println("Enter a valid integer.");
                ft.stop();
                notifLbl.setOpacity(1);
                notifLbl.setText("Not an integer");
                notifLbl.setTextFill(Color.web("#ff0000"));
                ft.play();
                return;
            }
        }

        int LED = Integer.parseInt(num);

        //Check if entered number is within range
        if (LED < 1 || LED > LEDs.length - 1) {
            System.out.println("Input must be between 1 and " + LEDs.length);
            ft.stop();
            notifLbl.setOpacity(1);
            notifLbl.setText("LEDs are 1 to " + (LEDs.length - 1) + " only");
            notifLbl.setTextFill(Color.web("#ff0000"));
            ft.play();
            numOfLEDTF.clear();
            return;
        }

        //Check if LED was entered before, if not, enter it and set its status
        if (status[LED] == false) {

            LEDs[counter] = LED;
            status[LED] = true;
            counter++;
            System.out.println("LED added");
            ft.stop();
            notifLbl.setOpacity(1);
            notifLbl.setText("LED added");
            notifLbl.setTextFill(Color.web("#249d02"));
            ft.play();
            updateLEDList();

        } else {

            System.out.println("LED has been entered before");
            ft.stop();
            notifLbl.setOpacity(1);
            notifLbl.setText("LED already added");
            notifLbl.setTextFill(Color.web("#ff0000"));
            ft.play();
            numOfLEDTF.clear();
            return;
        }

        //Clear the TextField after entering
        numOfLEDTF.clear();

        if (counter == LEDs.length) {
            System.out.println("All LEDs added");
            ft.stop();
            notifLbl.setOpacity(1);
            notifLbl.setText("All LEDs added, click start!");
            notifLbl.setTextFill(Color.web("#249d02"));
            ft.play();
            enterBtn.setText("Start");
        }
    }
    
    //Action for backBtn, either removes last added LED or resets depending on state
    public void backBtnAction() {
        if (counter == 1) {

            //Go back to entering number;
            resetBtnAction();

        } else {

            //Remove last added LED
            counter--;
            status[LEDs[counter]] = false;
            enterBtn.setText("Enter");
            updateLEDList();

            System.out.println("last added LED removed");
            ft.stop();
            notifLbl.setOpacity(1);
            notifLbl.setText("Last LED removed");
            notifLbl.setTextFill(Color.web("#249d02"));
            ft.play();
        }
    }
    
    //Action for resetBtn
    public void resetBtnAction() {

        //Go back to entering number of LEDs
        numOfLEDTF.clear();
        randomBtn.setDisable(true);
        backBtn.setDisable(true);
        resetBtn.setDisable(true);
        randomBtn.setDisable(true);
        enterBtn.setText("Enter");
        instructLbl.setText("Enter number of LEDs");
        counter = -1;
        ft.stop();
        notifLbl.setOpacity(1);
        notifLbl.setText("Reset successful");
        notifLbl.setTextFill(Color.web("#249d02"));
        ft.play();
        System.out.println("Reset");
        updateLEDList();
    }

    //Connect LEDs based on the LCS method
    public void connectLEDs() {
        int[] sources = new int[LEDs.length];
        for (int i = 0; i < sources.length; i++) {
            sources[i] = i;
        }

        LinkedList<Integer> list = LCS(LEDs, sources);

        connectAndBuild(sources, list);

        String res = "Connect LEDs: ";
        for (int i = list.size() - 1; i >= 0; i--) {
            res += list.get(i) + " ";
        }
        System.out.println(res);
    }

    //Updates the list of to-be-entered LEDs
    public void updateLEDList() {

        String res = "LEDs left to enter: \n";
        boolean empty = true;
        if (counter == -1) {
            LEDTA.setText("");
        } else {
            for (int i = 1; i < status.length; i++) {
                if (!status[i]) {
                    empty = false;
                    res = res + (i) + "\n";
                }
            }

            if (empty) {
                LEDTA.setText("All LEDs entered");
            } else {
                LEDTA.setText(res);
            }
        }
    }
    
    //Alert stage for confirmation of reset
    public void alert() throws IOException {

        if (cheapCount == 0) {
            AnchorPane alertAP = (AnchorPane) FXMLLoader.load(getClass().getResource("resources/fxml/AlertFXML.fxml"));
            Scene scene = new Scene(alertAP);
            alertStage.setScene(scene);
            alertStage.setTitle("Confirmation");
            alertStage.getIcons().add(new Image(Max_LED.class.getResourceAsStream("resources/images/Notification.png")));
            alertStage.setResizable(false);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            cheapCount = 1;
        }

        alertStage.showAndWait();

        if (cheapCount == 2) {
            resetBtnAction();
        }
    }

    //Action for yesBtn in alertStage
    public void yesBtnAction() {

        //Go back to entering number of LEDs
        ((Stage) (yesBtn.getScene().getWindow())).close();
        cheapCount = 2;
    }

    //Action for noBtn in alertStage
    public void noBtnAction() {
        ((Stage) (noBtn.getScene().getWindow())).hide();
        cheapCount = 1;
    }

    public void connectAndBuild(int[] sources, LinkedList<Integer> list) {
        
        //In case of cases larger than 50 LEDs, only set up Label and TextArea
        if (LEDs.length > 50) {
            
            //Initializing panes, scene and stage and setting up their heirachy 
            ScrollPane sp = new ScrollPane();
            AnchorPane ap = new AnchorPane();
            Scene scene = new Scene(sp);
            Stage stage = new Stage();
            stage.setScene(scene);
            ap.setStyle("-fx-background-color: #282828;");
            sp.setContent(ap);
            sp.getStylesheets().add("max_led/resources/css/Style.css");
            
            sp.setMaxSize(372, 35);
            sp.setMinSize(372, 35);
            sp.setPrefSize(372, 35);
            
            //Adding Label and TextArea to display LCS
            TextArea orderTA = new TextArea();
            orderTA.setEditable(false);
            orderTA.setPrefSize(172, 42);
            orderTA.setMinSize(172, 42);
            orderTA.setMaxSize(172, 42);

            InnerShadow shadow = new InnerShadow(BlurType.THREE_PASS_BOX, Color.web("#030200"), 20, 0, 0, 0);
            shadow.setWidth(60);
            shadow.setHeight(60);
            orderTA.setEffect(shadow);

            String order = "";
            for (int i = list.size() - 1; i >= 0; i--) {
                order = order + " " + list.get(i);
            }
            orderTA.setText(order);

            Label LCSLbl = new Label("LCS = " + list.size() + "           Order: ");
            LCSLbl.setTextFill(Color.web("#ff6600"));
            AnchorPane.setTopAnchor(LCSLbl, 10.0);
            AnchorPane.setLeftAnchor(LCSLbl, 20.0);
            AnchorPane.setTopAnchor(orderTA, 3.0);
            AnchorPane.setLeftAnchor(orderTA, 210.0);

            ap.getChildren().add(orderTA);
            ap.getChildren().add(LCSLbl);
            
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.getIcons().add(new Image("max_led/resources/images/icon.jpg"));
            stage.setTitle("Connected LEDs");
            stage.show();
        } else {

            //Values for stage width and height
            double width = 40 * (LEDs.length + 2);
            double height = 40 * (LEDs.length + 2);

            //stage -> scene -> sp -> ap -> hbox -> connectGP and tableGP
            ScrollPane sp = new ScrollPane();       //sp to enable scrolling
            AnchorPane ap = new AnchorPane();       //ap to add connection lines
            HBox hbox = new HBox();                 //hbox to contain tableGP and connectGP

            GridPane connectGP = new GridPane();    //connectGP to add light and source icons
            GridPane tableGP = new GridPane();      //tableGP to display cost table

            hbox.getChildren().add(connectGP);
            hbox.getChildren().add(tableGP);
            ap.getChildren().add(hbox);
            sp.setContent(ap);

            //Setting up stage and scene
            Scene tableScene = new Scene(sp);
            Stage tableStage = new Stage();
            tableStage.setScene(tableScene);

            //Defining connectGP rows and columns
            //Rows and columns of connections
            ColumnConstraints[] connectColumn = new ColumnConstraints[2];
            RowConstraints[] connectRow = new RowConstraints[LEDs.length];

            for (int i = 0; i < LEDs.length; i++) {
                connectRow[i] = new RowConstraints();
                connectRow[i].setValignment(VPos.BOTTOM);
                connectRow[i].setMaxHeight(40);
                connectRow[i].setMinHeight(40);
                connectRow[i].setPrefHeight(40);
                connectGP.getRowConstraints().add(connectRow[i]);
            }

            for (int i = 0; i < 2; i++) {
                connectColumn[i] = new ColumnConstraints();
                connectColumn[i].setHalignment(HPos.CENTER);
                connectColumn[i].setMaxWidth(240);
                connectColumn[i].setMinWidth(240);
                connectColumn[i].setPrefWidth(240);
                connectGP.getColumnConstraints().add(connectColumn[i]);
            }

            //Images for lights and sources
            Image lighton = new Image("max_led/resources/images/lighton.png");
            Image lightoff = new Image("max_led/resources/images/lightoff.png");
            Image source = new Image("max_led/resources/images/source.png");

            ImageView[] LEDimage = new ImageView[LEDs.length - 1];
            Label[] lightLbl = new Label[LEDs.length - 1];

            ImageView[] sourceImage = new ImageView[sources.length - 1];
            Label[] sourceLbl = new Label[sources.length - 1];

            Line[] line = new Line[list.size()];
            int lineCount = 0;

            boolean isOn = false;

            //Setting up images for sources and lights
            for (int i = 0; i < LEDs.length - 1; i++) {
                sourceImage[i] = new ImageView(source);
                sourceLbl[i] = new Label("" + (sources[i + 1]));
                sourceLbl[i].setTextFill(Color.web("#ff6600"));
                sourceLbl[i].setPadding(new Insets(0, 0, -5, -40));
                GridPane.setConstraints(sourceLbl[i], 0, i + 1);
                GridPane.setConstraints(sourceImage[i], 0, i + 1);
                connectGP.getChildren().addAll(sourceImage[i], sourceLbl[i]);

                for (int j = 0; j < list.size(); j++) {
                    if (LEDs[i + 1] == list.get(j)) {
                        isOn = true;
                    }
                }

                if (isOn) {
                    LEDimage[i] = new ImageView(lighton);
                    line[lineCount] = new Line(131, ((LEDs[i + 1] + 1) * 40) - 6, 360, (i + 2) * 40);
                    line[lineCount].setStyle("-fx-stroke: #ffd11a");
                    ap.getChildren().add(line[lineCount]);
                    lineCount++;
                } else {
                    LEDimage[i] = new ImageView(lightoff);
                }

                lightLbl[i] = new Label("" + LEDs[i + 1]);
                lightLbl[i].setTextFill(Color.web("#ff6600"));
                lightLbl[i].setPadding(new Insets(0, 0, -5, 35));
                GridPane.setConstraints(lightLbl[i], 1, i + 1);

                GridPane.setConstraints(LEDimage[i], 1, i + 1);
                connectGP.getChildren().addAll(LEDimage[i], lightLbl[i]);
                isOn = false;
            }

            //Adding Label and TextArea to connectGP to display LCS
            TextArea orderTA = new TextArea();
            orderTA.setEditable(false);
            orderTA.setPrefSize(172, 42);
            orderTA.setMinSize(172, 42);
            orderTA.setMaxSize(172, 42);

            InnerShadow shadow = new InnerShadow(BlurType.THREE_PASS_BOX, Color.web("#030200"), 20, 0, 0, 0);
            shadow.setWidth(60);
            shadow.setHeight(60);
            orderTA.setEffect(shadow);

            String order = "";
            for (int i = list.size() - 1; i >= 0; i--) {
                order = order + " " + list.get(i);
            }
            orderTA.setText(order);

            Label LCSLbl = new Label("LCS = " + list.size() + "      Order: ");
            LCSLbl.setTextFill(Color.web("#ff6600"));
            AnchorPane.setTopAnchor(LCSLbl, 10.0);
            AnchorPane.setLeftAnchor(LCSLbl, 80.0);
            AnchorPane.setTopAnchor(orderTA, 3.0);
            AnchorPane.setLeftAnchor(orderTA, 210.0);

            ap.getChildren().add(orderTA);
            ap.getChildren().add(LCSLbl);

            connectGP.setStyle("-fx-background-color: #282828;");

            //Setting up GridPanes
            tableGP.setStyle("-fx-background-color: #282828;");

            //Centering component alignment within cells
            tableGP.setGridLinesVisible(true);
            GridPane.setHalignment(grid, HPos.CENTER);
            GridPane.setValignment(grid, VPos.CENTER);

            //Setting size for pane
            tableGP.setMaxSize(width, height);
            tableGP.setPrefSize(width, height);
            tableGP.setMinSize(width, height);

            //Setting an appropriate size until a certain max is hit
            if (height > (22 * 40) + 50) {
                tableStage.setHeight((22 * 40) + 37);
                tableStage.setWidth((22 * 40) + 240 + 248);
            } else {
                tableStage.setHeight(height + 37);
                tableStage.setWidth(width + 240 + 248);
            }

            sp.getStylesheets().add("max_led/Style.css");

            //Adding rows and columns to GridPane for table
            ColumnConstraints[] col = new ColumnConstraints[LEDs.length + 1];
            for (int i = 0; i < LEDs.length + 1; i++) {
                col[i] = new ColumnConstraints();
                col[i].setHalignment(HPos.CENTER);
                col[i].setMaxWidth(40);
                col[i].setMinWidth(40);
                col[i].setPrefWidth(40);
                tableGP.getColumnConstraints().add(col[i]);
            }

            RowConstraints[] row = new RowConstraints[LEDs.length + 1];
            for (int i = 0; i < LEDs.length + 1; i++) {
                row[i] = new RowConstraints();
                row[i].setValignment(VPos.CENTER);
                row[i].setMaxHeight(40);
                row[i].setMinHeight(40);
                row[i].setPrefHeight(40);
                tableGP.getRowConstraints().add(row[i]);
            }

            //Filling Labels into the table's GridPane
            Label[][] lbl = new Label[LEDs.length + 1][LEDs.length + 1];
            lbl[0][0] = new Label("X");
            lbl[0][1] = new Label("X");
            lbl[1][0] = new Label("X");
            for (int i = 2; i < LEDs.length + 1; i++) {
                lbl[i][0] = new Label("" + sources[i - 1]);
                lbl[i][0].setTextFill(Color.web("#ff6600"));
            }

            for (int i = 2; i < LEDs.length + 1; i++) {
                lbl[0][i] = new Label("" + LEDs[i - 1]);
                lbl[0][i].setTextFill(Color.web("#ff6600"));
            }

            for (int i = 1; i < LEDs.length + 1; i++) {
                for (int j = 1; j < LEDs.length + 1; j++) {
                    lbl[j][i] = new Label("" + cost[i - 1][j - 1]);
                    lbl[j][i].setTextFill(Color.web("#ffc800"));
                }
            }

            for (int i = 0; i < LEDs.length + 1; i++) {
                for (int j = 0; j < LEDs.length + 1; j++) {
                    GridPane.setConstraints(lbl[i][j], i, j);
                }
            }

            for (int i = 0; i < LEDs.length + 1; i++) {
                for (int j = 0; j < LEDs.length + 1; j++) {
                    tableGP.getChildren().add(lbl[i][j]);
                }
            }

            tableStage.setResizable(false);
            tableStage.initModality(Modality.APPLICATION_MODAL);
            
            tableStage.getIcons().add(new Image("max_led/resources/images/icon.jpg"));
            tableStage.setTitle("Connections & Table");
            tableStage.show();
        }
    }

    //Button function to generate randomn sequence of LEDs
    public void randomBtnAction() {

        int size = LEDs.length;

        //Generate array of random numbers 0 to 1 and sort them while attaching the original index for a randomized array
        RandomNode[] random = new RandomNode[size];
        
        for (int i = 0; i < size; i++) {
            random[i] = new RandomNode(i, Math.random());
        }

        Arrays.sort(random);

        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random[i].index+1;
        }

        LEDs = arr;

        connectLEDs();
        notifLbl.setOpacity(1);
        notifLbl.setTextFill(Color.web("#249d02"));
        notifLbl.setText("Random generation done!");
        ft.play();
    }
}
