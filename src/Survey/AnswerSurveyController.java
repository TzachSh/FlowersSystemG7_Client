package Survey;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Employee;
import PacketSender.Command;
import PacketSender.Packet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class AnswerSurveyController implements Initializable{
	
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
	
	@FXML Button btnSubmit;
	
	private ArrayList<Survey> surveyList;
	private ArrayList<SurveyQuestion> surveyQuestionList;
	private ArrayList<Question> questionList;
	
	public static Employee branchEmployee;

	/**
	 * Show the scene view of complains management
	 * 
	 * @param primaryStage - current stage to build
	 */
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
	
	private void setSliderChangeListener(Slider slider , Label label)
	{
		slider.valueProperty().addListener((obs, oldval, newVal) -> 
	    label.setText(String.format("%d",newVal.intValue())));
	}
	
	private void initSliders()
	{
		setSliderChangeListener(sliderA1,lblA1);
		setSliderChangeListener(sliderA2,lblA2);
		setSliderChangeListener(sliderA3,lblA3);
		setSliderChangeListener(sliderA4,lblA4);
		setSliderChangeListener(sliderA5,lblA5);
		setSliderChangeListener(sliderA6,lblA6);
	}
	
	private void initData()
	{
		ArrayList<Object> paramListSurvey = new ArrayList<>();
		ArrayList<Object> paramListQuestion = new ArrayList<>();
		ArrayList<Object> paramListSurveyQuestion = new ArrayList<>();
		
		Packet packet = new Packet();
		packet.addCommand(Command.getSurvey);
		packet.addCommand(Command.getQuestions);
				
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initSliders();
	}
	
}
