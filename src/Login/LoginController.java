package Login;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;

import Branches.Employee;
import Customers.Customer;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.CatalogInBranch;
import Products.CatalogProduct;
import Products.Product;
import Products.SelectProductController;
import Products.SelectProductUI;
import Products.SelectProductController.CatalogProductDetails;
import Users.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
	 * Set user as employee or customer and fill all object parameters
	 * @param user
	 */
	public void determineEmployeeOrCustomer(User user)
	{
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
				ArrayList<Customer> customer = p.<Customer>convertedResultListForCommand(Command.getCustomersKeyByuId);
				ArrayList<Employee> employee = p.<Employee>convertedResultListForCommand(Command.getEmployeeByUid);
			
				// it's a customer, set user instance as customer object
				if (customer.size() > 0)
				{
					userLogged = customer.get(0);
					// <?---- open a menu of customers >
				}
				
				// it's an employee, set user instance as employee object
				else if (employee.size() > 0)
				{
					userLogged = employee.get(0);
					// <?---- open a menu of employee by it's role >
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
						determineEmployeeOrCustomer(user);
					}
					else
					{
						displayAlert(AlertType.ERROR, "Error", "Login Failed", "User name Or Password are incorrect!");
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
