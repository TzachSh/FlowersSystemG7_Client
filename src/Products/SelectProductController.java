package Products;



import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import Branches.Branch;
import Branches.Employee;
import Customers.AccountStatus;
import Login.CustomerMenuController;
import Login.LoginController;
import Login.ManagersMenuController;
import PacketSender.Command;
import PacketSender.FileSystem;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * Controller
 * Display catalog with selected branch
 */
public class SelectProductController implements Initializable
{
    @FXML
    private Separator sep;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnAddCatalogProduct;

    @FXML
    private Label lblTitle;

    @FXML
    private ListView<Product> listViewCatalog;

    @FXML
    private Label lblBranch;

    /** enum for indicate the window that call to this controller  */
    public enum CatalogUse { updateSale, order, updateCatalog, viewingCatalog, cart }
    
    /**  linkedHashMap for easy mapping from catalogProduct to it's details such sales and image */
	public static LinkedHashMap<Product, CatalogProductDetails> catalogProductWithAdditionalDetails = new LinkedHashMap<>();

	/** the selection products in the listview, used for order operation */
	public static ArrayList<Product> productsSelected = new ArrayList<>();
	/**all flowers*/
	public static ArrayList<Flower> flowersList = new ArrayList<>();
	/**
	 * all product types
	 */
	public static ArrayList<ProductType> productsTypeList = new ArrayList<>();
	/**
	 * purpose catalog
	 */
	public static CatalogUse catalogUse;
	/**
	 * title for catalog
	 */
	private static String title;
	/**
	 * dynamic list to display products
	 */
	private ObservableList<Product> data;
	public static Stage mainStage;
	private static SelectProductController controllerInstance;
	/**
	 * selected branch
	 */
	public static Branch currentBranch;

	
	/**
	 * Set the controller to initialize for updating sale
	 */
	public void setForUpdateSale()
	{
		catalogUse = CatalogUse.updateSale;
		title = "Updating Sales";
	}
	
	/**
	 *  Set the controller to initialize for ordering
	 */
	public void setForOrder()
	{
		catalogUse = CatalogUse.order;
		title = "Add To Cart";
	}
	
	/**
	 *  Set the controller to initialize for ordering from cart
	 */
	public void setForCart()
	{
		catalogUse = CatalogUse.cart;
		title = "Add To Cart";	
	}
	
	/**
	 * Set the controller to initialize for viewing in catalog and also order
	 */
	public void setForViewingCatalog()
	{
		catalogUse = CatalogUse.viewingCatalog;
		title = "Catalog Products Viewer";
	}
	
	/**
	 * Set the controller to initialize for updating Catalog
	 */
	public void setForUpdateCatalog()
	{
		catalogUse = CatalogUse.updateCatalog;
		title = "Updating Catalog Products";
	}
	
