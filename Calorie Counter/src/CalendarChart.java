import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

public class CalendarChart {
	
	private Stage stage;
	private String month, log;
	private Button prevMonthButton, nextMonthButton;
	private Label monthLabel;
	private HBox topHBox;
	private GridPane imagePane, labelPane;
	private ArrayList<ArrayList<String>> yearList;
	File storedUserCalorieData;
	List<Label> daysLabelList;
	private Position selectedMonthPosition;
	private int currentPosition;
	
		
	public void createCalendarChartWindow(String log){
		this.log = log;
		createStage();
		   
		createFormattedTopBanner();
		
		createCalendarBackground();
		
		formatLabelsPane();
		
		formatColumns();
		
		fetchStoredCalorieDataToLabels();
		
		createFormattedDailyLabels();

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
		monthLabel.setStyle("-fx-font-size: 20;");
		topHBox.getChildren().addAll(topHBoxPane);
		
	}
	
	private void createCalendarBackground(){
		File calendarBackgroundImg = new File("./src//images//calendar.png");

		Image image = new Image(calendarBackgroundImg.toURI().toString());
		ArrayList<ImageView> calendarImageList = new ArrayList<>();
		imagePane = new GridPane();
		imagePane.setAlignment(Pos.CENTER);
		
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
		Date today = new Date();
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM");
		String date = DATE_FORMAT.format(today);
		
		List<String> inputDaysList = new ArrayList<String>();
		
		for (int i = 1, day = 1; i < 32; i++, day++){
			String dayy = Integer.toString(day);
				if (i < 10){
					inputDaysList.add(new String(date + "//0" + dayy));
				}
				else{	inputDaysList.add(new String(date + "//" + dayy));
				}
		}

		storedUserCalorieData = new File(log + "\\monthlyInfo.txt");
		daysLabelList = new ArrayList<Label>();
		
		for (String dayList : inputDaysList ){
			daysLabelList.add(new Label(readStoredCalorieSum(dayList, storedUserCalorieData)));
		}
		labelPane.getChildren().addAll(daysLabelList);
	}
	
	public static String readStoredCalorieSum(String date, File file){
		
		String calSum = null;
		String strWithoutChar = null;
		String strWithoutChar1 = null;
		String last = null;
		String percent = null;
		
		try {
			String textLine;
			if (searchText(file,date) == null){
				calSum = "";
				return calSum;
			}
			else if (searchText(file,date) != null){	
				textLine = searchText(file, date);
				int z = textLine.indexOf('X');
				int y = textLine.indexOf('Y');
				int a = z+1;
				int b = z+2;
				int c = z+3;
				int d = z+4;
			
				int a1 = y+1;
				int a2 = y+2;
				int a3 = y+3;
		
				String mychar = String.valueOf(textLine.charAt(a));
				String mychar1 = String.valueOf(textLine.charAt(b));
				String mychar2 = String.valueOf(textLine.charAt(c));
				String mychar3 = String.valueOf(textLine.charAt(d));
			
				String percent1 = String.valueOf(textLine.charAt(a1));
				String percent2 = String.valueOf(textLine.charAt(a2));
				String percent3 = String.valueOf(textLine.charAt(a3));
			
				calSum = mychar + mychar1 + mychar2 + mychar3;
			
			
				if (calSum.indexOf(' ') >= 0){
					String[] array = calSum.split(" ");
					calSum = array[0];
				}	
			
				percent = System.lineSeparator() + percent1 + percent2 + percent3;
			
				if (percent.indexOf('q') >= 0){
					int i = percent.indexOf("q");
					strWithoutChar = new StringBuilder(percent).deleteCharAt(i).toString();
					percent = strWithoutChar;
				}
				
				if (percent.indexOf(".") >= 0){
					int i = percent.indexOf(".");
					strWithoutChar1 = new StringBuilder(percent).deleteCharAt(i).toString();
					percent = strWithoutChar1;
				}
				percent = percent.concat("%");
				last = calSum.concat(percent);
			
				return last;

			}
		} catch (IOException e) {
		}
		return last;
	}
	
	private void createFormattedDailyLabels(){
		for (int i = 1, horizontal = 0, vertical = 0; i < 32; i++, ++horizontal){
			if (horizontal > 6){
				horizontal = 0;
				vertical++;
			}
			
		Label l = new Label(Integer.toString(i));
		labelPane.getChildren().addAll(l);
		GridPane.setConstraints(l, horizontal, vertical);
		l.setId("label-month");
			
		GridPane.setHalignment(l, HPos.CENTER);
		GridPane.setValignment(l, VPos.TOP);
		
		}
	}

