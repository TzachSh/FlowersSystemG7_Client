package Survey;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Employee;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Users.User;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CreateSurveyController implements Initializable {
	@FXML 
	private TabPane tabSteps;
	@FXML
	private Button btnNext;
	@FXML 
	private Button btnPrev;
	@FXML
	private Button btnSave;
	@FXML 
	private TextField txtQ1;
	@FXML 
	private TextField txtQ2;
	@FXML 
	private TextField txtQ3;
	@FXML 
	private TextField txtQ4;
	@FXML 
	private TextField txtQ5;
	@FXML 
	private TextField txtQ6;
	@FXML 
	private TextField txtCfmQ1;
	@FXML 
	private TextField txtCfmQ2;
	@FXML 
	private TextField txtCfmQ3;
	@FXML 
	private TextField txtCfmQ4;
	@FXML 
	private TextField txtCfmQ5;
	@FXML 
	private TextField txtCfmQ6;
	@FXML
	private TextField txtSubject;
	
	public static Employee employee;
	
	private int curStep = 1;
	/**
	 * Show the scene view of complains management
	 * 
	 * @param primaryStage - current stage to build
	 */
	public void start(Stage primaryStage) {
		
		String title = "Survey Creator";
		String srcFXML = "/Survey/CreateSurveyUI.fxml";
		
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
	
	public CreateSurveyController(Employee employee)
	{
		
	}
	
	private void performNextStep()
	{
		int currentSelected = tabSteps.getSelectionModel().getSelectedIndex();
		if(currentSelected < 6) {
			curStep++;
			tabSteps.getSelectionModel().getSelectedItem().setDisable(true);
			tabSteps.getSelectionModel().select(currentSelected+1);
			tabSteps.getSelectionModel().getSelectedItem().setDisable(false);
		}
	}
	
	private void performPrevStep()
	{
		int currentSelected = tabSteps.getSelectionModel().getSelectedIndex();
		if(currentSelected > 0) {
			curStep--;
			tabSteps.getSelectionModel().getSelectedItem().setDisable(true);
			tabSteps.getSelectionModel().select(currentSelected-1);
			tabSteps.getSelectionModel().getSelectedItem().setDisable(false);
		}
	}
	
	private boolean stepValidation(int curStep , TextField txtField)
	{
		return (curStep == tabSteps.getSelectionModel().getSelectedIndex()+1) && (!txtField.getText().isEmpty());		
	}
	
	private void setConfirmationQuestionTextField(TextField txtField , String text)
	{
		txtField.setText(text);
	}
	
	private void setButtonsVisiblity(boolean isVisible)
	{
		btnNext.setVisible(isVisible);
		btnPrev.setVisible(isVisible);
	}
	
	@FXML
	private void nextPressedHandler(Event event)
	{
		if(stepValidation(1, txtQ1))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ1,txtQ1.getText());
		}
		else if(stepValidation(2, txtQ2))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ2,txtQ2.getText());
		}
		else if(stepValidation(3, txtQ3))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ3,txtQ3.getText());
		}
		else if(stepValidation(4, txtQ4))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ4,txtQ4.getText());
		}
		else if(stepValidation(5, txtQ5))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ5,txtQ5.getText());
		}
		else if(stepValidation(6, txtQ6))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ6,txtQ6.getText());
			setButtonsVisiblity(false);
		}
		else if(curStep == 7)
		{
			// Nothing to handle
		}
		else
		{
			Alert alert = new Alert(AlertType.INFORMATION,"Please Enter a question to finish this step");
			alert.show();
		}
	}
	
	private void resertComponents()
	{
		txtQ1.clear();
		txtQ2.clear();
		txtQ3.clear();
		txtQ4.clear();
		txtQ5.clear();
		txtQ6.clear();
		
		txtCfmQ1.clear();
		txtCfmQ2.clear();
		txtCfmQ3.clear();
		txtCfmQ4.clear();
		txtCfmQ5.clear();
		txtCfmQ6.clear();
		
		tabSteps.getSelectionModel().getSelectedItem().setDisable(true);
		tabSteps.getSelectionModel().select(0);
		tabSteps.getSelectionModel().getSelectedItem().setDisable(false);
		
		setButtonsVisiblity(true);
	}
	
	@FXML
	private void cancelPressedHandler(Event event)
	{
		resertComponents();
	}
	
	@FXML
	private void prevPressedHandler(Event event)
	{
		if(curStep > 0)
			performPrevStep();
	}
	
	@FXML
	private void savePressedHandler(Event event)
	{
		String subject = txtSubject.getText();
		//int creatorId = User.getuId();
		int creatorId = 1;
		
		//Parameter list for addSurvey
		ArrayList<Object> paramListSurvey = new ArrayList<>();
		Survey survey = new Survey(subject, creatorId);
		paramListSurvey.add(survey);
		
		//Parameter list for addQuestion
		ArrayList<Object> paramListQuestion = new ArrayList<>();
		paramListQuestion.add(new Question(txtCfmQ1.getText()));
		paramListQuestion.add(new Question(txtCfmQ2.getText()));
		paramListQuestion.add(new Question(txtCfmQ3.getText()));
		paramListQuestion.add(new Question(txtCfmQ4.getText()));
		paramListQuestion.add(new Question(txtCfmQ5.getText()));
		paramListQuestion.add(new Question(txtCfmQ6.getText()));
		
		Packet packet = new Packet();
		packet.addCommand(Command.addSurvey);
		packet.setParametersForCommand(Command.addSurvey, paramListSurvey);
		
		packet.addCommand(Command.addQuestions);
		packet.setParametersForCommand(Command.addQuestions , paramListQuestion);
		
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
					
				}
			}
		});
		sender.start();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
