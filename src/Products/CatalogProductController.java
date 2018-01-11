package Products;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Customers.Customer;
import PacketSender.Command;
import PacketSender.FileSystem;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.SelectProductController.CatalogUse;
import Users.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;




public class CatalogProductController implements Initializable
{
	// Views
    @FXML
    private ComboBox<String> cmbType;

    @FXML
    private TextField txtName;

    @FXML
    private Label errFlower;

    @FXML
    private Label errFlowerName;

    @FXML
    private ImageView imgProduct;

    @FXML
    private Label errNameTooMuchLong;

    @FXML
    private Button btnNext2;

    @FXML
    private Label errFlowerQtyNumber;

    @FXML
    private Button btnNext1;

    @FXML
    private Tab tabFlowers;

    @FXML
    private Button btnCancel3;

    @FXML
    private Label errName;

    @FXML
    private Label errPriceNum;

    @FXML
    private Button btnCancel1;

    @FXML
    private Button btnDel;

    @FXML
    private ComboBox<String> cmbFlower;

    @FXML
    private TextField txtQty;

    @FXML
    private Label errPrice;

    @FXML
    private Label errImage;

    @FXML
    private Button btnImage;

    @FXML
    private Tab tabImage;

    @FXML
    private Label errType;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnPrev3;

    @FXML
    private Tab tabProduct;

    @FXML
    private Button btnPrev2;

    @FXML
    private Button btnAdd;

    @FXML
    private Label errFlowerQty;

    @FXML
    private TextField txtPrice;

    @FXML
    private ListView<String> lstFlower;

    @FXML
    private TabPane tabPane;
	    
		private static String title;
		private static boolean updateForm;
		private static CatalogProduct catalogProduct;
		private static FileSystem imageProduct;
		private static ArrayList<FlowerInProduct> flowersInTheProduct = new ArrayList<>(); 
		private ArrayList<ProductType> productTypesList = new ArrayList<>();
		private ArrayList<Flower> flowersList = new ArrayList<>();
		private Stage primaryStage;
		public static boolean comesFromCatalog = false;
	/**
	 * Prepare the Form for updating an exists product
	 * @param product The instance of the product to update
	 */
	public void setCatalogProductForUpdating(CatalogProduct product, FileSystem productImage)
	{
		if (product != null && productImage != null) // uses for update product
		{
			catalogProduct = product;
			imageProduct = productImage;
			flowersInTheProduct = catalogProduct.getFlowerInProductList();
			updateForm = true;
			title = "Update Catalog Product";
			imageProduct.setProductId(catalogProduct.getId()); // link image product id
		}
		else // set for insert
		{
			setCatalogProductForInserting();
		}
	}
	
	/**
	 * Prepare the Form for inserting a new product
	 */
	public void setCatalogProductForInserting()
	{
		catalogProduct = new CatalogProduct();
		imageProduct = new FileSystem();
		flowersInTheProduct = new ArrayList<>();
		updateForm = false;
		title = "Insert New Catalog Product";
	}
	
	/**
	 * Get the Product Type object from the collection by his index
	 */
	public ProductType getProductTypeByIndex(int index)
	{
		return productTypesList.get(index);
	}

	/**
	 * Set Product Details for product instance
	 * @param name The name of the product
	 * @param price The price of the product
	 * @param index the index of the product type on the collection
	 */
	public void updateProductDetails(String name, double price, int index)
	{
		// overwrite the new fields for CatalogProduct object
		int typeId = getProductTypeByIndex(index).getId();
		catalogProduct.setName(name);
		catalogProduct.setPrice(price);
		catalogProduct.setProductTypeId(typeId);
	}
	
	public void setProductsTypes(ArrayList<ProductType> productTypesList)
	{
		this.productTypesList = productTypesList;
	}
	
	public void setFlowers(ArrayList<Flower> flowersList)
	{
		this.flowersList = flowersList;
	}
	
