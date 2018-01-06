package Customers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ComplainsController implements Initializable {

	@FXML private TextField txtCustId;
	@FXML private TextField txtCustName;
	@FXML private Button btnSearch;
	@FXML private Button btnAdd;
	@FXML private ListView<Complain> cListView;
	@FXML private Button btnAddSave;
	@FXML private TextArea txtAddDesc;
	@FXML private TextField txtAddTitle;
	@FXML private TextField txtAddCustNum;
	
	private ObservableList<Complain> data;
	
	private Stage primaryStage;
	
	public void start(Stage primaryStage) {
				
		this.primaryStage = primaryStage;
		
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
					ArrayList<Complain> cList = p.<Complain>convertedResultListForCommand(Command.getComplains);
					data = FXCollections.observableArrayList(cList);
					cListView.setCellFactory(new Callback<ListView<Complain>, ListCell<Complain>>() {
						
						@Override
						public ListCell<Complain> call(ListView<Complain> param) {
							// TODO Auto-generated method stub
							return new ListCell<Complain>() {
								
							private void setCellHandler(Complain complain)
							{
								String textDate = "Date";
								String textTitle = "Title";
								
								
								VBox dateElement = new VBox(new Label(textDate), new Text(String.format("%s", complain.getCreationDate().toString())));
							 	VBox titleElement = new VBox(new Label(textTitle) , new Text(String.format("%s", complain.getTitle()))); 
							 	VBox replyElement = new VBox(createButtonHandler(complain));
							 	HBox hBox = new HBox(replyElement,dateElement,titleElement);
	                            hBox.setSpacing(10);
	                            dateElement.setPadding(new Insets(10,10,10,20));
	                            titleElement.setPadding(new Insets(10,10,10,20));
	                            replyElement.setPadding(new Insets(20,10,10,10));
	                            hBox.setPadding(new Insets(10));
	                            setGraphic(hBox);
							}
							
							private Button createButtonHandler(Complain complain)
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
					cListView.setItems(data);
				}
				else
				{
					
				}
			}
		});
		sender.start();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		displayComplains();
	}
	
}
