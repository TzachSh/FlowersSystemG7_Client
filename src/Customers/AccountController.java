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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		apAddAccount.setVisible(false);
		
	}

	public void addAccountNow()
	{
		if(txtCreditCard1.getText().isEmpty()||txtCreditCard2.getText().isEmpty()||txtCreditCard3.getText().isEmpty()||txtCreditCard4.getText().isEmpty()||txtCreditCard5.getText().isEmpty())
		{
			showError("Please Insert Valid Credit Card");
			return;
		}
		String fullCreditCar=txtCreditCard1.getText().toString()+txtCreditCard2.getText().toString()+txtCreditCard3.getText().toString()+txtCreditCard4.getText().toString()+txtCreditCard5.getText().toString();
		Packet packet = new Packet();
		
		packet.addCommand(Command.addAccounts);
		ArrayList<Object> acclist=new ArrayList<>();
		
		acclist.add(new Account(currentCustomer.getId(), currentUser.getBranchId(), 0, AccountStatus.Active, fullCreditCar));
		packet.setParametersForCommand(Command.addAccounts, acclist);
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
	
	public void checkUserExist()
	{
		if(txtID.getText().isEmpty()) {
			showError("Please Insert Customer's ID");
			return;
		}
		txtID.setDisable(true);
		btnCheckCustomersID.setDisable(true);
		Packet packet = new Packet();
		
		packet.addCommand(Command.getCustomersKeyByuId);
		ArrayList<Object> userl=new ArrayList<>();
		userl.add(new Customer(Integer.parseInt(txtID.getText()),0));
		packet.setParametersForCommand(Command.getCustomersKeyByuId, userl);
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			
		@Override
		public void onWaitingForResult() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onReceivingResult(Packet p) {
			// TODO Auto-generated method stub
			ArrayList<Customer> cList = new ArrayList<>();
			if (p.getResultState())
			{
				
				cList = p.<Customer>convertedResultListForCommand(Command.getCustomersKeyByuId);
				if(cList.isEmpty())
				{
					showError("Customer Not Exist In The DataBase Please Add Customer First");
					btnCheckCustomersID.setDisable(false);
					txtID.setDisable(false);
					txtID.setText("");
					return;
				}
				apAddAccount.setVisible(true);
				currentCustomer=cList.get(0);

			}
			else
				System.out.println("Fail: " + p.getExceptionMessage());
			
		}
		});
		send.start();

	}
	
	public void showError(String str)
	{
		JOptionPane.showMessageDialog(null, 
				str, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
	}
	public void start(Stage primaryStage)  throws Exception{
		// TODO Auto-generated method stub
		String title = "Add Account UI";
		String srcFXML = "/Customers/addAccountUI.fxml";
		String srcCSS = "/Customers/application.css";
		

	
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