package Products;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CatalogDiscountController extends Application implements Initializable {
	

	@FXML
	ComboBox<CatalogProduct> cmbCatalogProduct;
	@FXML
	TextField txtDiscount;
	
	private int brannchId;
	private ArrayList<CatalogInBranch> disc;
	public CatalogDiscountController(int branchId)
	{
		super();
		this.brannchId=branchId;
	}
	public CatalogDiscountController()
	{
		super();
	}
	public void start(Stage primaryStage) throws Exception {

		String title = "Products";
		String srcFXML = "/Products/ProductDiscountUI.fxml";
		String srcCSS = "/Products/application.css";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
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
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				Platform.exit();
			}
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// set listener to combobox when other item has been choosed
				cmbCatalogProduct.valueProperty().addListener(new ChangeListener<CatalogProduct>() {
					@Override
					public void changed(ObservableValue<? extends CatalogProduct> observable, CatalogProduct oldValue,
							CatalogProduct newValue) {
							
						double discount =(disc.stream().filter(c->c.getCatalogProductId()==newValue.getCatalogProductId()).findFirst()).get().getDiscount();
						txtDiscount.setText("" +discount);
					}
				});
				
				
				Packet packet = new Packet();//create packet to send
				packet.addCommand(Command.getCatalogProducts);//add command
				packet.addCommand(Command.getDiscountsByBranch);
				ArrayList<Object> param = new ArrayList<>(Arrays.asList(brannchId));
				packet.setParametersForCommand(Command.getCatalogProducts,param);
				packet.setParametersForCommand(Command.getDiscountsByBranch, param);
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
							ArrayList<CatalogProduct> cList = p.<CatalogProduct>convertedResultListForCommand(Command.getCatalogProducts);
							
							cmbCatalogProduct.getItems().addAll(cList);
							
							disc = p.<CatalogInBranch>convertedResultListForCommand(Command.getDiscountsByBranch);
							
						}
						else//if it was error in connection
							JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
					}
				});
				send.start();
		
	}
}
