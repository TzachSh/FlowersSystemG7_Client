package Orders;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.swing.JOptionPane;

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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
	}
	private void getOrdersByCId() {
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.getOrdersByCIdandBrId);//add command
		ArrayList<Object> list = new ArrayList<>();
		list.add(CustomerMenuController.currentAcc);
		packet.setParametersForCommand(Command.getOrdersByCIdandBrId, list);
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
					ArrayList<Order> orderList = p.<Order>convertedResultListForCommand(Command.getOrdersByCIdandBrId);
					data = FXCollections.observableArrayList(orderList);
					fillOrders();
				}
				else//if it was error in connection
					JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
			}
		});
		send.start();
	}
	protected void fillOrders() {
		listOrder.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {
			
			@Override
			public ListCell<Order> call(ListView<Order> param) {
				return new ListCell<Order>() {
					
				private void setCellHandler(Order order)
				{
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    VBox orderCol = new VBox(new Text(""+order.getoId()));
                    orderCol.setMinWidth(30);
                    VBox amountCol = new VBox(new Text(String.format("%.2f¤",order.getTotal())));
                    amountCol.setMinWidth(50);
                    VBox createdCol = new VBox(new Text(formatter.format(order.getCreationDate())));
                    createdCol.setMinWidth(70);
                    formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                    String reqDate = formatter.format(order.getRequestedDate());
                    VBox requestedDateCol = new VBox(new Text(reqDate));
                    requestedDateCol.setMinWidth(120);
                    Button details=  new Button("Details");
                    details.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							
							
						}
					});
                    VBox detailsCol = new VBox(details);
                    detailsCol.setMinWidth(50);
                    HBox lineBox = new HBox(orderCol,createdCol,requestedDateCol,amountCol,detailsCol);
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
            	//listViewCatalog.getSelectionModel().select(-1);
                event.consume();
            }
        });		
	}
    

}
