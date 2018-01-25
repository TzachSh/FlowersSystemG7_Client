package Login;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Branches.Branch;
import Branches.Employee;
import Branches.ReportsController;
import Branches.Role;
import Customers.Account;
import Customers.AccountController;
import Customers.Customer;
import Customers.CustomerController;
import Customers.UpdateCustomerController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.FlowerController;
import Products.SelectProductController;
import Survey.AnswerSurveyController;
import javafx.application.Platform;
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

public class ManagersMenuController implements Initializable {


	@FXML
    private Hyperlink linkChangeBranch;
	@FXML
	private Label lblBranchList;
    @FXML
    private Hyperlink linkLogout;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnCatalogSales;

    @FXML
    private Button btnSurvey;

    @FXML
    private Button btnFlower;

    @FXML
    private ComboBox<String> cmbBranch;

    @FXML
    private Button btnUpdateCatalog;

    @FXML
    private Button btnAccount;

    @FXML
    private Button btnCreateClient;
    @FXML
    private Button btnUpdateClient;
    
    public static ArrayList<Branch> branchesList = new ArrayList<>();
	private static Stage primaryStage;
	private static Employee employee = (Employee)LoginController.userLogged;
	public static Branch currentBranch=null;
	
	private static LoginController loginController;
    
    
	public void setLoginController(LoginController login)
	{
	   loginController = login;
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
	 * Event that occurs when choosing an item on the Branch comboBox
	 */
	public void onClickingBranchComboBox()
	{
		int index = cmbBranch.getSelectionModel().getSelectedIndex();
		if (index != -1)
		{
			btnCatalogSales.setDisable(false);
			Branch branch = branchesList.get(index);
			currentBranch = branch;
			lblBranchList.setVisible(false);
			btnCreateClient.setDisable(false);
			btnUpdateClient.setDisable(false);
			btnAccount.setDisable(false);
		}
		else
		{
			btnCatalogSales.setDisable(true);
			lblBranchList.setVisible(true);
			currentBranch = null;
			btnCreateClient.setDisable(true);
			btnUpdateClient.setDisable(true);
			btnAccount.setDisable(true);
		}
	}
	
	public void start(Stage mainStage) throws Exception {
			String title = "Main Menu";
			String srcFXML = "/Login/ManagersMenu.fxml";
			
		
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource(srcFXML));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				mainStage.setTitle(title);
				mainStage.setScene(scene);
				mainStage.setResizable(false);
				mainStage.show();
				primaryStage=mainStage;
				
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
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
		      		        	event.consume();
		      		        }
		      		});
			}
		});
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	
	 /**
		 * Get from the server the collections of branches and accounts for current user
		 */
		private void initializeCollections()
		{
			Packet packet = new Packet();
			packet.addCommand(Command.getBranches);
			
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
		
			if (employee.getRole() != Role.BranchesManager)
			{
				cmbBranch.getSelectionModel().select(getIndexByBranchId(employee.getBranchId()));
				cmbBranch.setDisable(true);
			}
		}
		
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeCollections();
		Employee employee=(Employee)LoginController.userLogged;
		if(employee.getRole().toString().equals((Role.Branch).toString()))
		{
			btnReports.setDisable(true);
			btnCreateClient.setDisable(true);
			btnUpdateClient.setDisable(true);
			btnAccount.setDisable(true);	
		}
		lblBranchList.setVisible(true);
		if(employee.getRole().toString().equals((Role.BranchesManager).toString()))
			{
				if(cmbBranch.getSelectionModel().getSelectedIndex()==-1)
				{
					btnCreateClient.setDisable(true);
					btnUpdateClient.setDisable(true);
					btnAccount.setDisable(true);	
				}
			}
}
	
	public void onClickUpdateClient()
	{
		UpdateCustomerController updateClient = new UpdateCustomerController();
		try {
			primaryStage.close();
			updateClient.start(new Stage());
			
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	
	public void onClickUpdateCatalog()
	{
		primaryStage.close();
		SelectProductController selectController = new SelectProductController();
		selectController.setForUpdateCatalog();
		try {
			selectController.start(new Stage());
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	public void onClickFetchReport()
	{
		ReportsController reportController = new ReportsController();
		try {
			primaryStage.close();
			reportController.start(new Stage());
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	public void onClickUpdateSaleCatalog()
	{
		primaryStage.close();
		SelectProductController selectController = new SelectProductController();
		selectController.setForUpdateSale();
		try {
			selectController.start(new Stage());
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	
	public void onClickInsertSurveyResults()
	{
		AnswerSurveyController sc = new AnswerSurveyController();
		primaryStage.close();
		sc.start(new Stage());
	}
	
	public void onClickCreateCustomer()
	{
		CustomerController customerController = new CustomerController();
		try {
			primaryStage.close();
			customerController.start(new Stage());
			
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	public void onClickCreateAccount()
	{
		AccountController accountController = new AccountController();

		try {
			primaryStage.close();
			accountController.start(new Stage());
			
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}
	
	public void onClickAddFlower() 
	{
		FlowerController flowerController = new FlowerController();

		try {
			primaryStage.close();
			flowerController.start(new Stage());
			
		} catch (Exception e) {
			displayAlert(AlertType.ERROR, "Error", "Exception Error:", e.getMessage());
		}
	}

}