	/**
	 * Send to the server request to get the discount of the selected branch
	 */
	public void setDiscountsForSelectedBranch()
	{
		if (currentBranch == null)
			return;
		
		int branchId = currentBranch.getbId();
		Packet packet = new Packet();
		packet.addCommand(Command.getBranchSales);
		
		ArrayList<Object> param = new ArrayList<>();
		param.add(branchId);
		
		packet.setParametersForCommand(Command.getBranchSales, param);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

			@Override
			public void onReceivingResult(Packet p) {
				ArrayList<CatalogInBranch> sales = p.<CatalogInBranch>convertedResultListForCommand(Command.getBranchSales);

				// link each catalog product to it's sales, if there is a sale
				for (Map.Entry<Product, CatalogProductDetails> entry : catalogProductWithAdditionalDetails.entrySet())
				{
					CatalogProduct pro = (CatalogProduct)entry.getKey();
					CatalogProductDetails proDetails = entry.getValue();
					
					proDetails.catalogSale = null;
					
					for (int i = 0; i < sales.size(); i++)
					{
						CatalogInBranch catalogSale = sales.get(i);
						if (catalogSale.getCatalogProductId() == pro.getCatalogProductId())
							proDetails.catalogSale = new CatalogInBranch(catalogSale.getBranchId(), catalogSale.getCatalogProductId(), catalogSale.getDiscount());
					}
				}
				
				fillCatalogItems();
				listViewCatalog.getSelectionModel().select(-1);
			}

			@Override
			public void onWaitingForResult() { }
			
		});
		
		send.start();
	}
	
	/**
	 * Get the instance of Product Type by type id
	 * @param typeId The Type id for searching type
	 * @return product type description
	 */
	public ProductType getProductTypeByTypeId(int typeId)
	{
		for (ProductType type : productsTypeList)
		{
			if (type.getId() == typeId)
				return type;
		}
		return null;
	}
	
	/**
	 * Set a specific product as deleted, by changing it's state in db to unActive
	 * @param pro The product object to check as deleted
	 */
	public void setProductAsDeleted(CatalogProduct pro)
	{
		Packet packet = new Packet();
		packet.addCommand(Command.setProductAsDeleted);
		
		ArrayList<Object> product=new ArrayList<>();
		product.add(pro);
		packet.setParametersForCommand(Command.setProductAsDeleted, product);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState())
				{
					deleteProductFromLists((Product)pro);
					fillCatalogItems();
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
	 * Delete an instance product from all lists
	 * @param product to delete
	 */
	private void deleteProductFromLists(Product product)
	{
		for (
			    Iterator<Map.Entry<Product, CatalogProductDetails>> iter = catalogProductWithAdditionalDetails.entrySet().iterator();
			    iter.hasNext();
			) {
			    Map.Entry<Product, CatalogProductDetails> entry = iter.next();
			    if (product.getId() == entry.getKey().getId()) {
			        iter.remove();
			        break; // if only want to remove first match.
			    }
			}
	}
	
	/**
	 * Get from the server all the collections that uses for the controller and set to each attribute
	 */
	public void initializeCollections()
	{
		Packet packet = new Packet();
		packet.addCommand(Command.getCatalogProducts);
		packet.addCommand(Command.getFlowersInProducts);
		packet.addCommand(Command.getCatalogImage);
		packet.addCommand(Command.getFlowers);
		packet.addCommand(Command.getProductTypes);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			@Override
			public void onWaitingForResult() { }

			@Override
			public void onReceivingResult(Packet p)
			{
				if (p.getResultState())
				{
					ArrayList<FileSystem> catalogImagesList = p.<FileSystem>convertedResultListForCommand(Command.getCatalogImage);
					ArrayList<CatalogProduct> catalogProductsList = p.<CatalogProduct>convertedResultListForCommand(Command.getCatalogProducts);
					ArrayList<FlowerInProduct> flowersInProductList = p.<FlowerInProduct>convertedResultListForCommand(Command.getFlowersInProducts);
					flowersList = p.<Flower>convertedResultListForCommand(Command.getFlowers);
					productsTypeList = p.<ProductType>convertedResultListForCommand(Command.getProductTypes);
					
					catalogProductWithAdditionalDetails.clear();
					for (int i = 0; i < catalogProductsList.size(); i++)
					{
						CatalogProduct pro = catalogProductsList.get(i);
						FileSystem imgPro = catalogImagesList.get(i);
						
						// link product id to image instance
						imgPro.setProductId(pro.getId());
						
						// link flowers in product to product instance
						ArrayList<FlowerInProduct> currentflowersInProduct = new ArrayList<>();
						for (FlowerInProduct fp : flowersInProductList)
						{
							if (fp.getProductId() == pro.getId())
								currentflowersInProduct.add(fp);
						}
						pro.setFlowerInProductList(currentflowersInProduct);
						
						CatalogProductDetails cp = new CatalogProductDetails();
						cp.catalogImage = imgPro;
						catalogProductWithAdditionalDetails.put(pro, cp);
					}
					
					initFormByUses();
					setDiscountsForSelectedBranch();
					fillCatalogItems();
				}
				else
				{
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error:", p.getExceptionMessage());
				}
				
			}
		});
		send.start();
	}
	/**
	 * create window
	 * @param primaryStage - current stage to build
	 * @throws Exception error message
	 */
	public void start(Stage primaryStage) throws Exception {	
		
		mainStage = primaryStage;
		String srcFXML = "/Products/SelectProductApp.fxml";
		String srcCSS = "/Products/application.css";
		
		Parent root = FXMLLoader.load(getClass().getResource(srcFXML));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
		primaryStage.setTitle("Catalog Product");
		primaryStage.setScene(scene);		
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	            onClosingForm();
	          }
	         });
	}
	
	/**
	 * Delete Sale For specific catalog in branch in db and in the list
	 * @param catInBranch CatalogInBranch object to delete
	 * @param pro CatalogProduct object of deleted product
	 */
	public void deleteSaleForCatalogInBranch(CatalogInBranch catInBranch, CatalogProduct pro)
	{
		Packet packet = new Packet();
		packet.addCommand(Command.deleteSaleCatalogInBranch);
		
		ArrayList<Object> param = new ArrayList<>();
		param.add(catInBranch);
		
		packet.setParametersForCommand(Command.deleteSaleCatalogInBranch, param);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState())
				{
					int pId = pro.getId();
					getProductById(pId).catalogSale = null;
					fillCatalogItems();
					ConstantData.displayAlert(AlertType.INFORMATION, "Success", "Deleted Successfull", "The Sale for current product was deleted Successfully!");
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
	 * Get Catalog Product Details by Product Id
	 * @param productId product id
	 * @return catalog product details
	 */
	public CatalogProductDetails getProductById(int productId)
	{
		for (Map.Entry<Product, CatalogProductDetails> entry : catalogProductWithAdditionalDetails.entrySet())
		{
			if (entry.getKey().getId() == productId)
				return entry.getValue();
		}
		return null;
	}
	
	/**
	 * Get Catalog Product by Product Id
	 * @param productId product id
	 * @return Catalog product
	 */
	public CatalogProduct getCatalogProductById(int productId)
	{
		for (Map.Entry<Product, CatalogProductDetails> entry : catalogProductWithAdditionalDetails.entrySet())
		{
			if (entry.getKey().getId() == productId)
				return (CatalogProduct)entry.getKey();
		}
		return null;
	}
	
	/**
	 * Fill all catalog items to the customize listview dynamically
	 */
	public void fillCatalogItems()
	{
		data = FXCollections.observableArrayList(new ArrayList<>(catalogProductWithAdditionalDetails.keySet()));
		listViewCatalog.setCellFactory(new Callback<ListView<Product>, ListCell<Product>>() {
			
			@Override
			public ListCell<Product> call(ListView<Product> param) {
				return new ListCell<Product>() {
					
				private void setCellHandler(Product pro)
				{
					CatalogProductDetails proDetails = getProductById(pro.getId());
					
					if (proDetails == null)
						return;
					
					CatalogProduct catalogProduct = (CatalogProduct)pro;
					
					// define catalog image
					ImageView imgView = new ImageView();
					Image img;
					try
					{
						img = proDetails.catalogImage.getImageInstance();
					}
					catch (IOException e) 
					{ 
						img = proDetails.catalogImage.getImage();
					}
					catch (Exception e) 
					{ 
						img = new Image("blank.png");
					}
					
					imgView.setImage(img);
					imgView.setFitWidth(100);
					imgView.setFitHeight(100);
					//
					
					Text proName = new Text(catalogProduct.getName());
					proName.setFont(new Font(16));
					proName.setStyle("-fx-font-weight: bold");
					
					ProductType productType = getProductTypeByTypeId(pro.getProductTypeId());
					Text proType = new Text(productType.getDescription());
					proType.setFont(new Font(14));
					proType.setStyle("-fx-font-weight: bold");
					
					Text price = new Text(String.format("%.2f$", pro.getPrice()));
					price.setFont(new Font(14));
					
					VBox productImage = new VBox(imgView);
					productImage.setPadding(new Insets(0, 20, 0, 0));
					
					VBox productDetails;
					
					boolean hasSale = proDetails.catalogSale != null;
					
					// if there is a discount
					if (hasSale)
					{
						price.setStrikethrough(true);
						price.setFill(Color.RED);
						
						// add text for price after discount
						double finalPrice = getFinalPrice(catalogProduct);
						Text sale = new Text(String.format("%.2f$", finalPrice));
						sale.setFill(Color.GREEN);
						sale.setFont(new Font(14));
						sale.setStyle("-fx-font-weight: bold");
						
						Text saleDis = new Text(proDetails.catalogSale.getDiscount() + "%" + " FREE!");
						saleDis.setFont(new Font(13.5));
						saleDis.setFill(Color.RED);
						saleDis.setStyle("-fx-font-weight: bold");
						
						HBox priceFields = new HBox(sale, price);
						priceFields.setSpacing(10);
						priceFields.setPadding(new Insets(0, 10, 0, 0));
						
						ImageView saleImg = new ImageView();
						try
						{
							saleImg.setImage(new Image("/sale.png"));
							saleImg.setFitWidth(30);
							saleImg.setFitHeight(30);
						}
						catch (Exception e) { }
						
						HBox proNameBox = new HBox(proName, saleImg);
						proNameBox.setSpacing(7);
						
						productDetails = new VBox(proNameBox, proType, priceFields, saleDis);
					}
					else
					{
						price.setStyle("-fx-font-weight: bold");
						price.setFill(Color.GREEN);
						productDetails = new VBox(proName, proType, price);
					}
					
					
					Text flowersTitle = new Text("Flowers Collection:");
					flowersTitle.setUnderline(true);
					flowersTitle.setFont(new Font(14));
					flowersTitle.setStyle("-fx-font-weight: bold");
					VBox flowers = new VBox(flowersTitle);
				
					for (FlowerInProduct fp : pro.getFlowerInProductList())
					{
						Flower flowerFound = getFlowerByFlowerId(fp.getFlowerId());
						if (flowerFound != null)
						{
							Text flower = new Text(String.format("%s, Qty: %d", flowerFound.getName(), fp.getQuantity()));
							flower.setFont(new Font(13.5));
							flowers.getChildren().add(flower);
						}
					}
					
					flowers.setSpacing(2);
					
					VBox buttons = createButtonsVBox(hasSale, catalogProduct);
					
					productDetails.setSpacing(5);
					
				    Region region1 = new Region();
				    HBox.setHgrow(region1, Priority.ALWAYS);
				    
				    Region region2 = new Region();
				    HBox.setHgrow(region2, Priority.ALWAYS);
				        
				 	HBox hBox = new HBox(productImage, productDetails, region1, flowers ,region2 , buttons);
				 	hBox.setStyle("-fx-border-style: solid inside;"
				 	        + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
				 	        + "-fx-border-radius: 5;");
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(10));
                    setGraphic(hBox);
				}
				
			
				
				private VBox createButtonsVBox(boolean hasSale, CatalogProduct pro)
				{
					VBox buttons = new VBox();
					if (catalogUse == CatalogUse.updateCatalog)
					{
						Button modify = new Button("Modify");
						Image imageModify = new Image("pencil.png");
						ImageView viewModify = new ImageView(imageModify);
						viewModify.setFitWidth(20);
						viewModify.setFitHeight(20);
						modify.setGraphic(viewModify);
						modify.setPrefWidth(78);
						
						registerUpdateCatalog(modify, pro);
						
						Button remove = new Button("Delete");
						Image imageRemove = new Image("delete.png");
						ImageView viewRemove = new ImageView(imageRemove);
						viewRemove.setFitWidth(20);
						viewRemove.setFitHeight(20);
						remove.setGraphic(viewRemove);
						remove.setPrefWidth(78);
						
						registerDeleteProduct(remove, pro);
						
						buttons = new VBox(modify, remove);
					}
					else if (catalogUse == CatalogUse.updateSale)
					{
						Button modify;
						if (hasSale)
						{
							modify = new Button("Delete Sale");
							Image imageModify = new Image("delete.png");
							ImageView viewModify = new ImageView(imageModify);
							viewModify.setFitWidth(15);
							viewModify.setFitHeight(15);
							modify.setGraphic(viewModify);
							
							registerDeleteSaleButtonHandler(modify, pro);
						}
						else
						{
							modify = new Button("Add Sale");
							Image imageModify = new Image("add.png");
							ImageView viewModify = new ImageView(imageModify);
							viewModify.setFitWidth(15);
							viewModify.setFitHeight(15);
							modify.setGraphic(viewModify);
							
							registerAddSaleButtonHandler(modify, pro);
						}
						
						modify.setPrefWidth(95);
						buttons = new VBox(modify);
					}
					else if (catalogUse == CatalogUse.order || catalogUse == CatalogUse.viewingCatalog || catalogUse == CatalogUse.cart)
					{
						Button add = new Button("Add");
					
						if (productsSelected.contains(pro)) // new, add to collection
						{
							setDelOrderStyles(add);
						}
						else // exists, remove from collection
						{
							setAddOrderStyles(add);
						}
						
						if (currentBranch == null || !CustomerMenuController.hasAccountForCurrentBranch || productsSelected.contains(pro))
						{
							add.setDisable(true);
						}
						else
						{
							add.setDisable(false);
						}
					        
						registerAddOrderButtonHandler(add, pro);
						
						buttons = new VBox(add);
					}
					buttons.setSpacing(10);
					
					return buttons;
				}
				
				private void setAddOrderStyles(Button add)
				{
					add.setText("Add");
					Image imageModify = new Image("add.png");
					ImageView viewModify = new ImageView(imageModify);
					viewModify.setFitWidth(15);
					viewModify.setFitHeight(15);
					add.setGraphic(viewModify);
					add.setPrefWidth(60);
					
					setStyle("-fx-background-color: white;");
				}
				
				private void setDelOrderStyles(Button del)
				{
					del.setText("Added");
					setStyle("-fx-background-color: moccasin;");
				}
				
				public void registerAddOrderButtonHandler(Button button, CatalogProduct pro)
				{
					button.setOnMouseClicked((event) -> {
						
						if (!productsSelected.contains(pro)) // new, add to collection
						{
							productsSelected.add(pro);
							
							setDelOrderStyles(button);
						}
						else // exists, remove from collection
						{
							
							setAddOrderStyles(button);
						}
						
						updateTotalPriceAndAddToCartButton();
					
					});
				}
				
				public void registerDeleteSaleButtonHandler(Button button, CatalogProduct pro)
				{
				
					button.setOnMouseClicked((event) -> {
						try
						{
							CatalogInBranch catInBranch = getProductById(pro.getId()).catalogSale;
							
							deleteSaleForCatalogInBranch(catInBranch, pro);
						}
						catch (Exception e) 
						{
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Discount Window", e.getMessage());
						}
					});
				}
				
				public void registerAddSaleButtonHandler(Button button, CatalogProduct pro)
				{
				
					button.setOnMouseClicked((event) -> {
						try
						{
							CatalogDiscountController disController = new CatalogDiscountController();		
							disController.setCatalogDiscount(controllerInstance, pro);
							disController.start(new Stage());
						}
						catch (Exception e) 
						{
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Discount Window", e.getMessage());
						}
					});
				}
				
				public void registerUpdateCatalog(Button button, CatalogProduct pro)
				{
					button.setOnMouseClicked((event) -> {
						
						try
						{
						Stage stage = (Stage)button.getScene().getWindow();
						CatalogProductController catalogProductController = new CatalogProductController();
						CatalogProductDetails proDetails = getProductById(pro.getId());
						catalogProductController.setCatalogProductForUpdating(pro, proDetails.catalogImage, controllerInstance);
						catalogProductController.start(new Stage());
						stage.close();
						}
						catch (Exception e) 
						{
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Catalog Window", e.getMessage());
						}
					});
				}

				public void registerDeleteProduct(Button button, CatalogProduct pro)
				{
					button.setOnMouseClicked((event) -> {
						setProductAsDeleted(pro);
					});
				}
				
				
			    @Override
				protected void updateItem(Product item, boolean empty) {
					//super.updateItem(item, empty);
						
					 if (item != null) {	
						 	setCellHandler(item);
                        }
				}};
			}
		});
		listViewCatalog.setItems(data);
		
		
		listViewCatalog.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
            	//listViewCatalog.getSelectionModel().select(-1);
                event.consume();
            }
        });
	}
	


	/**
	 * Calculate the final price, even after the discount if have
	 * @param pro to get price
	 * @return get final price 
	 */
	private double getFinalPrice(CatalogProduct pro)
	{
		CatalogProductDetails productDetails = catalogProductWithAdditionalDetails.get(pro);
		if (productDetails.catalogSale == null)
			return pro.getPrice();
		
		int discount = productDetails.catalogSale.getDiscount();
		double percantage = (double)discount / 100.0;
		
		double priceAfterDiscount = pro.getPrice() * (1 - percantage);
		return priceAfterDiscount;
	}
	
	/**
	 * Update the label of total price and the disabling state of cart button
	 */
	private void updateTotalPriceAndAddToCartButton()
	{
		if (productsSelected.size() == 0)
		{
			if (CustomerMenuController.hasAccountForCurrentBranch)
				lblBranch.setVisible(false);
			
			btnAddCatalogProduct.setDisable(true);
		}
		else
		{
			double totalPrice = 0.0;
			for (Product product : productsSelected)
			{
				totalPrice += getFinalPrice((CatalogProduct)product);
			}
			
			lblBranch.setVisible(true);
			lblBranch.setText(String.format("Total Products: %d, Total Price: %.2f$", productsSelected.size(), totalPrice));
			btnAddCatalogProduct.setDisable(false);
		}
	}
	
	/**
	 * Get instance of flower from the collection by flower id
	 * @param flowerId The flower Id to search for
	 * @return flowers
	 */
	private Flower getFlowerByFlowerId(int flowerId)
	{
		for (Flower flower : flowersList)
		{
			if (flower.getId() == flowerId)
				return flower;
		}
		return null;
	}

	/**
	 * Clear the sales for each catalog product in the collection
	 */
	public void clearCatalogInBranchInstances()
	{
		for (Map.Entry<Product, CatalogProductDetails> entry : catalogProductWithAdditionalDetails.entrySet())
		{
			entry.getValue().catalogSale = null;
		}
		
		productsSelected.clear();
		CartController.cartProducts.clear();
	}
	
	/**
	 * Event that occurs when closing the form
	 */
	public void onClosingForm()
	{
		try
		{
			mainStage.close();
			if (catalogUse == CatalogUse.updateCatalog || catalogUse == CatalogUse.updateSale)
			{
				ManagersMenuController menu = new ManagersMenuController();
				menu.start(new Stage());
			}
			else
			{
				CustomerMenuController menu = new CustomerMenuController();
				menu.start(new Stage());
			}
		}
		catch (Exception e) 
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
		}
	}
	
	/**
	 * Add an event to the Add Catalog Button
	 */
	public void registerAddCatalogButtonHandle()
	{
		btnAddCatalogProduct.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	try 
	        		{
	            		Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
	            		primaryStage.hide(); //hiding primary window

	            		CatalogProductController catalogProductController = new CatalogProductController();
	            		catalogProductController.setCatalogProductForInserting(controllerInstance);
	            		CatalogProductController.comesFromCatalog = true;
	        			catalogProductController.start(primaryStage);
	        		}
	        		catch (Exception e) 
	        		{
	        			ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Catalog Window", e.getMessage());
	        		}
	            }
	        });
	}
	
	/**
	 * Add an event to the Add Cart Button
	 */
	public void registerAddToCartButtonHandle()
	{
		btnAddCatalogProduct.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	try 
	        		{
	        			((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
	        			Stage primaryStage = new Stage();

	        			// call to controller of order to open order window
	        			CartController cartController = new CartController();
	        			cartController.setComesFromCatalog(true);
	        			//cartController.addProductsToCartMap(productsSelected);
	        			cartController.start(primaryStage);
	        		}
	        		catch (Exception e) 
	        		{
	        			ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add To Cart Window", e.getMessage());
	        		}
	            }
	        });
	}
	
	/**
	 * Initialize the form by the source that call to this controller
	 */
	public void initFormByUses()
	{
		if (catalogUse == CatalogUse.viewingCatalog || catalogUse == CatalogUse.order || catalogUse == CatalogUse.cart)
		{
			updateTotalPriceAndAddToCartButton();
			btnAddCatalogProduct.setText("Go To Cart");
			
			Image image = new Image("cart.png");
			ImageView view = new ImageView(image);
			view.setFitWidth(30);
			view.setFitHeight(30);
			btnAddCatalogProduct.setGraphic(view);
			btnAddCatalogProduct.setPrefWidth(115);
			
			registerAddToCartButtonHandle();
			
			if (CustomerMenuController.currentAcc != null && CustomerMenuController.currentAcc.getAccountStatus() == AccountStatus.Blocked)
			{
				lblBranch.setText("Your Account has been BLOCKED! Contact with Branch Manager");
			}
			else if (CustomerMenuController.currentBranch == null)
				lblBranch.setText("Select Branch for Viewing Discounts Sales and for Buying");
			
			else if (!CustomerMenuController.hasAccountForCurrentBranch)
				lblBranch.setText("You don't have an Account! Contact with Branch Manager for Open");
	
		}
		else if (catalogUse == CatalogUse.updateSale)
		{
			btnAddCatalogProduct.setVisible(false);
			sep.setVisible(false);
		
			lblBranch.setVisible(false);
		}
		else if (catalogUse == CatalogUse.updateCatalog)
		{
			btnAddCatalogProduct.setVisible(true);
			btnAddCatalogProduct.setText("Add To Catalog");
			btnAddCatalogProduct.setPrefWidth(120);
			Image image = new Image("add.png");
			ImageView view = new ImageView(image);
			view.setFitWidth(15);
			view.setFitHeight(15);
			btnAddCatalogProduct.setGraphic(view);
			
			
			sep.setVisible(true);
			lblBranch.setVisible(false);
			
			lblBranch.setVisible(false);
			
			registerAddCatalogButtonHandle();
		}
	}
	/**
	 * setup controls and fetch data 
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		lblTitle.setVisible(true);
		lblTitle.setText(title);
		initializeCollections();
		controllerInstance = this;
		
		if (LoginController.userLogged instanceof Employee)
		{
			currentBranch = ManagersMenuController.currentBranch;
		}
		else
		{
			currentBranch = CustomerMenuController.currentBranch;
		}
	}

	/**
	 * This Class uses for store details about Catalog Product,
	 * such a image and sale
	 */
	public class CatalogProductDetails
	{
		public FileSystem catalogImage;
		public CatalogInBranch catalogSale;
	}
}
