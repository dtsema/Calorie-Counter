import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UserData {
	
	CalorieGraph calorieGraph;
	private String nameInput, passwordInput;
	private String registerName, registerPass, registerAge, registerWeight;
	private File userPasswordSaltFile, userPasswordEncryptedFile, savedUserDataFile;
	private byte[] saltByte, encryptedByte;
	
	public void authenticateUserRetrieveData() throws IOException{
		
		if (checkIfUserExists()){
			createUserFiles();
			readConvertTextFiles();
		
			try {
				if (userAuthenticated()){
					extractStoredUserData();
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
	
	private boolean checkIfUserExists(){
		boolean userExists = false;
		nameInput = LoginPageFormatted.userNameInputField.getText();
		passwordInput = LoginPageFormatted.passwordInputLogin.getText(); 
		
		if (nameInput.trim().isEmpty() || passwordInput.trim().isEmpty()){
			createErrorAlert("Please enter username and password", "");	
		}
		else{
			File storedUserNameFile = new File(nameInput);
				if (storedUserNameFile.exists()){
					userExists = true;
				}
				else {
					userExists = false;
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
	
	private void createUserFiles(){
		userPasswordSaltFile = new File(nameInput + "\\salt.txt");
		userPasswordEncryptedFile = new File(nameInput + "\\encrypted.txt");
		savedUserDataFile = new File(nameInput + "\\userdata.txt");
	}
	
	private void readConvertTextFiles() throws IOException{
		String salt = readTextDoc(userPasswordSaltFile);
		String encrypted = readTextDoc(userPasswordEncryptedFile);
		saltByte = Base64.decode(salt);
		encryptedByte = Base64.decode(encrypted);
	}
	@SuppressWarnings("resource")
	private static String readTextDoc(File filePath) throws IOException{
		BufferedReader br = null;
		String line = null;
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			while ((line = br.readLine()) != null){
				return line;
			}
			if (br != null){
					br.close();
			}
		}catch (Exception e){
				
			}
		return line;
	}
	
	public boolean userAuthenticated() throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encryptedAttemptedPassword = getEncryptedPassword(passwordInput, saltByte);
		return Arrays.equals(encryptedByte, encryptedAttemptedPassword);
	}
	
	
	private void extractStoredUserData(){
		try {
			String userData = readWholeDoc();
			String[] userDataArray = userData.split(System.lineSeparator());
		
			boolean radioBut = false;
			if (userDataArray[3].equals("true")){
				radioBut = true;
			}
			else radioBut = false;
			
			int actlvl = 0;
			if (userDataArray[4].equals("1")){
				actlvl = 1;	
			}
			else if(userDataArray[4].equals("2")){
				actlvl = 2;}
			else if(userDataArray[4].equals("3")){
				actlvl = 3;}
			calorieGraph = new CalorieGraph("Foods Consumed", userDataArray[0], userDataArray[1], userDataArray[2], radioBut,
					actlvl);
			calorieGraph.createCalorieGraph();
		} catch (IOException e1) {		
			}
	}
	
	private String readWholeDoc() throws IOException{
		 BufferedReader file = new BufferedReader(new FileReader(savedUserDataFile));
			String line;
			StringBuffer inputBuffer = new StringBuffer();
			
			while ((line = file.readLine()) != null){
				inputBuffer = inputBuffer.append(line);
				inputBuffer.append(System.lineSeparator());
			}
			
			String inputStr = inputBuffer.toString();
			file.close();
			return inputStr;
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
		createNewUserAuthentication();
		File file = new File(registerName + "\\username.txt");
	
		if (file.exists()){
			createErrorAlert("User Exists", "Error, User is already in system");
		}
		else{
			try{	
				byte[] salt = generateSalt();
				String resalt = Base64.encodeBytes(salt);
				String encrypted = Base64.encodeBytes(getEncryptedPassword(registerPass, salt));
			
				file.getParentFile().mkdir();
				file.createNewFile();
				
				File userName = new File(registerName + "\\username.txt");
				File userData = new File(registerName + "\\userdata.txt");
				File log = new File(registerName + "\\salt.txt");
				File log2 = new File(registerName + "\\encrypted.txt");
				File monthlyInfo = new File(registerName + "\\monthlyInfo.txt");
				
				
				FileWriter writer = new FileWriter(log, true);
				BufferedWriter reader = new BufferedWriter(writer);
			
				FileWriter writer2 = new FileWriter(log2, true);
				BufferedWriter reader2 = new BufferedWriter(writer2);
			
				FileWriter writer3 = new FileWriter(userName, true);
				BufferedWriter reader3 = new BufferedWriter(writer3);
			
				FileWriter writer4 = new FileWriter(userData, true);
				BufferedWriter reader4 = new BufferedWriter(writer4);
			
				FileWriter writer5 = new FileWriter(monthlyInfo, true);
				BufferedWriter reader5 = new BufferedWriter(writer5);

				reader.write(resalt);
				reader.close();
			
				reader2.write(encrypted);
				reader2.close();
			
				reader3.write(registerName);
				reader3.close();
			
				reader4.write(registerName+ System.lineSeparator() + registerWeight 
							+ System.lineSeparator() + registerAge + System.lineSeparator() 
								+ chosenGenderMale() + System.lineSeparator()
									+ activeLevel() );
				reader4.close();
			
				reader5.write("");
				reader5.close();
							
		}catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException E){
			}
		
		try{
			
			if (registerName == null || registerName.trim().isEmpty()||
				registerWeight == null || registerWeight.trim().isEmpty()||
						registerAge == null || registerAge.trim().isEmpty())
			{
			createErrorAlert("Error", "You did not fill out all of the required information");
		}
		else {
			calorieGraph = new CalorieGraph("Foods Consumed", registerName, 
					registerWeight, registerAge, 
					chosenGenderMale(),activeLevel());
			calorieGraph.createCalorieGraph();
		CalorieCounterApp.loginStage.close();
		}	
		}catch (Exception E){}
		}
	}
	
	private void createNewUserAuthentication(){
		registerName= LoginPageFormatted.RegisterNameInputField.getText();
		registerPass=  LoginPageFormatted.passwordInputRegister.getText();
		registerAge= LoginPageFormatted.ageInputField.getText();
		registerWeight= LoginPageFormatted.weightInputField.getText();
	}
	
	
	private byte[] generateSalt() throws NoSuchAlgorithmException {
		   SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		   byte[] salt = new byte[8];
		   random.nextBytes(salt);
		 
		   return salt;
	}
	
	private boolean chosenGenderMale(){
		boolean male = false;
		if(LoginPageFormatted.male.isSelected())
			male = true;
	
		if(LoginPageFormatted.female.isSelected())
			male = false;
		
		return male;
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