import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

public class CalendarChart {
	
	private Stage stage;
	private String month, userName, storedMonth;
	private Button prevMonthButton, nextMonthButton;
	private Label monthLabel;
	private HBox topHBox;
	private GridPane imagePane, labelPane, datesPane;
	File storedUserCalorieData;
	List<Label> daysLabelList;
	private Position selectedMonthPosition;
	private int currentPosition;
	ArrayList<ArrayList<Label>> months;
	HashMap<String, String> dateSumPercentMap;
	ArrayList<Pair<Integer, Integer>> constraintsList;
	Properties propSum, propPercent;
	InputStream inputSum, inputPercent;
	private Label sumPercentLabel;
	
		
	public void createCalendarChartWindow(String userName){
		this.userName = userName;
		createStage();
		   
		createFormattedTopBanner();
		
		createCalendarBackground();
		
		formatLabelsPane();
		
		formatColumns();
		
		fetchStoredCalorieDataToLabels();
		
		formatDatesLabelsPane();
		
		formatDateColumns();
		
		changeMonthAndDataOnClick();
		
		setStage();
		
	}
	
	private void createStage(){
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Monthly Results");
		stage.setMinHeight(500);
		stage.setMinWidth(500);
	}
	
	private void createFormattedTopBanner(){
		prevMonthButton = new Button();
		prevMonthButton.setId("button-left");
		prevMonthButton.setPadding(new Insets(-10,-10,-10,-10));
		
		nextMonthButton = new Button();
		nextMonthButton.setPadding(new Insets(-10,-10,-10,-10));
		nextMonthButton.setId("button-right");
		
		topHBox = new HBox();
		topHBox.setMinWidth(900);
		topHBox.setMinHeight(50);
		topHBox.setPadding(new Insets(20, 0, -20, 0));
		GridPane.setConstraints(topHBox, 0, 0);
		
		GridPane topHBoxPane = new GridPane();
		GridPane.setHalignment(prevMonthButton, HPos.LEFT);
		GridPane.setHalignment(nextMonthButton, HPos.RIGHT);
		topHBox.setAlignment(Pos.CENTER);
		
	    for (int i = 0; i < 3; i++) {
	    	ColumnConstraints column = new ColumnConstraints(100);
	        topHBoxPane.getColumnConstraints().add(column);
	     }
		
	    GridPane.setConstraints(nextMonthButton, 2, 0);
		topHBoxPane.setMinHeight(60);
			  
	    month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG,
					Locale.getDefault());
			
		monthLabel = new Label(month);
		GridPane.setConstraints(monthLabel, 1, 0);
		GridPane.setValignment(monthLabel, VPos.BOTTOM);
		GridPane.setConstraints(prevMonthButton, 0, 0);
	
