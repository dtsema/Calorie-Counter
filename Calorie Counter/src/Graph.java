
public class Graph {

	private String food;
	private double quantity;
	private int calories;
	
	public Graph(){
		this.food = "";
		this.quantity = 0;
		this.calories = 0;
	}
	
	public Graph(String food, double quantity, int calories){
		this.food = food;
		this.quantity = quantity;
		this.calories = calories;
	}

	public String getFood() {
		return food;
	}

	public void setFood(String food) {
		this.food = food;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	



}
