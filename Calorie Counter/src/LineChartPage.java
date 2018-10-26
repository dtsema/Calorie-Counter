import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class LineChartPage {
	int position;
	private VBox vBox;
	private String userName;
	private LineChart<Number, Number> lineChart;
	XYChart.Series<Number, Number> series; 
	public ArrayList<LinkedHashMap<String, String>> mapList;
	private Position monthPosition;
	
	public LineChartPage(String userName){
		this.userName = userName;
	}
	
	public void createLineChartPage() throws IndexOutOfBoundsException{
		   
		createMapOfStoredDatesPercents();
		   
		createLineChart();
			
		createChartFromMapData();
			
		createChangeMonthButtons();	 
		  
		setStage();
		  
		}
	
	private void createMapOfStoredDatesPercents(){
		
		File storedUserCalDataLog = new File(userName + "\\monthlyInfo.txt");
		 
		mapList = new  ArrayList<LinkedHashMap<String, String>>(); 
		   
		for (int i = 1; i < 13; i++){
			if (i < 10){
				List<String> month = createListForSearch("0" +Integer.toString(i));
				month = searchLog(month, storedUserCalDataLog);
				mapList.add(storedDataToDatePercent(month));
			}
			else {
				List<String> month = createListForSearch(Integer.toString(i));
				month = searchLog(month, storedUserCalDataLog);
				mapList.add(storedDataToDatePercent(month));
			}
		}
	}
	
	private static List<String> createListForSearch (String month){
		List<String> inputDaysList = new ArrayList<String>();
		int day = 1;
		
		for (int i = 1; i < 32; i++, day++){
			String dayy = Integer.toString(day);
			if (i < 10){
				inputDaysList.add(new String(month + "//0" + dayy));	
			}	
			else{	
				inputDaysList.add(new String(month + "//" + dayy));
			}
		}
		return inputDaysList;
	}
	
	private static List<String> searchLog (List<String> inputDaysList, File log1){
		List<String> daysLabelList = new ArrayList<String>();
		
		for (String dayList : inputDaysList ){
			if (CalendarChart.getCalSumPercent(dayList, log1) != ""){
				daysLabelList.add(dayList + " " + CalendarChart.getCalSumPercent(dayList, log1));
			}
			else{
				continue;
			}
			
		}
		daysLabelList.removeAll(Collections.singleton(""));
		daysLabelList.removeAll(Collections.singleton(null));
		
		return daysLabelList;
	}
	
	private static LinkedHashMap<String, String> storedDataToDatePercent (List<String> daysLabelList){
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		
		for (int i = 0; i < daysLabelList.size(); i++){
		
			String[] array = new String[2];
			String[] array2 = new String[2];
			
			array = daysLabelList.get(i).split(" ");
			String percent = (array[1]);
			
			
			array2 = array[0].split("//");
			String date = array2[1];
			map.put(date,  percent);
		}
		return map;
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
	    		for(int i = 0; i < mapList.get(position).size(); i++){
	    	    	series.getData().add(new XYChart.Data<Number, Number>(Integer.parseInt((mapList.get(position).keySet().toArray()[i].toString())),
	    	    			Integer.parseInt((mapList.get(position).values().toArray()[i].toString()))));
   		    }
	    		
	    	}catch(Exception e5){
	    		
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


