package Survey;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Employee;
import Branches.Role;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CreateSurveyController implements Initializable {
	@FXML
	private TabPane tabOptions;
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
	@FXML
	private ListView<Survey> sListView;
	@FXML
	private ObservableList<Survey> dataSurvey;
	
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
		if(subject.isEmpty())
		{
			Alert alert = new Alert(AlertType.INFORMATION,"Please enter a subject");
			alert.show();
			return;
		}
		
		//int creatorId = User.getuId();
		int creatorId = employee.getuId();
		
		//Parameter list for addSurvey
		ArrayList<Object> paramListSurvey = new ArrayList<>();
		Survey survey = new Survey(subject, creatorId,false);
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
		packet.addCommand(Command.addQuestionsToServey);
		ArrayList<Object> paramListSurvyQuestion = new ArrayList<>();
		paramListSurvyQuestion.add(new SurveyQuestion(0,0));
		packet.setParametersForCommand(Command.addQuestionsToServey, paramListSurvyQuestion);
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
					resertComponents();
					Alert alert = new Alert(AlertType.INFORMATION,"The survey inserted successfully!");
					alert.show();
					tabOptions.getSelectionModel().select(0);
					displaySurvey();
				}
			}
		});
		sender.start();
	}
	
	@FXML
	private void displaySurvey()
	{
		Packet packet = new Packet();
		ArrayList<Object> paramList = new ArrayList<>();
		packet.addCommand(Command.getSurvey);
		packet.setParametersForCommand(Command.getSurvey, paramList);
		
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
					ArrayList<Survey> survyList = p.<Survey>convertedResultListForCommand(Command.getSurvey);
					dataSurvey = FXCollections.observableArrayList(survyList);
					sListView.setItems(dataSurvey);
				}
			}
		});
		sender.start();
	}
	
	private void setListCellFactory()
	{
		sListView.setCellFactory(new Callback<ListView<Survey>, ListCell<Survey>>() {
			
			/**
			 * Create handler for each cell of the list view
			 */
			@Override
			public ListCell<Survey> call(ListView<Survey> param) {
				// TODO Auto-generated method stub
				return new ListCell<Survey>() {
					
				/**
				 * 	Set handler for each row, is a corresponding to the status of the complain, if it's active will show a "Reply" button near to it, else will be shown "Done"
				 * @param complain - show the complain's details in the fields such as date , subject and content
				 */
				private void setCellHandler(Survey survey,SurveyConclusion surveyConclusion)
				{
					String textTitle = "Subject: ";
					String textActive = "Status: ";
					String textConclusion = "Expert Conclusion: ";
					String status = ( (survey.isActive() == true ) ? "Active" : "InActive");
					Text activeStatus = new Text(status);
					
					HBox titleElement = new HBox(new Label(textTitle), new Text(survey.getSubject()));
					HBox statusElement = new HBox (new Label(textActive) , activeStatus);
					HBox conclusionElement = new HBox (new Label(textConclusion) , new Text("Text Text Text Text"));
					VBox detailsElement = new VBox(titleElement,statusElement,conclusionElement);
		
					VBox operationElement=null;
					if(employee.getRole() == Role.CustomerService)
						operationElement = new VBox(createActivityButtonHandler(survey,activeStatus));
					else if(employee.getRole() == Role.ServiceExpert && !survey.isActive())
						operationElement = new VBox(createAddConclusionButton(survey));
					else if(employee.getRole() == Role.ServiceExpert && survey.isActive()) {
						String textStillActive = "Running";
						operationElement = new VBox(new Label(textStillActive));
					}
					
				 	HBox hBox = new HBox(operationElement,detailsElement);
				 	titleElement.setPadding(new Insets(5,10,5,20));
				 	statusElement.setPadding(new Insets(5,10,5,20));
				 	conclusionElement.setPadding(new Insets(5,10,5,20));
				 	operationElement.setPadding(new Insets(5,10,5,0));
                    hBox.setStyle("-fx-border-style:solid inside;"+
                    			  "-fx-border-width:1;"+
                    			  "-fx-border-insets:5;"+
                    			  "-fx-border-radius:5;");
                    hBox.setPadding(new Insets(10));
                    setGraphic(hBox);
				}
				
				private void performOperation(Survey survey , boolean state)
				{
					Packet packet = new Packet();
					ArrayList<Object> paramList = new ArrayList<>(); 
					survey.setActive(state);
					paramList.add(survey);
					packet.addCommand(Command.updateSurvey);
					packet.setParametersForCommand(Command.updateSurvey, paramList);
					
					SystemSender sender = new SystemSender(packet);
					sender.registerHandler(new IResultHandler() {
						
						@Override
						public void onWaitingForResult() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onReceivingResult(Packet p) {
							// TODO Auto-generated method stub
								if (p.getResultState()) {
								//	defineOptionTabState(dataSurvey);
								}
						}
					});
					sender.start();
					
				}
				
				private Button createAddConclusionButton(Survey survey) {
					
					String btnText = "Add Conclusion";
					Button btnAddConclusion = new Button(btnText);
					
					btnAddConclusion.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent arg0) {
							// TODO Auto-generated method stub
							ServiceExpertController sc = new ServiceExpertController();
							sc.serviceExpert = employee;
							sc.setSurvey(survey);
							sc.start(new Stage());
						}
					});
					
					return btnAddConclusion;
				}
				/**
				 * Creating a button handler which is navigates to the relevant reply view for each complain
				 * @param complain - Create a new handler for this complain
				 * @return Button which handled to open a matching view of a specific complain
				 */
				private Button createActivityButtonHandler(Survey survey , Text activeStatus)
				{
					
						String textAction = (survey.isActive() == true ) ? "Inactivate" : "Activate";
						Button btnAction = new Button(textAction);
						
						btnAction.setOnMouseClicked((event) -> {
							if(survey.isActive()) {
								btnAction.setText("Activate");
								activeStatus.setText("Inactive");
								performOperation(survey, false);		
							}
							else
							{
								if (!isActivatedSurvey(dataSurvey)) {
									btnAction.setText("Inactivate");
									activeStatus.setText("Active");
									performOperation(survey, true);
								}
								else
								{
									Alert alert = new Alert(AlertType.INFORMATION,"Only one survey can be activated in a time");
									alert.show();
								}
							}
						});

						return btnAction;
				}
				
				/**
				 * Update each row of the list view by the received item
				 * @param item - the complain to show it's details
				 * @param empty - to check if the item is null or not
				 */
					@Override
				protected void updateItem(Survey item, boolean empty) {
						
					// TODO Auto-generated method stub
					super.updateItem(item, empty);
					 if (item != null) {	
						 	setCellHandler(item , null);
                        }
				}};
			}
		});
	}

	private boolean isActivatedSurvey(ObservableList<Survey> surveyList)
	{
		boolean retVal = false;
		for(Survey survey : surveyList)
			if(survey.isActive()) {
				retVal = true;
				break;
			}
		return retVal;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		setListCellFactory();
		displaySurvey();
	}
}
