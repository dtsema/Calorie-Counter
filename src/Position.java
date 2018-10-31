import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Position {
	private final List<String> year;
	private int position;

		Position() {
			Calendar calendar = Calendar.getInstance();    
			String currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		
			year = new ArrayList<>();
			year.add("January");
			year.add("February");
			year.add("March");
			year.add("April");
			year.add("May");
			year.add("June");
			year.add("July");
			year.add("August");
			year.add("September");
			year.add("October");
			year.add("November");
			year.add("December");
		   
			this.position = year.indexOf(currentMonth);
		    
		  }

		String nextMonth() {
		   ++position;
		   if (position >= year.size()) position = 0;
		   return year.get(position); 
		  }
		
		 String previousMonth() {
		   --position;
		   if (position < 0) position = year.size() - 1;
		   return year.get(position);
		  }
		 
		 int getIntPosition(){
			 return position;
		  }
		 
		 String getPositionMonth(){
			 return year.get(position);
		  }
		}