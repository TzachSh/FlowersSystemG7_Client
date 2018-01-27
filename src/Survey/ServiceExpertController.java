package Survey;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Employee;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

/***
 * 
 * Controller Class to define the functionality of Service Expert Survey Conclusion
 *
 */
public class ServiceExpertController implements Initializable {
	
	/*
	 * FXML components to define the slider to show the answers average facility
	 */
	@FXML private Slider sliderA1;
	@FXML private Slider sliderA2;
	@FXML private Slider sliderA3;
	@FXML private Slider sliderA4;
	@FXML private Slider sliderA5;
	@FXML private Slider sliderA6;
	
	/*
	 * FXML components to define the labels for showing the numerical answers average
	 */
	@FXML private Label lblA1;
	@FXML private Label lblA2;
	@FXML private Label lblA3;
	@FXML private Label lblA4;
	@FXML private Label lblA5;
	@FXML private Label lblA6;
	
	/*
	 * FXML components to define the labels for showing the questions
	 */
	@FXML private Label lblQ1;
	@FXML private Label lblQ2;
	@FXML private Label lblQ3;
	@FXML private Label lblQ4;
	@FXML private Label lblQ5;
	@FXML private Label lblQ6;
	
	/*
	 * Labels to define the duration of the survey
	 */
	@FXML private Label lblStart;
	@FXML private Label lblEnd;
	
	/*
	 * FXML component to define the conclusion text area field
	 */
	@FXML private TextArea txtConclusion;
	/*
	 * FXML component to define the submit button
	 */
	@FXML Button btnSubmit;
	
	/**
	 * The relevant survey to conclude 
	 */
	private Survey survey;
	/**
	 * survey question
	 */
	private ArrayList<SurveyQuestion> surveyQuestionList;
	/**
	 * all questions
	 */
	private ArrayList<Question> questionList;
	/**
	 * average answers for surveys
	 */
	private ArrayList<AnswerSurvey> averageAnswerSurveyList;
	public static Employee serviceExpert;
	
	/**
	 * Limited characters to insert in to the TextArea
	 */
	private final int TEXTLIMIT = 200;
	
	/**
	 * Save current stage
	 */
	private static Stage stage;
	
