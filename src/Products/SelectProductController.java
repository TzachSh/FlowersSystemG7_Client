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
import Customers.Account;
import Customers.AccountStatus;
import Customers.Customer;
import PacketSender.Command;
import PacketSender.FileSystem;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Users.User;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
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
import javafx.util.Callback;

public class SelectProductController implements Initializable
{
    @FXML
    private Separator sep;
    
    @FXML
    private Hyperlink linkChangeBranch;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnAddCatalogProduct;

    @FXML
    private ComboBox<String> cmbBranch;

    @FXML
    private Label lblTitle;

    @FXML
    private ListView<Product> listViewCatalog;

    @FXML
    private Label lblBranch;

    @FXML
    private Label lblBranchSelect;
    
    @FXML
    private Label lblCurrentBranch;

    /** enum for indicate the window that call to this controller  */
    public enum CatalogUse { updateSale, order, updateCatalog, viewingCatalog, cart }
    
    /**  linkedHashMap for easy mapping from catalogProduct to it's details such sales and image */
	public static LinkedHashMap<Product, CatalogProductDetails> catalogProductWithAdditionalDetails = new LinkedHashMap<>();

	/** the selection products in the listview, used for order operation */
	public static ArrayList<Product> productsSelected = new ArrayList<>();

	public static ArrayList<Account> userAccountsList = new ArrayList<>();
	public static ArrayList<Branch> branchList = new ArrayList<>();
	public static ArrayList<Flower> flowersList = new ArrayList<>();
	public static ArrayList<ProductType> productsTypeList = new ArrayList<>();
	public static CatalogUse catalogUse;
	private static String title;
	private static int branchId;
	private static User userLogged;
	private ObservableList<Product> data;
	private boolean hasAccountForCurrentBranch = false;
	public static Stage mainStage;
	private static SelectProductController controllerInstance;
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
	 * Set the controller to initialize for updating sale
	 * @param user Branch Employee object
	 */
	public void setForUpdateSale(User user)
	{
		userLogged = user;
		catalogUse = CatalogUse.updateSale;
		title = "Updating Sales";
		branchId = ((Employee)user).getBranchId();
	}
	
	/**
	 *  Set the controller to initialize for ordering
	 */
	public void setForOrder(User user)
	{
		userLogged = user;
		catalogUse = CatalogUse.order;
		title = "Add To Cart";
	}
	
	/**
	 *  Set the controller to initialize for ordering from cart
	 */
	public void setForCart(User user)
	{
		userLogged = user;
		catalogUse = CatalogUse.cart;
		title = "Add To Cart";	
	}
	
	/**
	 * Set the controller to initialize for viewing in catalog and also order
	 */
	public void setForViewingCatalog(User user)
	{
		userLogged = user;
		catalogUse = CatalogUse.viewingCatalog;
		title = "Catalog Products Viewer";
	}
	
	/**
	 * Set the controller to initialize for updating Catalog
	 */
	public void setForUpdateCatalog(User user)
	{
		userLogged = user;
		catalogUse = CatalogUse.updateCatalog;
		title = "Updating Catalog Products";
	}
	
	/**
	 * Initialize the ComboBox of Branches with List of branches
	 * @param branchesList the collection of branches to insert to Branch ComboBox
	 */
	public void setComboBoxBrancesList(ArrayList<Branch> branchesList)
	{
		ArrayList<String> branches = new ArrayList<>();
		
		for (Branch br : branchesList)
			branches.add(br.toString());
		
		ObservableList<String> observeBranchesList = FXCollections.observableArrayList(branches);
		
		cmbBranch.setItems(observeBranchesList);
		
		cmbBranch.getSelectionModel().select(CartController.branchId);
	}
	
