package Orders;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import Commons.ProductInOrder;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class OrderController implements Initializable, ChangeListener<String>{

	@FXML
	private TextField txtTimeRequested;
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
	private double totalAfter;
	private double blncePay;
	private ToggleGroup toggleGroup = new ToggleGroup();
	private int tabActive=0;
	private int emptyLines=3;
	private static Stage primaryStage;
	private double blnce;
	private Account account;
	private MemberShipAccount memberAc;
	private final double deliveryPrice=10;
	public OrderController() {}
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
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		registerDateTimePicker();
		registerTxtRequestedTime();
		btnNext.setDisable(true);
		registerRadionBtn();
		registerChkExpressDelivery();
		registerChkDelivery();
		getPriceDetails();
		registerLblLeftToPayListener();
		lblLeftToPay.setText(String.format("%.2f¤", totalAfter-blncePay));
		txtAddress.textProperty().addListener(this);
		txtName.textProperty().addListener(this);
		txtPhone.textProperty().addListener(this);
		delivery.setDisable(true);
		payment.setDisable(true);
	}

	private void registerLblLeftToPayListener() {
		txtBlncePay.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if( !newValue.matches("[0-9]*\\.?[0-9]?[0-9]?"))//check if number
								
				{
					txtBlncePay.setText(oldValue);
				}
				else {
					try {
					blncePay=Double.parseDouble(txtBlncePay.getText());
					}catch(Exception e) {};
					double left = totalAfter-blncePay;
					if(left < 0 || blncePay>blnce)
						txtBlncePay.setText(oldValue);
					else
						lblLeftToPay.setText(String.format("%.2f¤", left));
				}
			}
		});
		
	}
	private void getPriceDetails() {
		lblTotalBeforeDiscount.setText(""+CartController.getTotalPrice());
		account = CustomerMenuController.currentAcc;		
		if(account.getMemberShip() != null)
		{ 
			radAccount.setVisible(false);
			radCash.setVisible(false);
			double discount = CartController.getTotalPrice()*account.getMemberShip().getDiscount()/100;
			lblDiscount.setText(String.format("%.2f¤",discount));
			totalAfter=CartController.getTotalPrice()*(1-account.getMemberShip().getDiscount()/100);
			if(chkDelivery.isSelected())
				totalAfter+=deliveryPrice;
			lblTotal.setText(String.format("%.2f¤",totalAfter));
		}
		else
		{
			totalAfter=CartController.getTotalPrice();
			lblDiscount.setText("0¤");
			lblTotal.setText(String.format("%.2f¤",totalAfter));
		}
		blnce = account.getBalance();
		if(blnce<=0)
		{
			blncePay=0;
			txtBlncePay.setDisable(true);
			txtBlncePay.setText("0¤");
		}
		lblAvailableBalance.setText(String.format("%.2f¤",blnce));
		
	}
	private void registerChkDelivery() {
		chkDelivery.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(chkDelivery.isSelected())
				{
					txtAddress.setDisable(false);
					txtName.setDisable(false);
					txtPhone.setDisable(false);
				}
				else
				{
					txtAddress.setDisable(true);
					txtName.setDisable(true);
					txtPhone.setDisable(true);
				}
				
			}
		});
	}
	private void registerChkExpressDelivery() {
		chkExpressDelivery.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(chkExpressDelivery.isSelected())
				{
					chkDelivery.setSelected(true);
					chkDelivery.setDisable(true);
					requestedDate.setDisable(true);
					txtAddress.setDisable(false);
					txtName.setDisable(false);
					txtPhone.setDisable(false);
					txtTimeRequested.setDisable(true);
					requestedDate.setValue(LocalDate.now());
					LocalDateTime dt = LocalDateTime.now().plusHours(3);
					txtTimeRequested.setText(dt.getHour() +":"+(dt.getMinute()<10?"0"+dt.getMinute():dt.getMinute()));
				}
				else
				{
					chkDelivery.setDisable(false);
					requestedDate.setDisable(false);
					txtTimeRequested.setDisable(false);
					txtTimeRequested.setText("");

				}
				
			}
		});
		
	}
	private void registerRadionBtn() {
		

		radAccount.setToggleGroup(toggleGroup);
		radCash.setToggleGroup(toggleGroup);
		toggleGroup.selectToggle(radCash);
		
	}
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
			break;
		case 2:
			tabPane.getSelectionModel().select(1);
			delivery.setDisable(false);
			payment.setDisable(true);
			btnNext.setText("Next");
			break;
		    default:;
		}
		tabActive--;
		
	}
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
	private void registerTxtRequestedTime() {
		txtTimeRequested.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
					if(txtTimeRequested.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]"))
					{
						btnNext.setDisable(false);
					}
					else
						btnNext.setDisable(true);
			}
		});
	}
	public void onClickNextBtn() {
		switch(tabActive)
		{
		case 0:
			tabPane.getSelectionModel().select(1);
			delivery.setDisable(false);
			date.setDisable(true);			
			if(chkDelivery.isSelected() && emptyLines!=0)
				btnNext.setDisable(true);
			break;
		case 1:
			tabPane.getSelectionModel().select(2);
			checkDisplayMode();
			delivery.setDisable(true);
			payment.setDisable(false);
			btnNext.setText("Create order");				
			break;
		case 2:
			insertOrder();
			break;
		default:;
		}
		tabActive++;
	}
	protected void checkDisplayMode() {
				
	}
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
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if(oldValue.length()==0)
			emptyLines--;
		else if(newValue.length()==0)
			emptyLines++;
		if(!chkDelivery.isSelected())
			btnNext.setDisable(false);
		else
			btnNext.setDisable(emptyLines!=0);
	}
	private void insertOrder()
	{
		String[] time = txtTimeRequested.getText().split(":");
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.valueOf(requestedDate.getValue()));
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
		cal.add(Calendar.MINUTE, Integer.parseInt(time[1]));
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Date dateNow = new Date(Calendar.getInstance().getTime().getTime());
		
		
		//create order
		ArrayList<Object> order = new ArrayList<>();
		order.add(new Order(0,dateNow,timestamp,account.getCustomerId(),2,account.getBranchId(),totalAfter));
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
					primaryStage.close();
					CartController.cartProducts.clear();
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
