package Customers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import Branches.Branch;
import Branches.CustomerService;
import Branches.Employee;
import Branches.Role;
import Login.LoginController;
import Login.ServiceMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.ConstantData;
import Users.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
/**
 * 
 * Complains controller which performs adding new complains and follow them 
 *
 */

public class ComplainsController implements Initializable {

	/***
	 * FXML components to be used during runtime
	 */
	@FXML private TextField txtCustId;
	@FXML private Button btnSearch;
	@FXML private Button btnAdd;
	@FXML private ListView<Complain> cListView;
	@FXML private Button btnAddSave;
	@FXML private TextArea txtAddDesc;
	@FXML private TextField txtAddTitle;
	@FXML private TextField txtAddCustId;
	@FXML private TabPane complainsTabedPane;
	@FXML private ComboBox<Branch> cmbBranch;
	
	
	/***
	 * Lists to handle the data
	 */
	private ArrayList<Reply> replyList;
	private ObservableList<Complain> data;
	private ArrayList<Branch> branchList;
	private ObservableList<Branch> branchData;
	private ArrayList<Complain> allComplainsList;
	private ArrayList<Complain> currentServiceEmployeeComplains;
	
	/**
	 * get the current logged in employee
	 */
	public static Employee customerService = (Employee)LoginController.userLogged;
	/***
	 * Customer id to attach his complain
	 */
	private int customerId;
	
	/**
	 * Initializing a Combo Box component with the relevant branches which the customer is registered to 
	 */
	private void initCmb()
	{
		cmbSetConverter();
		
		branchData = FXCollections.observableArrayList(branchList);
		cmbBranch.setItems(branchData);
		cmbBranch.getSelectionModel().selectFirst();
	}
	
	/***
	 * Convert the value of the Branch ComboBox from string to Branch object or the opposite
	 */
	private void cmbSetConverter()
	{
		cmbBranch.setConverter(new StringConverter<Branch>() {

			/**
			 * Getting a Branch object by his name
			 * @param string - string to be converted to a Branch object
			 */
			@Override
			public Branch fromString(String string) {
				Branch retBranch = null;
				for(Branch branch : branchList)
					if(branch.getName().equals(string))
						retBranch = branch;
				return retBranch;
			}
			/**
			 * getting a String of the name of a branch by a Branch object
			 * Branch to be converted in to a String name
			 */
			@Override
			public String toString(Branch object) {
				return String.format("%s", object.getName());
			}
		});
	}
	