	/**
	 * Send to the server request to get the discount of the selected branch
	 * @param index The index in the collection of branches or in the comboBox
	 */
	public void setDiscountsForSelectedBranch(int index)
	{
		if (index == -1)
			return;
		
		int branchId = branchList.get(index).getbId();
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
	 * Get the instance of account by branch Id
	 * @param branchId The branch Id for searching account
	 */
	private Account getAccountByBranchId(int branchId)
	{
		for (Account account : userAccountsList)
		{
			if (account.getBranchId() == branchId)
				return account;
		}
		return null;
	}
	
	/**
	 * Get the instance of Product Type by type id
	 * @param typeId The Type id for searching type
	 */
	private ProductType getProductTypeByTypeId(int typeId)
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
					displayAlert(AlertType.ERROR, "Error", "Exception Error:", p.getExceptionMessage());
				}
			}

			@Override
			public void onWaitingForResult() { }
		});
		send.start();
		
	}
	
	/**
	 * Delete an instance product from all lists
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
		packet.addCommand(Command.getBranches);
		packet.addCommand(Command.getFlowers);
		packet.addCommand(Command.getProductTypes);
		
		if (userLogged instanceof Customer)
		{
			packet.addCommand(Command.getAccountbycID);
			int cid = ((Customer)userLogged).getId();
			
			ArrayList<Object> accl=new ArrayList<>();
			accl.add(cid);
			packet.setParametersForCommand(Command.getAccountbycID, accl);
		}
		
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
					branchList = p.<Branch>convertedResultListForCommand(Command.getBranches);
					flowersList = p.<Flower>convertedResultListForCommand(Command.getFlowers);
					productsTypeList = p.<ProductType>convertedResultListForCommand(Command.getProductTypes);
					if (userLogged instanceof Customer)
					{
						userAccountsList = p.<Account>convertedResultListForCommand(Command.getAccountbycID);
					}
					
					
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
					
					setComboBoxBrancesList(branchList);
					initFormByUses();
					fillCatalogItems();
				}
				else
				{
					displayAlert(AlertType.ERROR, "Error", "Exception Error:", p.getExceptionMessage());
				}
				
			}
		});
		send.start();
	}
	
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
	}
	
	/**
	 * return the index in the collection of branches that equals to branchId
	 * @param branchId The branch id to search for
	 * @return -1 if not found, else if found
	 */
	private int getIndexByBranchId(int branchId)
	{
		for (int i = 0; i < branchList.size(); i++)
		{
			if (branchList.get(i).getbId() == branchId)
				return i;
		}
		return -1;
	}
	
	/**
	 * Event that occurs when choosing an item on the Branch comboBox
	 */
	public void onClickingBranchComboBox()
	{
		linkChangeBranch.setVisible(true);
		linkChangeBranch.setVisited(false);
		
		cmbBranch.setDisable(true);
		int index = cmbBranch.getSelectionModel().getSelectedIndex();
		if (index != -1)
		{
			lblBranch.setVisible(false);
			lblCurrentBranch.setVisible(true);
			
			Branch branch = branchList.get(index);
			lblCurrentBranch.setText("Current Branch: " + branch);
			lblCurrentBranch.setTextFill(Color.BLACK);
			setDiscountsForSelectedBranch(index);
			
			// alert if the user has no account for this branch
			// if there is no account, disable the option for select products, or add to cart
			Account account = getAccountByBranchId(branch.getbId());
			if (account != null)
				hasAccountForCurrentBranch = true;
			else if (catalogUse != CatalogUse.updateSale)
			{
				hasAccountForCurrentBranch = false;
				displayAlert(AlertType.WARNING, "Warning!", "No Account!", "You don't have account for selected branch, Please contact with Branch Manager for open a new one");
			}

			
			updateTotalPriceAndAddToCartButton();
		}
		else
		{
			lblBranch.setVisible(true);
			lblCurrentBranch.setVisible(false);
			lblBranch.setText("Select Branch for Viewing Discounts Sales and for Buying");
			lblBranch.setTextFill(Color.RED);
		}
	}
	
	/**
	 * Get Catalog Product Details by Product Id
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
					
					Text price = new Text(String.format("%.2f¤", pro.getPrice()));
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
						Text sale = new Text(String.format("%.2f¤", finalPrice));
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
						}
						else
						{
							modify = new Button("Add Sale");
							Image imageModify = new Image("add.png");
							ImageView viewModify = new ImageView(imageModify);
							viewModify.setFitWidth(15);
							viewModify.setFitHeight(15);
							modify.setGraphic(viewModify);
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
						
						if (cmbBranch.getSelectionModel().getSelectedIndex() == -1 || !hasAccountForCurrentBranch || productsSelected.contains(pro))
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
					/*
					Image imageModify = new Image("x-button.png");
					ImageView viewModify = new ImageView(imageModify);
					viewModify.setFitWidth(15);
					viewModify.setFitHeight(15);
					del.setGraphic(viewModify);
					del.setPrefWidth(60);
					*/
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
							//productsSelected.remove(pro);
							
							setAddOrderStyles(button);
						}
						
						updateTotalPriceAndAddToCartButton();
					
					});
				}
				
				public void registerUpdateCatalog(Button button, CatalogProduct pro)
				{
					button.setOnMouseClicked((event) -> {
						
						try
						{
						Stage stage = (Stage)button.getScene().getWindow();
				    	stage.hide();
						CatalogProductController catalogProductController = new CatalogProductController();
						CatalogProductDetails proDetails = getProductById(pro.getId());
						catalogProductController.setCatalogProductForUpdating(pro, proDetails.catalogImage, controllerInstance);
						catalogProductController.start(new Stage());
						}
						catch (Exception e) 
						{
							displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Catalog Window", e.getMessage());
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
			lblBranch.setText(String.format("Total Products: %d, Total Price: %.2f¤", productsSelected.size(), totalPrice));
			btnAddCatalogProduct.setDisable(false);
		}
	}
	
	/**
	 * Get instance of flower from the collection by flower id
	 * @param flowerId The flower Id to search for
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
	 * Event that occurs when clicking on the link for changing the current branch
	 */
	public void onClickingChangeBranchLink()
	{
		if (productsSelected.size() > 0) // user is already select product from some branch
		{
			// alert warning and ask for yes or no, if press yes he will lost all current selected
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Selecting Another Branch");
			alert.setContentText("This Operation will Cause to your Cart and Choices to be erased! Are you Sure?");
			ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
			ButtonType noButton = new ButtonType("No", ButtonData.NO);
			
			alert.getButtonTypes().setAll(okButton, noButton);
			alert.showAndWait().ifPresent(type -> {
			        if (type == okButton)
			        {
			        	clearBranchSelection();
			        } 
			});
		}
		else
		{
			clearBranchSelection();
		}
	}
	
	/**
	 * Clear the combobox of the current branch, and set status for empty branch
	 */
	private void clearBranchSelection()
	{
		productsSelected.clear();
    	
    	cmbBranch.getSelectionModel().select(-1);
		lblBranch.setVisible(true);
		cmbBranch.setDisable(false);
		clearCatalogInBranchInstances();
		lblBranch.setText("Select Branch for Viewing Discounts Sales and for Buying");
		linkChangeBranch.setVisible(false);
		fillCatalogItems();
		
		btnAddCatalogProduct.setDisable(true);
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
		
		branchId = -1;
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
	        			catalogProductController.comesFromCatalog = true;
	        			catalogProductController.start(primaryStage);
	        		}
	        		catch (Exception e) 
	        		{
	        			displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Catalog Window", e.getMessage());
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
	        			CartController.branchId = cmbBranch.getSelectionModel().getSelectedIndex();
	        			cartController.addProductsToCartMap(productsSelected);
	        			cartController.start(primaryStage);
	        		}
	        		catch (Exception e) 
	        		{
	        			displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Catalog Window", e.getMessage());
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
			
			//linkChangeBranch.setVisible(false);
			registerAddToCartButtonHandle();
		}
		else if (catalogUse == CatalogUse.updateSale)
		{
			btnAddCatalogProduct.setVisible(false);
			sep.setVisible(false);
			// select the branch of employee manager on the combobox
			int index = getIndexByBranchId(branchId);
			cmbBranch.getSelectionModel().select(index);
			cmbBranch.setDisable(true);
			linkChangeBranch.setVisible(false);
			lblBranch.setVisible(false);
			//lblBranch.setText("Current Branch: " + branchList.get(index));
			//lblBranch.setTextFill(Color.BLACK);
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
			lblBranchSelect.setVisible(false);
			cmbBranch.setVisible(false);
			lblBranch.setVisible(false);
			linkChangeBranch.setVisible(false);
			registerAddCatalogButtonHandle();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		lblTitle.setVisible(true);
		lblTitle.setText(title);
		initializeCollections();
		controllerInstance = this;
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
