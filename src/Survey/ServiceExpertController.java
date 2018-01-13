package Survey;

import Branches.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class ServiceExpertController {
	
	@FXML private Slider sliderA1;
	@FXML private Slider sliderA2;
	@FXML private Slider sliderA3;
	@FXML private Slider sliderA4;
	@FXML private Slider sliderA5;
	@FXML private Slider sliderA6;
	
	@FXML private Label lblA1;
	@FXML private Label lblA2;
	@FXML private Label lblA3;
	@FXML private Label lblA4;
	@FXML private Label lblA5;
	@FXML private Label lblA6;
	
	@FXML private Label lblQ1;
	@FXML private Label lblQ2;
	@FXML private Label lblQ3;
	@FXML private Label lblQ4;
	@FXML private Label lblQ5;
	@FXML private Label lblQ6;
	
	@FXML Button btnSubmit;
	
	static Employee serviceExpert;
	
	
	public void start(Stage primaryStage) {
		
		String title = "Answer Survey";
		String srcFXML = "/Survey/AnswerSurveyUI.fxml";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	

}