	/***
	 * 
	 * @param primaryStage to set the scene properties
	 * Perform the stage initialization and show the scene window
	 */
	public void start(Stage primaryStage) {
		
		String title = "Survey Analyze";
		String srcFXML = "/Survey/ServiceExpertUI.fxml";
		stage = primaryStage;
		stage.setResizable(false);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			setOnCloseListener(stage);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	/***
	 * 
	 * @param survey to be set as the relevant survey for the conclusion
	 */
	public void setSurvey(Survey survey)
	{
		this.survey = survey;
	}
	/***
	 * Initialization of the lists to handle the data
	 */
	private void initData()
	{
		ArrayList<Object> paramListSurvey = new ArrayList<>();
		ArrayList<Object> paramListQuestion = new ArrayList<>();
		ArrayList<Object> paramListSurveyQuestion = new ArrayList<>();
		ArrayList<Object> paramListAverageAnswers = new ArrayList<>();
		ArrayList<Object> paramListConclusionList = new ArrayList<>();
		paramListAverageAnswers.add(survey.getId());
		
		Packet packet = new Packet();
		packet.addCommand(Command.getSurvey);
		packet.addCommand(Command.getQuestions);
		packet.addCommand(Command.getSurveyQuestions);
		packet.addCommand(Command.getAverageAnswersBySurveyId);
		packet.addCommand(Command.getConclusions);
		
		packet.setParametersForCommand(Command.getSurvey, paramListSurvey);
		packet.setParametersForCommand(Command.getQuestions, paramListQuestion);
		packet.setParametersForCommand(Command.getSurveyQuestions, paramListSurveyQuestion);
		packet.setParametersForCommand(Command.getAverageAnswersBySurveyId, paramListAverageAnswers);
		packet.setParametersForCommand(Command.getConclusions, paramListConclusionList);
		
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
					displayDates(survey);
					initSliders(averageAnswerSurveyList);
					initAnswerLabels(averageAnswerSurveyList);
				}
			}
		});
		sender.start();
	}
	
	/***
	 * 
	 * @param slider add listener to slider
	 * @param value new value
	 * Set the value of the slider by the relevant average answers 
	 */
	private void setSliderValue(Slider slider , double value)
	{
		slider.setValue(value);
	}
	/***
	 * 
	 * @param label update
	 * @param value new value
	 * Show the numeric number in the label of the slider
	 */
	private void initLabelsAverageAnswer(Label label , double value)
	{
		label.setText(String.format("%.2f",value));
	}
	
	/***
	 *  
	 * @param averageAnswerSurveyList
	 * Initialization of the labels of each answer results to show the average for each question
	 */
	private void initAnswerLabels(ArrayList<AnswerSurvey> averageAnswerSurveyList)
	{
		if (averageAnswerSurveyList.size() > 0) {
			initLabelsAverageAnswer(lblA1, averageAnswerSurveyList.get(0).getAvgaAnswerForQuestion());
			initLabelsAverageAnswer(lblA2, averageAnswerSurveyList.get(1).getAvgaAnswerForQuestion());
			initLabelsAverageAnswer(lblA3, averageAnswerSurveyList.get(2).getAvgaAnswerForQuestion());
			initLabelsAverageAnswer(lblA4, averageAnswerSurveyList.get(3).getAvgaAnswerForQuestion());
			initLabelsAverageAnswer(lblA5, averageAnswerSurveyList.get(4).getAvgaAnswerForQuestion());
			initLabelsAverageAnswer(lblA6, averageAnswerSurveyList.get(5).getAvgaAnswerForQuestion());
		}
	}
	
	/***
	 * 
	 * @param averageAnswerSurveyList to set the sliders values
	 * Set the slider values with the average value of each question in correspondence  
	 */
	private void initSliders(ArrayList<AnswerSurvey> averageAnswerSurveyList)
	{
		if(averageAnswerSurveyList.size() > 0) {
			setSliderValue(sliderA1, averageAnswerSurveyList.get(0).getAvgaAnswerForQuestion());
			setSliderValue(sliderA2, averageAnswerSurveyList.get(1).getAvgaAnswerForQuestion());
			setSliderValue(sliderA3, averageAnswerSurveyList.get(2).getAvgaAnswerForQuestion());
			setSliderValue(sliderA4, averageAnswerSurveyList.get(3).getAvgaAnswerForQuestion());
			setSliderValue(sliderA5, averageAnswerSurveyList.get(4).getAvgaAnswerForQuestion());
			setSliderValue(sliderA6, averageAnswerSurveyList.get(5).getAvgaAnswerForQuestion());
		}
		
	}
	
	/***
	 * 
	 * @param survey to get its questions
	 * @return array list with the specific survey questions
	 */
	private ArrayList<Question> getQuestionsOfSurvey(Survey survey)
	{
		ArrayList<Question> questionsOfSurvey = new ArrayList<>();
		
		for(SurveyQuestion surveyQuestion : survey.getSurveyQuestionList())
			for(Question question : questionList)
				if(surveyQuestion.getQuestionId() == question.getId())
					questionsOfSurvey.add(question);
		
		return questionsOfSurvey;
	}
	/***
	 * 
	 * @param question to set
	 * @param label to be set with the question
	 * 
	 * Set the label with the value of the question to be shown
	 */
	private void setLabelQuestion(Question question , Label label)
	{
		label.setText(question.getQuesiton());
	}
	
	/***
	 * Display all of the question in the relevant labels
	 */
	private void displayQuestion()
	{
		attachQuestionToSurvey(survey);
		ArrayList<Question> questionsToDisplay = getQuestionsOfSurvey(survey);
		
		if (questionsToDisplay.size() > 0) {
			setLabelQuestion(questionsToDisplay.get(0), lblQ1);
			setLabelQuestion(questionsToDisplay.get(1), lblQ2);
			setLabelQuestion(questionsToDisplay.get(2), lblQ3);
			setLabelQuestion(questionsToDisplay.get(3), lblQ4);
			setLabelQuestion(questionsToDisplay.get(4), lblQ5);
			setLabelQuestion(questionsToDisplay.get(5), lblQ6);
		}
	}
	
	/***
	 * 
	 * @param survey to show his dates
	 * Display the survey's durations in the labels
	 */
	private void displayDates(Survey survey)
	{
		if (lblStart != null && lblEnd != null) {
			lblStart.setText(survey.getActivatedDate().toString());
			lblEnd.setText(survey.getClosedDate().toString());
		}
	}
	/***
	 * 
	 * @param survey to get his questions attached in the list
	 * Aattch the questions of the survey to this survey
	 */
	private void attachQuestionToSurvey(Survey survey)
	{
		ArrayList<SurveyQuestion> setSurveyQuestionList = new ArrayList<>();
		
		for(SurveyQuestion surveyQuestion : surveyQuestionList)
			if(surveyQuestion.getSurveyId() == survey.getId())
				setSurveyQuestionList.add(new SurveyQuestion(surveyQuestion.getId(), survey.getId(), surveyQuestion.getQuestionId()));
		
		survey.setSurveyQuestionList(setSurveyQuestionList);
	}
	
	/***
	 * 
	 * @param event of the action
	 * Submit the conclusion form with the Expert Service conclusion,
	 * Then save it in the server's database
	 */
	@FXML
	private void onSubmitPressedHandle(Event event)
	{
		String conclusion  =txtConclusion.getText().replaceAll("'", "\\'");
		
		if(conclusion.isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR , "Fill in the conclusion");
			alert.show();
			return;
		}
		SurveyConclusion surveyConclusion = new SurveyConclusion(serviceExpert.geteId(), conclusion,survey.getId());
		
		ArrayList<Object> paramListAddConclusion = new ArrayList<>();
		
		paramListAddConclusion.add(surveyConclusion);
		
		Packet packet = new Packet();
		packet.addCommand(Command.addConclusion);
		packet.setParametersForCommand(Command.addConclusion, paramListAddConclusion);
		
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
					SurveyManagementController surveyController = new SurveyManagementController();
					SurveyManagementController.employee = serviceExpert;
					surveyController.start(new Stage());
				}
			}
		});
		sender.start();
	}
	/***
	 * 
	 * @param event of the action
	 * Close off the window of the form after submitting
	 */
	private void closeWindow(Event event)
	{
		((Node) event.getSource()).getScene().getWindow().hide();
	}
	/***
	 * 
	 * @param stage to handle it's close
	 * Set handle when the stage is closed to open the main survey management menu
	 */
	private void setOnCloseListener(Stage stage)
	{
		 stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  SurveyManagementController surveyController = new SurveyManagementController();
					SurveyManagementController.employee = serviceExpert;
					surveyController.start(new Stage());
	          }
	      });        
	}
	
	/***
	 * 
	 * @param textArea - to set his text limit.
	 *  Prevent writing more characters then the limited in database
	 */
	private void setTextAreaLengthProperty(TextArea textArea) {
		textArea.lengthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				if (newValue.intValue() > oldValue.intValue()) {
					/**
					 *  Check if the new character is greater than LIMIT
					 */
					if (textArea.getText().length() >= TEXTLIMIT) {

						/**
						*if it's the limited character then just setText to previous one
						*/
						textArea.setText(textArea.getText().substring(0, TEXTLIMIT));
					}
				}
			}
		});
	}
	/***
	 * Initialize the data at the first program level
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		setTextAreaLengthProperty(txtConclusion);
		initData();
	}
}
