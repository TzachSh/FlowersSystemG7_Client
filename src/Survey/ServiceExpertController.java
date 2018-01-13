package Survey;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Employee;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ServiceExpertController implements Initializable {
	
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
	
	@FXML private TextArea txtConclusion;
	@FXML Button btnSubmit;
	
	private Survey survey;
	private ArrayList<SurveyQuestion> surveyQuestionList;
	private ArrayList<Question> questionList;
	private ArrayList<AnswerSurvey> averageAnswerSurveyList;
	public static Employee serviceExpert;
	
	public void start(Stage primaryStage) {
		
		String title = "Survey Analyze";
		String srcFXML = "/Survey/ServiceExpertUI.fxml";
		
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
	
	public void setSurvey(Survey survey)
	{
		this.survey = survey;
	}
	
	private void initData()
	{
		ArrayList<Object> paramListSurvey = new ArrayList<>();
		ArrayList<Object> paramListQuestion = new ArrayList<>();
		ArrayList<Object> paramListSurveyQuestion = new ArrayList<>();
		ArrayList<Object> paramListAverageAnswers = new ArrayList<>();
		paramListAverageAnswers.add(survey.getId());
		
		Packet packet = new Packet();
		packet.addCommand(Command.getSurvey);
		packet.addCommand(Command.getQuestions);
		packet.addCommand(Command.getSurveyQuestions);
		packet.addCommand(Command.getAverageAnswersBySurveyId);
		
		packet.setParametersForCommand(Command.getSurvey, paramListSurvey);
		packet.setParametersForCommand(Command.getQuestions, paramListQuestion);
		packet.setParametersForCommand(Command.getSurveyQuestions, paramListSurveyQuestion);
		packet.setParametersForCommand(Command.getAverageAnswersBySurveyId, paramListAverageAnswers);
		
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
					questionList = p.<Question>convertedResultListForCommand(Command.getQuestions);
					surveyQuestionList = p.<SurveyQuestion>convertedResultListForCommand(Command.getSurveyQuestions);
					averageAnswerSurveyList = p.<AnswerSurvey>convertedResultListForCommand(Command.getAverageAnswersBySurveyId);
					displayQuestion();
					initSliders(averageAnswerSurveyList);
					initAnswerLabels(averageAnswerSurveyList);
				}
			}
		});
		sender.start();
	}
	
	private void setSliderValue(Slider slider , int value)
	{
		slider.setValue(value);
	}
	
	private void initLabelsAverageAnswer(Label label , int value)
	{
		label.setText(String.format("%d",value));
	}
	
	private void initAnswerLabels(ArrayList<AnswerSurvey> averageAnswerSurveyList2)
	{
		initLabelsAverageAnswer(lblA1,averageAnswerSurveyList.get(0).getAnswer());
		initLabelsAverageAnswer(lblA2,averageAnswerSurveyList.get(1).getAnswer());
		initLabelsAverageAnswer(lblA3,averageAnswerSurveyList.get(2).getAnswer());
		initLabelsAverageAnswer(lblA4,averageAnswerSurveyList.get(3).getAnswer());
		initLabelsAverageAnswer(lblA5,averageAnswerSurveyList.get(4).getAnswer());
		initLabelsAverageAnswer(lblA6,averageAnswerSurveyList.get(5).getAnswer());
	}
	
	private void initSliders(ArrayList<AnswerSurvey> averageAnswerSurveyList)
	{
		setSliderValue(sliderA1,averageAnswerSurveyList.get(0).getAnswer());
		setSliderValue(sliderA2,averageAnswerSurveyList.get(1).getAnswer());
		setSliderValue(sliderA3,averageAnswerSurveyList.get(2).getAnswer());
		setSliderValue(sliderA4,averageAnswerSurveyList.get(3).getAnswer());
		setSliderValue(sliderA5,averageAnswerSurveyList.get(4).getAnswer());
		setSliderValue(sliderA6,averageAnswerSurveyList.get(5).getAnswer());
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
		attachQuestionToSurvey(survey);
		ArrayList<Question> questionsToDisplay = getQuestionsOfSurvey(survey);
		
		setLabelQuestion(questionsToDisplay.get(0), lblQ1);
		setLabelQuestion(questionsToDisplay.get(1), lblQ2);
		setLabelQuestion(questionsToDisplay.get(2), lblQ3);
		setLabelQuestion(questionsToDisplay.get(3), lblQ4);
		setLabelQuestion(questionsToDisplay.get(4), lblQ5);
		setLabelQuestion(questionsToDisplay.get(5), lblQ6);
	}
	
	
	private void attachQuestionToSurvey(Survey survey)
	{
		ArrayList<SurveyQuestion> setSurveyQuestionList = new ArrayList<>();
		
		for(SurveyQuestion surveyQuestion : surveyQuestionList)
			if(surveyQuestion.getSurveyId() == survey.getId())
				setSurveyQuestionList.add(new SurveyQuestion(surveyQuestion.getId(), survey.getId(), surveyQuestion.getQuestionId()));
		
		survey.setSurveyQuestionList(setSurveyQuestionList);
	}
	
	@FXML
	private void onSubmitPressedHandle(Event event)
	{
		SurveyConclusion surveyConclusion = new SurveyConclusion(serviceExpert.geteId(), txtConclusion.getText());
		
		ArrayList<Object> paramListConclusion = new ArrayList<>();
		paramListConclusion.add(surveyConclusion);
		
		Packet packet = new Packet();
		packet.addCommand(Command.addConclusion);
		packet.setParametersForCommand(Command.addConclusion, paramListConclusion);
		
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
					Alert alert = new Alert(AlertType.INFORMATION,"The conclusion has been saved");
					alert.showAndWait();
					closeWindow(event);
				}
			}
		});
		sender.start();
	}
	
	private void closeWindow(Event event)
	{
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initData();
	}
}
