package Orders;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
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

public class OrderManagementController implements Initializable {

	private static Stage primaryStage;
	@FXML
    private Button btnBack;
    @FXML
    private ListView<Order> listOrder;
    private ObservableList<Order> data;
    @FXML
    private ComboBox<String> cmbStatus;
    private HashMap<Status,ArrayList<Order>> ordersMap = new HashMap<Status,ArrayList<Order>>();
    
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
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getOrdersByCId();
		initCmbBox();

	}
	
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
					ordersMap.put(Status.Canceled, new ArrayList<>());
					ordersMap.put(Status.Completed, new ArrayList<>());
					ordersMap.put(Status.Pending, new ArrayList<>());
					ArrayList<Order> orderList = p.<Order>convertedResultListForCommand(Command.getOrdersByCIdandBrId);
					ArrayList<OrderPayment> payment = p.<OrderPayment>convertedResultListForCommand(Command.getPaymentDetails);
					for(Order order : orderList)
					{
						order.setOrderPaymentList((ArrayList<OrderPayment>) payment.stream().filter(c->c.getOrderId()==order.getoId()).collect(Collectors.toList()));
						ordersMap.get(order.getStatus()).add(order);
					}
					data = FXCollections.observableArrayList(orderList);
					fillOrders();
				}
				else//if it was error in connection
					JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
			}
		});
		send.start();
	}
	
	
	
	private void initCmbBox()
	{
		cmbStatus.getItems().addAll("All",Status.Canceled.name(),Status.Completed.name(),Status.Pending.name());
		cmbStatus.getSelectionModel().select(0);
		
		cmbStatus.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
	        	switch(newValue)
	        	{
	        	case "Canceled":
	        			listOrder.getItems().clear();
	        			listOrder.getItems().addAll(ordersMap.get(Status.Canceled));
	        			break;
	        	case "Pending":
	        			listOrder.getItems().clear();
	        			listOrder.getItems().addAll(ordersMap.get(Status.Pending));
	        			break;
	        	case "Completed":
        			listOrder.getItems().clear();
        			listOrder.getItems().addAll(ordersMap.get(Status.Completed));
        			break;
        		default:
        			listOrder.getItems().clear();
        			listOrder.getItems().addAll(ordersMap.get(Status.Pending));
        			listOrder.getItems().addAll(ordersMap.get(Status.Completed));
        			listOrder.getItems().addAll(ordersMap.get(Status.Canceled));
        			break;
	        	}
	        }    
	    });
	}
	
	private void fillOrders() {
		listOrder.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {
			
			@Override
			public ListCell<Order> call(ListView<Order> param) {
				return new ListCell<Order>() {
					
				private void setCellHandler(Order order)
				{
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    VBox orderCol = new VBox(new Text(""+order.getoId()));
                    orderCol.setMinWidth(0);
                    orderCol.setAlignment(Pos.CENTER);
                    VBox amountCol = new VBox(new Text(String.format("%.2f₪",order.getTotal())));
                    amountCol.setMinWidth(50);
                    amountCol.setAlignment(Pos.CENTER_RIGHT);
                    VBox createdCol = new VBox(new Text(formatter.format(order.getCreationDate())));
                    createdCol.setMinWidth(70);
                    createdCol.setAlignment(Pos.CENTER);
                    formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                    String reqDate = formatter.format(order.getRequestedDate());
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
							if (paymentValue.length() == 0)
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
						
					 if (item != null) {	
						 	setCellHandler(item);
                        }
				}};
			}
		});
		listOrder.setItems(data);
		
		listOrder.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        });		
	}
    

}
