import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
		String newDate = System.lineSeparator() + date + " X" + String.valueOf(foodCalories) + " Y" + dvPercentFormatted;
	
	
	
		File log = new File(nameField + "\\monthlyInfo.txt");
			try{
				FileWriter writer = new FileWriter(log, true);
				BufferedWriter reader = new BufferedWriter(writer);
				
				int i = (int) foodCalories;
				int j = (int)percentOfDailyValueConsumed;
				
				if(!replaceTodaysSavedData(newDate, log)){
					reader.write(date + " X" + String.valueOf(i) + " Y" + String.valueOf(j) + "q");
					reader.close();}
			}catch (IOException E){
			
		}
		
	}
	
	private boolean replaceTodaysSavedData (String replaceWith, File file1){
		boolean x = false;
		try{

			Date today = new Date();
			
			SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM//dd/yy");
			String date = DATE_FORMAT.format(today);
			BufferedReader file = new BufferedReader(new FileReader(file1));
			String line;
			StringBuffer inputBuffer = new StringBuffer();
				
			while ((line = file.readLine()) != null){
				inputBuffer = inputBuffer.append(line);
				inputBuffer.append(System.lineSeparator());
				}
			String inputStr = inputBuffer.toString();
			file.close();
				
			date = date.toString();
				
			if (inputStr.contains(date)){	
				String[] lines = inputStr.split("q");
				ArrayList<String> lineslist = new ArrayList<String>(Arrays.asList(lines));
				int i;
				for (i = 0; i<lineslist.size(); i++){
						
					if (lineslist.get(i).contains(date)){
						 break;}	
				}
				String lineWithCurrentDate = lineslist.get(i);
					
				inputStr = inputStr.replace(lineWithCurrentDate, replaceWith);
				x = true;
			}
			else{
				x = false;
				}
				
				
				FileOutputStream fileOut = new FileOutputStream(file1);
				fileOut.write(inputStr.getBytes());
				fileOut.close();
			
				
			}catch (Exception e){
				System.out.println("error");
				
			}
				return x;
	}
	
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
