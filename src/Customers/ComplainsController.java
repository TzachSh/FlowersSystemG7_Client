package Customers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import Branches.CustomerService;
import Branches.Employee;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Users.User;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
/**
 * 
 * Complains controller which performs adding new complains and follow them 
 *
 */
public class ComplainsController implements Initializable {

	//FXML components
	@FXML private TextField txtCustId;
	@FXML private Button btnSearch;
	@FXML private Button btnAdd;
	@FXML private ListView<Complain> cListView;
	@FXML private Button btnAddSave;
	@FXML private TextArea txtAddDesc;
	@FXML private TextField txtAddTitle;
	@FXML private TextField txtAddCustId;
	@FXML private TabPane complainsTabedPane;
	
	//List to be updated
	private ObservableList<Complain> data;
	private ArrayList<Complain> allComplainsList;
	private ArrayList<Complain> currentServiceEmployeeComplains;
	
	//Save current user accessed
	public static Employee customerService;
	
	
	
	public Employee getCustomerService() {
		return customerService;
	}

	public void setCustomerService(Employee customerService) {
		ComplainsController.customerService = customerService;
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
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public ComplainsController(Employee employee)
	{
		this.customerService = employee;
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
					currentServiceEmployeeComplains = new ArrayList<>();
					
					for(Complain complain : allComplainsList)
						if (complain.getCustomerServiceId() == customerService.getuId())
							currentServiceEmployeeComplains.add(complain);

					data = FXCollections.observableArrayList(currentServiceEmployeeComplains);
					cListView.setItems(data);
				}	
			}
		});
		sender.start();
	}
	/**handle adding new complain button pressed.
	 * getting information from the fields and send a request to the DB for inserting
	 * @param event - the button event handler
	 */
	@FXML
	public void handleAddNewComplain(Event event)
	{
		int customerId = Integer.parseInt(txtAddCustId.getText());
		String title = txtAddTitle.getText();
		String details = txtAddDesc.getText();
		int customerServiceId = customerService.getuId();
		java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
		Complain complain = new Complain(sqlDate, title, details, customerId, customerServiceId,true); // Update to customerServiceId 
		
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
			System.out.println("Enter customer ID!");
			return;
		}

		int customerId = Integer.parseInt(txtCustId.getText());
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
					String textDate = "Date: ";
					String textTitle = "Subject: ";
					String textContent = "Content: ";
					String textDone = "Done";
					
					HBox dateElement = new HBox(new Label(textDate), new Text(String.format("%s", complain.getCreationDate().toString())));
					HBox titleElement = new HBox (new Label(textTitle) , new Text(String.format("%s", complain.getTitle())));
					HBox infoElement = new HBox (new Label(textContent) , new Text(String.format("%s", complain.getDetails())));
					VBox detialsElements = new VBox(dateElement,titleElement,infoElement);
					VBox operationElement;
					if(complain.isActive())
						operationElement = new VBox(createReplyButtonHandler(complain));
					else
						operationElement = new VBox(new Label(textDone));
				 	HBox hBox = new HBox(operationElement,detialsElements);
				 	dateElement.setPadding(new Insets(5,10,5,20));
				 	titleElement.setPadding(new Insets(5,10,5,20));
				 	infoElement.setPadding(new Insets(5,10,5,20));
				 	operationElement.setPadding(new Insets(5,10,5,0));
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
							FXMLLoader loader = new FXMLLoader();
							Pane root;
							try {
								root = loader.load(getClass().getResource(srcFXML).openStream());
								ReplyController replyController = loader.<ReplyController>getController();
								replyController.setComponents(complain);

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
				}};
			}
		});
	}
	
	/**
	 * Perform an initialization for the list view by defining the handler for each row, setting the handler for the search text field
	 * and showing all the relevant complains of the current employee 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		setListCellFactory();
		setSearchOnTextChange();
		displayComplains();
	}
	
}
