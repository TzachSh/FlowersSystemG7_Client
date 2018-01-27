package Products;



import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.sun.media.jfxmediaimpl.platform.Platform;
import com.sun.scenario.effect.impl.prism.PrImage;

import Customers.Customer;
import Login.CustomerMenuController;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.application.Application;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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

/**
 * This class controlling the creation custom which customer created.
 * The customer can add flower that matches the dominant color which he entered
 * and the max price will be the maximum which he entered. Each time the customer add new flower it will shown in listView
 * The customer can also reduce the number of flowers in the product. Customer can add blessing to his product if he didn't do it and add "add to order" the
 * System will ask him if he want to add blessing otherwise it will open cart with new product in it
 */
public class CustomProductController implements Initializable {
	/**
	 * FXML controls
	 */

    @FXML
    private AnchorPane paneFilter;
    @FXML
    private TextField txtMinCost;
	@FXML
	private CheckBox chkColor;
	@FXML
	private TabPane paneFlowers;
	@FXML
	private Button btnFind;
	@FXML
	private Button btnAddToCart;
	@FXML
	private TextField txtMaxCost;
	@FXML
	private ComboBox<ColorProduct> cmbColor;
	@FXML
	private ListView<Flower> flowerListView;
	@FXML
	private Label lblCashLeft;
	@FXML
	private Button btnReset;
	@FXML
	private Label lblTotalPrice;
	@FXML
	private ComboBox<ProductType> cmbProductType;
	@FXML
	private TextArea txtBlessing;
	@FXML
	private Button btnBackToCart;

	/**
	 * storing flowers in observable list to get option to update it when app is run
	 */
	private ObservableList<Flower> data;
	
	/**
	 * all flowers from db
	 */
	private ArrayList<Flower> flowerList;
	private static Stage stage;
	/**
	 * total price of custom product
	 */
	private double total;
	/**
	 * min range price for flower
	 */
	private double minPrice = 0;
	/**
	 * max range price for the flower
	 */
	private double maxPrice = 0;
	
