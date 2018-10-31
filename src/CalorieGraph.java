import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CalorieGraph {
	TableView<Graph> table;
	TextField quantityInput;
	TextField calorieInput;
	ComboBox<String> foodInput;
	HashMap<String, String> foodCaloriesMap;
	ResultsWindow resultsWindow;
	String title, nameField, weightField, ageField, gender;
	Integer activeLvl;
	HBox hBox;
	VBox layout;
	private TableColumn<Graph, String> foodColumn;
	private TableColumn<Graph, Double>  quantityColumn;
	private TableColumn<Graph, Integer> calorieColumn;
	
	public CalorieGraph(String nameField, String weightField, 
			String ageField, String gender, int activeLvl){
		this.nameField = nameField;
		this.weightField = weightField;
		this.ageField = ageField;
		this.gender = gender;
		this.activeLvl = activeLvl;
	}
	
	public void createCalorieGraph(){
		
		createTableColumns();
		
		createButtons();
	
		createFormatLabels();
	
		setStage();
	}
	
	private void createTableColumns(){

		foodColumn = new TableColumn<>("Food");
		foodColumn.setMinWidth(200);
		foodColumn.setCellValueFactory(new PropertyValueFactory<>("food"));

		quantityColumn = new TableColumn<>("Servings");
		quantityColumn.setMinWidth(200);
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		
		calorieColumn = new TableColumn<>("Calories per Serving");
		calorieColumn.setMinWidth(200);
		calorieColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));

		table = new TableView<>();
		table.setItems(getGraph());
		table.getColumns().addAll(foodColumn, quantityColumn, calorieColumn);
		
		calorieInput = new TextField();
		calorieInput.setPromptText("Calories");
		calorieInput.setMinWidth(100);
		
		foodInput = new ComboBox<>();
		foodInput.setPromptText("Food");
		foodInput.setMinWidth(100);
		foodInput.setEditable(true);
		
		foodCaloriesMap = new HashMap<>();
		
		addPresetFoodCalories("pizza", "200");
		addPresetFoodCalories("cola", "22");
		addPresetFoodCalories("cheese", "2300");
			
		foodInput.setOnAction(e -> {
			calorieInput.setText(presetCalories());});
		
		
		quantityInput = new TextField();
		quantityInput.setPromptText("Servings");
		quantityInput.setMinWidth(100);
	}
	
	public ObservableList<Graph> getGraph(){
		ObservableList<Graph> foods = FXCollections.observableArrayList();
		foods.add(new Graph("Apple", 1, 100));
		foods.add(new Graph("Sandwhich", 2, 400));
		foods.add(new Graph("Pizza", 3, 500));
		foods.add(new Graph("Fish", 1, 240));
		foods.add(new Graph("Hummus", 12, 40));
		return foods;
	}
	
	private void addPresetFoodCalories(String food, String calories){
		foodCaloriesMap.put(food, calories);
		foodInput.getItems().add(food);
	}
	
	private String presetCalories(){
		return foodCaloriesMap.get(foodInput.getSelectionModel().getSelectedItem());
	}

	private void createButtons(){
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> addButtonClicked());
		
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteButtonClicked());
		
		Button calorieSum = new Button("Submit");
		
		resultsWindow= new ResultsWindow(table, calorieColumn, quantityColumn,
				DailyCalorieIntake(ageField, activeLvl, gender), nameField);
		
		calorieSum.setOnAction(e -> { 
			resultsWindow.resetFoodCalories();
			resultsWindow.createResultsWindow();
		});
		
		hBox = new HBox();
		hBox.setPadding(new Insets(10,10,10,10));
		hBox.setSpacing(10);
		hBox.getChildren().addAll(foodInput, quantityInput, calorieInput, addButton, deleteButton, calorieSum);
	}
	
	public void addButtonClicked(){
		Graph graph = new Graph();
		
		if (foodInput.getValue().isEmpty()){
			createErrorAlert("No Food Item Given", "Please add a food item first");
		}else 
			{graph.setFood(foodInput.getValue());}
		
		if (quantityInput.getText().isEmpty()){
			createErrorAlert("No Quantity Given", "Please enter the amount of servings first");
		}else
			{graph.setQuantity(Integer.parseInt(quantityInput.getText()));}
		
		if (calorieInput.getText().isEmpty()){
			createErrorAlert("No Calorie Amount Given", "Please enter the amount of calories per serving first");
		}else{
			graph.setCalories(Integer.parseInt(calorieInput.getText()));
			table.getItems().add(graph);
			quantityInput.clear();
			calorieInput.clear();
		}
	}
	
	private void createErrorAlert(String title, String content){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.show();
		}
	
	public void deleteButtonClicked(){
		ObservableList<Graph> productSelected, allProducts;
		allProducts = table.getItems();
		productSelected = table.getSelectionModel().getSelectedItems();
		
		productSelected.forEach(allProducts::remove);
	}
	
	public static int DailyCalorieIntake(String agefield, Integer activeLvl, String gender){
		int age = Integer.parseInt(agefield);
		boolean male;
		if (gender.equals("male")){
			male = true;
		}
		else {
			male = false;
		}
		
		int caloriesDailyValue = 0;
	
		if(age<6 && age>2 && male == true && activeLvl == 1
				|| age<8 && age>3 && male == false && activeLvl == 1
				|| age == 3 && male == false && activeLvl == 2){
					caloriesDailyValue = 1200;
		
		}
		else if(age<3 && male == true && activeLvl == 1 
				||age<3 && male == true && activeLvl == 2
				|| age<3 && male == true && activeLvl == 3
				|| age<4 && male == false && activeLvl == 1
				|| age<3 && male == false && activeLvl == 2
				|| age<3 && male == false && activeLvl == 3){
					caloriesDailyValue = 1000;
		}
		
		else if(age>5 && age<9 && male == true && activeLvl == 1
				||age>2 && age<6 && male == true && activeLvl == 2
				|| age==3 && male == true && activeLvl == 3
				|| age>7 && age<11 && male == false && activeLvl == 1
				|| age>3 && age<7 && male == false && activeLvl == 2
				|| age<5 && age>2 && male == false && activeLvl == 3){
					caloriesDailyValue = 1400;
		}
		else if(age>8 && age<11 && male == true && activeLvl == 1
				||age<9 && age>5 && male == true && activeLvl == 2
				|| age<6 && age>3 && male == true && activeLvl == 3
				|| age<14 && age>10 && male == false && activeLvl == 1
				|| age<10 && age>6 && male == false && activeLvl == 2
				|| age<7 && age>4 && male == false && activeLvl == 3
				|| age>51 && male == false && activeLvl == 1){
					caloriesDailyValue = 1600;
		}
		else if(age>10 && age<13 && male == true && activeLvl == 1
				||age<11 && age>8 && male == true && activeLvl == 2
				|| age<8 && age>5 && male == true && activeLvl == 3
				|| age<19 && age>13 && male == false && activeLvl == 1
				|| age<51 && age>25 && male == false && activeLvl == 1
				|| age>51 && male == false && activeLvl == 2
				|| age<12 && age>9 && male == false && activeLvl == 2
				|| age<10 && age>6 && male == false && activeLvl == 3){
					caloriesDailyValue = 1800;
		}
		else if(age>12 && age<15 && male == true && activeLvl == 1
				||age>60 && male == true && activeLvl == 1
				||age==11 && male == true && activeLvl == 2
				|| age<10 && age>7 && male == true && activeLvl == 3
				|| age<26 && age>18 && male == false && activeLvl == 1
				|| age<19 && age>11 && male == false && activeLvl == 2
				|| age<12 && age>9 && male == false && activeLvl == 3
				|| age>60 && male == false && activeLvl == 3){
					caloriesDailyValue = 2000;
		}
			
		else if( age==15 && male == true && activeLvl == 1
				||age>40 && age<61 && male == true && activeLvl == 1
				||age<14 && age>11 && male == true && activeLvl == 2
				||age>65 && male == true && activeLvl == 2
				|| age<12 && age>9 && male == true && activeLvl == 3
				|| age<26 && age>19 && male == false && activeLvl == 2
				|| age<14 && age>11 && male == false && activeLvl == 3
				|| age<61 && age>30 && male == false && activeLvl == 3){
					caloriesDailyValue = 2200;
		}	
	
		else if(age>15 && age<19 && male == true && activeLvl == 1
				||age>20 && age<41 && male == true && activeLvl == 1
				||age==14 && male == true && activeLvl == 2
				||age>45 && age<66 && male == true && activeLvl == 2
				|| age==12 && male == true && activeLvl == 3
				|| age>75 && male == true && activeLvl == 3
				|| age<31 && age>13 && male == false && activeLvl == 3
				|| age>60 && male == true && activeLvl == 3){
					caloriesDailyValue = 2400;
		}
	
		else if(age>18 && age<21 && male == true && activeLvl == 1
				||age==15 && male == true && activeLvl == 2
				||age>25 && age<46 && male == true && activeLvl == 2
				|| age==13 && male == true && activeLvl == 3
				|| age>55 && age<76 && male == true && activeLvl == 3){
					caloriesDailyValue = 2600;
		}
		else if(age>15 && age<26 && male == true && activeLvl == 2
				||age==14 && male == true && activeLvl == 3
				|| age>35 && age<56 && male == true && activeLvl == 3){
					caloriesDailyValue = 2800;
		}
		else if(age>18 && age<36 && male == true && activeLvl == 3){
					caloriesDailyValue = 3000;
		}
		else if(age>15 && age<19 && male == true && activeLvl == 3){
					caloriesDailyValue = 3200;
		}
		return caloriesDailyValue;
		}
	
	private void createFormatLabels(){
		Label name = new Label();
		name.setText("Name: " + nameField);
		name.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
		GridPane.setConstraints(name, 0, 0);

		Label weight = new Label();
		int x = Integer.parseInt(weightField);
		weight.setText("Weight: " + String.valueOf(x));
		weight.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
		GridPane.setConstraints(weight, 2, 0);

		Label age = new Label();
		age.setText("Age: " + String.valueOf(ageField));
		age.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
		GridPane.setConstraints(age, 4, 0);

		final Separator sepVer = new Separator();
		sepVer.setOrientation(Orientation.VERTICAL);
		sepVer.setValignment(VPos.CENTER);
		GridPane.setConstraints(sepVer, 1, 0);
		
		final Separator sepVer1 = new Separator();
		sepVer1.setOrientation(Orientation.VERTICAL);
		sepVer1.setValignment(VPos.CENTER);
		GridPane.setConstraints(sepVer1, 3, 0);
		
		GridPane pane = new GridPane();
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		pane.getChildren().addAll(name, weight, age, sepVer, sepVer1);
		
		layout = new VBox();
		layout.getChildren().addAll(pane, table, hBox);
	}

	private void setStage(){
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Foods Consumed");
		stage.setMinWidth(300);
		
		Scene scene = new Scene(layout);
		scene.getStylesheets().add("format.css");
		stage.setScene(scene);
		stage.show();
	}
}
	
	
