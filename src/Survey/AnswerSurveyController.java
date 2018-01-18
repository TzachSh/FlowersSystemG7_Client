package Survey;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Employee;
import Customers.ComplainsController;
import Login.LoginController;
import Login.ManagersMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;
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
	
	@FXML private Label lblQ1;
	@FXML private Label lblQ2;
	@FXML private Label lblQ3;
	@FXML private Label lblQ4;
	@FXML private Label lblQ5;
	@FXML private Label lblQ6;
	
	@FXML Button btnSubmit;
	
	private Survey activeSurvey;
	
	private ArrayList<Survey> surveyList;
	private ArrayList<SurveyQuestion> surveyQuestionList;
	private ArrayList<Question> questionList;
	
	private Stage stage;
	public static Employee branchEmployee = (Employee)LoginController.userLogged;

	/**
	 * Show the scene view of complains management
	 * 
	 * @param primaryStage - current stage to build
	 */
	public void start(Stage primaryStage) {
		
		String title = "Answer Survey";
		String srcFXML = "/Survey/AnswerSurveyUI.fxml";
		stage = primaryStage;
		
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
		packet.addCommand(Command.getSurveyQuestions);
		
		packet.setParametersForCommand(Command.getSurvey, paramListSurvey);
		packet.setParametersForCommand(Command.getQuestions, paramListQuestion);
		packet.setParametersForCommand(Command.getSurveyQuestions, paramListSurveyQuestion);
		
		SystemSender sender = new SystemSender(packet);
		sender.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{
					surveyList = p.<Survey>convertedResultListForCommand(Command.getSurvey);
					questionList = p.<Question>convertedResultListForCommand(Command.getQuestions);
					surveyQuestionList = p.<SurveyQuestion>convertedResultListForCommand(Command.getSurveyQuestions);
					
					displayQuestion();
				}
			}
		});
		sender.start();
	}
	
	private ArrayList<Integer> getAllAnswers()
	{
		ArrayList<Integer> answersList = new ArrayList<>();
		
		answersList.add(getAnswer(lblA1));
		answersList.add(getAnswer(lblA2));
		answersList.add(getAnswer(lblA3));
		answersList.add(getAnswer(lblA4));
		answersList.add(getAnswer(lblA5));
		answersList.add(getAnswer(lblA6));
		
		return answersList;
	}
	
	private Integer getAnswer(Label label)
	{
		return Integer.parseInt(label.getText());
	}
	
	@FXML
	private void onSubmitPressedHandler(Event event)
	{ 
		ArrayList<Integer> answersList = getAllAnswers();
		int curAnswerIndex = 0;
		
		ArrayList<Object> paramListAnswerSurvey = new ArrayList<>();
		
		for(SurveyQuestion surveyQuestion : activeSurvey.getSurveyQuestionList())
			paramListAnswerSurvey.add(new AnswerSurvey(surveyQuestion.getId(), branchEmployee.getBranchId(), answersList.get(curAnswerIndex++)));
		
		Packet packet = new Packet();
		packet.addCommand(Command.addAnswerSurvey);
		packet.setParametersForCommand(Command.addAnswerSurvey, paramListAnswerSurvey);
		
		SystemSender sender = new SystemSender(packet);
		
		sender.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{
					Alert alert = new Alert(AlertType.INFORMATION,"The answers have been submited!");
					alert.showAndWait();
					((Node) event.getSource()).getScene().getWindow().hide();
					ManagersMenuController mc = new ManagersMenuController();
					try {
						mc.start(new Stage());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		sender.start();
	}
	
	private Survey getActiveSurvey()
	{
		Survey retSurvey = null;
		for(Survey survey : surveyList)
			if(survey.isActive())
				retSurvey = survey;
		
		return retSurvey;
	}
	
	private void attachQuestionToSurvey(Survey survey)
	{
		ArrayList<SurveyQuestion> setSurveyQuestionList = new ArrayList<>();
		
		for(SurveyQuestion surveyQuestion : surveyQuestionList)
			if(surveyQuestion.getSurveyId() == survey.getId())
				setSurveyQuestionList.add(new SurveyQuestion(surveyQuestion.getId(), survey.getId(), surveyQuestion.getQuestionId()));
		
		survey.setSurveyQuestionList(setSurveyQuestionList);
	}
	
	private ArrayList<Question> getQuestionsOfSurvey(Survey survey)
	{
		ArrayList<Question> questionsOfSurvey = new ArrayList<>();
		
		for(SurveyQuestion surveyQuestion : survey.getSurveyQuestionList())
			for(Question question : questionList)
				if(surveyQuestion.getQuestionId() == question.getId())
					questionsOfSurvey.add(question);
		
		return questionsOfSurvey;
	}
	
	private void setLabelQuestion(Question question , Label label)
	{
		label.setText(question.getQuesiton());
	}
	
	private void displayQuestion()
	{
	    activeSurvey = getActiveSurvey();
	    if(activeSurvey == null) {
	    	Alert alert = new Alert(AlertType.INFORMATION,"The is no active surevy!");
			alert.showAndWait();
			stage.close();
			ManagersMenuController mc = new ManagersMenuController();
			try {
				mc.start(new Stage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    else {	
		attachQuestionToSurvey(activeSurvey);
		ArrayList<Question> questionsToDisplay = getQuestionsOfSurvey(activeSurvey);
		
		setLabelQuestion(questionsToDisplay.get(0), lblQ1);
		setLabelQuestion(questionsToDisplay.get(1), lblQ2);
		setLabelQuestion(questionsToDisplay.get(2), lblQ3);
		setLabelQuestion(questionsToDisplay.get(3), lblQ4);
		setLabelQuestion(questionsToDisplay.get(4), lblQ5);
		setLabelQuestion(questionsToDisplay.get(5), lblQ6);
	    }
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initSliders();
		initData();
	}
	
}
