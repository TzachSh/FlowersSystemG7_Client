package Login;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Branches.Employee;
import Branches.Role;
import Customers.Customer;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.ConstantData;
import Users.Permission;
import Users.User;
import Users.UsersManagementController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController implements Initializable {
    @FXML
    private Button btnConfig;

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPassword;
	
    /**
     * user logged information
     */
	public static User userLogged;
	/**
	 * current stage
	 */
	private static Stage mainStage;
	
	/**
	 * change login status to avoid login with same account
	 * @param user user who make the action
	 * @param loggedIn status true login else logout
	 */
	private void changeUserLoginState(User user, boolean loggedIn)
	{
		Packet packet = new Packet();
		packet.addCommand(Command.setUserLoggedInState);
		
		user.setLogged(loggedIn);
		ArrayList<Object> paramState = new ArrayList<>(Arrays.asList(user));
		packet.setParametersForCommand(Command.setUserLoggedInState, paramState);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

		@Override
		public void onReceivingResult(Packet p) {
			if (p.getResultState())
			{
				if (!loggedIn) // only for logout operation
				{
					ConstantData.displayAlert(AlertType.INFORMATION, "Logout", "Logout Successfull", "You are Logged out from the system Successfully");
					Platform.exit();
				}
			}
			else
			{
				ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error", p.getExceptionMessage());
			}
	   	}

		@Override
		public void onWaitingForResult() { }			
	});
					
	   send.start();
	}
	
	/**
	 * Perform logged out from the system, and set the logged in status to 0
	 * @param user The user that logged out
	 */
	public void performLoggedOut(User user)
	{
		changeUserLoginState(user, false);
	}
	
	/**
	 * Perform logged in to the system, and set the logged in status to 1
	 * @param user The user that logged in
	 */
	public void performLoggedIn(User user)
	{
		changeUserLoginState(user, true);
	}
	
	/**
	 * Set user as employee or customer and fill all object parameters
	 * @param user to check employee or customer
	 */
	public void determineEmployeeOrCustomer(User user)
	{
		LoginController currentLogin = this;
		Packet packet = new Packet();
		packet.addCommand(Command.getCustomersKeyByuId);
		packet.addCommand(Command.getEmployeeByUid);
		packet.addCommand(Command.getUserByuId);
		
		ArrayList<Object> param = new ArrayList<>(Arrays.asList(user.getuId()));

		packet.setParametersForCommand(Command.getCustomersKeyByuId, param);
		packet.setParametersForCommand(Command.getEmployeeByUid, param);
		packet.setParametersForCommand(Command.getUserByuId, param);
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

		@Override
		public void onReceivingResult(Packet p) {
			if (p.getResultState())
			{
				ArrayList<Customer> customerList = p.<Customer>convertedResultListForCommand(Command.getCustomersKeyByuId);
				ArrayList<Employee> employeeList = p.<Employee>convertedResultListForCommand(Command.getEmployeeByUid);
				ArrayList<User> userList = p.<User>convertedResultListForCommand(Command.getUserByuId);
				
				performLoggedIn(user);
				
				// it's a customer, set user instance as customer object
				// it's an administrator, set user instance as user object
				if (userList.size() > 0 && userList.get(0).getPermission() == Permission.Administrator)
				{
					User administrator = userList.get(0);
					userLogged = new User(administrator);
					
					// <?---- open a menu of customers >
					
					try
					{
						performLoggedIn(user);
						mainStage.close();
						UsersManagementController umController = new UsersManagementController();
						umController.setLoginController(currentLogin);
						umController.start(new Stage());
					}
					catch (Exception e)
					{
						performLoggedOut(user);
						ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
					}
				}
				else if (customerList.size() > 0)
				{
					Customer customer = customerList.get(0);
					userLogged = new Customer(user, customer.getId());
					
					// <?---- open a menu of customers >
					try
					{
						mainStage.close();
						CustomerMenuController menu = new CustomerMenuController();
						menu.setLoginController(currentLogin);
						menu.start(new Stage());
					}
					catch (Exception e)
					{
						performLoggedOut(user);
						ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
					}
				}
				
				// it's an employee, set user instance as employee object
				else if (employeeList.size() > 0)
				{
					Employee employee = employeeList.get(0);
					userLogged = new Employee(user, employee.geteId(), employee.getRole(), employee.getBranchId());
					
					// <?---- open a menu of employee by it's role >
					
					Role roleEmp = employee.getRole();
					if (roleEmp == Role.CustomerService || roleEmp == Role.ServiceExpert)
					{
						try
						{
							mainStage.close();
							ServiceMenuController menu = new ServiceMenuController();
							menu.setLoginController(currentLogin);							
							menu.start(new Stage());							
						}
						catch (Exception e)
						{
							performLoggedOut(user);
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
						}
					}
					else
					{
						try
						{
							mainStage.close();
							ManagersMenuController menu = new ManagersMenuController();
							menu.setLoginController(currentLogin);							
							menu.start(new Stage());							
						}
						catch (Exception e)
						{
							performLoggedOut(user);
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
						}
					}
				}
				
				// it's an administrator, set user instance as user object
				else if (userList.size() > 0)
				{
					User administrator = userList.get(0);
					userLogged = new User(administrator);
					
					// <?---- open a menu of customers >
					
					try
					{
						performLoggedIn(user);
						mainStage.close();
						UsersManagementController umController = new UsersManagementController();
						umController.setLoginController(currentLogin);
						umController.start(new Stage());
					}
					catch (Exception e)
					{
						performLoggedOut(user);
						ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
					}
				}
				
			}
			else
			{
				ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception Error", p.getExceptionMessage());
			}
		}

		@Override
		public void onWaitingForResult() { }
					
		});
				
	  send.start();
	}
	
    /**
     * Login Event that occurs when clicking on 'login' button
     */
    public void performLogin()
    {
    	String userName = txtUser.getText();
    	String pass = txtPassword.getText();
    	
    	if (userName.isEmpty() || pass.isEmpty())
    	{
    		ConstantData.displayAlert(AlertType.ERROR, "Error", "Login Failed", "User name or Password are missing!");
    		return;
    	}
    	
    	User user = new User(userName, pass);
    	
    	Packet packet = new Packet();
		packet.addCommand(Command.getUserByNameAndPass);
		
		ArrayList<Object> param = new ArrayList<>(Arrays.asList(user));
	
		packet.setParametersForCommand(Command.getUserByNameAndPass, param);
	
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);
		
		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState())
				{
					ArrayList<User> userList = p.<User>convertedResultListForCommand(Command.getUserByNameAndPass);
					
					// user name and password validated successfully
					if (userList.size() > 0)
					{
						User user = userList.get(0);
						
						if(user.getPermission() == Permission.Blocked)
						{
							ConstantData.displayAlert(AlertType.INFORMATION, "Blocked", "User Blocked", "Your user has been blocked. Contact the system administrator");
							return;
						}
						
						// check if user is already logged in
						if (user.isLogged())
						{
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Login Failed", "User Is already Logged In!");
							return;
						}
						
						// user successful login
						// determine if it's customer or employee and initialize it's fields
						user.setLogged(true);
						determineEmployeeOrCustomer(user);
						// initialize all static collections data for system
						ConstantData.initColorsAndTypes();
					}
					else
					{
						ConstantData.displayAlert(AlertType.ERROR, "Error", "Login Failed", "User name or Password are incorrect!");
					}
				}
				else
				{
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}

			@Override
			public void onWaitingForResult() { }
			
		});
		send.start();
    }
    
    /**
     * Open a new window for configuration the connection to the server
     * @param event actual event
     */
    public void showConfigurationForm(ActionEvent event)
    {
    	try
    	{
    	ConfigurationController config = new ConfigurationController();
    	config.start(new Stage());
    	}
    	catch (Exception e)
    	{
    		ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception", e.getMessage());
    	}
    }
    /**
     *
	 * Show the scene view  
	 * @param primaryStage - current stage to build
     * @throws Exception message if failed
     */
	public void start(Stage primaryStage) throws Exception {
		this.mainStage = primaryStage;
		
		String title = "Login";
		String srcFXML = "/Login/LoginUI.fxml";

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
		        	 Platform.exit();
		          }
		      }); 
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	/**
	 * initialize controls
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Image img = new Image("settings.png");
		ImageView imgView = new ImageView(img);
		imgView.setFitWidth(25);
		imgView.setFitHeight(25);
		btnConfig.setGraphic(imgView);
		
		txtUser.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			//checking the length
				if(newValue.length()>50)
					txtUser.setText(oldValue);
			}
		});
		
		txtPassword.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			//checking the length
				if(newValue.length()>50)
					txtPassword.setText(oldValue);
			}
		});
	}

}
