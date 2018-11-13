import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UserData {
	
	CalorieGraph calorieGraph;
	private String nameInput, passwordInput, salt, encrypted, name, weight, age,
	gender, activityLevel;
	private String registerName, registerPass, registerAge, registerWeight;
	private byte[] saltByte, encryptedByte;
	
	
	public void authenticateUserRetrieveData() throws IOException{
		
		if (UserExists()){
			extractStoredUserData();
			decodeStoredPassword();
		
			try {
				if (userAuthenticated()){
					createCalorieGraphPage();
					LoginPageFormatted.closeStage();
				}
				else{
					LoginPageFormatted.passwordInputLogin.clear();
					createErrorAlert("Invalid username or password", "");
					}
			}catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			}
		}
		else{
			createErrorAlert("Username does not exist", "Please create an account");
		}
	}
	
	private boolean UserExists(){
		boolean userExists = false;
		nameInput = LoginPageFormatted.userNameInputField.getText();
		passwordInput = LoginPageFormatted.passwordInputLogin.getText(); 
		
		if (nameInput.trim().isEmpty() || passwordInput.trim().isEmpty()){
			createErrorAlert("Please enter username and password", "");	
		}
		else{
			Properties prop = new Properties();
			InputStream input = null;

			try {
				input = new FileInputStream(nameInput + ".properties");
				prop.load(input);

				if (prop.isEmpty()){
					userExists = false;
				}
				else userExists = true;

			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return userExists;
	}
	
	private void createErrorAlert(String title, String content){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.show();
		}
	
	private void extractStoredUserData(){
		
		Properties prop = new Properties();
		InputStream input = null;
		
		try{
			input = new FileInputStream(nameInput+ ".properties");
			prop.load(input);
			
			 name = prop.getProperty("username");
			 weight = prop.getProperty("weight");
			 age = prop.getProperty("age");
			 gender = prop.getProperty("gender");
			 activityLevel = prop.getProperty("activityLevel");
			salt = prop.getProperty("salt");
			encrypted = prop.getProperty("encrypted");
			
			
		}catch(IOException io){
			io.printStackTrace();
		}finally{
			if (input != null) {
				try{
					input.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	private void decodeStoredPassword() throws IOException{
		saltByte = Base64.decode(salt);
		encryptedByte = Base64.decode(encrypted);
	}

	
	public boolean userAuthenticated() throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encryptedAttemptedPassword = getEncryptedPassword(passwordInput, saltByte);
		return Arrays.equals(encryptedByte, encryptedAttemptedPassword);
	}
	
	private void createCalorieGraphPage(){
		new CalorieGraph(name, weight, age, gender,
				Integer.parseInt(activityLevel)).createCalorieGraph();
	}

	
	public byte[] getEncryptedPassword(String passwordInput, byte[] saltByte) throws NoSuchAlgorithmException, InvalidKeySpecException{
		String algorithm = "PBKDF2WithHmacSHA1";
		int derivedKeyLength = 160;
		int iterations = 20000;
		KeySpec spec = new PBEKeySpec(passwordInput.toCharArray(), saltByte, iterations, derivedKeyLength);
		
		 SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		 return f.generateSecret(spec).getEncoded();
	}
	
	
	public void storeNewUserData(){
		getRegistrationData();
		storeRegistrationData();
		try{
			if (regFormIncomplete()){
			createErrorAlert("Error", "You did not fill out all of the required information");
			}
			else {
				createNewCalorieGraph();
			}	
		}catch (Exception E){}
	}
	
	
	private void getRegistrationData(){
		registerName= LoginPageFormatted.RegisterNameInputField.getText();
		registerPass=  LoginPageFormatted.passwordInputRegister.getText();
		registerAge= LoginPageFormatted.ageInputField.getText();
		registerWeight= LoginPageFormatted.weightInputField.getText();
	}
	
	private void storeRegistrationData(){
		Properties prop = new Properties();
		OutputStream output = null;
		FileInputStream file;
		
		try {
			file = new FileInputStream(registerName + ".properties");
			prop.load(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (!prop.isEmpty()){
			createErrorAlert("User Exists", "Error, User is already in system");
		}
		else{
			try {
				
				output = new FileOutputStream(registerName + ".properties");
				
				byte[] salt = generateSalt();
				String resalt = Base64.encodeBytes(salt);
				String encrypted = Base64.encodeBytes(getEncryptedPassword(registerPass, salt));
		
				prop.setProperty("username", registerName);
				prop.setProperty("salt", resalt);
				prop.setProperty("encrypted", encrypted);
				prop.setProperty("age", registerAge);
				prop.setProperty("weight", registerWeight);
				prop.setProperty("gender", getGender());
				prop.setProperty("activityLevel", Integer.toString(activeLevel()));
				
				prop.store(output, null);
				
			}catch(IOException | NoSuchAlgorithmException | InvalidKeySpecException io){
				io.printStackTrace();
			}finally{if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}}
		}
	}
	
	private byte[] generateSalt() throws NoSuchAlgorithmException {
		   SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		   byte[] salt = new byte[8];
		   random.nextBytes(salt);
		 
		   return salt;
	}
	
	private Boolean regFormIncomplete(){
		return registerName == null || registerName.trim().isEmpty()||
				registerWeight == null || registerWeight.trim().isEmpty()||
				registerAge == null || registerAge.trim().isEmpty();
	}
	
	private void createNewCalorieGraph(){
		calorieGraph = new CalorieGraph(registerName, registerWeight, registerAge, 
				getGender(),activeLevel());
			calorieGraph.createCalorieGraph();
			CalorieCounterApp.loginStage.close();
	}
	
	
	private String getGender(){
		String gender = "";
		if(LoginPageFormatted.male.isSelected())
			gender = "male";
	
		if(LoginPageFormatted.female.isSelected())
			gender = "female";
		
		return gender;
	}
	
	
	private int activeLevel(){
		int message = 0;
		if(LoginPageFormatted.sedentary.isSelected())
			message = 1;
		if(LoginPageFormatted.modActive.isSelected())
			message = 2;
		if(LoginPageFormatted.active.isSelected())
			message = 3;
		
		return message;
		
	}
}