	private void changeMonthAndDataOnClick(){
		yearList = new ArrayList<ArrayList<String>>();
		selectedMonthPosition = new Position();
		
		for (int i = 0; i<daysLabelList.size(); i++){
			GridPane.setHalignment(daysLabelList.get(i), HPos.CENTER);
			GridPane.setValignment(daysLabelList.get(i), VPos.CENTER);
		}
		
		for (int i = 0, horizontal = 0, vertical = 0; i < daysLabelList.size(); i++, ++horizontal){
			if (horizontal > 6){
				vertical++;
				horizontal = 0;
			}
			GridPane.setConstraints(daysLabelList.get(i), horizontal, vertical);	
		}

		for (int i = 1; i < 13; i++){
			yearList.add(monthDayFormatter(Integer.toString(i)));
		}
		
		nextMonthButton.setOnAction(e -> {monthLabel.setText(selectedMonthPosition.nextMonth());
											currentPosition = selectedMonthPosition.getIntPosition();
												changeLabels();
		});
		
		prevMonthButton.setOnAction(e -> {monthLabel.setText(selectedMonthPosition.previousMonth());
											currentPosition = selectedMonthPosition.getIntPosition();
												changeLabels();		
		});
	}
	
	private static ArrayList<String> monthDayFormatter(String month){
		ArrayList<String> arrayy = new ArrayList<String>();
			for (int i = 1; i < 31; i++){
				if(i < 10 && Integer.parseInt(month) < 10){
					arrayy.add(new String("0" + month + "//0" + Integer.toString(i)));}
			 
				else if (i < 10 && Integer.parseInt(month) > 9){
					arrayy.add(new String(month + "//0" + Integer.toString(i)));}
			 
				else if (i > 9 && Integer.parseInt(month) < 10){
					arrayy.add(new String("0" + month + "//" + Integer.toString(i)));}
			
				else if (i > 9 && Integer.parseInt(month) > 9){
					arrayy.add(new String(month + "//" + Integer.toString(i)));}
			 }
		return arrayy;
	 }
	
	private void changeLabels(){
		for (int i = 0; i < yearList.get(currentPosition).size(); i++){
			daysLabelList.get(i).setText((readStoredCalorieSum((yearList.get(currentPosition)).get(i), storedUserCalorieData)));
		}
	}
	
	private void setStage(){
		GridPane pane3 = new GridPane();
		StackPane root = new StackPane();
		root.getChildren().addAll(imagePane, labelPane);
		GridPane.setConstraints(root, 0, 1);
		pane3.getChildren().addAll(root, topHBox);
		Scene scene = new Scene(pane3);
		scene.getStylesheets().add("format.css");
		stage.setScene(scene);
		stage.show();
	}
		
	public static String getCalSumPercent(String date, File file){
			
		String calSum = null;
		String strWithoutChar = null;
		String strWithoutChar1 = null;
		String last = null;
		String percent = null;
			
		try { String textLine;
			if (searchText(file,date) == null){
				calSum = "";
				return calSum;
			}
			else if (searchText(file,date) != null){	
				textLine = searchText(file, date);
				int z = textLine.indexOf('X');
				int y = textLine.indexOf('Y');
				int a = z+1;
				int b = z+2;
				int c = z+3;
				int d = z+4;
				
				int a1 = y+1;
				int a2 = y+2;
				int a3 = y+3;
			
				String mychar = String.valueOf(textLine.charAt(a));
				String mychar1 = String.valueOf(textLine.charAt(b));
				String mychar2 = String.valueOf(textLine.charAt(c));
				String mychar3 = String.valueOf(textLine.charAt(d));
				
				String percent1 = String.valueOf(textLine.charAt(a1));
				String percent2 = String.valueOf(textLine.charAt(a2));
				String percent3 = String.valueOf(textLine.charAt(a3));
				
				calSum = mychar + mychar1 + mychar2 + mychar3;
				
				if (calSum.indexOf(' ') >= 0){
					String[] array = calSum.split(" ");
						calSum = array[0];
				}
				
				percent = percent1 + percent2 + percent3;
				
				if (percent.indexOf('q') >= 0){
					int i = percent.indexOf("q");
				strWithoutChar = new StringBuilder(percent).deleteCharAt(i).toString();
					percent = strWithoutChar;
					
				}
				if (percent.indexOf(".") >= 0){
					int i = percent.indexOf(".");
					strWithoutChar1 = new StringBuilder(percent).deleteCharAt(i).toString();
					percent = strWithoutChar1;
				}
				
				last = calSum.concat(percent);
				
				return percent;

			}
		} catch (IOException e) {
			}
	return last;
	}
	
	private static String searchText(File filePath, String searchQuery) throws IOException{
		searchQuery = searchQuery.trim();
		BufferedReader br = null;
			
		try{ br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			String line;
				
			while ((line = br.readLine()) != null){
				if (!line.contains(searchQuery)) {
						continue;
				}
						
				else if (line.contains(searchQuery)){
					return line;
				}
				return line;
			}
		}
		finally{
			try{ if (br != null){
					br.close();
					}
			}catch (Exception e){
				System.out.println("Error reading txt");
			}
				
		}
		return null;
		}

}
