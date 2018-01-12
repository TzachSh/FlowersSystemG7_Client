package Products;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import Branches.Branch;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.SelectProductController.CatalogProductDetails;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CatalogDiscountController implements Initializable {
	

	 @FXML
	 private Button btnCancel;

	 @FXML
	 private TextField txtDiscount;

	 @FXML
	 private Label lblErrorDiscount;

	 @FXML
	 private Button btnSave;

	 @FXML
	 private TextField txtBranch;

	 @FXML
	 private TextField txtProduct;
	
	 private static SelectProductController selectIController;
	 private static CatalogProduct catalogProduct;
	 private static Stage mainStage;
	 
	public void setCatalogDiscount(SelectProductController selectController, CatalogProduct product)
	{
		selectIController = selectController;
		catalogProduct = product;
	}
	
	public void onPressedCancelButton()
	{
		mainStage.close();
	}
	
	/**
	 * Show an Alert dialog with custom info
	 */
	public void displayAlert(AlertType type , String title , String header , String content)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	public void onPressedSaveButton()
	{
		int discountConverted = Integer.valueOf(txtDiscount.getText());
		int branchId = selectIController.branchId;
		int catPId = catalogProduct.getCatalogProductId();
		
		CatalogInBranch catInBranch = new CatalogInBranch(branchId, catPId, discountConverted);
		Packet packet = new Packet();
		packet.addCommand(Command.addSaleCatalogInBranch);
		
		ArrayList<Object> param = new ArrayList<>();
		param.add(catInBranch);
		
		packet.setParametersForCommand(Command.addSaleCatalogInBranch, param);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState())
				{
					int pId = catalogProduct.getId();
					selectIController.getProductById(pId).catalogSale = catInBranch;
					selectIController.fillCatalogItems();
					displayAlert(AlertType.INFORMATION, "Success", "Updated Successfull", "The Sale for current product was updated Successfully!");
					mainStage.close();
				}
				else
				{
					displayAlert(AlertType.ERROR, "Error", "Exception Error:", p.getExceptionMessage());
				}
			}

			@Override
			public void onWaitingForResult() { }
					
		});
				
		send.start();
		
	}

	public void start(Stage primaryStage) throws Exception {
		
		mainStage = primaryStage;
		String title = "Product Discount";
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
	}
	
	/**
	 * Handler event on changing text on Discount textField
	 */
	public void setDiscountChangeHandler()
	{
		txtDiscount.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				boolean valid = true;
				if (newValue.isEmpty())
				{
					lblErrorDiscount.setVisible(true);
					valid = false;
				}
				else
				{
					// check if number is valid
					try
					{
						int discountConverted = Integer.valueOf(newValue);
						if (discountConverted <= 0)
							throw new NumberFormatException();
						
						lblErrorDiscount.setVisible(false);
					}
					catch (NumberFormatException e)
					{
						lblErrorDiscount.setVisible(true);
						valid = false;
					}
				}
				
				if (valid)
				{
					btnSave.setDisable(false);
				}
				else
				{
					btnSave.setDisable(true);
				}
			}
		});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Branch branch = selectIController.getBranchByBranchId(selectIController.branchId);
		txtBranch.setText(branch.getName());
		
		txtProduct.setText(catalogProduct.getName());
		
		setDiscountChangeHandler();
	}
}
