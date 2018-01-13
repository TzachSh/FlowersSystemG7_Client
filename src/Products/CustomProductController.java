package Products;



import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import Customers.Customer;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CustomProductController implements Initializable {

	@FXML
	private TitledPane paneFlowers;
	@FXML
	private Button btnFind;
	@FXML
	private Button btnAddToCart;
	@FXML
	private TextField txtMaxCost;
	@FXML
	private ComboBox<String> cmbColor;
	@FXML
	private ListView<Flower> flowerListView;
	private Customer customer;
	private CartController cartController;
	private ArrayList<ColorProduct> cList;
	private ObservableList<Flower> data;
	private ArrayList<Flower> flowerList;
	public CustomProductController() {
		super();
	}
	public CustomProductController(CartController cartController,Customer customer) {
		this.customer=customer;
		this.cartController=cartController;
	}
	private void getColors()
	{
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.getColors);//add command
	
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			@Override
			public void onWaitingForResult() {//waiting when send
			}

			@Override
			public void onReceivingResult(Packet p)//set combobox values
			{
				ArrayList<String> col = new ArrayList<>();
				if (p.getResultState())
				{
					cList = p.<ColorProduct>convertedResultListForCommand(Command.getColors);
					
					for (ColorProduct color : cList)
					{
						col.add(color.getColorName());
					}
					cmbColor.getItems().addAll(col);
				}
				else//if it was error in connection
					JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
			}
		});
		send.start();
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getFlowers();
		getColors();
		
		paneFlowers.setVisible(false);
		setSettingBtn();
		txtMaxCost.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
					if((!newValue.matches("[0-9]*\\.?[0-9]?[0-9]?") && newValue.length()>0)//check if number
							|| (newValue.indexOf(".")>3//check if before point is less then 100 
									|| (newValue.length()>3 &&newValue.indexOf(".")==-1  )))//check if no point and less then 100
					{
						if(oldValue.length()>0)	
							txtMaxCost.setText(oldValue);
						else
							txtMaxCost.setText("");
					}
			}
		});
	
	}
	private void setSettingBtn() {
		btnFind.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					isValid();
					btnFind.setDisable(true);
					paneFlowers.setVisible(true);
					txtMaxCost.setDisable(true);
					cmbColor.setDisable(true);
					initList();
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				}
			}

			private void isValid() throws Exception {
				String msg= new String();
				if(txtMaxCost.getText().length()==0)
					msg = "Invalid cost";
				if(cmbColor.getValue()== null)
					msg= msg+"\r\nChoose dominant color";
				if(msg.length()>0)
					throw new Exception(msg);
			}
		});
		
	}
	public void start(Stage primaryStage) throws IOException {
		String srcFXML = "/Products/CustomProductUI.fxml";
		String srcCSS = "/Products/application.css";
		Parent root = FXMLLoader.load(getClass().getResource(srcFXML));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
		primaryStage.setTitle("Custom product");
		primaryStage.setScene(scene);		
		primaryStage.show();
	}

	public void getFlowers()
	{
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.getFlowers);//add command
	
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {//waiting when send
			}

			@Override
			public void onReceivingResult(Packet p)//set combobox values
			{
				if (p.getResultState())
				{
					flowerList= p.<Flower>convertedResultListForCommand(Command.getFlowers);
					
				}
				else//if it was error in connection
					JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
			}
		});
		send.start();
	}
	public void initList()
	{
		data = FXCollections.observableArrayList(flowerList);
		flowerListView.setCellFactory(new Callback<ListView<Flower>, ListCell<Flower>>() {
			
			@Override
			public ListCell<Flower> call(ListView<Flower> param) {
				
				return new ListCell<Flower>(){
							private void setCellHandler(Flower flo)
							{
								Text flower = new Text(flo.getName());
								VBox nameBox = new VBox(flower);
								Text price = new Text(""+flo.getPrice());
								VBox priceBox = new VBox(price);
								
								HBox line = new HBox(nameBox,priceBox);
								line.setStyle("-fx-border-style: solid inside;"
							 	        + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
							 	        + "-fx-border-radius: 5;");
								line.setSpacing(10);
			                    line.setPadding(new Insets(10));
			                    setGraphic(line);
							}
							
							 @Override
								protected void updateItem(Flower item, boolean empty) {
									//super.updateItem(item, empty);
										
									 if (item != null) {	
										 	setCellHandler(item);
				                        }
							 }
							
						};
			}
		});
		
		flowerListView.setItems(data);
	}
}
