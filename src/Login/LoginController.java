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
import Users.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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
	
	public static User userLogged;
	
	private static Stage mainStage;
	
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
					displayAlert(AlertType.INFORMATION, "Logout", "Logout Successfull", "You are Logged out from the system Successfully");
					Platform.exit();
				}
			}
			else
			{
				displayAlert(AlertType.ERROR, "Error", "Exception Error", p.getExceptionMessage());
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
	 * @param user
	 */
	public void determineEmployeeOrCustomer(User user)
	{
		LoginController currentLogin = this;
		Packet packet = new Packet();
		packet.addCommand(Command.getCustomersKeyByuId);
		packet.addCommand(Command.getEmployeeByUid);
		
		ArrayList<Object> param = new ArrayList<>(Arrays.asList(user.getuId()));

		packet.setParametersForCommand(Command.getCustomersKeyByuId, param);
		packet.setParametersForCommand(Command.getEmployeeByUid, param);
		
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
			
				// it's a customer, set user instance as customer object
				if (customerList.size() > 0)
				{
					Customer customer = customerList.get(0);
					userLogged = new Customer(user, customer.getId(), customer.getMembershipId());
					
					// <?---- open a menu of customers >
					
					try
					{
						performLoggedIn(user);
						mainStage.close();
						CustomerMenuController menu = new CustomerMenuController();
						menu.setLoginController(currentLogin);
						menu.start(new Stage());
					}
					catch (Exception e)
					{
						performLoggedOut(user);
						displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
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
							displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
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
							displayAlert(AlertType.ERROR, "Error", "Exception Error", e.getMessage());
						}
					}
				}
			}
			else
			{
				displayAlert(AlertType.ERROR, "Error", "Exception Error", p.getExceptionMessage());
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
    		displayAlert(AlertType.ERROR, "Error", "Login Failed", "User name or Password are missing!");
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
						// check if user is already logged in
						if (user.isLogged())
						{
							displayAlert(AlertType.ERROR, "Error", "Login Failed", "User Is already Logged In!");
							return;
						}
						
						// user successful login
						// determine if it's customer or employee and initialize it's fields
						user.setLogged(true);
						determineEmployeeOrCustomer(user);
					}
					else
					{
						displayAlert(AlertType.ERROR, "Error", "Login Failed", "User name or Password are incorrect!");
					}
				}
				else
				{
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}

			@Override
			public void onWaitingForResult() { }
			
		});
		
		send.start();
    }
    
    /**
     * Open a new window for configuration the connection to the server
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
    		displayAlert(AlertType.ERROR, "Error", "Exception", e.getMessage());
    	}
    }
    
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Image img = new Image("settings.png");
		ImageView imgView = new ImageView(img);
		imgView.setFitWidth(25);
		imgView.setFitHeight(25);
		btnConfig.setGraphic(imgView);
	}

}