	/**
	 * all flowers in the custom product
	 */
	private LinkedHashMap<Flower,Integer> flowerInProduct= new LinkedHashMap<>();
	/**
	 * to available run javafx
	 */
	public CustomProductController() {
		super();
	}
	/**
	 * Initialize the data and controls before open the window
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getFlowers();
		cmbProductType.getItems().addAll(ConstantData.productTypeList);
		cmbColor.getItems().addAll(ConstantData.productColorList);
		//init controls
		paneFlowers.setVisible(false);
		//validate price by using regex
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
		
		txtMinCost.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
					if((!newValue.matches("[0-9]*\\.?[0-9]?[0-9]?") && newValue.length()>0)//check if number
							|| (newValue.indexOf(".")>3//check if before point is less then 100 
									|| (newValue.length()>3 &&newValue.indexOf(".")==-1  )))//check if no point and less then 100
					{
						if(oldValue.length()>0)	
							txtMinCost.setText(oldValue);
						else
							txtMinCost.setText("");
					}
			}
		});
	
	}
	/**
	 * Back Button behavior
	 * close current window and open cart window
	 */
	public void onClickRegisterBtnBack() {
		Stage cartStage = new Stage();
		CartController cartController = new CartController();
		cartController.setComesFromCatalog(false);
		stage.close();
		try {
			cartController.start(cartStage);
		} catch (Exception e) {
		}
	}
	/**
	 * init addToOrder handle. if blessing is empty ask if customer want to add blessing
	 */
	public void onClickAddToOrderBtn() 
	{
		if (flowerInProduct.size() == 0)
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "No Items Selected", "Please Select First Some Items");
			return;
		}
		
				//display notification
		if (txtBlessing.getText().length()==0 && JOptionPane.showConfirmDialog(null, "Do you want to add blessing?", "Notification",
		        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			paneFlowers.getSelectionModel().select(1);

		} else {
		   addToCartCloseWindow();//continue to cart
		}		
	}
	/**
	 * getParameters for new custom product and send to create
	 */
	protected void addToCartCloseWindow() {
			int productType = cmbProductType.getSelectionModel().getSelectedItem().getId();//get typeId from combobox
			CustomProduct product = new CustomProduct(-1,productType , total , null, null, txtBlessing.getText());
			ArrayList<Object> pList = new ArrayList<>(Arrays.asList(product));
			ArrayList<Object> flowerInProductList = new ArrayList<>();
			for(Entry<Flower, Integer> set : flowerInProduct.entrySet())
			{
				flowerInProductList.add(new FlowerInProduct(set.getKey().getId(), set.getValue()));
			}
			createCustomProduct(pList,flowerInProductList);
		
	}
	/**
	 * @param pList contains custom product 
	 * @param flowerInProductList contains all flowers in the product
	 * sending to server product with flowers to insert to database and get the product back to display in the cart
	 */
	private void createCustomProduct(ArrayList<Object> pList, ArrayList<Object> flowerInProductList) {
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.CreateCustomProduct);//add command
		packet.addCommand(Command.insertFlowersInProduct);//add command
		
		packet.setParametersForCommand(Command.CreateCustomProduct,pList);
		packet.setParametersForCommand(Command.insertFlowersInProduct,flowerInProductList);
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
					Stage cartStage = new Stage();
					CartController cartController = new CartController();
        			cartController.setComesFromCatalog(false);
        			Product product= p.<Product>convertedResultListForCommand(Command.CreateCustomProduct).get(0);
        			product.setFlowerInProductList(p.<FlowerInProduct>convertedResultListForCommand(Command.insertFlowersInProduct));
        			cartController.addProductsToCartMap(product);
        			try {
						cartController.start(cartStage);
						closeStage();	
					} catch (Exception e) {
						ConstantData.displayAlert(AlertType.ERROR, "Error", "Error","Failed to open Cart "+e.getMessage());
					}
				}
				else//if it was error in connection
				{
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Error",p.getExceptionMessage());
				}
			}
		});
		send.start();
		
	}
	/**
	 * close current window
	 */
	protected void closeStage() {
		Stage stage = (Stage)btnAddToCart.getScene().getWindow();
		stage.close();
		
	}
	
	/**
	 * Handle reset button behavior
	 * reset the form
	 */
	public void onClickResetButton()
	{
		txtMinCost.setText("");
		txtMaxCost.setText("");
		cmbColor.getSelectionModel().clearSelection();
		paneFlowers.setVisible(false);
		txtMaxCost.setDisable(false);
		cmbColor.setDisable(false);
		btnFind.setDisable(false);
		lblCashLeft.setText("");
		flowerInProduct.clear();
		lblTotalPrice.setText("0.0$");
		total = 0;
		minPrice = 0;
		maxPrice = 0;
		paneFilter.setDisable(false);
	}
	/**
	 * Handle set settings button click
	 * search flowers by parameters
	 */
	public void onClickSetSetting()
	{
		try {
			isValid();
			minPrice = Double.valueOf(txtMinCost.getText());
			maxPrice = Double.valueOf(txtMaxCost.getText());
			paneFilter.setDisable(true);
			btnFind.setDisable(true);
			paneFlowers.setVisible(true);
			txtMaxCost.setDisable(true);
			cmbColor.setDisable(true);
			initList();
			
			lblTotalPrice.setText("0.0$");
		}
		catch(Exception e)
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Error",e.getMessage());
		}
	}
	/**
	 * check validation form
	 * @throws Exception error message
	 */
	private void isValid() throws Exception {
		String msg= new String();
		if(txtMaxCost.getText().length()==0 || txtMinCost.getText().length()==0)
			msg = "Invalid Range";
		if(chkColor.isSelected() && cmbColor.getValue() == null)
			msg= msg+"\r\nChoose Dominant Color";
		if(cmbProductType.getValue()== null)
			msg= msg+"\r\nChoose Product Type";
		if(msg.length() > 0)
			throw new Exception(msg);
	}
	/**
	 * Show the scene view of complains management 
	 * @param primaryStage - current stage to build
	 * @throws IOException error message
	 */
	public void start(Stage primaryStage) throws IOException {
		String srcFXML = "/Products/CustomProductUI.fxml";
		String srcCSS = "/Products/application.css";
		Parent root = FXMLLoader.load(getClass().getResource(srcFXML));
		Scene scene = new Scene(root);
		stage = primaryStage;
		scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
		primaryStage.setTitle("Custom product");
		primaryStage.setScene(scene);		
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	//display notification
	        	  CustomerMenuController menu = new CustomerMenuController();
				  try {
					menu.start(new Stage());
					primaryStage.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          }
	       });
	}
	/**
	 * get all flowers from the server
	 */
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
					flowerList= p.<Flower>convertedResultListForCommand(Command.getFlowers);								
				else//if it was error in connection
				{
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Error", p.getExceptionMessage());
				}
			}
		});
		send.start();
	}
	
	/**
	 * Event that occurs when changed the state on the checkbox of color
	 */
	public void onChangedChecked()
	{
		if (chkColor.isSelected())
		{
			cmbColor.setDisable(false);
		}
		else
		{
			cmbColor.setDisable(true);
		}
	}
	
	/**
	 * calculate and update the total price of all flowers collection
	 */
	public void calcTotalPrice()
	{
		double total = 0;
		for (Map.Entry<Flower,Integer> entry : flowerInProduct.entrySet())
		{
			total += entry.getValue() * entry.getKey().getPrice();
		}
		lblTotalPrice.setText(String.format("%.2f$",total));
		this.total = total;
	}
	/**
	 * initialize flower list
	 */
	public void initList()
	{
		ArrayList<Flower> tempList;
		if (chkColor.isSelected())
		{
			tempList = flowerList.stream().filter(c->c.getPrice() <= maxPrice && c.getPrice() >= minPrice &&
						c.getColor()==cmbColor.getSelectionModel().getSelectedItem().getColId()).collect(Collectors.toCollection(ArrayList::new));
		}
		else
		{
			tempList = flowerList.stream().filter(c->c.getPrice() <= maxPrice && c.getPrice() >= minPrice).collect(Collectors.toCollection(ArrayList::new));
		}
		
		if (tempList.size() == 0)
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "No Flowers Found", "Try To Change The Current Filters");
			paneFilter.setDisable(false);
			paneFlowers.setVisible(false);
			txtMaxCost.setDisable(false);
			cmbColor.setDisable(false);
			btnFind.setDisable(false);
			return;
		}
		
		data = FXCollections.observableArrayList(tempList);
		flowerListView.setCellFactory(new Callback<ListView<Flower>, ListCell<Flower>>() {
			
			@Override
			public ListCell<Flower> call(ListView<Flower> param) {
				
				return new ListCell<Flower>(){
							private void setCellHandler(Flower flo)
							{
								Text flower = new Text(flo.getName());
								flower.setFont(new Font(14));
								flower.setStyle("-fx-font-weight: bold");
								
								// color of flower
								ColorProduct colorFlower = ConstantData.getColorOfFlowerByColorId(flo.getColor());
								Label color = new Label(colorFlower.getColorName());
								color.setFont(new Font(12));
							
								
								Button decButton = new Button();
								Image imageDec = new Image("minusQty.png");
						        ImageView viewDec = new ImageView(imageDec);
						       
								viewDec.setFitWidth(10);
								viewDec.setFitHeight(10);
								decButton.setGraphic(viewDec);
								decButton.setPrefWidth(12);
								
								
			                    Button incButton = new Button();
							    Image imageInc = new Image("addQty.png");
							    ImageView viewInc = new ImageView(imageInc);
								viewInc.setFitWidth(10);
								viewInc.setFitHeight(10);
								incButton.setGraphic(viewInc);
								incButton.setPrefWidth(12);
								
								
								Text qty = new Text(""+(flowerInProduct.get(flo)==null?0:flowerInProduct.get(flo)));
								qty.setFont(new Font(13.5));
								qty.setFill(Color.RED);
								qty.setTextAlignment(TextAlignment.CENTER);
								
								Text price = new Text(flo.getPrice() + "$");
								price.setFont(new Font(12));
							
								HBox flowerColorPrice = new HBox(color, price);
								flowerColorPrice.setPadding(new Insets(0, 5, 0, 0));
								flowerColorPrice.setSpacing(10);
								
							
								Region region1 = new Region();
								HBox.setHgrow(region1, Priority.ALWAYS);
								
								VBox flowerDetails = new VBox(flower, flowerColorPrice);

								
								decButton.setOnMouseClicked((event) -> {
									if(flowerInProduct.containsKey(flo) && flowerInProduct.get(flo)>0)
									{
										flowerInProduct.put(flo, flowerInProduct.get(flo)-1);
										qty.setText(""+flowerInProduct.get(flo));
										if(flowerInProduct.get(flo)==0)
											flowerInProduct.remove(flo);
										
										// update total price
										calcTotalPrice();
									}
								});
								
								incButton.setOnMouseClicked((event) -> {
									if(flowerInProduct.containsKey(flo))
									{
										flowerInProduct.put(flo, flowerInProduct.get(flo)+1);
									}
									else {
										flowerInProduct.put(flo, 1);
										
									}
								
									qty.setText(""+flowerInProduct.get(flo));
									 calcTotalPrice();
								});
								
							
								HBox qtyOptions = new HBox(decButton, qty, incButton);
								qtyOptions.setSpacing(8);
								qtyOptions.setAlignment(Pos.CENTER);
								
								HBox line = new HBox(flowerDetails, region1, qtyOptions);
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
		flowerListView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        });
	}
}
