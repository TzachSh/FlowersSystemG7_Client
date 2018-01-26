package Orders;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.ResourceBundle;

import Commons.ProductInOrder;
import Commons.Status;
import Customers.Account;
import Customers.MemberShipAccount;
import Login.CustomerMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.CartController;
import Products.ConstantData;
import Products.Product;
import Products.SelectProductController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableIntegerArray;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
/***
 * 
 * Controller class to define order process functionality
 *
 */
public class OrderController implements Initializable{

	@FXML
	private ComboBox<Integer> cmbHour;
	@FXML
	private Label lblErrAdd;
	@FXML
	private Label lblErrName;
	@FXML
	private Label lblErrPhone;	
	@FXML
	private ComboBox<Integer> cmbMin;
	@FXML
	private Button btnNext;
	@FXML
	private Button btnPrev;
	@FXML
	private TextField txtAddress;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtPhone;

	@FXML
	private DatePicker requestedDate;
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab date;
	@FXML
	private Tab delivery;
	@FXML
	private Tab payment;
	@FXML
	private RadioButton radAccount;
	@FXML
	private RadioButton radCash;
	@FXML
	private Label lblErrTime;
	@FXML
	private Label lblTotalBeforeDiscount;
	@FXML 
	private Label lblDiscount;
	@FXML
	private Label lblTotal;
	@FXML
	private Label lblLeftToPay;
	@FXML
	private Label lblAvailableBalance;
	@FXML
	private TextField txtBlncePay;
	@FXML
	private CheckBox chkExpressDelivery;
	@FXML
	private CheckBox chkDelivery;
	@FXML
	private Label lblDeliveryCost;
	/**delivery standard cost*/
	private final double deliveryPayment=10;
	/** cost after discount*/
	private double totalAfter;
	/** amount to pay with balance from the customer account*/
	private double blncePay;
	/**control the radio buttons options*/
	private ToggleGroup toggleGroup = new ToggleGroup();
	/**selected active tab*/
	private int tabActive=0;
	/**current stage*/
	private static Stage primaryStage;
	/** account balance*/
	private double blnce;
	/** customer account to create order*/
	private Account account;
	/**list of all hours*/
	ArrayList<Integer> hours= new ArrayList<>();
	/**list of all minutes*/
	ArrayList<Integer> min = new ArrayList<>();
	/**validation phone*/
	private boolean phoneCorrect;
	/**validation name*/
	private boolean nameCorrect;
	/**validation address*/
	private boolean addressCorrect;
	/***
	 * Default constructor
	 */
	public OrderController() {}
	/***
	 * 
	 * @param arg0 - Stage to be loaded
	 * @throws Exception - throw the relevant exception if can't load the stage
	 */
	public void start(Stage arg0) throws Exception {
		primaryStage=arg0;
		String title = "Create new order";
		String srcFXML = "/Orders/OrderApp.fxml";
		String srcCSS = "/Orders/application.css";

		
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
				displayCart();
				arg0.close();
			}
		});
	}
	/***
	 * Initialize all of the data to be displayed
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblDeliveryCost.setTextFill(Color.RED);
		lblErrTime.setTextFill(Color.RED);
		lblErrTime.setVisible(false);
		lblDeliveryCost.setVisible(false);
		lblErrName.setVisible(false);
		lblErrAdd.setVisible(false);
		lblErrPhone.setVisible(false);
		lblLeftToPay.setTextFill(Color.RED);
		lblErrAdd.setTextFill(Color.RED);
		lblErrPhone.setTextFill(Color.RED);
		lblErrName.setTextFill(Color.RED);
		registerDateTimePicker();
		initCmbTime();
		btnNext.setDisable(true);
		registerRadionBtn();
		registerChkExpressDelivery();
		registerChkDelivery();
		getPriceDetails();
		registerLblLeftToPayListener();
		addListeners();
		if(chkDelivery.isSelected())
			lblLeftToPay.setText(String.format("%.2f₪", totalAfter-blncePay+deliveryPayment));
		else
			lblLeftToPay.setText(String.format("%.2f₪", totalAfter-blncePay));
		delivery.setDisable(true);
		payment.setDisable(true);
	}
	private void addListeners() {
		txtAddress.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
			{
				if(newValue.length()>50)
				{
					txtAddress.setText(oldValue);
					lblErrAdd.setText("Too long");
					lblErrAdd.setVisible(true);
				}
				else if(newValue.length()<5)
				{
					addressCorrect=false;
					btnNext.setDisable(true);
					lblErrAdd.setText("Too short");
					lblErrAdd.setVisible(true);
				}
				else 
				{
					addressCorrect=true;
					lblErrAdd.setVisible(false);
					if(phoneCorrect && nameCorrect)
						btnNext.setDisable(false);
				}
			}

		});
		txtPhone.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
			{
				if(newValue.matches("^(?:(?:(\\+?972|\\(\\+?972\\)|\\+?\\(972\\))(?:\\s|\\.|-)?([1-9]\\d?))|(0[23489]{1})|(0[57]{1}[0-9]))(?:\\s|\\.|-)?([^0\\D]{1}\\d{2}(?:\\s|\\.|-)?\\d{4})$"))//check if correct number
				{
					phoneCorrect=true;
					if(nameCorrect && addressCorrect)
					{
						lblErrPhone.setVisible(false);
						lblErrPhone.setText("incorrect number format");
						btnNext.setDisable(false);
					}
				}
				else 
				{
					lblErrPhone.setVisible(true);
					lblErrPhone.setText("Incorrect number");
					phoneCorrect=false;
					btnNext.setDisable(true);
				}
			}
		});
		txtName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.length()>50)//check the max len of the name
					txtName.setText(oldValue);
				else if(newValue.length()>=2 && newValue.matches("[a-zA-Z]+"))//check alphabetical
				{
					nameCorrect=true;
					if(phoneCorrect && addressCorrect)
						btnNext.setDisable(false);
					lblErrName.setVisible(false);
				}
				else//else incorrect
				{
					nameCorrect=false;
					btnNext.setDisable(true);
					lblErrName.setVisible(true);
					lblErrName.setText("Name incorrect");
				}
			}
		});
		
		
	}
	/***
	 * Initialize the combobox time to be selected
	 */
	private void initCmbTime() {
				
		for(int i = 0 ; i < 60;i++)
		{
			if(i<24)
				hours.add(i);
			min.add(i);
		}
		cmbHour.setItems(FXCollections.observableArrayList(hours));
		cmbMin.setItems(FXCollections.observableArrayList(min));
	}
	/***
	 * Listener to define the left pay amount
	 */
	private void registerLblLeftToPayListener() {
		txtBlncePay.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if( !newValue.matches("[0-9]*\\.?[0-9]?[0-9]?"))//check if number
				{
					lblLeftToPay.setText("Invalid price number");
					lblLeftToPay.setVisible(true);
					btnNext.setDisable(true);
				}
				else {
					try {
					blncePay=Double.parseDouble(txtBlncePay.getText());
					}catch(Exception e) {
						blncePay=0;
					};
					double left;
					if(chkDelivery.isSelected())
						left = totalAfter-blncePay+deliveryPayment;
					else
						left = totalAfter-blncePay;
					if(left < 0)
					{
						lblLeftToPay.setVisible(true);
						lblLeftToPay.setText("The amount is bigger than the price");
						btnNext.setDisable(true);
					}
					else if(blncePay>blnce)
					{
						lblLeftToPay.setVisible(true);
						lblLeftToPay.setText("The amount is bigger than the balance");
						btnNext.setDisable(true);
					}
					else
					{
						lblLeftToPay.setText(String.format("%.2f₪", left));
						btnNext.setDisable(false);
					}
				}
			}
		});
		
	}
	/***
	 * Get the current price details of an order
	 */
	private void getPriceDetails() {
		lblTotalBeforeDiscount.setText(String.format("%.2f$",CartController.getTotalPrice()));
		account = CustomerMenuController.currentAcc;		
		if(account.getMemberShip() != null)
		{ 
			double discount = CartController.getTotalPrice()*account.getMemberShip().getDiscount()/100;
			lblDiscount.setText(String.format("%.2f$",discount));
			totalAfter=CartController.getTotalPrice()*(1-account.getMemberShip().getDiscount()/100);
			lblTotal.setText(String.format("%.2f$",totalAfter));
		}
		else
		{
			totalAfter=CartController.getTotalPrice();
			lblDiscount.setText("0$");
			lblTotal.setText(String.format("%.2f$",totalAfter));
		}
		blnce = account.getBalance();
		if(blnce<=0)
		{
			blncePay=0;
			txtBlncePay.setDisable(true);
			txtBlncePay.setText("0$");	
		}
		lblAvailableBalance.setText(String.format("%.2f$",blnce));
	}
	/***
	 * Set delivery option check box
	 */
	private void registerChkDelivery() {
		chkDelivery.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(chkDelivery.isSelected())
				{
					lblTotal.setText(String.format("%.2f₪+%.2f₪",totalAfter,deliveryPayment));
					lblDeliveryCost.setVisible(true);
					txtAddress.setDisable(false);
					txtName.setDisable(false);
					txtPhone.setDisable(false);
				}
				else
				{
					lblTotal.setText(String.format("%.2f₪",totalAfter));
					lblDeliveryCost.setVisible(false);
					txtAddress.setText("");
					txtName.setText("");
					txtPhone.setText("");
					txtAddress.setDisable(true);
					txtName.setDisable(true);
					txtPhone.setDisable(true);
					btnNext.setDisable(false);
				}
				
			}
		});
	}
	/***
	 * Set Express delivery option check box
	 */
	private void registerChkExpressDelivery() {
		chkExpressDelivery.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(chkExpressDelivery.isSelected())
				{
					lblTotal.setText(String.format("%.2f₪+%.2f₪",totalAfter,deliveryPayment));
					lblDeliveryCost.setVisible(true);
					chkDelivery.setSelected(true);
					chkDelivery.setDisable(true);
					requestedDate.setDisable(true);
					txtAddress.setDisable(false);
					txtName.setDisable(false);
					txtPhone.setDisable(false);
					requestedDate.setValue(LocalDate.now());
					LocalDateTime dt = LocalDateTime.now().plusHours(3);
					btnNext.setDisable(false);
					cmbHour.getSelectionModel().clearAndSelect(dt.getHour());
					cmbMin.getSelectionModel().clearAndSelect(dt.getMinute());
					cmbHour.setDisable(true);
					cmbMin.setDisable(true);
				}
				else
				{
					
					lblDeliveryCost.setVisible(false);
					cmbHour.setDisable(false);
					cmbMin.setDisable(false);
					chkDelivery.setDisable(false);
					requestedDate.setDisable(false);
					initCmbTime();
				}
				
			}
		});
		
	}
	/***
	 * Register radio buttons in a toggle group
	 */
	private void registerRadionBtn() {
		radAccount.setToggleGroup(toggleGroup);
		radCash.setToggleGroup(toggleGroup);
		toggleGroup.selectToggle(radCash);
	}
	/***
	 * Perform back operation
	 */
	public void onClickBackBtn() {
		switch(tabActive)
		{
		case 0:
			primaryStage.close();
		    displayCart();
		    break;
		case 1:
			tabPane.getSelectionModel().select(0);
			date.setDisable(false);
			delivery.setDisable(true);
			btnNext.setDisable(false);
			break;
		case 2:
			tabPane.getSelectionModel().select(1);
			delivery.setDisable(false);
			payment.setDisable(true);
			btnNext.setText("Next");
			if(chkDelivery.isSelected())
				lblDeliveryCost.setVisible(true);
			break;
		    default:;
		}
		tabActive--;
		
	}
	/***
	 * Check the ComboBox and DatePicker for correct input
	 */
	public void onClickCmbCorrect()
	{
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(Date.valueOf(requestedDate.getValue()));
			cal.add(Calendar.HOUR_OF_DAY, cmbHour.getSelectionModel().getSelectedItem());
			cal.add(Calendar.MINUTE, cmbMin.getSelectionModel().getSelectedItem());
			Timestamp requested = new Timestamp(cal.getTimeInMillis());
			Timestamp cur = Timestamp.valueOf(LocalDateTime.now());
			Map<TimeUnit,Long> diffTime = computeDiff(requested,cur);
			if ( diffTime.get(TimeUnit.DAYS) == 0 && diffTime.get(TimeUnit.HOURS) < 0)
			{
				btnNext.setDisable(true);
				lblErrTime.setVisible(true);
			}
			else if (diffTime.get(TimeUnit.HOURS) >= 3 || (diffTime.get(TimeUnit.HOURS)>=2 && diffTime.get(TimeUnit.MINUTES)>58)) 
			{
					btnNext.setDisable(false);
					lblErrTime.setVisible(false);
			}
			else {
				btnNext.setDisable(true);
				lblErrTime.setVisible(true);
			}
		}
		catch(Exception e) {
			
		}
	}
	/**
	 * compute the difference between two timestamps
	 * @param requestedTime requested date and time
	 * @param currentTime current date and time
	 * @return difference between timestamps
	 */
	private Map<TimeUnit,Long> computeDiff(Timestamp requestedTime, Timestamp currentTime) {
	    long diffInMillies = requestedTime.getTime() - currentTime.getTime();
	    List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
	    Collections.reverse(units);
	    Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
	    long milliesRest = diffInMillies;
	    for ( TimeUnit unit : units ) {
	        long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
	        long diffInMilliesForUnit = unit.toMillis(diff);
	        milliesRest = milliesRest - diffInMilliesForUnit;
	        result.put(unit,diff);
	    }
	    return result;
	}
	/***
	 * Load the controller of the cart
	 */
	protected void displayCart() {
		 Stage cartStage = new Stage();
			CartController cartController = new CartController();
			cartController.setComesFromCatalog(false);
			try {
				cartController.start(cartStage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	/***
	 * Perform next operation to go trough the next window
	 */
	public void onClickNextBtn() {
		switch(tabActive)
		{
		case 0:
			tabPane.getSelectionModel().select(1);
			delivery.setDisable(false);
			date.setDisable(true);			
			if(chkDelivery.isSelected() && (!nameCorrect || !phoneCorrect || !addressCorrect))//if delivery is selected and not all fields are filled
				btnNext.setDisable(true);
			break;
		case 1:
			tabPane.getSelectionModel().select(2);
			delivery.setDisable(true);
			payment.setDisable(false);
			btnNext.setText("Create order");
			lblDeliveryCost.setVisible(false);
			
			if( lblLeftToPay.getText().matches("[0-9]*\\.?[0-9]?[0-9]?"))//check if number
			{
				lblLeftToPay.setText("Invalid price number");
				btnNext.setDisable(true);
			}
			else {
				try {
				blncePay=Double.parseDouble(txtBlncePay.getText());
				}catch(Exception e) {
					blncePay=0;
				};
				double left;
				if(chkDelivery.isSelected())
					left = totalAfter-blncePay+deliveryPayment;
				else
					left = totalAfter-blncePay;
				if(left < 0 )
				{
					lblLeftToPay.setVisible(true);
					lblLeftToPay.setText("The amount is bigger than the price");
					btnNext.setDisable(true);
				}
				else if(blncePay>blnce)
				{
					lblLeftToPay.setVisible(true);
					lblLeftToPay.setText("The amount is bigger than the balance");
					btnNext.setDisable(true);
				}
				else
				{
					lblLeftToPay.setText(String.format("%.2f₪", left));
					btnNext.setDisable(false);
				}
			}
			break;
		case 2:
			insertOrder();
			break;
		default:;
		}
		tabActive++;
	}

	/***
	 * Register the handler for the DateTime picker with the relevant dates
	 */
	private void registerDateTimePicker() {
		DatePicker minDate = new DatePicker(); // DatePicker, used to define max date available
		minDate.setValue(LocalDate.now()); // Max date available will be one year
		final Callback<DatePicker, DateCell> dayCellFactory;

		dayCellFactory = (final DatePicker datePicker) -> new DateCell() {
		    @Override
		    public void updateItem(LocalDate item, boolean empty) {
		        super.updateItem(item, empty);
		        if (item.isBefore(minDate.getValue())) { //Disable all dates after required date
		            setDisable(true);
		            setStyle("-fx-background-color: #ffc0cb;"); //To set background on different color
		        }
		    }
		};
		//update  DatePicker cell factory
		requestedDate.setDayCellFactory(dayCellFactory);
		
	}
	/***
	 * Perform insert order
	 */
	private void insertOrder()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.valueOf(requestedDate.getValue()));
		cal.add(Calendar.HOUR_OF_DAY, cmbHour.getSelectionModel().getSelectedItem());
		cal.add(Calendar.MINUTE, cmbMin.getSelectionModel().getSelectedItem());
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Date dateNow = new Date(Calendar.getInstance().getTime().getTime());
		
		
		//create order
		ArrayList<Object> order = new ArrayList<>();
		if(chkDelivery.isSelected())
			totalAfter+=deliveryPayment;
		order.add(new Order(0,dateNow,timestamp,account.getCustomerId(),Status.Pending,account.getBranchId(),totalAfter));
		ArrayList<Object> prodInOrder = new ArrayList<>();
		//set all products in order
		for(Entry<Product,Integer> prod : CartController.cartProducts.entrySet())
		{
			prodInOrder.add(new ProductInOrder(0, prod.getKey().getId(), prod.getValue()));
		}
		//set payment in order
		PaymentMethod payment;
		if(radCash.isSelected())//check the payment option
			 payment = PaymentMethod.Cash;
		else
			payment = PaymentMethod.CreditCard;
		double amount=totalAfter-blncePay;
		ArrayList<Object> orderPayment = new ArrayList<>();
		//if exists membership customer will pay all in the end of the membership
		//because of it there is no payment date
		if(account.getMemberShip()!=null)
			orderPayment.add(new OrderPayment(payment, amount));
		//otherwise payment date is now
		else
			orderPayment.add(new OrderPayment(payment, amount,Date.valueOf(LocalDate.now())));
		//if customer uses his balance to pay
		ArrayList<Object> acList = new ArrayList<>();
		if(blncePay>0)
		{
			orderPayment.add(new OrderPayment(PaymentMethod.BalancePayment, blncePay,Date.valueOf(LocalDate.now())));
			account.setBalance(blnce-blncePay);
			acList.add(account);
		}
		ArrayList<Object> delivery = new ArrayList<>();
		if(chkDelivery.isSelected())
		{
			delivery.add(new Delivery(txtAddress.getText(),txtPhone.getText(),txtName.getText()));
		}
		saveOrder(order,prodInOrder,orderPayment,acList,delivery);		
		
	}
	/***
	 * Send to server the new order and it details
	 * @param order to save
	 * @param productInOrder with all of his product in order list
	 * @param payments with all of the payments
	 * @param acList list of all the account
	 * @param delivery to save
	 */
	private void saveOrder(ArrayList<Object> order,ArrayList<Object> productInOrder,ArrayList<Object> payments,ArrayList<Object> acList,ArrayList<Object> delivery)
	{
		Packet packet = new Packet();
		packet.addCommand(Command.createOrder);
		packet.addCommand(Command.createProductsInOrder);
		packet.addCommand(Command.createOrderPayments);
		packet.setParametersForCommand(Command.createOrder, order);
		packet.setParametersForCommand(Command.createProductsInOrder, productInOrder);
		packet.setParametersForCommand(Command.createOrderPayments, payments);
		if(blncePay>0)
		{
			packet.addCommand(Command.updateAccountsBycId);
			packet.setParametersForCommand(Command.updateAccountsBycId, acList);
		}
		if(chkDelivery.isSelected())
		{
			packet.addCommand(Command.createDelivery);
			packet.setParametersForCommand(Command.createDelivery, delivery);
		}
		
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState())
				{
					ConstantData.displayAlert(AlertType.INFORMATION, "Order created", "Order confirmation", "Order has been created thanks for buying in our shop");
					CartController.cartProducts.clear();
					SelectProductController.productsSelected.clear();
					primaryStage.close();
					CustomerMenuController custController = new CustomerMenuController();
					try {
						custController.start(new Stage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
				{
					ConstantData.displayAlert(AlertType.ERROR, "Order creation failed", "Order failed", "Sorry the server error "+p.getExceptionMessage());
				}
			}

			@Override
			public void onWaitingForResult() { }
					
		});
				
		send.start();
	}
}
