package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import Branches.Employee;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Users.Permission;
import Users.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AccountController implements Initializable{
	////
	@FXML
	private TextField txtID;
	@FXML
	private Button btnCheckCustomersID;
	@FXML
	private AnchorPane apAddAccount;
	@FXML
	private TextField txtCreditCard1;
	@FXML
	private TextField txtCreditCard2;
	@FXML
	private TextField txtCreditCard3;
	@FXML
	private TextField txtCreditCard4;
	@FXML
	private TextField txtCreditCard5;
	@FXML
	private Button btnFinish;
	
	private Employee currentUser;
	private Customer currentCustomer;
	
	/*public AccountController(User user)
	{
		this.currentUser=(Employee)user;
	}
	*/
	/**
	 * This function initialize the settings
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		//validate id
		txtID.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				//checking that the id len is 9
				if(newValue.length()>9)
					txtID.setText(oldValue);
				if(!newValue.equals("") && (newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))//checking that its only numbers
					txtID.setText(oldValue);
			
			}
		});
		//validate text for credit card
		txtCreditCard1.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				//checking that the credit card section is only 4 digits and only numbers
				if(newValue.length()>4)
					txtCreditCard1.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard1.setText(oldValue);
			}
			
		});
		txtCreditCard2.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				//checking that the credit card section is only 4 digits and only numbers
				if(newValue.length()>4)
					txtCreditCard2.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard2.setText(oldValue);
			}
			
		});
		txtCreditCard3.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				//checking that the credit card section is only 4 digits and only numbers
				if(newValue.length()>4)
					txtCreditCard3.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard3.setText(oldValue);
			}
			
		});
		txtCreditCard4.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				//checking that the credit card section is only 4 digits and only numbers
				if(newValue.length()>4)
					txtCreditCard4.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard4.setText(oldValue);
			}
			
		});
		txtCreditCard5.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				//checking that the credit card section is only 4 digits and only numbers
				if(newValue.length()>4)
					txtCreditCard5.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard5.setText(oldValue);
			}
			
		});		
		//setting visible for the account section
		apAddAccount.setVisible(false);
		
	}
	/**
	 * This function adds the account to the database 
	 */
	public void addAccountNow()
	{
		//checking that the credit card text fields is not empty
		if(txtCreditCard1.getText().isEmpty()||txtCreditCard2.getText().isEmpty()||txtCreditCard3.getText().isEmpty()||txtCreditCard4.getText().isEmpty()||txtCreditCard5.getText().isEmpty())
		{
			showError("Please Insert Valid Credit Card");
			return;
		}
		//checking if the digits is 4 in the credit card fields
		if(txtCreditCard1.getText().length()!=4||txtCreditCard2.getText().length()!=4||txtCreditCard3.getText().length()!=4||txtCreditCard4.getText().length()!=4||txtCreditCard5.getText().length()!=4)
		{
			showError("Please Insert 4 Digits For Credit Card Section");
			return;
		}
		//getting the information
		String fullCreditCar=txtCreditCard1.getText().toString()+txtCreditCard2.getText().toString()+txtCreditCard3.getText().toString()+txtCreditCard4.getText().toString()+txtCreditCard5.getText().toString();
		//opening packet
		Packet packet = new Packet();
		//adding command and the information for the query to use them
		packet.addCommand(Command.addAccounts);
		ArrayList<Object> acclist=new ArrayList<>();
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@change branch=1 to user.getbranch of the menahel
		acclist.add(new Account(currentCustomer.getId(), 1, 0, AccountStatus.Active, fullCreditCar));
		packet.setParametersForCommand(Command.addAccounts, acclist);
		//sending the packet
		SystemSender send = new SystemSender(packet);
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
							"The Account Has Been Added", 
			                "Success", 
			                JOptionPane.CLOSED_OPTION);
				}
				else
					System.out.println("Fail: " + p.getExceptionMessage());
			}
		});
		send.start();
	}
	/**
	 * This function checks that the user exist in the database
	 */
	public void checkUserExist()
	{
		//checking that the id field is not empty
		if(txtID.getText().isEmpty()) {
			showError("Please Insert Customer's ID");
			return;
		}
		//setting the id field and btn to be disable
		txtID.setDisable(true);
		btnCheckCustomersID.setDisable(true);
		//openeing packet
		Packet packet = new Packet();
		//adding getting customers
		packet.addCommand(Command.getCustomersKeyByuId);
		ArrayList<Object> userl=new ArrayList<>();
		//adding the information to the packet
		userl.add(Integer.parseInt(txtID.getText()));
		packet.setParametersForCommand(Command.getCustomersKeyByuId, userl);
		//sending the packet
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			
		@Override
		public void onWaitingForResult() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onReceivingResult(Packet p) {
			// TODO Auto-generated method stub
			//customer list
			ArrayList<Customer> cList = new ArrayList<>();
			if (p.getResultState())
			{
				cList = p.<Customer>convertedResultListForCommand(Command.getCustomersKeyByuId);
				if(cList.isEmpty())//checking that there is customer with this id
				{
					showError("Customer Not Exist In The DataBase Please Add Customer First");
					btnCheckCustomersID.setDisable(false);
					txtID.setDisable(false);
					txtID.setText("");
					return;
				}
				//the customer exist , so we add the information
				currentCustomer=cList.get(0);
				Packet packet = new Packet();
				packet.addCommand(Command.getAccountbycID);
				//adding the information to the packet
				ArrayList<Object> accl=new ArrayList<>();
				accl.add(cList.get(0).getId());
				packet.setParametersForCommand(Command.getAccountbycID, accl);
				//sending the packet
				SystemSender send = new SystemSender(packet);
				send.registerHandler(new IResultHandler() {
					
					@Override
					public void onWaitingForResult() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onReceivingResult(Packet p) {
						// TODO Auto-generated method stub
						ArrayList<Account> accList = new ArrayList<>();
						if (p.getResultState())
						{
							//checking if the customer got account
							accList=p.<Account>convertedResultListForCommand(Command.getAccountbycID);
							if(!accList.isEmpty())
							{
								showError("Customer Already Have Account");
								btnCheckCustomersID.setDisable(false);
								txtID.setDisable(false);
								txtID.setText("");
								return;
							}
							//there is no account for the user , so we set visible for the adding account 
							apAddAccount.setVisible(true);
							
						}
						else
							System.out.println("Fail: " + p.getExceptionMessage());
					

					}
				});
				send.start();
						
			}
			else
				System.out.println("Fail: " + p.getExceptionMessage());
			
		}
		});
		send.start();

	}
	/**
	 * This function show error message 
	 * @param str error message
	 */
	public void showError(String str)
	{
		JOptionPane.showMessageDialog(null, 
				str, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
	}
	/**
	 * This function  initialize the window
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage)  throws Exception{
		// TODO Auto-generated method stub
		String title = "Add Account UI";
		String srcFXML = "/Customers/addAccountUI.fxml";
		String srcCSS = "/Customers/application.css";
		

		//setting the window settings
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
				Platform.exit();
			}
		});
	}

}
