package Products;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import PacketSender.Command;
import PacketSender.FileSystem;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
/**
 * Controller
 * Manage catalog products
 */
public class CatalogProductController implements Initializable {
	// Views
	@FXML
	private ComboBox<String> cmbType;
	@FXML
	private TextField txtName;
	@FXML
	private Label errFlower;
	@FXML
	private ImageView imgProduct;
	@FXML
	private Label errNameTooMuchLong;
	@FXML
	private Button btnNext2;
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
	private ComboBox<String> cmbFlower;
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
	private TextField txtPrice;
	@FXML
	private ListView<FlowerInProduct> lstFlower;
	@FXML
	private TabPane tabPane;
	/**
	 * dynamic list with flowers in product
	 */
	private ObservableList<FlowerInProduct> data;
	/**
	 * title for the GUI
	 */
	private static String title;
	/**
	 * 
	 */
	private static boolean updateForm;
	/**
	 * instance of catalog product
	 */
	private static CatalogProduct catalogProduct;
	/**
	 * image of the product
	 */
	private static FileSystem imageProduct;
	/**
	 *  flower in products
	 */
	private static ArrayList<FlowerInProduct> flowersInTheProduct = new ArrayList<>();
	/** set all active and not active catalog products from db */
	private static ArrayList<CatalogProduct> catalogProductsList = new ArrayList<>();
	/**
	 *  all flowers details
	 */
	private ArrayList<Flower> flowersList = new ArrayList<>();
	private static Stage primaryStage;
	/**
	 * if comes from catalog
	 */
	public static boolean comesFromCatalog = false;
	
	private static SelectProductController selectController;

	/**
	 * Prepare the Form for updating an exists product
	 * @param product  The instance of the product to update
	 * @param productImage product image to load
	 * @param selectProController instance of SelectProductController
	 */
	public void setCatalogProductForUpdating(CatalogProduct product, FileSystem productImage,
			SelectProductController selectProController) {
		selectController = selectProController;
		if (product != null && productImage != null) // uses for update product
		{
			catalogProduct = product;
			imageProduct = productImage;
			flowersInTheProduct = catalogProduct.getFlowerInProductList();
			updateForm = true;
			title = "Update Catalog Product";
			imageProduct.setProductId(catalogProduct.getId()); // link image product id
		} else // set for insert
		{
			setCatalogProductForInserting(selectProController);
		}
	}

	/**
	 * Prepare the Form for inserting a new product
	 * @param selectProController instance of SelectProductController
	 */
	public void setCatalogProductForInserting(SelectProductController selectProController) {
		selectController = selectProController;
		catalogProduct = new CatalogProduct();
		imageProduct = new FileSystem();
		flowersInTheProduct = new ArrayList<>();
		updateForm = false;
		title = "Insert New Catalog Product";
	}

	/**
	 * Set Product Details for product instance
	 * 
	 * @param name
	 *            The name of the product
	 * @param price
	 *            The price of the product
	 * @param index
	 *            the index of the product type on the collection
	 */
	public void updateProductDetails(String name, double price, int index) {
		// overwrite the new fields for CatalogProduct object
		int typeId = ConstantData.productTypeList.get(index).getId();
		catalogProduct.setName(name);
		catalogProduct.setPrice(price);
		catalogProduct.setProductTypeId(typeId);
	}
	
	/**
	 * Update flower list
	 * @param flowersList list of flowers
	 */
	public void setFlowers(ArrayList<Flower> flowersList) {
		this.flowersList = flowersList;
	}