	/**
	 * Send a request to the server for getting all the Collections for product Types and Flowers
	 */
	public void pushAllTypesAndFlowers()
	{
		Packet packet = new Packet();
		packet.addCommand(Command.getProductTypes);
		packet.addCommand(Command.getFlowers);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			@Override
			public void onWaitingForResult() {	}

			@Override
			public void onReceivingResult(Packet p)
			{
				if (p.getResultState())
				{
					setProductsTypes(p.<ProductType>convertedResultListForCommand(Command.getProductTypes));
					setFlowers(p.<Flower>convertedResultListForCommand(Command.getFlowers));
					
					setComboBoxTypesList(productTypesList);
					setComboBoxFlowersList(flowersList);
					
					/* load all exists product info */
					loadExistsProductDetails();
					
					txtName.requestFocus();
				}
				else
				{
					displayAlert(AlertType.ERROR, "Error", "Exception Error:", p.getExceptionMessage());
				}
			}
		});
		send.start();
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
	
	/**
	 * Initialize the ComboBox of Types with List of Types
	 * @param productTypesList the collection of Types to insert to Types ComboBox
	 */
	public void setComboBoxTypesList(ArrayList<ProductType> productTypesList)
	{
		ArrayList<String> types = new ArrayList<>();
		
		for (ProductType p : productTypesList)
			types.add(p.toString());
		
		ObservableList<String> observeTypesList = FXCollections.observableArrayList(types);
		cmbType.setItems(observeTypesList);
	}
	
	/**
	 * This method check validation of flowers relating to this product
	 * If there is no found any flowers to the product, show error Label
	 */
	public void flowersDetailsValidation()
	{
		 // there is no flowers registered to this product
		if (flowersInTheProduct == null || flowersInTheProduct.size() == 0)
		{
			errFlower.setVisible(true);
			btnNext2.setDisable(true);
		}
		else // there is one or more flowers
		{
			errFlower.setVisible(false);
			btnNext2.setDisable(false);
		}
	}
	
	/**
	 * This method check validation of Product Details pane
	 * 
	 * @param name The name of the product
	 * @param price The price of the product
	 * @param selectedTypeIndex The type of the product
	 */
	public void productDetailsValidation(String name, String price, int selectedTypeIndex)
	{
		boolean valid = true;
		
		if (name.isEmpty())
		{
			errName.setVisible(true);
			valid = false;
		}
		else
		{
			errName.setVisible(false);
		}
		
		if (price.isEmpty())
		{
			errPrice.setVisible(true);
			valid = false;
			errPriceNum.setVisible(false);
		}
		else
		{
			errPrice.setVisible(false);
			
			// check if number is valid
			try
			{
				double priceConverted = Double.valueOf(price);
				if (priceConverted <= 0)
					throw new NumberFormatException();
				
				errPriceNum.setVisible(false);
			}
			catch (NumberFormatException e)
			{
				errPriceNum.setVisible(true);
				valid = false;
			}
		}
		
		if (selectedTypeIndex == -1)
		{
			errType.setVisible(true);
			valid = false;
		}
		else
		{
			errType.setVisible(false);
		}
		
		if (valid)
		{
			btnNext1.setDisable(false);
		}
		else
		{
			btnNext1.setDisable(true);
		}
	}
	
	/**
	 * Handler event on pressing the 'Next' Button of the Flower tab
	 */
	public void pressedNextOnFlowerDetails()
	{
		tabProduct.setDisable(true);
		tabFlowers.setDisable(true);
		tabImage.setDisable(false);
		
		// select Image Pane as the next tab
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(2);
	}
	
	/**
	 * Handler event on pressing the 'Save' Button
	 * Send request to the server for saving the product 
	 */
	public void pressedSaveButton()
	{
		if (!updateForm)
		{
			insertProductToDB(catalogProduct, imageProduct, flowersInTheProduct);
		}
		else
		{
			updateProductToDB(catalogProduct, imageProduct, flowersInTheProduct);
		}
	}
	
	public void clearAllForm()
	{
		txtName.clear();
		cmbType.getSelectionModel().select(-1);
		txtPrice.clear();
		
		
		clearAddingFlowerFields();
		lstFlower.getItems().clear();
		
		File file = new File("blank.png");
	    Image image = new Image(file.toURI().toString());
        imgProduct.setImage(image);
        
        tabProduct.setDisable(false);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(0);
		
		tabFlowers.setDisable(true);
		tabImage.setDisable(true);
		
		catalogProduct = new CatalogProduct();
		flowersInTheProduct = new ArrayList<>();
		
		productDetailsValidation("", "", -1);
		addingFlowerValidation(-1, "");
		flowersDetailsValidation();
		imageCheckValidation(false);
	}
	
	/**
	 * Insert instance of catalogProduct to db
	 * @param pro instance of CatalogProduct
	 */
	public void insertProductToDB(CatalogProduct pro, FileSystem imageProduct, ArrayList<FlowerInProduct> flowerInProduct)
	{
		try
		{
	    ArrayList<Object> imgparams = new ArrayList<>();
		imgparams.add(imageProduct);
		
		ArrayList<Object> params = new ArrayList<>();
		params.add(pro);
	
		ArrayList<Object> fp = new ArrayList<>(flowerInProduct);
		
		Packet packet = new Packet();
		packet.addCommand(Command.insertCatalogProduct);
		packet.addCommand(Command.updateCatalogImage);
		packet.addCommand(Command.insertFlowersInProduct);
		
		packet.setParametersForCommand(Command.insertCatalogProduct, params);
		packet.setParametersForCommand(Command.updateCatalogImage, imgparams);
		packet.setParametersForCommand(Command.insertFlowersInProduct, fp);
	
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() { }
			
			@Override
			public void onReceivingResult(Packet p) 
			{
				if (p.getResultState())
				{
					// update new id to instance of product
					CatalogProduct productResult = (CatalogProduct)p.getParameterForCommand(Command.insertCatalogProduct).get(0);
					pro.setId(productResult.getId());
					imageProduct.setProductId(productResult.getId());
					displayAlert(AlertType.INFORMATION, "Success Inserting", "Inserting To Database", "Successfull!");
					pressedCancelButton();
				}
				else
				{
					String exception = p.getExceptionMessage();
					if (exception.toLowerCase().contains("duplicate"))
					{
						displayAlert(AlertType.ERROR, "Duplicate Product Name", "Failed on Inserting Catalog Product", "The Product Name is Already Exists! Please Enter another one.");
				
						tabFlowers.setDisable(true);
						tabImage.setDisable(true);
						tabProduct.setDisable(false);
						SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
						selectionModel.select(0);
						
						txtName.requestFocus();
					}
					else
					{
						displayAlert(AlertType.ERROR, "Error Inserting", "Failed on Inserting Catalog Product", exception);
					}
				}
			}
		});
		send.start();
		}
		catch (Exception e)
		{
			displayAlert(AlertType.ERROR, "Error Inserting", "Failed on Inserting Image", e.getMessage());
		}
	}
	
	/**
	 * Update instance of catalogProduct to db
	 * @param pro the product instance of CatalogProduct
     * @param img the image instance of CatalogProduct
     * @param flowerInProduct the collection flowers of this product
	 */
	public void updateProductToDB(CatalogProduct pro, FileSystem img, ArrayList<FlowerInProduct> flowerInProduct)
	{
		try
		{
		ArrayList<Object> params = new ArrayList<>();
		params.add(pro);
	
		ArrayList<Object> fp = new ArrayList<>(flowerInProduct);
		Packet packet = new Packet();
		
		if (!pro.getImgUrl().isEmpty()) // if user update exists image
		{
			ArrayList<Object> imgparams = new ArrayList<>();
			imgparams.add(img);
			
			packet.addCommand(Command.updateCatalogImage);
			packet.setParametersForCommand(Command.updateCatalogImage, imgparams);
		}
		
		packet.addCommand(Command.updateProduct);
		packet.addCommand(Command.updateCatalogProduct);
		
		packet.addCommand(Command.deleteFlowersInProduct);
		packet.addCommand(Command.updateFlowersInProduct);
		
		packet.setParametersForCommand(Command.updateProduct, params);
		packet.setParametersForCommand(Command.updateCatalogProduct, params);
		
		packet.setParametersForCommand(Command.deleteFlowersInProduct, params);
		packet.setParametersForCommand(Command.updateFlowersInProduct, fp);
		
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() { }
			
			@Override
			public void onReceivingResult(Packet p) 
			{
				if (p.getResultState())
				{
					displayAlert(AlertType.INFORMATION, "Success Updating", "Updating To Database", "Successfull!");
					pressedCancelButton();
				}
				else
				{
					String exception = p.getExceptionMessage();
					if (exception.toLowerCase().contains("duplicate"))
					{
						displayAlert(AlertType.ERROR, "Duplicate Product Name", "Failed on Updating Catalog Product", "The Product Name is Already Exists! Please Enter another one.");
					
						tabFlowers.setDisable(true);
						tabImage.setDisable(true);
						tabProduct.setDisable(false);
						SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
						selectionModel.select(0);
					}
					else
					{
						displayAlert(AlertType.ERROR, "Error Updating", "Failed on Updating Catalog Product", exception);
					}
				}
			}
		});
		send.start();
		}
		catch (Exception e)
		{
			displayAlert(AlertType.ERROR, "Error Inserting", "Failed on Inserting Image", e.getMessage());
		}
	}
	
	public void updateProductIdInInstances(int productId)
	{
		catalogProduct.setId(productId);
		for (FlowerInProduct fp : flowersInTheProduct)
			fp.setProduct(productId);
	}
	
	/**
	 * Get the index of the collection of types by type Id
	 * @param typeId The type Id to search for index
	 * @return The index of the item in the collection that found, If not found, return -1
	 */
	public int getIndexOfTypeInTheCollectionByTypeId(int typeId)
	{
		for (int i = 0; i < productTypesList.size(); i++)
		{
			if (productTypesList.get(i).getId() == typeId)
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Get the instance of flower by flower Id
	 * @param flowerId The flower Id to search for index
	 * @return The instance of the flower in the collection that found, If not found, return null
	 */
	public Flower getFlowerInTheCollectionByFlowerId(int flowerId)
	{
		for (Flower flower : flowersList)
		{
			if (flower.getId() == flowerId)
				return flower;
		}
		
		return null;
	}
	
	/**
	 * Loads exists product and flowers in this product to the form
	 */
	public void loadExistsProductDetails()
	{
		if (updateForm)
		{
			/* loads Product details tab  */
			txtName.setText(catalogProduct.getName());
			int typeIndex = getIndexOfTypeInTheCollectionByTypeId(catalogProduct.getProductTypeId());
			cmbType.getSelectionModel().select(typeIndex);
			txtPrice.setText("" + catalogProduct.getPrice());
		
			/* loads Exists Flowers tab */
			for (FlowerInProduct fp : flowersInTheProduct)
			{
				lstFlower.getItems().add(lstFlower.getItems().size(), getFlowerFormatOnList(fp));
				lstFlower.scrollTo(lstFlower.getItems().size() - 1);
			}
		
			/*  load Exists Image tab */
			loadImage(imageProduct);
		}
	}
	
	/**
	 * Handler event on pressing the 'Cancel' Button
	 */
	public void pressedCancelButton()
	{
		 try {
		  Stage stage = (Stage)btnCancel1.getScene().getWindow();
		  stage.close();
		  
		  if (updateForm || comesFromCatalog)
		  {
			SelectProductController selectController = new SelectProductController();
			selectController.setForUpdateCatalog(SelectProductUI.customer);
		  	selectController.start(new Stage());
		  }
		  else
		  {
			  //<< back to menu >>
		  }
		} 
		 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Handler event on pressing the 'Next' Button of the Product tab
	 */
	public void pressedNextOnProductDetails()
	{
		String name = txtName.getText();
		int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
		double price = Double.valueOf(txtPrice.getText());
		
		try
		{
			updateProductDetails(name, price, selectedTypeIndex);
		
			// select Flower Pane as the next tab
			tabFlowers.setDisable(false);
			SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
			selectionModel.select(1);
			
			tabProduct.setDisable(true);
			tabImage.setDisable(true);
		}
		catch (IndexOutOfBoundsException e)
		{
			tabFlowers.setDisable(true);
			displayAlert(AlertType.ERROR, "Error", "Selected Type Error", "Failed On Saving Product Details, Make Sure You Selected Type");
		}
	}
	
	/**
	 * Handler event on pressing the 'Prev' Button of the Flower tab
	 */
	public void pressedPrevOnFlowerDetails()
	{
		// select Product Pane as the next tab
		tabProduct.setDisable(false);
		tabFlowers.setDisable(true);
		tabImage.setDisable(true);
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(0);
	}
	
	/**
	 * Handler event on pressing the 'Import Image' Button
	 */
	public void pressedImportImageButton()
	{
		  FileChooser fileChooser = new FileChooser();
		  
		  //Set extension filter
          FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
          FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
          fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
          
          File file = fileChooser.showOpenDialog(primaryStage);
          
          if (file != null)
          {
        	 catalogProduct.setImgUrl(file.getPath());
        	 imageProduct.loadImageFromLocal(file.getPath());
        	
             loadImage(file);
             
          	 imageCheckValidation(true);
          }
	}
	
	/**
	 * Load a local image to the image viewer
	 * @param path The path of the local image
	 */
	private void loadImage(FileSystem imageProduct)
	{
		try
		{
			Image img = imageProduct.getImageInstance();
			imgProduct.setImage(img);
			imageCheckValidation(true);
		}
		catch (IOException e)
		{
			displayAlert(AlertType.ERROR, "Image File Corrupted", "Image Loading was Failed:", e.getMessage());
			imageCheckValidation(false);
			
			tabFlowers.setDisable(true);
			tabImage.setDisable(false);
			tabProduct.setDisable(true);
			SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
			selectionModel.select(2);
		}
	}
	
	/**
	 * Load a local file instance of image to the image viewer
	 * @param file The file instance that contains the image
	 */
	private void loadImage(File file)
	{
		 Image image = new Image(file.toURI().toString());
         imgProduct.setImage(image);
	}
	
	/**
	 * Check validation when there is a image for this product
	 */
	public void imageCheckValidation(boolean imageInserted)
	{
		if (imageInserted == true)
		{
			btnSave.setDisable(false);
			errImage.setVisible(false);
		}
		else
		{
			btnSave.setDisable(true);
			errImage.setVisible(true);
		}
	}
	
	/**
	 * Handler event on pressing the 'Prev' Button of the Image tab
	 */
	public void pressedPrevOnImageDetails()
	{
		tabProduct.setDisable(true);
		tabFlowers.setDisable(false);
		tabImage.setDisable(true);
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(1);
	}
	
	/**
	 * Initialize the ComboBox of Flowers with List of flowers
	 * @param flowersList the collection of flowers to insert to Flower ComboBox
	 */
	public void setComboBoxFlowersList(ArrayList<Flower> flowersList)
	{
		ArrayList<String> flowers = new ArrayList<>();
		
		for (Flower f : flowersList)
			flowers.add(f.toString());
		
		ObservableList<String> observeFlowersList = FXCollections.observableArrayList(flowers);
		cmbFlower.setItems(observeFlowersList);
	}
	
	/**
	 * Handler event on changing the product name textField
	 */
	public void setNameFieldChangeHandler()
	{
		txtName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
				productDetailsValidation(newValue, txtPrice.getText(), selectedTypeIndex);
			}
		});
	}
	
	/**
	 * Handler event on changing the item selected on Type ComboBox
	 */
	public void setTypeChangeHandler(ActionEvent event)
	{
		int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
		productDetailsValidation(txtName.getText(), txtPrice.getText(), selectedTypeIndex);
		txtPrice.requestFocus();
	}
	
	/**
	 * This method check validation of adding a flower pane
	 * 
	 * @param selectedFlowerIndex the selected ComboBox index of flower
	 * @param qty the quantity of the flower field
	 */
	public void addingFlowerValidation(int selectedFlowerIndex, String qty)
	{
		boolean valid = true;
		
		// check Quantity field
		if (qty.isEmpty())
		{
			errFlowerQty.setVisible(true);
			valid = false;
			errFlowerQtyNumber.setVisible(false);
		}
		else
		{
			errFlowerQty.setVisible(false);
			
			// check if number is valid
			try
			{
				int qtyConverted = Integer.valueOf(qty);
				if (qtyConverted <= 0)
					throw new NumberFormatException();
				
				errFlowerQtyNumber.setVisible(false);
			}
			catch (NumberFormatException e)
			{
				errFlowerQtyNumber.setVisible(true);
				valid = false;
			}
		}
		
		// check Flower combobox
		if (selectedFlowerIndex == -1)
		{
			errFlowerName.setVisible(true);
			txtQty.setDisable(true);
			valid = false;
		}
		else
		{
			errFlowerName.setVisible(false);
			txtQty.setDisable(false);
		}
		
		if (valid)
		{
			btnAdd.setDisable(false);
		}
		else
		{
			btnAdd.setDisable(true);
		}
	}
	
	/**
	 * Handler event when pressing on del button and delete the selected flower
	 */
	public void pressedDelFlowersButtonHandler()
	{
		int index = lstFlower.getSelectionModel().getSelectedIndex();
		deleteExistsFlowerFromList(index);
		
	}
	/**
	 * Delete an exists flower from the list
	 * @param index The index of the flower in the index
	 */
	public void deleteExistsFlowerFromList(int index)
	{
		flowersInTheProduct.remove(index);
		lstFlower.getItems().remove(index);
		
		// use the validation for set error label for empty flowers when needed
		flowersDetailsValidation();
	}
	
	/**
	 * Handler event on selecting some item on exists flowers list
	 */
	public void setFlowerItemSelectedHandler()
	{
		lstFlower.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	int index = lstFlower.getSelectionModel().getSelectedIndex();
		    	if (index == -1)
		    		btnDel.setDisable(true);
		    	else
		    		btnDel.setDisable(false);
		    }
		});
	}
	
	/**
	 * Returns a String formatted that contains the flower name with its quantity
	 * @param flowerInProduct the flower in product instance
	 */
	private String getFlowerFormatOnList(FlowerInProduct flowerInProduct)
	{
		String flowerName = "";

		Flower flower = getFlowerInTheCollectionByFlowerId(flowerInProduct.getFlowerId());
		if (flower != null)
			flowerName = flower.getName();
		
		return String.format("Flower: %s, Qty: %d", flowerName, flowerInProduct.getQuantity());
	}
	
	/**
	 * Returns a String formatted that contains the flower name with its quantity
	 * @param flower The flower name
	 * @param qty The quantity of the flower
	 */
	private String getFlowerFormatOnList(String flower, int qty)
	{
		return String.format("Flower: %s, Qty: %d", flower, qty);
	}
	
	/**
	 * Handler event on pressing on Add Flower Button
	 */
	public void pressedAddFlowerButton()
	{
		int selectedFlowerIndex = cmbFlower.getSelectionModel().getSelectedIndex();
		Flower selectedFlower = flowersList.get(selectedFlowerIndex);
		
		String flowerName = selectedFlower.getName();
		int flowerId = selectedFlower.getId();
		int qty = Integer.valueOf(txtQty.getText());
		
		try
		{
			// add flower to flowerInProduct list
			addFlowerToTheProduct(flowerId, qty);
			
			clearAddingFlowerFields();
		
			String flowerFormat =  getFlowerFormatOnList(flowerName, qty);
			lstFlower.getItems().add(lstFlower.getItems().size(), flowerFormat);
			lstFlower.scrollTo(lstFlower.getItems().size() - 1);
		}
		catch (FlowerIsAlreadyExistsException e)
		{
			displayAlert(AlertType.ERROR, "Flower Exists", "Failed on adding the flower: " + flowerName, "This Flower is already exists");
		}
		finally
		{
			// use the validation for remove the error label for empty flowers
			flowersDetailsValidation();
		}
	}
	
	/**
	 * Add The flower with its details to the product
	 * @param flowerId The flower ID
	 * @param qty Quantity of the flower for this product
	 * @throws FlowerIsAlreadyExistsException Exception when the flower is already exists in this product
	 */
	public void addFlowerToTheProduct(int flowerId, int qty) throws FlowerIsAlreadyExistsException
	{
		if (checkIfFlowerInProductExistsByID(flowerId))
			throw new FlowerIsAlreadyExistsException();
		
		FlowerInProduct fPro;
		if (updateForm)
		{
			// put the exists id of the product
			fPro = new FlowerInProduct(flowerId, catalogProduct.getId(), qty);
		}
		else
		{
			// insert with unknown id of product
			fPro = new FlowerInProduct(flowerId, qty);
		}
		
		flowersInTheProduct.add(fPro);
	}
	
	/**
	 * Check if Flower exists in flower in product list by flower id
	 * @param flowerId The flower Id
	 * @return true if exists, false else
	 */
	private boolean checkIfFlowerInProductExistsByID(int flowerId)
	{
		for (FlowerInProduct fp : flowersInTheProduct)
		{
			if (fp.getFlowerId() == flowerId)
				return true;
		}
		
		return false;
	}
	
	/**
	 * clear the ComboBox and the Qty Field to default values
	 */
	public void clearAddingFlowerFields()
	{
		txtQty.clear();
		cmbFlower.getSelectionModel().select(-1);
	}
	
	/**
	 * Handler event on changing the item selected on Flower ComboBox
	 */
	public void setFlowerChangeHandler(ActionEvent event)
	{
		int selectedFlowerIndex = cmbFlower.getSelectionModel().getSelectedIndex();
		addingFlowerValidation(selectedFlowerIndex, txtQty.getText());
		txtQty.requestFocus();
	}
	
	/**
	 * Handler event on changing text on Price textField
	 */
	public void setPriceChangeHandler()
	{
		txtPrice.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
				productDetailsValidation(txtName.getText(), newValue, selectedTypeIndex);
			}
		});
	}
	
	/**
	 * Handler event on changing text on Qty textField
	 */
	public void setQuantityFlowerChangeHandler()
	{
		txtQty.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				int selectedFlowerIndex = cmbFlower.getSelectionModel().getSelectedIndex();
				addingFlowerValidation(selectedFlowerIndex, newValue);
			}
		});
	}

	public void start(Stage primaryStage) throws Exception {	
		
		this.primaryStage = primaryStage;
		String srcFXML = "/Products/EditProductApp.fxml";
		String srcCSS = "/Products/application.css";
		
		Parent root = FXMLLoader.load(getClass().getResource(srcFXML));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
		primaryStage.setTitle(title);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);		
		primaryStage.show();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
		/* call method to push collections from server */
		pushAllTypesAndFlowers();
		
		/* register all handlers events */
		setNameFieldChangeHandler();
		setPriceChangeHandler();
		setQuantityFlowerChangeHandler();
		setFlowerItemSelectedHandler();
		
		/* display all missing fields */
		int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
		int selectedFlowerIndex = cmbFlower.getSelectionModel().getSelectedIndex();
		productDetailsValidation(txtName.getText(), txtPrice.getText(), selectedTypeIndex);
		addingFlowerValidation(selectedFlowerIndex, txtQty.getText());
		flowersDetailsValidation();
	}
	
}
