package Customers;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Users.Permission;
import Users.User;

import javax.swing.JOptionPane;

import com.sun.org.apache.bcel.internal.generic.NEW;

import Login.ManagersMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.CatalogProduct;
import Products.ConstantData;
import Products.FlowerInProduct;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.invoke.util.BytecodeName;

public class CustomerController implements Initializable {
	
	private String cusid,user,password,confirmpassword,creditcard,status,permission;
	private ArrayList<Account> accList ;
	private ArrayList<User> uList;
	private ArrayList<Customer> cList ;
	
	@FXML
	private TextField txtID;
	
	@FXML
	private TextField txtUser;
	@FXML
	private TextField txtPassword;
	
	@FXML
	private TextField txtConfirmPassword;
	
	@FXML
	private TextField txtCreditCard;
	
	@FXML
	private TextField txtDiscount;
	
	@FXML
	private Button btnRegister;
	
	@FXML
	private Button btnFinish;
	
	@FXML
	private Button btnCancel;
	
	@FXML
	private Button btnAddCreditCard;
	
	@FXML
	private Button btnDeleteCreditCard;
	@FXML
	private AnchorPane anchorpane1;
	
	@FXML
	private AnchorPane anchorpane2;
	
	@FXML
	private ComboBox<String> cbStatus;
	
	@FXML
	private ComboBox<String> cbPermission;
	private static Stage myStage;
	
	public void start(Stage primaryStage) throws Exception {

		String title = "Add Customer UI";
		String srcFXML = "/Customers/addCustomerUI.fxml";
		String srcCSS = "/Customers/application.css";
		myStage=primaryStage;

	
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				 primaryStage.close();
				  ManagersMenuController menu = new ManagersMenuController();
				  try {
					menu.start(new Stage());
				} catch (Exception e) {
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
				}
			}
		});
		
	}

	public void emptyCreditCardText()
	{
		if(txtCreditCard.getText().isEmpty())
			return;
		txtCreditCard.setText("");
		creditcard="";
		txtCreditCard.setDisable(false);
		btnAddCreditCard.setDisable(false);


	}

	
	public void addCreditCard()
	{
		if(txtCreditCard.getText().isEmpty())
		{
			showError("Please Fill Credit Card Number");
			
			return;
		}
		creditcard=txtCreditCard.getText();
		txtCreditCard.setDisable(true);
		btnAddCreditCard.setDisable(true);
		
	}

	
	public void showError(String str)
	{
		JOptionPane.showMessageDialog(null, 
				str, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
	}
	

	public void registerNextInformation()
	{
		
		
		
		
		//disapling old information
		txtUser.setDisable(true);
		txtConfirmPassword.setDisable(true);
		txtPassword.setDisable(true);
		btnRegister.setDisable(true	);
		//show additional information
		anchorpane2.setVisible(true);
		
	}
	
	public void registerNow()
	{
		//int membershipid=1;
		if(txtID.getText().toString().isEmpty()||txtPassword.getText().isEmpty()||txtUser.getText().isEmpty()||txtConfirmPassword.getText().isEmpty()) {
			showError("Please Fill All Information");
			
			return;
		}
		cusid=txtID.getText();
		user=txtUser.getText();
		password=txtPassword.getText();
		confirmpassword=txtConfirmPassword.getText();
		//check input
		if(!(checkInput(1)&&checkInput(2)))
		{
			cusid="";
			user="";
			password="";
			confirmpassword="";
			txtUser.setText("");
			txtPassword.setText("");
			txtConfirmPassword.setText("");
			txtID.setText("");
			return;
		}
		Packet packet = new Packet();
		//adding user
		packet.addCommand(Command.addUsers);
		ArrayList<Object>userlist;
		userlist=new ArrayList<>();
		userlist.add(new User(Integer.parseInt(cusid),user, password,false, Permission.Limited));
		packet.setParametersForCommand(Command.addUsers, userlist);
		//adding customer
		packet.addCommand(Command.addCustomers);
		ArrayList<Object>cuslist;
		cuslist=new ArrayList<>();
		
		
	
		cuslist.add(new Customer(Integer.parseInt(cusid)));
		packet.setParametersForCommand(Command.addCustomers,cuslist);

		
		
		SystemSender send = new SystemSender(packet);
		
		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			
		@Override
		public void onWaitingForResult() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onReceivingResult(Packet p) {
			// TODO Auto-generated method stub

			if (p.getResultState())
			{
				JOptionPane.showMessageDialog(null, 
						"The Client : "+user+"  : Has Been Added", 
		                "Success", 
		                JOptionPane.CLOSED_OPTION);
				myStage.close();
				 ManagersMenuController menu = new ManagersMenuController();
				  try {
					menu.start(new Stage());
				} catch (Exception e) {
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
				}

			}
			else
				System.out.println("Fail: " + p.getExceptionMessage());
			
		}
		});
		send.start();

			
		

	}
	
	private boolean checkInput(int choice)
	{
		boolean res=true;
		switch(choice)
		{
			case 1://check ID &check user & password & confirm password
				for(User users : uList)
				{
					if(users.getuId()==Integer.parseInt(cusid))
					{
						showError("Error, ID Exist ");
						res=false;
						break;
					}
					if(users.getUser().equals(user)) 
					{
						showError("User Exist Please Pick Another User");
						res=false;
						break;
					}
					
				}
				break;
			case 2://check both passwords 
				if(!(confirmpassword.equals(password)))
				{
					showError("Please Insert Same Password");
					res=false;
					break;
				}
					
			break;
			
				
			
		}
		
		return res;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//validate customer user field input
		txtUser.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				if(newValue.length()>50)
					txtUser.setText(oldValue);
				/*if(!newValue.equals("") &&newValue.matches("^[a-zA-Z0-9._-]{0,50}$"))
					txtUser.setText(newValue);
				else
					txtUser.setText(oldValue);*/

			}
		});
		//validate id
		txtID.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
			
				if(newValue.length()>9)
					txtID.setText(oldValue);
				if(!newValue.equals("") && (newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtID.setText(oldValue);
			
			}
		});
	
		//validate the password
		txtPassword.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				if(newValue.length()>50)
					txtPassword.setText(oldValue);
			}
		});
		txtConfirmPassword.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				if(newValue.length()>50)
					txtConfirmPassword.setText(oldValue);
			}
		});
		
		Packet packet = new Packet();
		
		//fill the required commands to the packet
		packet.addCommand(Command.getUsers);
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);
		
		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub

			}
			
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if (p.getResultState())
				{
					//getting the information from database 
					uList = p.<User>convertedResultListForCommand(Command.getUsers);
					//cList = p.<Customer>convertedResultListForCommand(Command.getCustomers);


				}
				else
					System.out.println("Fail: " + p.getExceptionMessage());
				
			}
		});
		send.start();
	}
	
}
