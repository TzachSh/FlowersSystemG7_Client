package Products;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import Products.SelectProductController.CatalogProductDetails;
import Products.SelectProductController.CatalogUse;
import Users.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
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
import javafx.util.Callback;

public class CartController implements Initializable
{
	  @FXML
	  private MenuItem btnAddFromCatalog;

	  @FXML
	  private Button btnPurchase;

	  @FXML
	  private Label lblPrice;

	  @FXML
      private ListView<Product> lstCart;

	  @FXML
	  private MenuItem btnAddCustomProduct;
	
	   /** Map for save all both catalog products and custom product and their quantity for the cart  */
	  public static LinkedHashMap<Product, Integer> cartProducts = new LinkedHashMap<>();
	
	  private ObservableList<Product> data;
	
	  public static int branchId  = -1;
	  
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
	   * Add Collection of products to the map with default quantity as 1
	   * @param productList The product list
	   */
	  public void addProductsToCartMap(ArrayList<Product> productList)
	  {
		  for (Product pro : productList)
		  {
			  if (!cartProducts.containsKey(pro))
				  cartProducts.put(pro, 1);
		  }
	  }
	  
	  	/**
		 * Fill all items to the customize listview dynamically
		 */
		public void fillCatalogItems()
		{
			data = FXCollections.observableArrayList(new ArrayList<>(cartProducts.keySet()));
			lstCart.setCellFactory(new Callback<ListView<Product>, ListCell<Product>>() {
				
				@Override
				public ListCell<Product> call(ListView<Product> param) {
					return new ListCell<Product>() {
						
					private void setCellHandler(Product pro)
					{
						int count = cartProducts.get(pro);
						
						// define catalog image or custom image
						ImageView imgView = new ImageView();
						Image img;
						
						// name of product
						Text proName = new Text();
						proName.setFont(new Font(16));
						proName.setStyle("-fx-font-weight: bold");
						
						// price of product
						Text price = new Text(String.format("%.2f¤", (pro.getPrice() * count)));
						price.setFont(new Font(14));
						
						VBox productDetails = null;
						
						if (pro instanceof CatalogProduct)
						{
							CatalogProductDetails proDetails = SelectProductController.catalogProductWithAdditionalDetails.get(pro);
							try
							{
								img = proDetails.catalogImage.getImageInstance();
							}
							catch (IOException e) 
							{ 
								img = proDetails.catalogImage.getImage();
							}
							
							proName.setText(((CatalogProduct)pro).getName());
							
							boolean hasSale = proDetails.catalogSale != null;
							
							// if there is a discount
							if (hasSale)
							{
								price.setStrikethrough(true);
								price.setFill(Color.RED);
								
								// add text for price after discount
								double finalPrice = getFinalPrice(pro) * count;
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
								
								productDetails = new VBox(proNameBox, priceFields, saleDis);
							}
						}
						else
						{
							img = new Image("customProduct");
						}
						
						imgView.setImage(img);
						imgView.setFitWidth(70);
						imgView.setFitHeight(70);
					
						//
						price.setStyle("-fx-font-weight: bold");
						price.setFill(Color.GREEN);
						
						if (productDetails == null)
						{
							productDetails = new VBox(proName, price);
							productDetails.setSpacing(5);
						}
						
						VBox productImage = new VBox(imgView);
						productImage.setPadding(new Insets(0, 20, 0, 0));
						

					    Region region1 = new Region();
					    HBox.setHgrow(region1, Priority.ALWAYS);
					    
					  
					    Button incButton = new Button();
					    Image imageInc = new Image("addQty.png");
					    ImageView viewInc = new ImageView(imageInc);
						viewInc.setFitWidth(10);
						viewInc.setFitHeight(10);
						incButton.setGraphic(viewInc);
						incButton.setPrefWidth(12);
					
						Text qty = new Text("" + cartProducts.get(pro));
						qty.setFont(new Font(13.5));
						qty.setFill(Color.RED);
						qty.setTextAlignment(TextAlignment.CENTER);
						
						Button decButton = new Button();
						Image imageDec = new Image("minusQty.png");
				        ImageView viewDec = new ImageView(imageDec);
						viewDec.setFitWidth(10);
						viewDec.setFitHeight(10);
						decButton.setGraphic(viewDec);
						decButton.setPrefWidth(12);
					    
						decButton.setOnMouseClicked((event) -> {
							if (cartProducts.get(pro) > 1)
							{
								cartProducts.put(pro, cartProducts.get(pro) - 1);
								qty.setText("" + cartProducts.get(pro));
								updateTotalPrice();
							}
						});
						
						incButton.setOnMouseClicked((event) -> {
							cartProducts.put(pro, cartProducts.get(pro) + 1);
							qty.setText("" + cartProducts.get(pro));
							updateTotalPrice();
						});
					
						HBox qtyOptions = new HBox(decButton, qty, incButton);
						qtyOptions.setSpacing(8);
						qtyOptions.setAlignment(Pos.CENTER);

						Button delButton = new Button();
						Image imageDel = new Image("delete.png");
				        ImageView viewDel = new ImageView(imageDel);
				        viewDel.setFitWidth(20);
						viewDel.setFitHeight(20);
						delButton.setGraphic(viewDel);
						delButton.setPrefWidth(20);
						
						delButton.setOnMouseClicked((event) -> {
							cartProducts.remove(pro);
							SelectProductController.productsSelected.remove(pro);
							fillCatalogItems();
							updateTotalPrice();
						});
						
						VBox delBox = new VBox(delButton);
						delBox.setAlignment(Pos.CENTER);
						
					 	HBox hBox = new HBox(delBox, productImage, productDetails ,region1, qtyOptions);
					 	hBox.setStyle("-fx-border-style: solid inside;"
					 	        + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
					 	        + "-fx-border-radius: 5;");
	                    hBox.setSpacing(10);
	                    hBox.setPadding(new Insets(10));
	                    
	                    
	                    
	                    setGraphic(hBox);
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
			lstCart.setItems(data);
			
			
			lstCart.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

	            @Override
	            public void handle(MouseEvent event) {
	            	//listViewCatalog.getSelectionModel().select(-1);
	                event.consume();
	            }
	        });
		}
		
		private double getFinalPrice(Product pro)
		{
			CatalogProductDetails productDetails = SelectProductController.catalogProductWithAdditionalDetails.get(pro);
			if (productDetails.catalogSale == null)
				return pro.getPrice();
			
			int discount = productDetails.catalogSale.getDiscount();
			double percantage = (double)discount / 100.0;
			
			double priceAfterDiscount = pro.getPrice() * (1 - percantage);
			return priceAfterDiscount;
		}
		
		private void updateTotalPrice()
		{
			double totalPrice = 0.0;
			int totalItems = 0;
			for (Map.Entry<Product, Integer> entry : cartProducts.entrySet())
			{
				totalPrice += getFinalPrice((CatalogProduct)entry.getKey()) * entry.getValue();
				totalItems += entry.getValue();
			}
			
			lblPrice.setText(String.format("Total Price: %.2f¤ , Total Items: %d", totalPrice, totalItems));
		}
		
		public void registerAddFromCatalogButton()
		{
			btnAddFromCatalog.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	try 
	        		{
	        			lstCart.getScene().getWindow().hide(); //hiding primary window
	        			Stage primaryStage = new Stage();
	        			SelectProductController catalogProductController = new SelectProductController();
	        			catalogProductController.setForCart(SelectProductUI.customer);
	        			catalogProductController.start(primaryStage);
	        		}
	        		catch (Exception e) 
	        		{
	        			displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add Catalog Window", e.getMessage());
	        		}
	            }
	        });
		}
	
	  public void start(Stage primaryStage) throws Exception {	
			
			String srcFXML = "/Products/CartUI.fxml";
			String srcCSS = "/Products/application.css";
			
			Parent root = FXMLLoader.load(getClass().getResource(srcFXML));
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			primaryStage.setTitle("Cart Shopping");
			primaryStage.setScene(scene);		
			primaryStage.show();
		}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		registerAddFromCatalogButton();
		fillCatalogItems();
		updateTotalPrice();
	}

}
