package Products;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import Login.CustomerMenuController;
import Login.ManagersMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.SelectProductController.CatalogUse;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class FlowerController extends Application implements Initializable {

	@FXML
	Label lblColor;//display color
	@FXML
	TextField txtName;
	@FXML
	TextField txtprice;
	@FXML
	Label lblErrPrice;
    @FXML
    private ListView<Flower> lstFlowers;
	@FXML
	Label lblErrName;
	@FXML
	ComboBox<String> cmbColor;//display possible color names
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnBack;
    
	private ArrayList<ColorProduct> cList ;//all possible colors for flower
	private ObservableList<Flower> data;
	private ArrayList<Flower> flowers = new ArrayList<>();
	private static Stage mainStage;
	
	/**
	 * init data for form
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// set listener to combobox when other item has been choosed
		cmbColor.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				lblColor.setStyle("-fx-background-color: "+arg2+";");
			}
		});
		
		txtprice.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
					if((!newValue.matches("[0-9]*\\.?[0-9]?[0-9]?") && newValue.length()>0)//check if number
							|| (newValue.indexOf(".") > 3//check if before point is less then 100 
									|| (newValue.length() > 3 && newValue.indexOf(".")== -1)))//check if no point and less then 100
					{
						if(oldValue.length()>0)	
							txtprice.setText(oldValue);
						else
							txtprice.setText("");
					}
					else
					{
						try
						{
							double price = Double.valueOf(txtprice.getText());
							if (price > 100)
							{
								lblErrPrice.setVisible(true);
								lblErrPrice.setText("Max Price: 100.0¤");
								btnCreate.setDisable(true);
							
							}
							else
							{
								lblErrPrice.setVisible(false);
								btnCreate.setDisable(false);
							}
						}
						catch (Exception e)
						{
							lblErrPrice.setVisible(true);
							lblErrPrice.setText("*");
							btnCreate.setDisable(true);
						}
					}
			}
		});
		
		txtName.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
					if(newValue.length() > 30)
					{
						txtprice.setText(oldValue);
						lblErrName.setVisible(true);
						lblErrName.setText("Too Long!");
						btnCreate.setDisable(true);
					}
					else
					{
						if (newValue.length() == 0)
						{
							lblErrName.setVisible(true);
							lblErrName.setText("*");
							btnCreate.setDisable(true);
						}
						else
						{
							lblErrName.setVisible(false);
							btnCreate.setDisable(false);
						}
					}
			}
		});
		
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
				ArrayList<String> col = new ArrayList<>();
				if (p.getResultState())
				{
					cList = ConstantData.productColorList;
					flowers = p.<Flower>convertedResultListForCommand(Command.getFlowers);
					
					for (ColorProduct color : cList)
					{
						col.add(color.getColorName());
					}
					cmbColor.getItems().addAll(col);
					
					fillFlowers();
				}
				else//if it was error in connection
					JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
			}
		});
		send.start();
	}
	

	
	/**
	 * Fill all items to the customize listview dynamically
	 */
	public void fillFlowers() {
		data = FXCollections.observableArrayList(flowers);
		lstFlowers.setCellFactory(new Callback<ListView<Flower>, ListCell<Flower>>() {

			@Override
			public ListCell<Flower> call(ListView<Flower> param) {
				return new ListCell<Flower>() {

					private void setCellHandler(Flower flower) {
						// name of flower
						Text proName = new Text(flower.getName());
						proName.setFont(new Font(14));
						proName.setStyle("-fx-font-weight: bold");
						
						// color of flower
						ColorProduct colorFlower = ConstantData.getColorOfFlowerByColorId(flower.getColor());
						Label color = new Label(colorFlower.getColorName());
						color.setFont(new Font(12));
						//color.setStyle("-fx-text-fill: "+colorFlower.getColorName()+"; -fx-font-weight: bold");
						
						Text price = new Text(flower.getPrice() + "¤");
						price.setFont(new Font(12));
						
						HBox flowerColorPrice = new HBox(color, price);
						flowerColorPrice.setPadding(new Insets(0, 10, 0, 0));
						flowerColorPrice.setSpacing(10);
						
						VBox flowerDetails = new VBox(proName, flowerColorPrice);
						flowerDetails.setStyle("-fx-border-style: solid inside;"
					 	        + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
					 	        + "-fx-border-radius: 5;");
						flowerDetails.setSpacing(10);
						flowerDetails.setPadding(new Insets(10));
		                    
		                setGraphic(flowerDetails);
					}

					@Override
					protected void updateItem(Flower item, boolean empty) {
						if (item != null) {
							setCellHandler(item);
						}
					}
				};
			}
		});
		lstFlowers.setItems(data);

		lstFlowers.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				event.consume();
			}
		});
	}
	
	/**
	 * 	when user click on create checking validation and sending it to the server
	 */
	public void onCreateClicked(ActionEvent event)
	{
		
		try {
			isValid();//check validation
			Packet packet = new Packet();//create packet to send
			packet.addCommand(Command.addFlower);//add command
			ArrayList<Object> flower = new ArrayList<Object>();//create array list for send to server
			
			ColorProduct codeColor = cList.stream().filter(c->c.getColorName().equals(cmbColor.getValue())).findFirst().orElse(null);//get color id for colorList
			
			String name=txtName.getText().replaceAll("'", "\\'");//remove special character
			
			Flower flowerAdded = new Flower(name,Double.parseDouble(txtprice.getText()),codeColor.getColId());
			flower.add(flowerAdded);//add new flower
			packet.setParametersForCommand(Command.addFlower,flower);//set flower list to command
			SystemSender sender = new SystemSender(packet);//create sender
			sender.registerHandler(new IResultHandler() {
				
				@Override
				public void onWaitingForResult() {//waiting
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onReceivingResult(Packet p) {//notify user about an answer from the server
					if(p.getResultState())
					{
						ConstantData.displayAlert(AlertType.INFORMATION, "Success", "Saved", "The flower has been saved");
						txtName.setText("");
						txtprice.setText("");
						cmbColor.setValue("Choose color");
						flowers.add(flowerAdded);
						fillFlowers();
					}
					else
					{
						if (p.getExceptionMessage().toLowerCase().contains("duplicate"))
						{
							ConstantData.displayAlert(AlertType.INFORMATION, "Error", "Duplicate Name", "The flower name is already exists!");
						}
						else
						{
							ConstantData.displayAlert(AlertType.INFORMATION, "Error", "Error", "The flower hasn't been saved");
						}
					}
				}
			});
			sender.start();
		}
		catch(Exception e)
		{
			ConstantData.displayAlert(AlertType.INFORMATION, "Error", "Error", e.getMessage());
		}
	}

	/***
	 *  check if all fields in the form is valid
	 * @throws Exception with error
	 */
	private void isValid() throws Exception {
		if(txtName.getText().length() == 0)
		{
			lblErrName.setText("*");
			throw new Exception("Please fill flower name before");
		}
		if(cmbColor.getValue()==null)
		{
			throw new Exception("Please choose color");
		}
		if(txtprice.getText().length()==0 || Double.parseDouble(txtprice.getText())<0) {
			lblErrPrice.setText("*");
			throw new Exception("Please fill price correctly");
		}
	}
	/**
	 * load fxml Flower GUI
	 */
	@Override
	public void start(Stage arg0) throws Exception {

		mainStage = arg0;
		String title = "Products";
		String srcFXML = "/Products/FlowerUI.fxml";
		String srcCSS = "/Products/application.css";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			arg0.setTitle(title);
			arg0.setScene(scene);
			arg0.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
		
		arg0.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				onClosingForm();
			}
		});
	}
	
	public void onClosingForm()
	{
		try
		{
			mainStage.close();
			
			ManagersMenuController menu = new ManagersMenuController();
			menu.start(new Stage());
		}
		catch (Exception e) 
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
		}
	}
}
