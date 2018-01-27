package Orders;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.swing.JOptionPane;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import Commons.Status;
import Login.CustomerMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.CatalogProduct;
import Products.ConstantData;
import Products.Flower;
import Products.FlowerInProduct;
import Products.Product;
import Products.SelectProductController;
import Products.SelectProductController.CatalogProductDetails;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
 * 
 * Controller
 * Handling order managing options
 *   
 */
public class OrderManagementController implements Initializable {

	private static Stage primaryStage;
	@FXML
    private Button btnBack;
    @FXML
    private ListView<Order> listOrder;
    private ObservableList<Order> data;
    @FXML
    private ComboBox<String> cmbStatus;
    /**
     * order list of the customer in the branch
     */
    private HashMap<Status,ArrayList<Order>> ordersMap;
    /**
     * handle click on back button
     * close current window and open customer menu
     * @param event event
     */
    @FXML
    void onClickBack(ActionEvent event) {
		CustomerMenuController menu = new CustomerMenuController();
		try {
			menu.start(new Stage());
			primaryStage.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
	 * @param arg0 - Stage to be loaded
	 * @throws Exception - throw the relevant exception if can't load the stage
	 */
    public void start(Stage arg0) throws Exception {
		primaryStage=arg0;
		String title = "Orders";
		String srcFXML = "/Orders/OrderListUI.fxml";
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
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				CustomerMenuController menu = new CustomerMenuController();
				try {
					menu.start(new Stage());
					primaryStage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
    /**
     * get data from the server
     * initialize controls
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setListOrdersHandlers();
		getOrdersByCId();
		initCmbBox();
	}
	/**
	 * get orders by customer id and branch id
	 */
	private void getOrdersByCId() {
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.getOrdersByCIdandBrId);//add command
		packet.addCommand(Command.getPaymentDetails);//add command
		ArrayList<Object> list = new ArrayList<>();
		list.add(CustomerMenuController.currentAcc);
		packet.setParametersForCommand(Command.getOrdersByCIdandBrId, list);
		packet.setParametersForCommand(Command.getPaymentDetails,list);
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
					ordersMap = new HashMap<Status,ArrayList<Order>>();
					ordersMap.put(Status.Canceled, new ArrayList<>());
					ordersMap.put(Status.Completed, new ArrayList<>());
					ordersMap.put(Status.Pending, new ArrayList<>());
					ArrayList<Order> orderList = p.<Order>convertedResultListForCommand(Command.getOrdersByCIdandBrId);
					updateStatusForOrders(orderList);
					ArrayList<OrderPayment> payment = p.<OrderPayment>convertedResultListForCommand(Command.getPaymentDetails);
					for(Order order : orderList)
					{
						order.setOrderPaymentList((ArrayList<OrderPayment>) payment.stream().filter(c->c.getOrderId()==order.getoId()).collect(Collectors.toList()));
						ordersMap.get(order.getStatus()).add(order);
					}
					data = FXCollections.observableArrayList(orderList);
					listOrder.setItems(data);
				}
				else{//if it was error in connection
					Alert alert = new Alert(AlertType.ERROR,"Connection error");
					alert.show();
				}
			}
		});
		send.start();
	}
	/**
	 * initialize combobox data and set behavior when changing
	 */
	private void initCmbBox()
	{
		cmbStatus.getItems().addAll("All",Status.Canceled.name(),Status.Completed.name(),Status.Pending.name());
		cmbStatus.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
	        	switch(newValue)
	        	{
	        	case "Canceled":
	        			data = FXCollections.observableArrayList(ordersMap.get(Status.Canceled));
	        			listOrder.getItems().clear();
	        			for(Order order : data)
	        				listOrder.getItems().add(order);
	        			break;
	        	case "Pending":
	        		data = FXCollections.observableArrayList(ordersMap.get(Status.Pending));
        			listOrder.getItems().clear();
        			for(Order order : data)
        				listOrder.getItems().add(order);
	        			break;
	        	case "Completed":
	        		data = FXCollections.observableArrayList(ordersMap.get(Status.Completed));
	        		listOrder.getItems().clear();
        			for(Order order : data)
        				listOrder.getItems().add(order);
        			break;
	        	case "All":
        			getOrdersByCId();
        			break;
	        	}
	        }    
	    });
		
		cmbStatus.getSelectionModel().select(0);
	}
	/**
	 * fill the listView with orders
	 */
	private void setListOrdersHandlers() {
		listOrder.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {
			
			@Override
			public ListCell<Order> call(ListView<Order> param) {
				return new ListCell<Order>() {
					
				private void setCellHandler(Order order)
				{
					Date t = order.getRequestedDate();
					Calendar c = Calendar.getInstance();
					c.setTime(t);
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    VBox orderCol = new VBox(new Text(""+order.getoId()));
                    orderCol.setMinWidth(30);
                    orderCol.setAlignment(Pos.CENTER);
                    VBox amountCol = new VBox(new Text(String.format("%.2f$",order.getTotal())));
                    amountCol.setMinWidth(50);
                    amountCol.setAlignment(Pos.CENTER_RIGHT);
                    VBox createdCol = new VBox(new Text(formatter.format(order.getCreationDate())));
                    createdCol.setMinWidth(70);
                    createdCol.setAlignment(Pos.CENTER);
                    formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String reqDate = formatter.format(c.getTime());
                    VBox requestedDateCol = new VBox(new Text(reqDate));
                    requestedDateCol.setMinWidth(110);
                    requestedDateCol.setAlignment(Pos.CENTER);
                    String paymentValue="-";
                    if(order.getStatus() != Status.Canceled) {
							for (OrderPayment pay : order.getOrderPaymentList()) {
								if (pay.getPaymentDate() == null) {
									paymentValue = "Not charged";
								}
							}
							if (paymentValue.length() == 1)
								paymentValue = "Charged";
	                }
                    VBox payment = new VBox(new Text(paymentValue));
                    payment.setMinWidth(100);
                    payment.setAlignment(Pos.CENTER);
                    Button details=  new Button("Details");
                    details.setOnMouseClicked((event) ->  {
							OrderDetailsController menu = new OrderDetailsController();
							menu.setOrder(order);
							try {
								menu.start(new Stage());
								primaryStage.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
					});
                    VBox detailsCol = new VBox(details);
                    detailsCol.setMinWidth(90);
                    detailsCol.setAlignment(Pos.CENTER);
                    HBox lineBox = new HBox(orderCol,createdCol,requestedDateCol,amountCol,payment,detailsCol);
                    setGraphic(lineBox);
				}

			    @Override
				protected void updateItem(Order item, boolean empty) {
						super.updateItem(item, empty);
					 if (item != null) {	
						 	setCellHandler(item);
                        }
					 else
						 setGraphic(null);
				}};
			}
		});
		
		listOrder.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        });		
	}
	
	/***
	 * 
	 * @param paramList - List with updated orders to be saved.
	 * Save the updated orders to the server database 
	 */
	private void updateOrdersToDB(ArrayList<Object> paramList)
	{
		Packet packet = new Packet();
		packet.addCommand(Command.updateOrder);
		packet.setParametersForCommand(Command.updateOrder, paramList);
		
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
					//Status updated successfully, No message is necessary 
				}
				else
				{
					ConstantData.displayAlert(AlertType.ERROR, "Orders Update", "Orders Updating To DB", "Failed to update the orders");
				}
			}
		});
		sender.start();
	}
	
	/***
	 * 
	 * @param orderList with the relevant orders to update their status
	 * When the current time is after the requested date time for each order  in the list,
	 * Then the status has to be updated to COMPLETED
	 */
	private void updateStatusForOrders(ArrayList<Order> orderList)
	{
		ArrayList<Object> paramListOrders = new ArrayList<>();
		//Get current time
		java.util.Date today = new java.util.Date();
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(today.getTime());
		
		for(Order order : orderList)
			if(sqlTimestamp.after(order.getRequestedDate()) && order.getStatus() == Status.Pending) {
				order.setStatus(Status.Completed);
				paramListOrders.add(order);
			}
		
		updateOrdersToDB(paramListOrders);
	}
	
}
