package Login;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Branches.Branch;
import Customers.Account;
import Customers.AccountStatus;
import Customers.Customer;
import Customers.MemberShipAccount;
import Customers.Membership;
import Customers.MyComplainsController;
import Customers.UpdateCustomerController;
import Orders.OrderManagementController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.CartController;
import Products.ConstantData;
import Products.SelectProductController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CustomerMenuController implements Initializable {
	
	@FXML
    private Label lblBranch;
	
	@FXML
	private Button btnMyOrders;

	@FXML
	private Hyperlink linkChangeBranch;

	@FXML
	private Hyperlink linkLogout;

	@FXML
	private Button btnCatalogViewer;

	@FXML
    private ComboBox<String> cmbBranch;

	@FXML
    private Button btnCart;
	
    @FXML
    private Button btnAccount;
    @FXML
    private Button btnComplains;
    /**
     * all sale branches
     */
	public static ArrayList<Branch> branchesList = new ArrayList<>();
	/**
	 * selected branch
	 */
    public static Branch currentBranch=null;
    /**
     * all customer accounts
     */
    private static ArrayList<Account> userAccountsList = new ArrayList<>();
    /**
     * customer account in the selected branch if exist
     */
    public static Account currentAcc;
    /**
     * contains value if customer has account in the branch for purchase access
     */
    public static boolean hasAccountForCurrentBranch = false;
    /**
     * Contains login details
     */
    private static LoginController loginController;
    /**
     * memberships of the customer's accounts
     */
    public static ArrayList<MemberShipAccount> memberShipsByAccount;
    /**
     * contains if alert has been passed
     */
    private static boolean noAccountAlerted = false;
    /**
     * stage build
     */
    private static Stage menuStage;
    
    public void setLoginController(LoginController login)
    {
    	loginController = login;
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
	 * Event Logged out that occurs when clicking on logout
	 */
	public void performLoggedOutHandler()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Logged Out");
		alert.setContentText("Are you Sure?");
		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		
		alert.getButtonTypes().setAll(okButton, noButton);
		alert.showAndWait().ifPresent(type -> {
		        if (type == okButton)
		        {
		        	loginController.performLoggedOut(LoginController.userLogged);
		        } 
		});
	}
	
	/**
	 * Event that occurs when clicking on catalog viewer
	 */
	public void onClickingCatalogViewer()
	{
		try
		{
			SelectProductController selectController = new SelectProductController();
			selectController.setForViewingCatalog();
			selectController.start(new Stage());
			menuStage.close();
		}
		catch (Exception e)
		{
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	
	/**
	 * Event that occurs when clicking on my account
	 */
	public void onClickingMyAccount()
	{
		
		UpdateCustomerController updateClient = new UpdateCustomerController();
		try {
			menuStage.close();
			updateClient.start(new Stage());
			
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	
	/**
	 * Event that occurs when clicking on shopping cart
	 */
	public void onClickingShoppingCart()
	{
		try 
		{
			menuStage.close(); //hiding primary window
			Stage primaryStage = new Stage();

			// call to controller of order to open order window
			CartController cartController = new CartController();
			cartController.setComesFromCatalog(false);
			//cartController.addProductsToCartMap(productsSelected);
			cartController.start(primaryStage);
		}
		catch (Exception e) 
		{
			displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Add To Cart Window", e.getMessage());
		}
	}
	
	/**
	 * Event that occurs when clicking on my orders
	 */
	public void onClickingMyOrders()
	{
		OrderManagementController orders = new OrderManagementController();
		try {
			menuStage.close();
			orders.start(new Stage());
			
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	
	/**
	 * Event that occurs when clicking on my complains
	 */
	public void onClickingMyComplains()
	{
		menuStage.close(); //hiding primary window
		MyComplainsController mc = new MyComplainsController();
		mc.start(new Stage());
	}
	/**
	 * Initialize the ComboBox of Branches with List of branches
	 * @param branchesList the collection of branches to insert to Branch ComboBox
	 */
	private void setComboBoxBrancesList(ArrayList<Branch> branchesList)
	{
		ArrayList<String> branches = new ArrayList<>();
		
		for (Branch br : branchesList)
			branches.add(br.toString());
		
		ObservableList<String> observeBranchesList = FXCollections.observableArrayList(branches);
		
		cmbBranch.setItems(observeBranchesList);
		
		if (currentBranch != null)
			cmbBranch.getSelectionModel().select(getIndexByBranchId(currentBranch.getbId()));
	}
	
	/**
	 * Get the index in the collection by the branch id
	 * @param branchId The branch id to search for
	 * @return index if found, -1 if not found
	 */
	private int getIndexByBranchId(int branchId)
	{
		for (int i = 0; i < branchesList.size(); i++)
		{
			if (branchesList.get(i).getbId() == branchId)
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Enable the buttons and labels when there is account
	 */
	private void enableComponentsWhenThereIsAccount()
	{
		lblBranch.setVisible(false);
		hasAccountForCurrentBranch = true;
		
		btnMyOrders.setDisable(false);
		btnCart.setDisable(false);
		btnAccount.setDisable(false);
	}
	
	/**
	 * Disable the buttons and labels when there is no account
	 */
	private void disableComponentsWhenThereIsNoAccount()
	{
		lblBranch.setVisible(true);
		
		hasAccountForCurrentBranch = false;
		
		btnMyOrders.setDisable(true);
		btnCart.setDisable(true);
		btnAccount.setDisable(true);
	}
	
	/**
	 * Event that occurs when choosing an item on the Branch comboBox
	 */
	public void onClickingBranchComboBox()
	{
		linkChangeBranch.setDisable(false);
		linkChangeBranch.setVisited(false);
		
		cmbBranch.setDisable(true);
		int index = cmbBranch.getSelectionModel().getSelectedIndex();
		if (index != -1)
		{
			Branch branch = branchesList.get(index);
			currentBranch = branch;
			currentAcc=userAccountsList.stream().filter(c->c.getBranchId()==currentBranch.getbId()).findFirst().orElse(null);
			// alert if the user has no account for this branch
			// if there is no account, disable the option for select products, or add to cart
			if (currentAcc != null)
			{
				if (currentAcc.getAccountStatus() == AccountStatus.Blocked)
				{
					lblBranch.setText("Your Account has been BLOCKED! Contact with Branch Manager");

					disableComponentsWhenThereIsNoAccount();
					
					if (!noAccountAlerted)
					{
						displayAlert(AlertType.WARNING, "Warning!", "Account Blocked", "Your Account has been BLOCKED!, Please contact with Branch Manager");
						noAccountAlerted = true;
					}
				}
				else
				{
					enableComponentsWhenThereIsAccount();
				}
				
				MemberShipAccount memberByAc = memberShipsByAccount.stream().filter(c->c.getAcNum()==currentAcc.getNum()).findFirst().orElse(null);
				if(memberByAc !=null)
				{
					Membership memberShip = ConstantData.memberShipList.stream().filter(c->c.getNum()==memberByAc.getmId()).findFirst().orElse(null);
					currentAcc.setMemberShip(memberShip);
				}
			}
			else 
			{
				lblBranch.setText("You don't have an Account! Contact with Branch Manager for Open");

				disableComponentsWhenThereIsNoAccount();
				
				if (!noAccountAlerted)
				{
					displayAlert(AlertType.WARNING, "Warning!", "No Account!", "You don't have account for selected branch, Please contact with Branch Manager for open a new one");
					noAccountAlerted = true;
				}
			}
		}
		else
		{
			currentBranch = null;
			disableComponentsWhenThereIsNoAccount();
			
			lblBranch.setText("Select Branch for Viewing Discounts Sales and for Buying");
		}
	}
	
	/**
	 * Event that occurs when clicking on the link for changing the current branch
	 */
	public void onClickingChangeBranchLink()
	{
		if (SelectProductController.productsSelected.size() > 0) // user is already select product from some branch
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
		SelectProductController.productsSelected.clear();
    	CartController.cartProducts.clear();
    	
    	cmbBranch.getSelectionModel().select(-1);
		cmbBranch.setDisable(false);
		linkChangeBranch.setDisable(true);

		btnCart.setDisable(true);
		
		currentBranch = null;
	}
	
    /**
	 * Get from the server the collections of branches and accounts for current user
	 */
	private void initializeCollections()
	{
		Packet packet = new Packet();
		packet.addCommand(Command.getBranches);
		packet.addCommand(Command.getAccountbycID);
		packet.addCommand(Command.getMemberShipAccount);
		
		Customer customer = (Customer)LoginController.userLogged;
		int cid = customer.getId();
		
		ArrayList<Object> accountParam = new ArrayList<>(Arrays.asList(cid));
		
		packet.setParametersForCommand(Command.getAccountbycID, accountParam);
		packet.setParametersForCommand(Command.getMemberShipAccount, accountParam);
		
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
					branchesList = p.<Branch>convertedResultListForCommand(Command.getBranches);
					setComboBoxBrancesList(branchesList);
					
					userAccountsList = p.<Account>convertedResultListForCommand(Command.getAccountbycID);
					
					memberShipsByAccount =p.<MemberShipAccount>convertedResultListForCommand(Command.getMemberShipAccount); 
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
	 * creating window
	 * @param primaryStage stage to build
	 * @throws Exception message if failed
	 */
	public void start(Stage primaryStage) throws Exception {
		menuStage = primaryStage;
		
		String title = "Main Menu";
		String srcFXML = "/Login/CustomerMenu.fxml";
		
	
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {

		            Alert alert = new Alert(Alert.AlertType.WARNING);
		      		alert.setTitle("Logged Out");
		      		alert.setContentText("Are you Sure?");
		      		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		      		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		      		
		      		alert.getButtonTypes().setAll(okButton, noButton);
		      		alert.showAndWait().ifPresent(type -> {
		      		        if (type == okButton)
		      		        {
		      		        	loginController.performLoggedOut(LoginController.userLogged);
		      		        	System.exit(0);
		      		        } 
		      		        else
		      		        {
		      		        	we.consume();
		      		        }
		      		});
		          }
		      }); 
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	/**
	 * initialize constant data and other collections to display
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(!ConstantData.isInit)
			ConstantData.initColorsAndTypes();
		initializeCollections();
	}
}
