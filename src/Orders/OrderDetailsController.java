package Orders;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import Commons.ProductInOrder;
import Commons.Refund;
import Commons.Status;
import Customers.Account;
import Login.CustomerMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.ISystemSender;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.CatalogProduct;
import Products.ConstantData;
import Products.Flower;
import Products.FlowerInProduct;
import Products.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
/**
 * Display Order details include products and payments
 * */
public class OrderDetailsController implements Initializable {
	
	@FXML
    private Button btnBack;

    @FXML
    private ListView<Product> listViewOrderLines;

    @FXML
    private Label lblCreDate;

    @FXML
    private Label lblReqDate;
    @FXML
    private Label lblRefundInfo;
    @FXML
    private Label lblRefundAmount;
    @FXML
    private CheckBox chkIsDelivery;

    @FXML
    private Button btnCacenlOrder;

    @FXML
    private ListView<OrderPayment> listViewOrderPayments;
    /**
     * dynamic product list from order
     */
	private ObservableList<Product> dataOrderLine;
	/**
	 * dynamic payment list from order
	 */
	private ObservableList<OrderPayment> dataOrderPayment;
	/**
	 * current stage
	 */
	private static Stage primaryStage;
	/**
	 * selected order from previous window
	 */
	private static Order order;
	/**
	 * all products in it quantity in the order
	 */
	public static LinkedHashMap<Product, Integer> orderProducts;
	/**
	 * default constructor
	 */
	public OrderDetailsController() {
		super();
	}
	/**
	 * Refund for cancellation
	 */
	private Refund refund;
	/**
	 * set selected order
	 * @param order instance of order
	 */
	public void setOrder(Order order) {
		this.order=order;
	}
	/**
	 * 
	 * @param arg0 - Stage to be loaded
	 * @throws Exception - throw the relevant exception if can't load the stage
	 */
	public void start(Stage arg0) throws Exception {
		primaryStage=arg0;
		String title = "Orders details";
		String srcFXML = "/Orders/OrderDetailsUI.fxml";
		String srcCSS = "/Orders/application.css";
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			arg0.setTitle(title);
			arg0.setScene(scene);
			arg0.setResizable(false);

			arg0.show();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				OrderManagementController orders = new OrderManagementController();
				try {
					orders.start(new Stage());
					primaryStage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
    }

	/**
	 * Back button handler
	 * close current window and open orders list
	 */
	public void onClickBack()
	{
		OrderManagementController orders = new OrderManagementController();
		try {
			orders.start(new Stage());
			primaryStage.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * init button cancel
	 * @param btn Button to initialize
	 * @param state if set disable
	 */
	private void setButtonDisabledState(Button btn , boolean state)
	{
		if(order.getStatus() == Status.Canceled || order.getStatus() == Status.Completed)
			btn.setDisable(state);
	}
	/**
	 * initialize data and fill the controls
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getOrderDetails();
		setOrderListCellHandler();
		setOrderPaymentsList();
		setRefundsDatails();
		setButtonDisabledState(btnCacenlOrder,true);
	}
	/**
	 * get order details from server
	 */
	private void getOrderDetails() {
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.getOrderInProductsDetails);
		ArrayList<Object> list = new ArrayList<>();
		list.add(order.getoId());
		packet.setParametersForCommand(Command.getOrderInProductsDetails, list);
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
				if (p.getResultState()) {
					orderProducts = new LinkedHashMap<>();
					ArrayList<ProductInOrder> productInOrder = p.<ProductInOrder>convertedResultListForCommand(Command.getOrderInProductsDetails);
					ArrayList<Product> products = p.<Product>convertedResultListForCommand(Command.getAllProductsInOrder);
					ArrayList<FlowerInProduct> flowerInProductList = p.<FlowerInProduct>convertedResultListForCommand(Command.getFlowersInProductInOrder);
					ArrayList<Flower> flowerList =p.<Flower>convertedResultListForCommand(Command.getAllFlowersInOrder);
					for(ProductInOrder pInOrder : productInOrder)
					{
						Product prod = products.stream().filter(c->c.getId()==pInOrder.getProductId()).findFirst().orElse(null);
						ArrayList<FlowerInProduct> listFlowersInProd = (ArrayList<FlowerInProduct>) flowerInProductList.stream().filter(c->c.getProductId()==prod.getId()).collect(Collectors.toList());
						for(FlowerInProduct floInProd : listFlowersInProd)
						{
							floInProd.setFlower(flowerList.stream().filter(c->c.getId()==floInProd.getFlowerId()).findFirst().orElse(null));
						}
						prod.setFlowerInProductList(listFlowersInProd);
						orderProducts.put(prod, pInOrder.getQuantity());
					}
					fillItems();
					fillPayment();
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					lblCreDate.setText(""+formatter.format(order.getCreationDate()));
					formatter = new SimpleDateFormat("dd/MM/yyyy HH:MM");
					lblReqDate.setText(formatter.format(order.getRequestedDate()));
				}
				else//if it was error in connection
					ConstantData.displayAlert(AlertType.ERROR, "Orders Error","Error Getting Orders", "Can't retrieve orders from server");
			}
		});
		send.start();
	}
	/**
	 * fill product control view
	 */
	public void fillItems()
	{
		dataOrderLine = FXCollections.observableArrayList(new ArrayList<>(orderProducts.keySet()));
		listViewOrderLines.setItems(dataOrderLine);
	}
	/**
	 * fill payment control view
	 */
	public void fillPayment()
	{
		dataOrderPayment = FXCollections.observableArrayList(order.getOrderPaymentList());
		listViewOrderPayments.setItems(dataOrderPayment);
	}
	
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
	/**
	 * update order status
	 * @param order order to change
	 * @param status new status
	 */
	private void changeOrderStatus(Order order , Status status)
	{
		order.setStatus(status);
	}
	/**
	 * get total payment
	 * @param order order to get payments of
	 * @return total amount
	 */
	private double getOrderPayments(Order order)
	{
		double payments = 0;
		for(OrderPayment orderPayment : order.getOrderPaymentList())
		{
			if(orderPayment.getPaymentDate()!=null)
				payments+=orderPayment.getAmount();	
		}
		return payments;
	}
	/**
	 * get relevant refund by comparing date and time of the requested and current timestamp
	 * @param requestedTime requested time of the order to be ready
	 * @param currentTime current time now
	 * @return new refund 
	 */
	private Refund getCancelRefund(Timestamp requestedTime , Timestamp currentTime)
	{
		Map<TimeUnit,Long> diffTime = computeDiff(requestedTime,currentTime);
		Refund refund = null;
		
		//Check if the cancel request is on the same day of the order's creation time
		if ( diffTime.get(TimeUnit.DAYS) == 0 && diffTime.get(TimeUnit.HOURS) < 0)
		{
			diffTime.put(TimeUnit.HOURS, diffTime.get(TimeUnit.HOURS) + 24);
		}
		
		if (diffTime.get(TimeUnit.DAYS) == 0) {

			if (diffTime.get(TimeUnit.HOURS) >= 3) // canceled 3 hours or more before the requested time
			{
				// Full refund

				java.sql.Date currentDate = new java.sql.Date(new java.util.Date().getTime()); // get current date
				refund = new Refund(currentDate, getOrderPayments(order), order.getoId()); // create new full refund
				
			} else if (diffTime.get(TimeUnit.HOURS) >= 1 && diffTime.get(TimeUnit.HOURS) <= 3) {

				// 50% refund

				java.sql.Date currentDate = new java.sql.Date(new java.util.Date().getTime()); // get current date
				refund = new Refund(currentDate, getOrderPayments(order) - (getOrderPayments(order) * 0.5),order.getoId()); // create new half refund
			}
			else {
				// No refund, just cancel
				
				refund = null;
			}
		}
		
		else if(diffTime.get(TimeUnit.DAYS)>0)
		{
			java.sql.Date currentDate = new java.sql.Date(new java.util.Date().getTime()); // get current date
			refund = new Refund(currentDate, getOrderPayments(order), order.getoId()); // create new full refund
		}

		//Passed 1 day or more
		else {
			// No refund, just cancel
			
			refund = null;
		}
		return refund;
	}
	
	/***
	 * 
	 * @return The information if the order has been charged or not
	 */
	private boolean isCharged()
	{
		boolean isCharged = false;
		for(OrderPayment orderPayment : order.getOrderPaymentList())
			if(orderPayment.getPaymentDate() != null)
				isCharged = true;
		
		return isCharged;
	}
	
	public Packet initPacket()
	{
		changeOrderStatus(order,Status.Canceled);
		
		Packet packet = new Packet();
		
		packet.addCommand(Command.updateOrder);
		ArrayList<Object> paramListOrder = new ArrayList<>();
		paramListOrder.add(order);
		packet.setParametersForCommand(Command.updateOrder, paramListOrder);
		
		//Get current time
		java.util.Date today = new java.util.Date();
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(today.getTime());
		
		refund = getCancelRefund(order.getRequestedDate(),sqlTimestamp);
		if(refund != null  &&  isCharged())
		{			
			packet.addCommand(Command.updateAccountBalance);
			packet.addCommand(Command.addOrderRefund);
			ArrayList<Object> paramListUpdateAccount = new ArrayList<>();
			Account ac = CustomerMenuController.currentAcc;
			paramListUpdateAccount.add(ac.getBranchId());
			paramListUpdateAccount.add(ac.getCustomerId());
			paramListUpdateAccount.add(refund.getAmount());
			packet.setParametersForCommand(Command.updateAccountBalance, paramListUpdateAccount);
			ArrayList<Object> paramListRefund = new ArrayList<>();
			paramListRefund.add(refund);
			packet.setParametersForCommand(Command.addOrderRefund, paramListRefund);
		}
		return packet;
	}
	/**
	 * Handle cancel button pressed
	 */
	@FXML
	private void onCancelPressedHandler()
	{	
		Packet packet = initPacket(); 
		ISystemSender sender = new SystemSender();
		saveToServer(sender,packet);
	}
	public void saveToServer(ISystemSender sender, Packet packet)
	{
		sender.setPacket(packet);
		
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
					String refundInfo;
					
					
					if(!isCharged()) 
						refundInfo = "\nThere is no refund, Because of no payment Yet"; 	
					else if(refund != null)
						 refundInfo = String.format("You have a refund of: %.2f$", refund.getAmount());
					else
						 refundInfo = new String("You have no refund");
					 
					 ConstantData.displayAlert(AlertType.INFORMATION, "Cancel Order", "Success", "Order has been canceled successfully, " + refundInfo);
					 Stage stage = (Stage) btnBack.getScene().getWindow();
					 stage.close();
					 OrderManagementController orderManagementController = new OrderManagementController();
					 try {
						orderManagementController.start(stage);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else
				{
					ConstantData.displayAlert(AlertType.ERROR, "Cancel Order", "Error", p.getExceptionMessage());
				}
			}
		});
		sender.start();
	} 
	
	/***
	 * Show refund details, if exists
	 */
	private void setRefundsDatails()
	{
		Packet packet = new Packet(); 
		packet.addCommand(Command.getRefunds);
		
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
					ArrayList<Refund> refundList = p.<Refund>convertedResultListForCommand(Command.getRefunds);
					for(Refund refund : refundList)
						if(refund.getRefundAbleId() == order.getoId())
						{
							lblRefundInfo.setVisible(true);
							lblRefundAmount.setText(String.format("%.2f$",refund.getAmount()));
							lblRefundAmount.setVisible(true);
						}
				}
			}
		});
		sender.start();
	}
	
	/***
	 * Set order payment list cell handler
	 */
	private void setOrderPaymentsList() {
		listViewOrderPayments.setCellFactory(new Callback<ListView<OrderPayment>, ListCell<OrderPayment>>() {

			@Override
			public ListCell<OrderPayment> call(ListView<OrderPayment> param) {
				return new ListCell<OrderPayment>() {

					private void setCellHandler(OrderPayment payment) {
						Text method = new Text(payment.getPaymentMethod().toString());
						VBox vMethod = new VBox(method);
						Text amount = new Text("" + payment.getAmount() + "$");
						VBox vamount = new VBox(amount);
						Text date;
						if (payment.getPaymentDate() == null)
							date = new Text("End of month");
						else {
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
							date = new Text("" + formatter.format(payment.getPaymentDate()));
						}
						VBox vDate = new VBox(date);
						vMethod.setMinWidth(100);
						vDate.setMinWidth(100);
						vamount.setMinWidth(100);

						method.setFont(new Font(14));
						method.setTextAlignment(TextAlignment.CENTER);
						amount.setFont(new Font(14));
						amount.setTextAlignment(TextAlignment.CENTER);
						date.setFont(new Font(14));
						date.setTextAlignment(TextAlignment.CENTER);

						HBox hBox = new HBox(vMethod, vamount, vDate);
						hBox.setStyle("-fx-border-style: solid inside;" + "-fx-border-width: 1;"
								+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;");
						hBox.setSpacing(10);
						hBox.setPadding(new Insets(10));

						setGraphic(hBox);
					}

					@Override
					protected void updateItem(OrderPayment item, boolean empty) {
						if (item != null) {
							setCellHandler(item);
						}
					}
				};
			}
		});
	}

	/***
	 * Set Orders list view cell handler
	 */
	private void setOrderListCellHandler() {
		listViewOrderLines.setCellFactory(new Callback<ListView<Product>, ListCell<Product>>() {

			@Override
			public ListCell<Product> call(ListView<Product> param) {
				return new ListCell<Product>() {

					private void setCellHandler(Product pro) {
						int count = orderProducts.get(pro);

						// name of product
						Text proName = new Text();
						if (pro instanceof CatalogProduct)
							proName.setText(((CatalogProduct) pro).getName());
						else
							proName.setText("Custom product");
						proName.setFont(new Font(14));
						proName.setStyle("-fx-font-weight: bold");
						Text txtName = new Text("Product name");
						txtName.setUnderline(true);
						txtName.setFont(new Font(14));
						txtName.setStyle("-fx-font-weight: bold");

						VBox productDetails = new VBox(txtName, proName);
						productDetails.setSpacing(5);
						productDetails.setAlignment(Pos.TOP_CENTER);

						// price of product
						Text price = new Text(String.format("%.2f$", (pro.getPrice() * count)));
						price.setFont(new Font(14));
						price.setTextAlignment(TextAlignment.CENTER);
						Text txtprice = new Text("Price");
						txtprice.setUnderline(true);
						txtprice.setFont(new Font(14));
						txtprice.setStyle("-fx-font-weight: bold");
						VBox productPrice = new VBox(txtprice, price);
						productPrice.setAlignment(Pos.TOP_CENTER);
						productPrice.setSpacing(8);

						Text txtQty = new Text("Quantity");
						txtQty.setUnderline(true);
						txtQty.setFont(new Font(14));
						txtQty.setStyle("-fx-font-weight: bold");
						Text qty = new Text("" + orderProducts.get(pro) + " Unit");
						qty.setFont(new Font(13.5));
						qty.setTextAlignment(TextAlignment.CENTER);
						VBox qtyOptions = new VBox(txtQty, qty);
						qtyOptions.setAlignment(Pos.TOP_CENTER);
						qtyOptions.setSpacing(8);

						Text flowersTitle = new Text("Flowers Collection:");
						flowersTitle.setUnderline(true);
						flowersTitle.setFont(new Font(14));
						flowersTitle.setStyle("-fx-font-weight: bold");
						VBox flowers = new VBox(flowersTitle);

						for (FlowerInProduct fp : pro.getFlowerInProductList()) {
							Flower flowerFound = fp.getFlower();
							if (flowerFound != null) {
								Text flower = new Text(
										String.format("%s, Qty: %d", flowerFound.getName(), fp.getQuantity()));
								flower.setFont(new Font(13.5));
								flowers.getChildren().add(flower);
							}
						}

						flowers.setSpacing(2);
						HBox hBox = new HBox(productDetails, flowers, qtyOptions, productPrice);
						hBox.setStyle("-fx-border-style: solid inside;" + "-fx-border-width: 1;"
								+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;");
						hBox.setSpacing(10);
						hBox.setPadding(new Insets(10));

						setGraphic(hBox);
					}

					@Override
					protected void updateItem(Product item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setCellHandler(item);
						} else
							setGraphic(null);
					}
				};
			}
		});

	}
}
