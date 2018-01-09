package Customers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ComplainsController implements Initializable {

	@FXML private TextField txtCustId;
	@FXML private Button btnSearch;
	@FXML private Button btnAdd;
	@FXML private ListView<Complain> cListView;
	@FXML private Button btnAddSave;
	@FXML private TextArea txtAddDesc;
	@FXML private TextField txtAddTitle;
	@FXML private TextField txtAddCustId;
	
	private ObservableList<Complain> data;
	private ArrayList<Complain> allComplainsList;
	
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
	
	@FXML
	private void displayComplains()
	{
		Packet packet = new Packet();
		packet.addCommand(Command.getComplains);
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
					allComplainsList = p.<Complain>convertedResultListForCommand(Command.getComplains);
					data = FXCollections.observableArrayList(allComplainsList);
					cListView.setItems(data);
				}	
			}
		});
		sender.start();
	}
	@FXML
	public void handleAddNewComplain(Event event)
	{
		int customerId = Integer.parseInt(txtAddCustId.getText());
		String title = txtAddTitle.getText();
		String details = txtAddDesc.getText();
		int customerServiceId = User.getuId();
		java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
		Complain complain = new Complain(sqlDate, title, details, customerId, 1,true); // Update to customerServiceId 
		
		Packet packet = new Packet();
		packet.addCommand(Command.addComplain);
		ArrayList<Object> paramList = new ArrayList<>();
		paramList.add(complain);
		packet.setParametersForCommand(Command.addComplain, paramList);
		SystemSender sender = new SystemSender(packet);
		sender.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if (p.getResultState())
				{
					data.add(complain);
				}
			}
		});
		sender.start();
	}
	
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
	
	@FXML
	private void setSearchOntTextChange()
	{
		txtCustId.textProperty().addListener((observable, oldValue, newValue) -> {
		   if(newValue.isEmpty())
		   {
			   data = FXCollections.observableArrayList(allComplainsList);
			   cListView.setItems(data);
		   }
		});
	}

	private void setListCellFactory()
	{
		cListView.setCellFactory(new Callback<ListView<Complain>, ListCell<Complain>>() {
			
			@Override
			public ListCell<Complain> call(ListView<Complain> param) {
				// TODO Auto-generated method stub
				return new ListCell<Complain>() {
					
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
				
				private Button createReplyButtonHandler(Complain complain)
				{
					
						String textReply = "Reply";
						Button btnReply;

						btnReply = new Button(textReply);
						btnReply.setOnMouseClicked((event) -> {
							String title = "Replyment";
							String srcFXML = "/Customers/ReplyToComplain.fxml";

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
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		setListCellFactory();
		setSearchOntTextChange();
		displayComplains();
	}
	
}
