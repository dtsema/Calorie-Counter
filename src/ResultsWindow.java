import java.util.Date;
import java.util.Properties;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ResultsWindow {
	Stage stage;
	int foodCalories, dailyCalorieIntake;
	double percentOfDailyValueConsumed;
	private TableView<Graph> tableViewGraph;
	private TableColumn<Graph, Integer> calorieColumn;
	private TableColumn<Graph, Double> quantityColumn;
	String nameField;
	private VBox vBox;
	Button calendarPageButton, lineChartButton;
	CalendarChart calendarChart;
	LineChartPage lineChart;
	
	ResultsWindow(TableView<Graph> tableViewGraph, TableColumn<Graph, Integer> calorieColumn, 
			TableColumn<Graph, Double> quantityColumn, Integer dailyCalorieIntake, String nameField){
		this.tableViewGraph = tableViewGraph;
		this.calorieColumn = calorieColumn;
		this.quantityColumn = quantityColumn;
		this.dailyCalorieIntake = dailyCalorieIntake;
		this.nameField = nameField;
	}
	
	public void createResultsWindow(){
		createStage();
		
		getTotalCaloriesConsumed();
		
		getPercentageCaloriesOfDailyValue();
		
		createButtons();
		
		createFormatLabels();
		
		storeFormattedCalorieData();
		
		createSetScene();
	}
	
	private void createStage(){
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Results");
		stage.setMinHeight(200);
		stage.setMinWidth(200);
	}
	
	private void getTotalCaloriesConsumed(){
		for (Graph graph : tableViewGraph.getItems()){
			
			int	calories = calorieColumn.getCellData(graph);
			double quantity = quantityColumn.getCellData(graph);
			foodCalories += (calories * quantity);}	
	}
	
	private void getPercentageCaloriesOfDailyValue(){
		double i = (double)foodCalories/(double)dailyCalorieIntake;
		
		percentOfDailyValueConsumed = (100*i);
	}
	
	private void createFormatLabels(){

		Label totalCaloriesConsumedLabel = new Label();
		totalCaloriesConsumedLabel.setText("Calorie Total: " + String.valueOf(foodCalories));
		totalCaloriesConsumedLabel.setStyle("-fx-font-size: 20px;" + "-fx-text-fill: #0044cc");
		
		Label percentOfDailyValueConsumedLabel = new Label();
		percentOfDailyValueConsumedLabel.setStyle("-fx-font-size: 20px;" + "-fx-text-fill: #0044cc");
		NumberFormat formatter = new DecimalFormat("#0.00");
		percentOfDailyValueConsumedLabel.setText("You are intaking " + formatter.format(percentOfDailyValueConsumed) + "% of your recommended daily allowance.");
	
		
		vBox = new VBox();
		vBox.setPadding(new Insets(10,10,10,10));
		vBox.setSpacing(10);
		vBox.getChildren().addAll(totalCaloriesConsumedLabel, percentOfDailyValueConsumedLabel, calendarPageButton, lineChartButton);
	}
	
	private void createButtons(){
		calendarPageButton = new Button("Calendar");
		calendarChart = new CalendarChart();
		calendarPageButton.setOnAction(e -> {
			calendarChart.createCalendarChartWindow(nameField);
		stage.close();
		});

		lineChart = new LineChartPage(nameField);
		lineChartButton = new Button("Line Chart");
		lineChartButton.setOnAction(e -> {
		lineChart.createLineChartPage();
		});
	}
	
	private void storeFormattedCalorieData(){

		Date today = new Date();
		
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM//dd/yy");
		String date = DATE_FORMAT.format(today);
		
		DecimalFormat decimalFormat = new DecimalFormat(".##");	
		String dvPercentFormatted = String.valueOf(decimalFormat.format(percentOfDailyValueConsumed));
		
		Properties prop = new Properties();
		InputStream inputSum = null;
		OutputStream oS = null;
		
		Properties prop1 = new Properties();
		InputStream inputSum1 = null;
		OutputStream oS1 = null;
		
		try {
			
		try{inputSum = new FileInputStream(nameField + "Sum.properties");
		prop.load(inputSum);
		prop.remove(date);}catch(FileNotFoundException e){
			
		}
		
		oS = new FileOutputStream(nameField + "Sum.properties");
		prop.store(oS, null);
		
		
		try{inputSum1 = new FileInputStream(nameField + "Percent.properties");
		prop1.load(inputSum1);
		prop1.remove(date);}catch(FileNotFoundException e){
			
		}
		
		oS1 = new FileOutputStream(nameField + "Percent.properties");
		prop1.store(oS1, null);
		
			
			FileWriter fileWritter = new FileWriter(nameField + "Sum.properties", true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.append(date+"="+Integer.toString(foodCalories)+ System.lineSeparator());
			bufferWritter.close();
		
			
			FileWriter fileWritter1 = new FileWriter(nameField + "Percent.properties", true);
			BufferedWriter bufferWritter1 = new BufferedWriter(fileWritter1);
			bufferWritter1.append(date+"="+dvPercentFormatted+System.lineSeparator());
			bufferWritter1.close();
			
			
			
		}catch(IOException io){
			io.printStackTrace();
		}finally{if (oS != null && oS1 != null) {
			try {
				oS.close();
				oS1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}}
	
	
	private void createSetScene(){
		Scene scene = new Scene(vBox);
		scene.getStylesheets().add("format.css");
		stage.setScene(scene);
		stage.show();
	}
	public void resetFoodCalories(){
		foodCalories = 0;
	}
}
