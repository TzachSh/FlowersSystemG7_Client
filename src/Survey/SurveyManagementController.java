package Survey;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Employee;
import Branches.Role;
import Customers.Complain;
import Login.CustomerMenuController;
import Login.LoginController;
import Login.ServiceMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.ConstantData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import javafx.stage.WindowEvent;
import javafx.util.Callback;
/***
 * 
 * Controller class to handle Survey Mangaement logic
 *
 */
public class SurveyManagementController implements Initializable {
	/***
	 * FXML components to be changed during runtime
	 */
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
	/***
	 * FXML list view component to be loaded with the surveys
	 */
	private ListView<Survey> sListView;
	/***
	 * Observable list to handle the surveys data
	 */
	private ObservableList<Survey> dataSurvey;
	
	/***
	 * Lists for data handle during runtime 
	 */
	private ArrayList<SurveyConclusion> surveyConclusionList;
	private ArrayList<Survey> survyList;
	/***
	 * Current logged in employee 
	 */
	public static Employee employee=(Employee)LoginController.userLogged;
	/***
	 * The first step of adding a survey starts in stage 1 by default
	 */
	private int curStep = 1;
	
	/**
	 * Save the current stage
	 */
	private static Stage stage;
	/**
	 * Show the scene view of complains management
	 * 
	 * @param primaryStage - current stage to build
	 */
	public void start(Stage primaryStage) {
		
		stage = primaryStage;
		stage.setResizable(false);
		String title = "Survey Creator";
		String srcFXML = "/Survey/SurveyManagementUI.fxml";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setTitle(title);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  
	        	  stage.close();
				  ServiceMenuController menu = new ServiceMenuController();
				  try {
					menu.start(new Stage());
				} catch (Exception e) {
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
				}

	          }
	      }); 
	}
	
	/***
	 * Handle press on the next step button to go in to the next stage
	 */
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
	/***
	 * Handle press on the previous button to go in the back stage
	 */
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
	/***
	 * 
	 * @param curStep - current step
	 * @param txtField - of the inserted question
	 * @return the validation state of the inserted question
	 */
	private boolean stepValidation(int curStep , TextField txtField)
	{
		return (curStep == tabSteps.getSelectionModel().getSelectedIndex()+1) && (!txtField.getText().isEmpty() && txtField.getText().matches(".*[a-z].*"));		
	}
	/***
	 * 
	 * @param txtField to be set
	 * @param text to be shown 
	 * Showing the inserted questions at the confirmation stage
	 */
	private void setConfirmationQuestionTextField(TextField txtField , String text)
	{
		txtField.setText(text);
	}
	/***
	 * 
	 * @param isVisible - to set the visibility of the the previous and next buttons 
	 */
	private void setButtonsVisiblity(boolean isVisible)
	{
		btnNext.setVisible(isVisible);
		btnPrev.setVisible(isVisible);
	}
	/***
	 * 
	 * @param event of action
	 * Handle press on the next button by validating the question info,
	 * then go to the next step if validate
	 */
	@FXML
	private void nextPressedHandler(Event event)
	{
		if(stepValidation(1, txtQ1))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ1,txtQ1.getText().replaceAll("'", "\\'"));
		}
		else if(stepValidation(2, txtQ2))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ2,txtQ2.getText().replaceAll("'", "\\'"));
		}
		else if(stepValidation(3, txtQ3))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ3,txtQ3.getText().replaceAll("'", "\\'"));
		}
		else if(stepValidation(4, txtQ4))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ4,txtQ4.getText().replaceAll("'", "\\'"));
		}
		else if(stepValidation(5, txtQ5))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ5,txtQ5.getText().replaceAll("'", "\\'"));
		}
		else if(stepValidation(6, txtQ6))
		{
			performNextStep();
			setConfirmationQuestionTextField(txtCfmQ6,txtQ6.getText().replaceAll("'", "\\'"));
			setButtonsVisiblity(false);
		}
		else if(curStep == 7)
		{
			// Nothing to handle, just no more step next
		}
		else
		{
			Alert alert = new Alert(AlertType.INFORMATION,"Please Enter a valid question - characters only - to finish this step");
			alert.show();
		}
	}
	/***
	 * Reset the stage of all the FXML components
	 */
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
		
		dataSurvey.clear();
		
		setButtonsVisiblity(true);
	}
	/***
	 * 
	 * @param event of action
	 * Handle cancel button pressed to clear all of the components
	 */
	@FXML
	private void cancelPressedHandler(Event event)
	{
		resertComponents();
	}
	/***
	 * 
	 * @param event of action
	 * Handle previous button click to perform back step operation  
	 */
	@FXML
	private void prevPressedHandler(Event event)
	{
		if(curStep > 0)
			performPrevStep();
	}
	/***
	 * 
	 * @param event of action
	 * Handle save button to save the survey in the database.
	 * Survey created inactive mode by default
	 */
	@FXML
	private void savePressedHandler(Event event)
	{
		String subject = txtSubject.getText().replaceAll("'", "\\'");
		if(subject.isEmpty() || !subject.matches(".*[a-z].*"))
		{
			Alert alert = new Alert(AlertType.INFORMATION,"Please enter a valid - with characters only - subject");
			alert.show();
			return;
		}
		
		//int creatorId = User.getuId();
		int creatorId = employee.geteId();
		
		//Parameter list for addSurvey
		ArrayList<Object> paramListSurvey = new ArrayList<>();
		java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
		Survey survey = new Survey(subject, creatorId,false);
		paramListSurvey.add(survey);
		
		//Parameter list for addQuestion
		ArrayList<Object> paramListQuestion = new ArrayList<>();
		paramListQuestion.add(new Question(txtCfmQ1.getText().replaceAll("'", "\\'")));
		paramListQuestion.add(new Question(txtCfmQ2.getText().replaceAll("'", "\\'")));
		paramListQuestion.add(new Question(txtCfmQ3.getText().replaceAll("'", "\\'")));
		paramListQuestion.add(new Question(txtCfmQ4.getText().replaceAll("'", "\\'")));
		paramListQuestion.add(new Question(txtCfmQ5.getText().replaceAll("'", "\\'")));
		paramListQuestion.add(new Question(txtCfmQ6.getText().replaceAll("'", "\\'")));
		
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
			/***
			 * While waiting for a result from the server	
			 */
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			/***
			 * When getting a result
			 */
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
	/***
	 * Display the surveys in the list view
	 */
	@FXML
	private void displaySurvey()
	{
		Packet packet = new Packet();
		ArrayList<Object> paramList = new ArrayList<>();
		ArrayList<Object> paramListConclusionList = new ArrayList<>();
		
		packet.addCommand(Command.getSurvey);
		packet.addCommand(Command.getConclusions);
		
		packet.setParametersForCommand(Command.getSurvey, paramList);
		packet.setParametersForCommand(Command.getConclusions, paramListConclusionList);
		
		
		SystemSender sender = new SystemSender(packet);
		sender.registerHandler(new IResultHandler() {
			
			/***
			 * While waiting for a result from the server
			 */
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			/***
			 * When getting a result from the server
			 */
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{	
					surveyConclusionList = p.<SurveyConclusion>convertedResultListForCommand(Command.getConclusions);
					survyList = p.<Survey>convertedResultListForCommand(Command.getSurvey);
					if(employee.getRole() == Role.ServiceExpert)
					{
						ArrayList<Survey> closedSurveyList = new ArrayList<>();
						
						for(Survey survey : survyList)
							if(survey.getClosedDate() != null)
								closedSurveyList.add(survey);
						
						dataSurvey = FXCollections.observableArrayList(closedSurveyList);
						sListView.setItems(dataSurvey);
					}
					else {
						dataSurvey = FXCollections.observableArrayList(survyList);
						sListView.setItems(dataSurvey);
					}
				}
				else
				{
					Alert alert = new Alert(AlertType.ERROR,p.getExceptionMessage());
					alert.show();
				}
			}
		});
		sender.start();
	}
	
	/***
	 * 
	 * @param surveyConclusionList with all the conclusion of all survey
	 * @param survey to find his conclusion
	 * @return the conclusion of the specific survey, if exists
	 */
	private SurveyConclusion getConclusionBySurvey(ArrayList<SurveyConclusion> surveyConclusionList , Survey survey)
	{
		SurveyConclusion surveyConclusion = null;
		for(SurveyConclusion sc : surveyConclusionList)
			if(sc.getSurId() == survey.getId())
				surveyConclusion = sc;
		
		return surveyConclusion;
		
	}
	/***
	 * Set the cell handler of each cell in the list view
	 */
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
					HBox titleElement;
					HBox statusElement;
					HBox conclusionElement;
					HBox activatedDateElement;
					HBox closedDateElement;
					VBox detailsElement;
					
				/**
				 * 	Set handler for each row, is a corresponding to the status of the survey, if it's active will show "Active" status, else will be shown "Inactive"
				 * @param survey - show the survey's details in the fields such as date , subject and content , status and conclusion
				 */
				private void setCellHandler(Survey survey)
				{
					String textTitle = "Subject: ";
					String textActivatedDate = "Activated Date: ";
					String textClosedDate = "Closed Date: ";
					String textActive = "Status: ";
					String textConclusion = "Expert Conclusion: ";
					String status = ( (survey.isActive() == true ) ? "Active" : "InActive");
					Text activeStatus = new Text(status);
					Text activatedDate =  new Text();
					Text closedDate = new Text();
					
					SurveyConclusion surveyConclusion = getConclusionBySurvey(surveyConclusionList, survey);
					
					titleElement = new HBox(new Label(textTitle), new Text(survey.getSubject()));
					statusElement = new HBox (new Label(textActive) , activeStatus);
					conclusionElement = new HBox (new Label(textConclusion) , getConclusionText(surveyConclusion));
					activatedDateElement = new HBox();
					closedDateElement = new HBox();
					detailsElement = new VBox();
		
					if(survey.getClosedDate() != null) {
						activatedDate = new Text(survey.getActivatedDate().toString());
						closedDate = new Text(survey.getClosedDate().toString());
						activatedDateElement = new HBox (new Label(textActivatedDate) , activatedDate);
						activatedDateElement.setPadding(new Insets(5,10,5,20));
						closedDateElement = new HBox (new Label(textClosedDate) , closedDate );
						closedDateElement.setPadding(new Insets(5,10,5,20));
						detailsElement = new VBox(titleElement,activatedDateElement,closedDateElement,statusElement,conclusionElement);
					}
					else if(survey.getActivatedDate() != null) {
						activatedDate = new Text(survey.getActivatedDate().toString());
						activatedDateElement = new HBox (new Label(textActivatedDate) , activatedDate);
						activatedDateElement.setPadding(new Insets(5,10,5,20));
						detailsElement = new VBox(titleElement,activatedDateElement,statusElement,conclusionElement);
					}
					else
					{
						activatedDateElement.setPadding(new Insets(5,10,5,20));
						detailsElement = new VBox(titleElement,statusElement,conclusionElement);
					}
					
					VBox operationElement=null;
					if(employee.getRole() == Role.CustomerService && surveyConclusion == null)
						operationElement = new VBox(createActivityButtonHandler(survey,activeStatus,closedDateElement,closedDate,activatedDateElement,activatedDate,detailsElement));
					else if(employee.getRole() == Role.ServiceExpert && surveyConclusion == null && survey.getClosedDate() == null)
					{
						String textActiveStatus = "Never Activated";
						operationElement = new VBox(new Label(textActiveStatus));
					}
					else if(employee.getRole() == Role.ServiceExpert && !survey.isActive() && surveyConclusion == null)
						operationElement = new VBox(createAddConclusionButton(survey));
					else if(employee.getRole() == Role.ServiceExpert || employee.getRole() == Role.CustomerService  && surveyConclusion != null)
					{
						String textConclusedState = "Already Conclused";
						operationElement = new VBox(new Label(textConclusedState));
					}
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
				
				private Text getConclusionText(SurveyConclusion surveyConclusion)
				{
					Text text;
					if(surveyConclusion != null)
						text = new Text(surveyConclusion.getConclusion());
					else
						text = new Text("Not Conclused Yet");
					
					return text;
						
				}
				/***
				 * 
				 * @param survey to be set
				 * @param state of the survey to set
				 * Then if the survey is activated then set it to inactive, else set it as active
				 */
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
						
						/***
						 * While waiting or result from server
						 */
						@Override
						public void onWaitingForResult() {
							// TODO Auto-generated method stub
							
						}
						/***
						 * While getting a result from server
						 */
						@Override
						public void onReceivingResult(Packet p) {
							// TODO Auto-generated method stub
								if (p.getResultState()) {
								
								}
								else
								{
									Alert alert = new Alert(AlertType.ERROR,p.getExceptionMessage());
									alert.show();
								}
						}
					});
					sender.start();
				}
				/***
				 * 
				 * @param event of action
				 * Handle window close
				 */
				private void closeWindow(Event event)
				{
					((Node) event.getSource()).getScene().getWindow().hide();
				}
				/***
				 * 
				 * @param survey to be handled with conclusion adding button
				 * @return Button which is handle add conclusion to a specific survey by opening a new survey analyze setup window
				 */
				private Button createAddConclusionButton(Survey survey) {
					
					String btnText = "Add Conclusion";
					Button btnAddConclusion = new Button(btnText);
					
					btnAddConclusion.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent arg0) {
							// TODO Auto-generated method stub
							closeWindow(arg0);
							ServiceExpertController sc = new ServiceExpertController();
							sc.serviceExpert = employee;
							sc.setSurvey(survey);
							sc.start(new Stage());
						}
					});
					
					return btnAddConclusion;
				}
			
				/***
				 * 
				 * @param survey to handle its operation
				 * @param activeStatus to have the activated status
				 * @param closedDateElement closed date element to be shown in the cell
				 * @param closedDate to be saved
				 * @param activatedDateElement activated date element to be shown in the cell
				 * @param activatedDate to be save
				 * @param detailsElement to be shown in the cell view with all of the survey details
				 * @return a button which is changing the state of the survey
				 * Performs the opposite operation of the current operation of the survey status.
				 * If the survey is activated then deactivate it and show and save his closed date, else do the opposite and active the survey
				 */
				private Button createActivityButtonHandler(Survey survey , Text activeStatus , HBox closedDateElement, Text closedDate , HBox activatedDateElement, Text activatedDate, VBox detailsElement)
				{
					
						String textAction = (survey.isActive() == true ) ? "Inactivate" : "Activate";
						Button btnAction = new Button(textAction);
						
						/***
						 * Set button handle on click to show closed date or activated date by the status
						 */
						btnAction.setOnMouseClicked((event) -> {
							if(survey.isActive()) {
								btnAction.setText("Activate");
								activeStatus.setText("Inactive");
								java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
								closedDate.setText(sqlDate.toString());
								if(closedDateElement == null) {
									closedDateElement.getChildren().addAll(new Label("Closed Date: "), closedDate);
									closedDateElement.setPadding(new Insets(5, 10, 5, 20));
									detailsElement.getChildren().add(closedDateElement);
								}
								else
								{
									closedDateElement.getChildren().remove(0 , closedDateElement.getChildren().size());
									closedDateElement.getChildren().addAll(new Label("Closed Date: "), closedDate);
									closedDateElement.setPadding(new Insets(5, 10, 5, 20));
									detailsElement.getChildren().remove(closedDateElement);
									detailsElement.getChildren().add(closedDateElement);
								}
								detailsElement.getChildren().remove(activatedDateElement);
								survey.setClosedDate(sqlDate);
								performOperation(survey, false);		
							}
							else
							{
								if (!isActivatedSurvey(dataSurvey)) {
									btnAction.setText("Inactivate");
									activeStatus.setText("Active");
									
									java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
									activatedDate.setText(sqlDate.toString());
									if(activatedDateElement == null) {
										activatedDateElement.getChildren().addAll(new Label("Activated Date: "), activatedDate);
										activatedDateElement.setPadding(new Insets(5, 10, 5, 20));
										detailsElement.getChildren().add(activatedDateElement);
									}
									else
									{
										activatedDateElement.getChildren().remove(0 , activatedDateElement.getChildren().size());
										activatedDateElement.getChildren().addAll(new Label("Activated Date: "), activatedDate);
										activatedDateElement.setPadding(new Insets(5, 10, 5, 20));
										detailsElement.getChildren().remove(activatedDateElement);
										detailsElement.getChildren().add(activatedDateElement);
									}
									detailsElement.getChildren().remove(closedDateElement);
									survey.setActivatedDate(sqlDate);
									survey.setClosedDate(null);
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
				 * @param item - the survey to show it's details
				 * @param empty - to check if the item is null or not
				 */
					@Override
				protected void updateItem(Survey item, boolean empty) {
						
					// TODO Auto-generated method stub
					super.updateItem(item, empty);
					 if (item != null) {	
						 	setCellHandler(item);
                        }
					 else
						 setGraphic(null);
				}};
			}
		});
	}
	/***
	 * 
	 * @param surveyList to check if there is an activated survey in it
	 * @return true if there is an activated survey, else - false
	 */
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
	/***
	 * Define that only a customer service can create a new survey
	 */
	private void isCreatAble()
	{
		if(employee.getRole() == Role.ServiceExpert)
			tabOptions.getTabs().get(1).setDisable(true);
	}
	/***
	 * Initialize all of the data and components for the first time
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		setListCellFactory();
		displaySurvey();
		isCreatAble();
	}
}
