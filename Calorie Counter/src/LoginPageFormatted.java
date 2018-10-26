import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPageFormatted {
	
	Scene loginScene;
	static Stage loginStage;
	
	UserData userData;
	
	GridPane grid;
	
	StackPane stackPane;
	
	String nameInput, passwordInput, weightInput, ageInput;
	
	Button loginButton, registerButton;
	
	static PasswordField passwordInputLogin, passwordInputRegister;
	
	static TextField RegisterNameInputField, weightInputField, ageInputField, userNameInputField;
	
	static RadioButton male, female, sedentary, modActive, active;

	
	
	public Stage creatStage(){
		loginStage = new Stage();
		userData = new UserData();
		
		createFormattedSceneSetStage();
		
		return loginStage;
	}
	
	public static void closeStage(){
		loginStage.close();
	}
	
	private void createFormattedSceneSetStage(){
		
		
		grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setAlignment(Pos.TOP_CENTER);
	
		createAndAddItemsToGrid();

		final Separator verticalSeparator = new Separator();
		verticalSeparator.setOrientation(Orientation.VERTICAL);
		verticalSeparator.setValignment(VPos.CENTER);
		verticalSeparator.setMinHeight(140);
		
		VBox verticalSparatorVBox = new VBox();
		verticalSparatorVBox.setAlignment(Pos.CENTER);
		verticalSparatorVBox.setPadding(new Insets(-80, 0, 100, 35));
		verticalSparatorVBox.getChildren().add(verticalSeparator);

		VBox bottomVBox = new VBox(10);
		bottomVBox.setPadding(new Insets(300, 130, 100, 130));
		bottomVBox.getChildren().addAll(registerButton, userNameInputField, passwordInputLogin, loginButton);
		
		stackPane = new StackPane();
		stackPane.getChildren().addAll(verticalSparatorVBox, bottomVBox, grid);
		
		loginScene = new Scene(stackPane, 350, 400);	
		loginScene.getStylesheets().add("format.css");
	
		loginStage.setScene(loginScene);
		loginStage.setResizable(false);
		loginStage.show();

		loginStage.setTitle("Calorie Counter");
		loginStage.setOnCloseRequest(e -> {
		loginStage.close();
	});
		
	}

	
	private void createAndAddItemsToGrid(){
		TextField[] textFields = createTextFields();
		Label[] labels = createLabels();
		RadioButton[] radioButtons = createRadioButtons();
		
		
		passwordInputLogin = new PasswordField();
		passwordInputLogin.setPromptText("Password");
		
		
		passwordInputRegister = new PasswordField();
		passwordInputRegister.setPromptText("Password");
		GridPane.setConstraints(passwordInputRegister, 0, 6);
		
		loginButton = new Button("Login");
		loginButton.setOnAction(e -> {
			try {
				userData.authenticateUserRetrieveData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		
		registerButton = new Button("Register");
		registerButton.setOnAction(e -> userData.storeNewUserData());
		
		
		HBox hBoxMaleFemaleRadioButtons = new HBox(10);
		hBoxMaleFemaleRadioButtons.getChildren().addAll(radioButtons[0], radioButtons[1]);
		GridPane.setConstraints(hBoxMaleFemaleRadioButtons, 2, 5);
		
		
		grid.getChildren().addAll(labels[0], textFields[0], labels[1],
				textFields[1], labels[2], textFields[2], passwordInputRegister,
					radioButtons[2], radioButtons[3], radioButtons[4],
						labels[4], labels[3], hBoxMaleFemaleRadioButtons);
		grid.setPickOnBounds(false);
		
	}
	
	private TextField[] createTextFields(){
		
		TextField[] textFields = new TextField[3];
		
		userNameInputField = new TextField();
		userNameInputField.setPromptText("User Name");
		
		RegisterNameInputField = new TextField();
		RegisterNameInputField.setPromptText("Username");
		GridPane.setConstraints(RegisterNameInputField, 0, 1);
		textFields[0] = RegisterNameInputField;
		
		weightInputField = new TextField();
		weightInputField.setPromptText("Weight");
		GridPane.setConstraints(weightInputField, 0, 3);
		textFields[1] = weightInputField;
		
		ageInputField = new TextField();
		ageInputField.setPromptText("Age");
		GridPane.setConstraints(ageInputField, 0, 5);
		textFields[2] = ageInputField;
		
		return textFields;
		
	}
	
	private Label[] createLabels(){
		Label[] labels = new Label[5];
		
		Label weight = new Label("Weight");
		GridPane.setConstraints(weight, 0, 2);
		labels[1] = weight;
		
		Label age = new Label("Age");
		GridPane.setConstraints(age, 0, 4);
		labels[2] = age;
		
		Label gender = new Label("Gender");
		GridPane.setConstraints(gender, 2, 4);
		labels[3] = gender;
		
		Label name = new Label("Username");
		GridPane.setConstraints(name, 0, 0);
		labels[0] = name;
		
		Label activityLevel = new Label("Activity Level");
		GridPane.setConstraints(activityLevel, 2, 0);
		labels[4] = activityLevel;
		
		return labels;
	}
	
	private RadioButton[] createRadioButtons(){
		RadioButton[] radioButtons = new RadioButton[5];
	
		male = new RadioButton("Male");
			radioButtons[0] = male;
		female = new RadioButton("Female");
			radioButtons[1] = female;
			
		ToggleGroup gender = new ToggleGroup();
		male.setToggleGroup(gender);
		female.setToggleGroup(gender);
		male.setSelected(true);
		
		sedentary = new RadioButton("Sedentary");
			radioButtons[2] = sedentary;
		modActive = new RadioButton("Moderately Active");
			radioButtons[3] = modActive;
		active = new RadioButton("Very Active");
			radioButtons[4] = active;
			
		ToggleGroup activityLevel = new ToggleGroup();
		sedentary.setToggleGroup(activityLevel);
		modActive.setToggleGroup(activityLevel);
		active.setToggleGroup(activityLevel);
		sedentary.setSelected(true);
			
		GridPane.setConstraints(sedentary, 2, 1);
		GridPane.setConstraints(modActive, 2, 2);
		GridPane.setConstraints(active, 2, 3);
		
		return radioButtons;
		
	}
}
	
	
	

