package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Users.User;
import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UpdateCustomerController implements Initializable {

	public User user;
	private ArrayList<Account> accList ;
	private ArrayList<User> uList;
	private ArrayList<Membership> memshipList ;
	private ArrayList<Customer> cList ;
	@FXML
	private TextField txtCustomerID;
	@FXML
	private TextField txtUser;
	@FXML
	private TextField txtBalance;
	@FXML
	private TextField txtPassword;
	@FXML
	private TextField txtNewPassword;
	@FXML
	private TextField txtConfirmPassword;
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
	private Button btnSave;
	@FXML
	private Button btnSearch;
	@FXML
	private ComboBox<String> cbStatus;
	@FXML
	private ComboBox<String> cbMemberShip;
	@FXML
	private AnchorPane apnextinfo;
	@FXML
	private AnchorPane appassword;
	@FXML
	private Button btnchangePassword;
	
	 public UpdateCustomerController() {
		// TODO Auto-generated constructor stub
	}
	 public UpdateCustomerController(User user) {
			// TODO Auto-generated constructor stub
		 this.user=user;
		}
	 
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		apnextinfo.setVisible(false);
		appassword.setVisible(false);
				
	}
	//
	private void initComboBox()
	{
		int i=1;
		ObservableList<String> observelistMembership,observelistStatus;
		
		ArrayList<String> membership = new ArrayList<>();
		for(Membership mem: memshipList)
		{
			membership.add(mem.getMembershipType().toString());
		}
		observelistMembership = FXCollections.observableArrayList(membership);
		cbMemberShip.setItems(observelistMembership);
		for(Membership mem: memshipList)
		{
			
			if(mem.getNum()==cList.get(0).getMembershipId())
				cbMemberShip.getSelectionModel().select(i);
			i++;
		}
		
		
		ArrayList<String> AccStatus = new ArrayList<>();
		AccStatus.add(AccountStatus.Active.toString());
		AccStatus.add(AccountStatus.Blocked.toString());
		AccStatus.add(AccountStatus.Closed.toString());
		
		observelistStatus = FXCollections.observableArrayList(AccStatus);
		cbStatus.setItems(observelistStatus);
		if(accList.get(0).getAccountStatus().toString().equals("Active"))
			cbStatus.getSelectionModel().select(0);
		else if(accList.get(0).getAccountStatus().toString().equals("Blocked"))
			cbStatus.getSelectionModel().select(1);
		else if(accList.get(0).getAccountStatus().toString().equals("Closed"))
			cbStatus.getSelectionModel().select(2);

		
	}
	public void searchForCustomerByID()
	{
		if(txtCustomerID.getText().isEmpty()) {
			showError("Please Insert Customer's ID");
			return;
		}
	
		Packet packet = new Packet();
		
		packet.addCommand(Command.getCustomersKeyByuId);
		
		
		ArrayList<Object> userl=new ArrayList<>();
		userl.add(new Customer(Integer.parseInt(txtCustomerID.getText()),0));
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

				cList=p.<Customer>convertedResultListForCommand(Command.getCustomersKeyByuId);
				
				
				if(cList.isEmpty())
				{
					showError("Customer Dose Not Exist");
					txtCustomerID.setText("");
					return;
				}
				
				//getting the infirmation
				Packet packet = new Packet();

				packet.addCommand(Command.getAccountbycID);
				packet.addCommand(Command.getUserByuId);
				packet.addCommand(Command.getMemberShip);
				
				ArrayList<Object> accl=new ArrayList<>();
				accl.add(new Account(0, cList.get(0).getId(), 0, 0, AccountStatus.Active, ""));
				packet.setParametersForCommand(Command.getAccountbycID, accl);
				
				ArrayList<Object> cusl=new ArrayList<>();
				cusl.add(new Customer(Integer.parseInt(txtCustomerID.getText()), 0));
				packet.setParametersForCommand(Command.getUserByuId, cusl);

		
				
				
				
				SystemSender send = new SystemSender(packet);
				send.registerHandler(new IResultHandler() {
					
					@Override
					public void onWaitingForResult() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onReceivingResult(Packet p) {
						accList=p.<Account>convertedResultListForCommand(Command.getAccountbycID);
						uList=p.<User>convertedResultListForCommand(Command.getUserByuId);
						memshipList=p.<Membership>convertedResultListForCommand(Command.getMemberShip);
						
						// TODO Auto-generated method stub
						txtCustomerID.setDisable(true);
						btnSearch.setDisable(true);
						apnextinfo.setVisible(true);
						initComboBox();
						txtUser.setText(uList.get(0).getUser());
						//check if there is account;
						txtBalance.setText(""+accList.get(0).getBalance());
						if((accList.get(0).getCreditCard().isEmpty()==false && accList.get(0).getCreditCard().length()==20))
						{
						txtCreditCard1.setText(accList.get(0).getCreditCard().substring(0, 4));
						txtCreditCard2.setText(accList.get(0).getCreditCard().substring(4, 8));
						txtCreditCard3.setText(accList.get(0).getCreditCard().substring(8, 12));
						txtCreditCard4.setText(accList.get(0).getCreditCard().substring(12, 16));
						txtCreditCard5.setText(accList.get(0).getCreditCard().substring(16, 20));
					}
				}
				});				
				send.start();
				}
	
		});
		send.start();
	}
	public void saveNewCustomerInformation()
	{
		if(txtUser.getText().isEmpty())
		{
			showError("Please Enter New User");
			return;
		}
		if(txtCreditCard1.getText().isEmpty()||txtCreditCard2.getText().isEmpty()||txtCreditCard3.getText().isEmpty()||txtCreditCard4.getText().isEmpty()||txtCreditCard5.getText().isEmpty())
		{
			showError("Please Enter Valid Credit Card Number");
			return;
		}
		if(uList.get(0).getPassword().equals(txtPassword.getText())==false)
		{
			showError("Please Enter The Correct Old Password");
			return;
		}
		if(txtNewPassword.getText().equals(txtConfirmPassword.getText())==false)
		{
			showError("New Password And Confirm Password Are Not Matched");
			return;
		}
		
	}
	public void changePasswordNow()
	{
		appassword.setVisible(true);
	
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
		String srcFXML = "/Customers/updateCustomerUI.fxml";
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
