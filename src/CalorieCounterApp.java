import javafx.application.Application;
import javafx.stage.Stage;

public class CalorieCounterApp extends Application{
	public static Stage loginStage;
	LoginPageFormatted loginPageFormatted;
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage loginStage){
		loginPageFormatted = new LoginPageFormatted();
		loginStage = loginPageFormatted.creatStage();	
	}	
}