import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;


public class LineChartPage {
	int position;
	private VBox vBox;
	private String userName, storedMonth;
	private LineChart<Number, Number> lineChart;
	XYChart.Series<Number, Number> series; 
	private Position monthPosition;
	ArrayList<Pair<Integer, Integer>> constraintsList;
	Properties propSum, propPercent;
	InputStream inputSum, inputPercent;
	private ArrayList<LinkedHashMap<String, String>> months;
	
	
	public LineChartPage(String userName){
		this.userName = userName;
	}
	
	public void createLineChartPage() throws IndexOutOfBoundsException{
		
		fetchStoredCaloriePercents();
		
		createLineChart();
			
		createChartFromMapData();
			
		createChangeMonthButtons();	 
		  
		setStage();
		  
		}
	
	private void fetchStoredCaloriePercents(){
		
		try{
			loadProperties();
			
			propertiesIntoMap();
		
		}catch(IOException io){
			io.printStackTrace();
		}finally{
			if (inputSum != null && inputPercent != null) {
				closeStreams();
			}
		}
	}
	
	private void closeStreams(){
		try{
			inputSum.close();
			inputPercent.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void loadProperties() throws IOException{
		propSum = new Properties();
		propPercent = new Properties();
		inputSum = null;
		inputPercent = null;
		inputSum = new FileInputStream(userName+ "Sum.properties");
		inputPercent = new FileInputStream(userName+ "Percent.properties");
		propSum.load(inputSum);
		propPercent.load(inputPercent);
		
	}
	
	private void propertiesIntoMap(){
		makeMonthLists();
		
		iteratePropertyKeys();
	}
	
	private void makeMonthLists(){
		months = new ArrayList<>();
		for (int i = 0; i < 12; i++){
			months.add(new LinkedHashMap<String, String>());
			
		}
	}
	
	private void iteratePropertyKeys(){
		Set<Object> keys = propSum.keySet();
		ArrayList<Object> keysList = new ArrayList<>(keys);
		
		for (int i = 0; i < keysList.size(); i++){
			String date = keysList.get(i).toString();
			String percent = propPercent.getProperty(date);
			
			String day = date.substring(4, 6);
			storedMonth = date.substring(0, 2);
			
			String[] percentSplit = new String[2];
			percentSplit = percent.split("\\.");
			String percentNoDecimal = (percentSplit[0]);
			
			months.get(correspondingMonthArray()).put(day, percentNoDecimal);
		}
	}
	
	private Integer correspondingMonthArray(){
		return Integer.parseInt(storedMonth)-1;
	}
	
	private void createLineChart(){

		final NumberAxis xAxis = new NumberAxis(1, 31, 1);
	    final NumberAxis yAxis = new NumberAxis();
	    xAxis.setLabel("Date");
	    yAxis.setLabel("% DV");
	    xAxis.setMinorTickVisible(false);
	  
	    lineChart = new LineChart<Number, Number>(
	        xAxis, yAxis);
	    
	    monthPosition = new Position();
	    lineChart.setTitle(monthPosition.getPositionMonth());
	   
	    series = new XYChart.Series<Number, Number>();
	    series.setName("My Data");
	}
	
	private void createChartFromMapData(){
		position = monthPosition.getIntPosition();
		
		 try{
	    		for(int i = 0; i < months.get(position).size(); i++){
	    	    	series.getData().add(new XYChart.Data<Number, Number>(Integer.parseInt((months.get(position).keySet().toArray()[i].toString())),
	    	    			Integer.parseInt((months.get(position).values().toArray()[i].toString()))));
   		    }
	    		
	    	}catch(Exception e5){
	    		e5.printStackTrace();
	    	}
	}
	
	private void createChangeMonthButtons(){
		 Button next = new Button("next");
		 Button prev = new Button("previous");
		 
		 HBox hbox = new HBox(200);
		 hbox.getChildren().addAll(next, prev);
		 vBox = new VBox();
		 vBox.getChildren().addAll(lineChart, hbox);
		    	
		 next.setOnAction(e -> {
			 series.getData().clear();
			 lineChart.setTitle(monthPosition.nextMonth());
			 createChartFromMapData();
		  });
		    
		 prev.setOnAction(e -> {
			 series.getData().clear();
			 lineChart.setTitle(monthPosition.previousMonth());
			 createChartFromMapData();
		  });
		 lineChart.getData().add(series);
	}
	
	private void setStage(){
		 Stage stage = new Stage(); 
		 Scene scene = new Scene(vBox, 800, 600);
		 stage.setScene(scene);
		 stage.show();
	}
}

