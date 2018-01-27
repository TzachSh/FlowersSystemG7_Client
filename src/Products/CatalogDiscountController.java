package Products;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
/**
 * Controller
 * Handle discount catalog product in branches
 *
 */
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
	 
	 /**
	  * set catalog product for update discount
	  * @param selectController catalog
	  * @param product catalog product
	  */
	public void setCatalogDiscount(SelectProductController selectController, CatalogProduct product)
	{
		selectIController = selectController;
		catalogProduct = product;
	}
	/**
	 * Cancel button behavior
	 * close current window 
	 */
	public void onPressedCancelButton()
	{
		mainStage.close();
	}
	

	/**
	 * save new discount to catalog product in the branch
	 */
	public void onPressedSaveButton()
	{
		int discountConverted = Integer.valueOf(txtDiscount.getText());
		int branchId = SelectProductController.currentBranch.getbId();
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
					ConstantData.displayAlert(AlertType.INFORMATION, "Success", "Updated Successfull", "The Sale for current product was updated Successfully!");
					mainStage.close();
				}
				else
				{
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error:", p.getExceptionMessage());
				}
			}

			@Override
			public void onWaitingForResult() { }
					
		});
				
		send.start();
		
	}
	/**
	 * Show the scene view of complains management
	 * 
	 * @param primaryStage - current stage to build
	 * @throws Exception error message
	 */
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
						if (discountConverted <= 0 || discountConverted>100)
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
	/**
	 *  set up controls for the window
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if (SelectProductController.currentBranch != null)
			txtBranch.setText(SelectProductController.currentBranch.getName());
		
		txtProduct.setText(catalogProduct.getName());
		
		setDiscountChangeHandler();
	}
}