		topHBoxPane.getChildren().addAll(monthLabel, prevMonthButton, nextMonthButton);
		monthLabel.setId("label-monthName");
		topHBox.getChildren().addAll(topHBoxPane);
		
	}
	
	private void createCalendarBackground(){
		InputStream calendarBackgroundImg = ResourceLoader.load("images/calendar.png");
		Image image = new Image(calendarBackgroundImg);
		ArrayList<ImageView> calendarImageList = new ArrayList<>();
		imagePane = new GridPane();
		imagePane.setAlignment(Pos.CENTER);
		
		setConstraints(calendarImageList, image);
	}
	
	private void setConstraints(ArrayList<ImageView> calendarImageList, Image image){
		for (int i = 0, vertical = 0, horizontal = 0; i < 35; i++, ++horizontal){
			if (horizontal>4){
				vertical++;
				horizontal = 0;
			}
			calendarImageList.add(new ImageView(image));
			GridPane.setConstraints(calendarImageList.get(i), vertical, horizontal);
			imagePane.getChildren().add(calendarImageList.get(i));
		}
	}
	
	private void formatLabelsPane(){
		labelPane = new GridPane();
		labelPane.setMinHeight(500);
		labelPane.setMinWidth(700);
		labelPane.setMaxSize(700, 500);
		labelPane.setGridLinesVisible(false);
		labelPane.setAlignment(Pos.CENTER);
		labelPane.setPadding(new Insets(10, 0, -10, 0));
	}
	
	private void formatColumns(){
		final int numCols = 7;
		final int numRows = 5;
		
		for(int i = 0; i < numCols; i++){
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPrefWidth(1000 / numCols);
			labelPane.getColumnConstraints().add(colConst);
		}
		for(int i = 0; i < numRows; i++){
			RowConstraints rowConst = new RowConstraints();
			rowConst.setPrefHeight(1000 / numRows);
			labelPane.getRowConstraints().add(rowConst);
		}
	}
	
	private void fetchStoredCalorieDataToLabels(){
		createMonthlyDataConstraintsList();
		
		try{
			propSum = new Properties();
			propPercent = new Properties();
			inputSum = null;
			inputPercent = null;
			inputSum = new FileInputStream(userName+ "Sum.properties");
			inputPercent = new FileInputStream(userName+ "Percent.properties");
			propSum.load(inputSum);
			propPercent.load(inputPercent);			
			propertiesIntoMap();
			
			createMonthList();
			
			for (Object key : dateSumPercentMap.keySet()){
				getConstraintForData(key);
				storeDataInCorrectMonthList();
			}
		}catch(IOException io){
			io.printStackTrace();
		}finally{
			if (inputSum != null && inputPercent != null) {
				try{
					inputSum.close();
					inputPercent.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createMonthlyDataConstraintsList(){
		constraintsList = new ArrayList<>();
		constraintsList.add(new Pair<> (0,0));
		
		for (int i = 1, horizontal = 0, vertical = 0; i < 32; i++, ++horizontal){
			if (horizontal > 5){
				constraintsList.add(new Pair<>(horizontal, vertical));
				vertical++;
				horizontal = 0;
			}
			constraintsList.add(new Pair<>(horizontal, vertical));
		}
	}
	
	
	private void propertiesIntoMap(){
		dateSumPercentMap = new HashMap<String, String>();
		Set<Object> keys = propSum.keySet();
		ArrayList<Object> keysList = new ArrayList<>(keys);
		
		
		for (int i = 0; i < keysList.size(); i++){
			String date = keysList.get(i).toString();
			String sum = propSum.getProperty(date);
			String percent = propPercent.getProperty(date);
			
			dateSumPercentMap.put(date, sum + System.lineSeparator() +percent + "%");
		}
	}
	
	private void createMonthList(){
		months = new ArrayList<>();
		for (int i = 0; i < 12; i++){
			months.add(new ArrayList<Label>());
		}
	}
	
	private void getConstraintForData(Object key){
		String date = key.toString();
		storedMonth = date.substring(0, 2);
		String day = date.substring(4, 6);
		Integer x = constraintsList.get(Integer.parseInt(day)).getKey();
		Integer y = constraintsList.get(Integer.parseInt(day)).getValue();
		sumPercentLabel = new Label(dateSumPercentMap.get(key));
		GridPane.setHalignment(sumPercentLabel, HPos.CENTER);
		GridPane.setValignment(sumPercentLabel, VPos.CENTER);
		GridPane.setConstraints(sumPercentLabel, x, y);
		sumPercentLabel.setId("label-monthData");
	}
	
	private void storeDataInCorrectMonthList(){
		months.get(Integer.parseInt(storedMonth)-1).add(sumPercentLabel);
	}
	
	private void formatDatesLabelsPane(){
		datesPane = new GridPane();
		datesPane.setLayoutX(105);
		datesPane.setMinHeight(490);
		datesPane.setMinWidth(700);
		datesPane.setMaxSize(700, 500);
		datesPane.setGridLinesVisible(false);
		datesPane.setAlignment(Pos.CENTER);
		datesPane.setPadding(new Insets(12, 0, -10, 0));
	}
	
	private void formatDateColumns(){
		final int numCols = 7;
		final int numRows = 5;
		
		for(int i = 0; i < numCols; i++){
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPrefWidth(1000 / numCols);
			datesPane.getColumnConstraints().add(colConst);
		}
		for(int i = 0; i < numRows; i++){
			RowConstraints rowConst = new RowConstraints();
			rowConst.setMinHeight(100);
			rowConst.setMaxHeight(100);
			datesPane.getRowConstraints().add(rowConst);
		}
		
		for (int i = 1; i < 32; i++){
			Label label = new Label(Integer.toString(i));
			GridPane.setConstraints(label, constraintsList.get(i).getKey(), constraintsList.get(i).getValue());
			GridPane.setHalignment(label, HPos.CENTER);
			GridPane.setValignment(label, VPos.TOP);
			label.setId("label-month");
			datesPane.getChildren().add(label);
		}
		
	}
	
	private void changeMonthAndDataOnClick(){
		selectedMonthPosition = new Position();
		currentPosition = selectedMonthPosition.getIntPosition();
		changeLabels();
		nextMonthButton.setOnAction(e -> {
			monthLabel.setText(selectedMonthPosition.nextMonth());
											currentPosition = selectedMonthPosition.getIntPosition();
												changeLabels();
		});
		
		prevMonthButton.setOnAction(e -> {
		monthLabel.setText(selectedMonthPosition.previousMonth());
											currentPosition = selectedMonthPosition.getIntPosition();
												changeLabels();		
		});
	}
	
	private void changeLabels(){
		labelPane.getChildren().clear();
		
		
		for (Label label : months.get(currentPosition)){
			labelPane.getChildren().add(label);	
		}
	}
	
	private void setStage(){
		GridPane pane3 = new GridPane();
		StackPane root = new StackPane();
		root.getChildren().addAll(imagePane, labelPane, datesPane);
		GridPane.setConstraints(root, 0, 1);
		pane3.getChildren().addAll(root, topHBox);
		Scene scene = new Scene(pane3);
		scene.getStylesheets().add("format.css");
		stage.setScene(scene);
		stage.show();
	}
}