	/**
	 * Check if there is in the collection of products the specific name
	 * 
	 * @param productName
	 *            The name if catalog product to check if it already exists
	 * @return if already exist
	 */
	public boolean productNameIsAlreadyExists(String productName) {
		for (CatalogProduct product : catalogProductsList) {
			if (updateForm) {
				if (product.getId() != catalogProduct.getId()
						&& product.getName().toLowerCase().equals(productName.toLowerCase()))
					return true;
			} else {
				if (product.getName().toLowerCase().equals(productName.toLowerCase()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Send a request to the server for getting all the Collections for product
	 * Types and Flowers
	 */
	public void pushAllTypesAndFlowers() {
		Packet packet = new Packet();
		packet.addCommand(Command.getFlowers);
		packet.addCommand(Command.getAllCatalogProducts);

		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			@Override
			public void onWaitingForResult() {
			}

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState()) {
					setFlowers(p.<Flower>convertedResultListForCommand(Command.getFlowers));
					catalogProductsList = p
							.<CatalogProduct>convertedResultListForCommand(Command.getAllCatalogProducts);
					setComboBoxTypesList(ConstantData.productTypeList);
					setComboBoxFlowersList(flowersList);

					/* load all exists product info */
					loadExistsProductDetails();

					txtName.requestFocus();
				} else {
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error:", p.getExceptionMessage());
				}
			}
		});
		send.start();
	}


	/**
	 * Initialize the ComboBox of Types with List of Types
	 * 
	 * @param productTypesList
	 *            the collection of Types to insert to Types ComboBox
	 */
	public void setComboBoxTypesList(ArrayList<ProductType> productTypesList) {
		ArrayList<String> types = new ArrayList<>();

		for (ProductType p : productTypesList)
			types.add(p.toString());

		ObservableList<String> observeTypesList = FXCollections.observableArrayList(types);
		cmbType.setItems(observeTypesList);
	}

	/**
	 * This method check validation of flowers relating to this product If there is
	 * no found any flowers to the product, show error Label
	 */
	public void flowersDetailsValidation() {
		// there is no flowers registered to this product
		if (flowersInTheProduct == null || flowersInTheProduct.size() == 0) {
			errFlower.setVisible(true);
			btnNext2.setDisable(true);
		} else // there is one or more flowers
		{
			errFlower.setVisible(false);
			btnNext2.setDisable(false);
		}
	}

	/**
	 * This method check validation of Product Details pane
	 * 
	 * @param name
	 *            The name of the product
	 * @param price
	 *            The price of the product
	 * @param selectedTypeIndex
	 *            The type of the product
	 */
	public void productDetailsValidation(String name, String price, int selectedTypeIndex) {
		boolean valid = true;

		if (name.isEmpty()) {
			errName.setVisible(true);
			valid = false;
		} else {
			errName.setVisible(false);
		}

		if (price.isEmpty()) {
			errPrice.setVisible(true);
			valid = false;
			errPriceNum.setVisible(false);
		} else {
			errPrice.setVisible(false);

			// check if number is valid
			try {
				double priceConverted = Double.valueOf(price);
				if (priceConverted <= 0)
					throw new NumberFormatException();
				
				if (priceConverted > 5000)
					throw new NumberFormatException("max");
				
				errPriceNum.setVisible(false);
			} catch (NumberFormatException e) {
				if (e.getMessage().equals("max"))
					errPriceNum.setText("Max: 5000.0$");
				
				errPriceNum.setVisible(true);
				valid = false;
			}
		}

		if (selectedTypeIndex == -1) {
			errType.setVisible(true);
			valid = false;
		} else {
			errType.setVisible(false);
		}

		if (valid) {
			btnNext1.setDisable(false);
		} else {
			btnNext1.setDisable(true);
		}
	}

	/**
	 * Handler event on pressing the 'Next' Button of the Flower tab
	 */
	public void pressedNextOnFlowerDetails() {
		tabProduct.setDisable(true);
		tabFlowers.setDisable(true);
		tabImage.setDisable(false);

		// select Image Pane as the next tab
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(2);
	}

	/**
	 * Handler event on pressing the 'Save' Button Send request to the server for
	 * saving the product
	 */
	public void pressedSaveButton() {
		if (!updateForm) {
			insertProductToDB(catalogProduct, imageProduct, flowersInTheProduct);
		} else {
			updateProductToDB(catalogProduct, imageProduct, flowersInTheProduct);
		}

		selectController.fillCatalogItems();
	}
	/**
	 * clear whole form
	 */
	public void clearAllForm() {
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
		addingFlowerValidation(-1);
		flowersDetailsValidation();
		imageCheckValidation(false);
	}

	/**
	 * Insert instance of catalogProduct to db
	 * 
	 * @param pro  The instance of the product
	 * @param imageProduct product image to load
	 * @param flowerInProduct list of flowers
	 */
	public void insertProductToDB(CatalogProduct pro, FileSystem imageProduct,
			ArrayList<FlowerInProduct> flowerInProduct) {
		try {
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
				public void onWaitingForResult() {
				}

				@Override
				public void onReceivingResult(Packet p) {
					if (p.getResultState()) {
						// update new id to instance of product
						CatalogProduct productResult = (CatalogProduct) p
								.getParameterForCommand(Command.insertCatalogProduct).get(0);
						pro.setId(productResult.getId());
						imageProduct.setProductId(productResult.getId());
						ConstantData.displayAlert(AlertType.INFORMATION, "Success Inserting", "Inserting To Database",
								"Successfull!");
						pressedCancelButton();
					} else {
						String exception = p.getExceptionMessage();
						if (exception.toLowerCase().contains("duplicate")) {
							ConstantData.displayAlert(AlertType.ERROR, "Duplicate Product Name",
									"Failed on Inserting Catalog Product",
									"The Product Name is Already Exists! Please Enter another one.");

							tabFlowers.setDisable(true);
							tabImage.setDisable(true);
							tabProduct.setDisable(false);
							SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
							selectionModel.select(0);

							txtName.requestFocus();
						} else {
							ConstantData.displayAlert(AlertType.ERROR, "Error Inserting", "Failed on Inserting Catalog Product",
									exception);
						}
					}
				}
			});
			send.start();
		} catch (Exception e) {
			ConstantData.displayAlert(AlertType.ERROR, "Error Inserting", "Failed on Inserting Image", e.getMessage());
		}
	}

	/**
	 * Update instance of catalogProduct to db
	 * 
	 * @param pro
	 *            the product instance of CatalogProduct
	 * @param img
	 *            the image instance of CatalogProduct
	 * @param flowerInProduct
	 *            the collection flowers of this product
	 */
	public void updateProductToDB(CatalogProduct pro, FileSystem img, ArrayList<FlowerInProduct> flowerInProduct) {
		try {
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
				public void onWaitingForResult() {
				}

				@Override
				public void onReceivingResult(Packet p) {
					if (p.getResultState()) {
						ConstantData.displayAlert(AlertType.INFORMATION, "Success Updating", "Updating To Database", "Successfull!");

						pressedCancelButton();
					} else {
						String exception = p.getExceptionMessage();
						if (exception.toLowerCase().contains("duplicate")) {
							ConstantData.displayAlert(AlertType.ERROR, "Duplicate Product Name",
									"Failed on Updating Catalog Product",
									"The Product Name is Already Exists! Please Enter another one.");

							tabFlowers.setDisable(true);
							tabImage.setDisable(true);
							tabProduct.setDisable(false);
							SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
							selectionModel.select(0);
						} else {
							ConstantData.displayAlert(AlertType.ERROR, "Error Updating", "Failed on Updating Catalog Product",
									exception);
						}
					}
				}
			});
			send.start();
		} catch (Exception e) {
			ConstantData.displayAlert(AlertType.ERROR, "Error Inserting", "Failed on Inserting Image", e.getMessage());
		}
	}

	public void updateProductIdInInstances(int productId) {
		catalogProduct.setId(productId);
		for (FlowerInProduct fp : flowersInTheProduct)
			fp.setProduct(productId);
	}

	/**
	 * Get the index of the collection of types by type Id
	 * 
	 * @param typeId
	 *            The type Id to search for index
	 * @return The index of the item in the collection that found, If not found,
	 *         return -1
	 */
	public int getIndexOfTypeInTheCollectionByTypeId(int typeId) {
		for (int i = 0; i < ConstantData.productTypeList.size(); i++) {
			if (ConstantData.productTypeList.get(i).getId() == typeId)
				return i;
		}

		return -1;
	}
	
	/**
	 * Get the index of the collection of flowers in product by flower Id
	 * 
	 * @param flowerid
	 *            The flower Id to search for index
	 * @return The index of the item in the collection that found, If not found,
	 *         return -1
	 */
	public int getIndexOfFlowerInTheCollectionByFlowerId(int flowerid) {
		for (int i = 0; i < flowersInTheProduct.size(); i++) {
			if (flowersInTheProduct.get(i).getFlowerId() == flowerid)
				return i;
		}

		return -1;
	}

	/**
	 * Get the instance of color by color id
	 * 
	 * @param colorId
	 *            The color Id to search for index
	 * @return The instance of the color in the collection that found, If not found,
	 *         return null
	 */
	public ColorProduct getColorOfFlowerByColorId(int colorId) {
		for (ColorProduct color : ConstantData.productColorList) {
			if (color.getColId() == colorId)
				return color;
		}

		return null;
	}

	/**
	 * Get the instance of flower by flower Id
	 * 
	 * @param flowerId
	 *            The flower Id to search for index
	 * @return The instance of the flower in the collection that found, If not
	 *         found, return null
	 */
	public Flower getFlowerInTheCollectionByFlowerId(int flowerId) {
		for (Flower flower : flowersList) {
			if (flower.getId() == flowerId)
				return flower;
		}

		return null;
	}

	/**
	 * Loads exists product and flowers in this product to the form
	 */
	public void loadExistsProductDetails() {
		if (updateForm) {
			/* loads Product details tab */
			txtName.setText(catalogProduct.getName());
			int typeIndex = getIndexOfTypeInTheCollectionByTypeId(catalogProduct.getProductTypeId());
			cmbType.getSelectionModel().select(typeIndex);
			txtPrice.setText("" + catalogProduct.getPrice());

			/* loads Exists Flowers tab */
			fillFlowersInProduct();

			/* load Exists Image tab */
			loadImage(imageProduct);
		}
	}

	/**
	 * Fill all items to the customize listview dynamically
	 */
	public void fillFlowersInProduct() {
		data = FXCollections.observableArrayList(flowersInTheProduct);
		lstFlower.setCellFactory(new Callback<ListView<FlowerInProduct>, ListCell<FlowerInProduct>>() {

			@Override
			public ListCell<FlowerInProduct> call(ListView<FlowerInProduct> param) {
				return new ListCell<FlowerInProduct>() {

					private void setCellHandler(FlowerInProduct pro) {
						// name of flower
						Flower flower = getFlowerInTheCollectionByFlowerId(pro.getFlowerId());
						Text proName = new Text(flower.getName());
						proName.setFont(new Font(14));
						proName.setStyle("-fx-font-weight: bold");

						// color of flower
						ColorProduct colorFlower = getColorOfFlowerByColorId(flower.getColor());
						Text color = new Text(colorFlower.getColorName());
						color.setFont(new Font(12));

						VBox flowerDetails = new VBox(proName, color);
						
						Region region1 = new Region();
						HBox.setHgrow(region1, Priority.ALWAYS);

						Button incButton = new Button();
						Image imageInc = new Image("addQty.png");
						ImageView viewInc = new ImageView(imageInc);
						viewInc.setFitWidth(10);
						viewInc.setFitHeight(10);
						incButton.setGraphic(viewInc);
						incButton.setPrefWidth(12);

						int currentQty = pro.getQuantity();
						Text qty = new Text("" + currentQty);
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
							if (currentQty > 1) {
								pro.setQuantity(currentQty - 1);
								qty.setText("" + pro.getQuantity());
							}
						});

						incButton.setOnMouseClicked((event) -> {
							pro.setQuantity(currentQty + 1);
							qty.setText("" + pro.getQuantity());
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
							int index = getIndexOfFlowerInTheCollectionByFlowerId(pro.getFlowerId());
							flowersInTheProduct.remove(index);
							fillFlowersInProduct();
							flowersDetailsValidation();
						});
						
						HBox hBox = new HBox(delButton, flowerDetails, region1, qtyOptions);
						hBox.setStyle("-fx-border-style: solid inside;"
					 	        + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
					 	        + "-fx-border-radius: 5;");
						hBox.setSpacing(10);
		                hBox.setPadding(new Insets(10));
		                    
		                setGraphic(hBox);
					}

					@Override
					protected void updateItem(FlowerInProduct item, boolean empty) {
						if (item != null) {
							setCellHandler(item);
						}
					}
				};
			}
		});
		lstFlower.setItems(data);

		lstFlower.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				event.consume();
			}
		});
	}

	/**
	 * Handler event on pressing the 'Cancel' Button
	 */
	public void pressedCancelButton() {
		try {

			primaryStage.close();

			if (updateForm || comesFromCatalog) {

				SelectProductController selectController = new SelectProductController();
				selectController.setForUpdateCatalog();
				selectController.start(new Stage());
			} else {
				// << back to menu >>
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handler event on pressing the 'Next' Button of the Product tab
	 */
	public void pressedNextOnProductDetails() {
		String name = txtName.getText();
		int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
		double price = Double.valueOf(txtPrice.getText());

		try {
			if (productNameIsAlreadyExists(name))
				throw new Exception();

			updateProductDetails(name, price, selectedTypeIndex);

			// select Flower Pane as the next tab
			tabFlowers.setDisable(false);
			SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
			selectionModel.select(1);

			tabProduct.setDisable(true);
			tabImage.setDisable(true);
		} catch (IndexOutOfBoundsException e) {
			tabFlowers.setDisable(true);
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Selected Type Error",
					"Failed On Saving Product Details, Make Sure You Selected Type");
		} catch (Exception e) {
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Duplicate Product Name",
					"The Product Name is Already Exists! Please Enter another one.");

			tabFlowers.setDisable(true);
			tabImage.setDisable(true);
			tabProduct.setDisable(false);
			SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
			selectionModel.select(0);
		}
	}

	/**
	 * Handler event on pressing the 'Prev' Button of the Flower tab
	 */
	public void pressedPrevOnFlowerDetails() {
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
	public void pressedImportImageButton() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
		FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
		fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

		File file = fileChooser.showOpenDialog(primaryStage);

		if (file != null) {
			catalogProduct.setImgUrl(file.getPath());
			imageProduct.loadImageFromLocal(file.getPath());

			loadImage(file);

			imageCheckValidation(true);
		}
	}

	/**
	 * Load a local image to the image viewer
	 * 
	 * @param imageProduct
	 *            The path of the local image
	 */
	private void loadImage(FileSystem imageProduct) {
		try {
			Image img = imageProduct.getImageInstance();
			imgProduct.setImage(img);
			imageCheckValidation(true);
		} catch (IOException e) {
			ConstantData.displayAlert(AlertType.ERROR, "Image File Corrupted", "Image Loading was Failed:", e.getMessage());
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
	 * 
	 * @param file
	 *            The file instance that contains the image
	 */
	private void loadImage(File file) {
		Image image = new Image(file.toURI().toString());
		imgProduct.setImage(image);
	}

	/**
	 * Check validation when there is a image for this product
	 * @param imageInserted if image inserted
	 */
	public void imageCheckValidation(boolean imageInserted) {
		if (imageInserted == true) {
			btnSave.setDisable(false);
			errImage.setVisible(false);
		} else {
			btnSave.setDisable(true);
			errImage.setVisible(true);
		}
	}

	/**
	 * Handler event on pressing the 'Prev' Button of the Image tab
	 */
	public void pressedPrevOnImageDetails() {
		tabProduct.setDisable(true);
		tabFlowers.setDisable(false);
		tabImage.setDisable(true);
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(1);
	}

	/**
	 * Initialize the ComboBox of Flowers with List of flowers
	 * 
	 * @param flowersList
	 *            the collection of flowers to insert to Flower ComboBox
	 */
	public void setComboBoxFlowersList(ArrayList<Flower> flowersList) {
		ArrayList<String> flowers = new ArrayList<>();

		for (Flower f : flowersList)
			flowers.add(f.toString());

		ObservableList<String> observeFlowersList = FXCollections.observableArrayList(flowers);
		cmbFlower.setItems(observeFlowersList);
	}

	/**
	 * Handler event on changing the product name textField
	 */
	public void setNameFieldChangeHandler() {
		txtName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.length() > 50) {
					txtName.setText(oldValue);
					errNameTooMuchLong.setVisible(true);
				}
				else
				{
					errNameTooMuchLong.setVisible(false);
				}

				int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
				productDetailsValidation(newValue, txtPrice.getText(), selectedTypeIndex);
			}
		});
	}

	/**
	 * Handler event on changing the item selected on Type ComboBox
	 * @param event actual event
	 */
	public void setTypeChangeHandler(ActionEvent event) {
		int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
		productDetailsValidation(txtName.getText(), txtPrice.getText(), selectedTypeIndex);
		txtPrice.requestFocus();
	}

	/**
	 * This method check validation of adding a flower pane
	 * 
	 * @param selectedFlowerIndex
	 *            the selected ComboBox index of flower
	 */
	public void addingFlowerValidation(int selectedFlowerIndex) {
		boolean valid = true;

		// check Flower combobox
		if (selectedFlowerIndex == -1) {
			valid = false;
		}

		if (valid) {
			btnAdd.setDisable(false);
		} else {
			btnAdd.setDisable(true);
		}
	}

	/**
	 * Handler event when pressing on del button and delete the selected flower
	 */
	public void pressedDelFlowersButtonHandler() {
		int index = lstFlower.getSelectionModel().getSelectedIndex();
		deleteExistsFlowerFromList(index);

	}

	/**
	 * Delete an exists flower from the list
	 * 
	 * @param index
	 *            The index of the flower in the index
	 */
	public void deleteExistsFlowerFromList(int index) {
		flowersInTheProduct.remove(index);
		lstFlower.getItems().remove(index);

		// use the validation for set error label for empty flowers when needed
		flowersDetailsValidation();
	}

	/**
	 * Returns a String formatted that contains the flower name with its quantity
	 * 
	 * @param flowerInProduct
	 *            the flower in product instance
     * @return formatted string with flower name and quantity
	 */
	private String getFlowerFormatOnList(FlowerInProduct flowerInProduct) {
		String flowerName = "";

		Flower flower = getFlowerInTheCollectionByFlowerId(flowerInProduct.getFlowerId());
		if (flower != null)
			flowerName = flower.getName();

		return String.format("Flower: %s, Qty: %d", flowerName, flowerInProduct.getQuantity());
	}

	/**
	 * Returns a String formatted that contains the flower name with its quantity
	 * 
	 * @param flower
	 *            The flower name
	 * @param qty
	 *            The quantity of the flower
	 *            @return formatted string with flower name and quantity 
	 */
	private String getFlowerFormatOnList(String flower, int qty) {
		return String.format("Flower: %s, Qty: %d", flower, qty);
	}

	/**
	 * Handler event on pressing on Add Flower Button
	 */
	public void pressedAddFlowerButton() {
		int selectedFlowerIndex = cmbFlower.getSelectionModel().getSelectedIndex();
		Flower selectedFlower = flowersList.get(selectedFlowerIndex);

		String flowerName = selectedFlower.getName();
		int flowerId = selectedFlower.getId();

		try {
			// add flower to flowerInProduct list
			addFlowerToTheProduct(flowerId, 1);

			clearAddingFlowerFields();

			fillFlowersInProduct();
			
			lstFlower.scrollTo(lstFlower.getItems().size() - 1);
		} catch (FlowerIsAlreadyExistsException e) {
			ConstantData.displayAlert(AlertType.ERROR, "Flower Exists", "Failed on adding the flower: " + flowerName,
					"This Flower is already exists");
		} finally {
			// use the validation for remove the error label for empty flowers
			flowersDetailsValidation();
		}
	}

	/**
	 * Add The flower with its details to the product
	 * 
	 * @param flowerId
	 *            The flower ID
	 * @param qty
	 *            Quantity of the flower for this product
	 * @throws FlowerIsAlreadyExistsException
	 *             Exception when the flower is already exists in this product
	 */
	public void addFlowerToTheProduct(int flowerId, int qty) throws FlowerIsAlreadyExistsException {
		if (checkIfFlowerInProductExistsByID(flowerId))
			throw new FlowerIsAlreadyExistsException();

		FlowerInProduct fPro;
		if (updateForm) {
			// put the exists id of the product
			fPro = new FlowerInProduct(flowerId, catalogProduct.getId(), qty);
		} else {
			// insert with unknown id of product
			fPro = new FlowerInProduct(flowerId, qty);
		}

		flowersInTheProduct.add(fPro);
	}

	/**
	 * Check if Flower exists in flower in product list by flower id
	 * 
	 * @param flowerId
	 *            The flower Id
	 * @return true if exists, false else
	 */
	private boolean checkIfFlowerInProductExistsByID(int flowerId) {
		for (FlowerInProduct fp : flowersInTheProduct) {
			if (fp.getFlowerId() == flowerId)
				return true;
		}

		return false;
	}

	/**
	 * clear the ComboBox and the Qty Field to default values
	 */
	public void clearAddingFlowerFields() {
		cmbFlower.getSelectionModel().select(-1);
	}

	/**
	 * Handler event on changing the item selected on Flower ComboBox
	 * @param event actual event
	 */
	public void setFlowerChangeHandler(ActionEvent event) {
		int selectedFlowerIndex = cmbFlower.getSelectionModel().getSelectedIndex();
		addingFlowerValidation(selectedFlowerIndex);
	}

	/**
	 * Handler event on changing text on Price textField
	 */
	public void setPriceChangeHandler() {
		txtPrice.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
				productDetailsValidation(txtName.getText(), newValue, selectedTypeIndex);
			}
		});
	}
	
	/**
	 * Show the scene view of complains management
	 * 
	 * @param primaryStage - current stage to build
	 * @throws Exception error message
	 */
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

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				try {
					primaryStage.close();

					if (updateForm || comesFromCatalog) {
						SelectProductController selectController = new SelectProductController();
						selectController.setForUpdateCatalog();
						selectController.start(new Stage());
					} else {
						// << back to menu >>
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * get data from server and initialize controls
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/* call method to push collections from server */
		pushAllTypesAndFlowers();

		/* register all handlers events */
		setNameFieldChangeHandler();
		setPriceChangeHandler();

		/* display all missing fields */
		int selectedTypeIndex = cmbType.getSelectionModel().getSelectedIndex();
		int selectedFlowerIndex = cmbFlower.getSelectionModel().getSelectedIndex();
		productDetailsValidation(txtName.getText(), txtPrice.getText(), selectedTypeIndex);
		addingFlowerValidation(selectedFlowerIndex);
		flowersDetailsValidation();
	}

}