	/**
	 * Show the scene view of complains management
	 * 
	 * @param primaryStage - current stage to build
	 */
	public void start(Stage primaryStage) {
		
		String title = "Complains Management";
		String srcFXML = "/Customers/ComplainsManagement.fxml";
		String srcCSS = "/Customers/application.css";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setResizable(false);
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  
	        	  primaryStage.close();
				  ServiceMenuController menu = new ServiceMenuController();
				  try {
					menu.start(new Stage());
				} catch (Exception e) {
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
				}

	          }
	      }); 
	}
	
	/**
	 * 
	 * @param complain
	 * @return the reply relies to this complain
	 */
	private Reply getReplyByComplain(Complain complain)
	{
		Reply retReply = null;

		for (Reply reply : replyList)
			if (reply.getComplainId() == complain.getId())
				retReply = reply;

		return retReply;
	}
	
	/**
	 * Display the relevant complains of current service employee which is connected to the system
	 * and add them to the list view component
	 */
	@FXML
	private void displayComplains()
	{
		Packet packet = new Packet();
		packet.addCommand(Command.getComplains);
		packet.addCommand(Command.getReplies);
		SystemSender sender = new SystemSender(packet);
		
		sender.registerHandler(new IResultHandler() {
			
			/**
			 * On waiting for a message from the server
			 */
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			/**
			 * On getting a message from the server
			 */
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{
					allComplainsList = p.<Complain>convertedResultListForCommand(Command.getComplains);
					replyList = p.<Reply>convertedResultListForCommand(Command.getReplies);
					
					if (customerService.getRole() == Role.CustomerService) {
						
						currentServiceEmployeeComplains = new ArrayList<>();
						for (Complain complain : allComplainsList)
							if (complain.getCustomerServiceId() == customerService.geteId())
								currentServiceEmployeeComplains.add(complain);
						
						// sort by the latest creation date
						Collections.sort(currentServiceEmployeeComplains); 
						
						// sort by the activation status
						Collections.sort(currentServiceEmployeeComplains, new Comparator<Complain>() { 
					        @Override
					        public int compare(Complain complain1, Complain complain2) {
					            return Boolean.compare(complain2.isActive(),complain1.isActive());
					        }
					    });

						data = FXCollections.observableArrayList(currentServiceEmployeeComplains);
						// binding the data
						cListView.setItems(data);
						
					}
					else if(customerService.getRole() == Role.ServiceExpert) {
						currentServiceEmployeeComplains = new ArrayList<>();
						for(Complain complain : allComplainsList)
							if(complain.isActive())
								currentServiceEmployeeComplains.add(complain);
						
						// sort by date
						Collections.sort(currentServiceEmployeeComplains);
						
						data = FXCollections.observableArrayList(currentServiceEmployeeComplains);
						// binding the data
						cListView.setItems(data);
						
					}			
				}	
			}
		});
		sender.start();
	}
	
	/***
	 * 
	 * @param currentTime - to define the current system time
	 * @param oldTime - to define the complain's creation date
	 * @return Text component with the passed time.
	 * Calculate the passed time from the complain's creation time, 
	 * Then create a Text component which is colored by a red color if passed more than 12 hours, else will be colored with green.
	 */
	private Text getPassedTimeInText(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime)
	{
	  long milliseconds1 = oldTime.getTime();
	  long milliseconds2 = currentTime.getTime();

	  long diff = milliseconds2 - milliseconds1;
	  long diffSeconds = diff / 1000 % 60;
	  long diffMinutes = diff / (60 * 1000) % 60;
	  long diffHours = diff / (60 * 60 * 1000) % 24;
	  long diffDays = diff / (24 * 60 * 60 * 1000);
	  
	  String passedTime = String.format("%02d:%02d Hours",diffHours ,diffMinutes);
	  Text textFieldPassedTime = new Text(passedTime);
	  if(diffHours > 12)
		textFieldPassedTime.setFill(Color.RED);
	  else
		textFieldPassedTime.setFill(Color.GREEN);
	  
	   return textFieldPassedTime; 
	}
	
	/**handle adding new complain button pressed.
	 * getting information from the fields and send a request to the DB for inserting
	 * @param event - the button event handler
	 */
	@FXML
	public void handleAddNewComplain(Event event)
	{
		int customerId = 0;
		String title = txtAddTitle.getText().replaceAll("'", "\\'");
		String details = txtAddDesc.getText().replaceAll("'", "\\'");
		
		if(title.isEmpty() || details.isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR , "Fill all the fields");
			alert.show();
			return;
		}
		
		try {
		customerId = Integer.parseInt(txtAddCustId.getText());
		}
		catch(Exception e)
		{
			Alert alert = new Alert(AlertType.ERROR , "Invalid customer id");
			alert.show();
			return;
		}
		int customerServiceId = customerService.geteId();
		java.util.Date today = new java.util.Date();
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(today.getTime());
		int branchId = cmbBranch.getSelectionModel().getSelectedItem().getbId();
		Complain complain = new Complain(sqlDate, title, details, customerId, customerServiceId,true,branchId); // Update to customerServiceId 
		
		Packet packet = new Packet();
		packet.addCommand(Command.addComplain);
		ArrayList<Object> paramList = new ArrayList<>();
		paramList.add(complain);
		packet.setParametersForCommand(Command.addComplain, paramList);
		SystemSender sender = new SystemSender(packet);
		sender.registerHandler(new IResultHandler() {
			/**
			 * On waiting for a message from the server
			 */
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
			}
			/**
			 * On getting a message from the server
			 */
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				Alert alert;
				if (p.getResultState())
				{
					displayComplains();
					alert = new Alert(AlertType.INFORMATION,"The complain has been added successfully!");
					 Optional<ButtonType> result = alert.showAndWait();
			            if (result.get() == ButtonType.OK){
			            	alert.hide();
			            	clearFields();
			            	complainsTabedPane.getSelectionModel().select(0);		                
			            }
				}
				else
				{
					alert = new Alert(AlertType.ERROR,p.getExceptionMessage());
					alert.show();
				}
			}
		});
		sender.start();
	}
	
	/**
	 * Clear all of the text fields used to add a new complain
	 */
	private void clearFields()
	{
		txtAddCustId.clear();
		txtAddDesc.clear();
		txtAddTitle.clear();
	}
	/**
	 * Display complains of a relevant customer by pressing the search button
	 * @param event - the button event handler
	 */
	
	@FXML
	private void handleSearchPressed(Event event) {
		
		if (txtCustId.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR,"Invalid customer ID");
			alert.show();
			txtCustId.clear();
			return;
		}
		
		try {
			customerId = Integer.parseInt(txtCustId.getText());
		}
		catch (Exception e) {
			// TODO: handle exception
			Alert alert = new Alert(AlertType.ERROR,"Invalid customer ID number");
			alert.show();
			txtCustId.clear();
			return;
		}
		data = FXCollections.observableArrayList(allComplainsList);
		data.removeIf((Complain complain)->
		{
			return complain.getCustomerId()!= customerId;	
		});
		cListView.setItems(data);
	}
	
	/**
	 * Handle the text field of searching to show all of the complains when it is empty
	 * after a search is completed
	 */
	@FXML
	private void setSearchOnTextChange()
	{
		txtCustId.textProperty().addListener((observable, oldValue, newValue) -> {
		   if(newValue.isEmpty())
		   {
			   data = FXCollections.observableArrayList(currentServiceEmployeeComplains);
			   cListView.setItems(data);
		   }
		});
	}
	/**
	 * Define a customized cell of each row of the list view, to show a button and labels in it.
	 */

	private void setListCellFactory()
	{
		cListView.setCellFactory(new Callback<ListView<Complain>, ListCell<Complain>>() {
			
			/**
			 * Create handler for each cell of the list view
			 */
			@Override
			public ListCell<Complain> call(ListView<Complain> param) {
				// TODO Auto-generated method stub
				return new ListCell<Complain>() {
					
				/**
				 * 	Set handler for each row, is a corresponding to the status of the complain, if it's active will show a "Reply" button near to it, else will be shown "Done"
				 * @param complain - show the complain's details in the fields such as date , subject and content
				 */
				private void setCellHandler(Complain complain)
				{	
					Reply reply = getReplyByComplain(complain);
					String textTimePassed = "Passed Time: ";
					String textTitle = "Subject: ";
					String textContent = "Content: ";
					String textReply = "Replyment: ";
					String textDone = "Done";
					
					Timestamp curerntTime = new Timestamp(System.currentTimeMillis());
					Text txtPassedTime = getPassedTimeInText(curerntTime, complain.getCreationDate());
				
					HBox timeElement = new HBox(new Label(textTimePassed), txtPassedTime);
					HBox titleElement = new HBox (new Label(textTitle) , new Text(String.format("%s", complain.getTitle())));
					HBox infoElement = new HBox (new Label(textContent) , new Text(String.format("%s", complain.getDetails())));
					
					VBox operationElement = null;
					VBox detailsElements = null;
					HBox hBox = null;
					
					if(customerService.getRole() == Role.CustomerService) {
						if(complain.isActive()) {
							operationElement = new VBox(createReplyButtonHandler(complain));
							operationElement.setPadding(new Insets(5,10,5,0));
							detailsElements = new VBox(timeElement,titleElement,infoElement);
						}
						else {
								if (reply != null) {
									HBox replyElement = new HBox(new Label(textReply), new Text(reply.getReplyment()));
									replyElement.setPadding(new Insets(5,10,5,20));
									detailsElements = new VBox(titleElement, infoElement, replyElement);
								}
							operationElement = new VBox(new Label(textDone));
							operationElement.setPadding(new Insets(5,10,5,0));
						}
						hBox = new HBox(operationElement,detailsElements);
					}
					else if(customerService.getRole() == Role.ServiceExpert)
					{
						if(complain.isActive()) {
							String textCreatorId = "Creator Employee Id:";
							operationElement = new VBox(createReplyButtonHandler(complain));
							operationElement.setPadding(new Insets(5,10,5,0));
							HBox creatorElement = new HBox(new Label(textCreatorId), new Text(String.format("%d",complain.getCustomerId())));
							creatorElement.setPadding(new Insets(5,10,5,20));
							detailsElements = new VBox(timeElement, titleElement, infoElement,creatorElement);
							hBox = new HBox(operationElement,detailsElements);
						}
						else {
								if (reply != null) {
									HBox replyElement = new HBox(new Label(textReply), new Text(reply.getReplyment()));
									replyElement.setPadding(new Insets(5,10,5,20));
									detailsElements = new VBox(titleElement, infoElement, replyElement);
								}
							operationElement = new VBox(new Label(textDone));
							operationElement.setPadding(new Insets(5,10,5,0));
							hBox = new HBox(operationElement,detailsElements);
						}
					}
						
				 	timeElement.setPadding(new Insets(5,10,5,20));
				 	titleElement.setPadding(new Insets(5,10,5,20));
				 	infoElement.setPadding(new Insets(5,10,5,20));
                    hBox.setStyle("-fx-border-style:solid inside;"+
                    			  "-fx-border-width:1;"+
                    			  "-fx-border-insets:5;"+
                    			  "-fx-border-radius:5;");
                    hBox.setPadding(new Insets(10));
                    setGraphic(hBox);
				}
				/**
				 * Creating a button handler which is navigates to the relevant reply view for each complain
				 * @param complain - Create a new handler for this complain
				 * @return Button which handled to open a matching view of a specific complain
				 */
				private Button createReplyButtonHandler(Complain complain)
				{
					
						String textReply = "Reply";
						Button btnReply;

						btnReply = new Button(textReply);
						btnReply.setOnMouseClicked((event) -> {
							String title = "Replyment";
							String srcFXML = "/Customers/ReplyUI.fxml";

							((Node) event.getSource()).getScene().getWindow().hide();
							Stage primaryStage = new Stage();
							primaryStage.setResizable(false);
							FXMLLoader loader = new FXMLLoader();
							Pane root;
							try {
								root = loader.load(getClass().getResource(srcFXML).openStream());
								ReplyController replyController = loader.<ReplyController>getController();
								replyController.setComponents(complain);

								primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							          public void handle(WindowEvent we) {
							        	  
							        	  primaryStage.close();
										  ServiceMenuController menu = new ServiceMenuController();
										  try {
											menu.start(new Stage());
										} catch (Exception e) {
											ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
										}

							          }
							      }); 

								
								Scene scene = new Scene(root);
								primaryStage.setTitle(title);
								primaryStage.setScene(scene);
								primaryStage.show();

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						});
						return btnReply;
					}

				/**
				 * Update each row of the list view by the received item
				 * @param item - the complain to show it's details
				 * @param empty - to check if the item is null or not
				 */
					@Override
				protected void updateItem(Complain item, boolean empty) {
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
	 * Initialization of the Branches ComboBox with all the branches
	 */
	private void initBranchesCmb()
	{
		Packet packet = new Packet();
		packet.addCommand(Command.getBranchesIncludeService);
		ArrayList<Object> paramListBranches = new ArrayList<>();
		packet.setParametersForCommand(Command.getBranchesIncludeService, paramListBranches);
		SystemSender sender = new SystemSender(packet);
		sender.registerHandler(new IResultHandler() {
			
			/**
			 * On waiting for a message from the server
			 */
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			/**
			 * On getting a message from the server
			 */
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{
					branchList = p.<Branch>convertedResultListForCommand(Command.getBranchesIncludeService);
					initCmb();
				}
			}
		});
		sender.start();
	}
		
	/***
	 * 
	 * @param TextField txtField - to set his text limit.
	 * @param limit - limited text length
	 *  Prevent writing more characters then the limited in database
	 */
	private void setTextFieldLengthProperty(TextField textField , int limit ) {
		textField.lengthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				if (newValue.intValue() > oldValue.intValue()) {
					/**
					 *  Check if the new character is greater than LIMIT
					 */
					if (textField.getText().length() >= limit) {

					/**
					 *  if it's the limited character then just setText to previous one
					 */
						textField.setText(textField.getText().substring(0, limit));
					}
				}
			}
		});
	}
	
	/***
	 * 
	 * @param TextArea textArea - to set his text limit.
	 *  Prevent writing more characters then the limited in database
	 */
	private void setTextAreaLengthProperty(TextArea textArea , int limit) {
		textArea.lengthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				if (newValue.intValue() > oldValue.intValue()) {
					/**
					 *  Check if the new character is greater than LIMIT
					 */
					if (textArea.getText().length() >= limit) {

						/**
						 *  if it's the limited character then just setText to previous one
						 */
						textArea.setText(textArea.getText().substring(0, limit));
					}
				}
			}
		});
	}
	
	/***
	 * Initialize all of the text field's property to be with characters limit
	 */
	private void setTextFieldsLimits()
	{
		setTextFieldLengthProperty(txtAddCustId, 9);
		setTextFieldLengthProperty(txtAddTitle, 20);
		setTextAreaLengthProperty(txtAddDesc, 200);
	}
	
	/**
	 * Perform an initialization for the list view by defining the handler for each row, setting the handler for the search text field
	 * and showing all the relevant complains of the current employee 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		setTextFieldsLimits();
		setListCellFactory();
		setSearchOnTextChange();
		displayComplains();
		initBranchesCmb();
	}
	
}
