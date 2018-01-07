package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.sun.scenario.effect.impl.state.LinearConvolveRenderState.PassType;

import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Users.User;
import javafx.application.Platform;
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
import javafx.scene.control.Label;
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
	private Label lbStatus;
	@FXML
	private Label lbBalance;
	@FXML
	private Label lbCreditCard;
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
	private Button btnCancelUpdatingPassword;
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
		
		txtCustomerID.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				/*if(txtCustomerID.getText().length()>9) 
				{
					String oldstring=txtCustomerID.getText().substring(0, 9);
					txtCustomerID.setText(oldstring);					
				}*/
				boolean res=true;
				if(newValue.length()>9)
					txtCustomerID.setText(oldValue);
				if(!newValue.equals("") && (newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCustomerID.setText(oldValue);
			
			}
		});
				
	}
	public void hidePasswordSection()
	{
		appassword.setVisible(false);
		btnCancelUpdatingPassword.setVisible(false);
		btnchangePassword.setVisible(true);
	}
	//
	private void initComboBox()
	{
		int i=0;
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
				{
					cbMemberShip.getSelectionModel().select(i);
					break;
				}
				i++;
			}
		if(accList.isEmpty()==false)
		{
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
		else//there is no account for this user
		{
			lbCreditCard.setVisible(false);
			lbStatus.setVisible(false);
			lbBalance.setVisible(false);
			cbStatus.setVisible(false);
			txtCreditCard1.setVisible(false);
			txtCreditCard2.setVisible(false);
			txtCreditCard3.setVisible(false);
			txtCreditCard4.setVisible(false);
			txtCreditCard5.setVisible(false);
			txtBalance.setVisible(false);
		}	
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
						if(accList.isEmpty()==false)
						{
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
		/*if(txtCreditCard1.getText().isEmpty()||txtCreditCard2.getText().isEmpty()||txtCreditCard3.getText().isEmpty()||txtCreditCard4.getText().isEmpty()||txtCreditCard5.getText().isEmpty())
		{
			showError("Please Enter Valid Credit Card Number");
			return;
		}*/
		if(appassword.isVisible())
		{
			if(txtPassword.getText().isEmpty()) 
			{		
				showError("Please Enter The Old Password ");
				return;
			}
			if(uList.get(0).getPassword().equals(txtPassword.getText())==false)
			{
				showError("Please Enter The Correct Old Password");
				return;
			}
			if(txtNewPassword.getText().isEmpty()) 
			{		
				showError("Please Enter The New Password ");
				return;
			}
			if(txtConfirmPassword.getText().isEmpty()) 
			{		
				showError("Please Enter The New Confirm Password ");
				return;
			}
			if(txtNewPassword.getText().equals(txtConfirmPassword.getText())==false)
			{
				showError("New Password And Confirm Password Are Not Matched");
				return;
			}
		}
		
		if(uList.isEmpty())
		{
			showError("Error Loading Customer , Please Try Again");
			return;
		}
		
		String newuser,newstatus,newmembership,newcreditcard,newpassword,orginalmemship="";
		int choosedmemid=-1,accountinfochanged=0;//accountinfochanged help us to know what the user changed to his information
		/*if its
		* 	0 nothing changed 
		*   1 it means that he only changed credit card
		*	2 it means that he only changed status
		*	3 it means that he changed both status and credit card
		*/
		//updating the information
		Packet packet = new Packet();

	
		newuser=txtUser.getText();
		newstatus=cbStatus.getSelectionModel().getSelectedItem();
		newmembership=cbMemberShip.getSelectionModel().getSelectedItem();
		newcreditcard=txtCreditCard1.getText()+txtCreditCard2.getText()+txtCreditCard3.getText()+txtCreditCard4.getText()+txtCreditCard5.getText();
		newpassword=txtNewPassword.getText();

				
		ArrayList<Object> accl=new ArrayList<>();
		ArrayList<Object> userl=new ArrayList<>();
		ArrayList<Object> cusl=new ArrayList<>();
		//updating user first
		packet.addCommand(Command.updateUserByuId);
		if(newpassword.isEmpty())//the user changed his password
			userl.add(new User(uList.get(0).getuId(),newuser, uList.get(0).getPassword(), uList.get(0).isLogged(), uList.get(0).getPermission()));
		else
			userl.add(new User(uList.get(0).getuId(),newuser, newpassword, uList.get(0).isLogged(), uList.get(0).getPermission()));
		//the user didnt change his password
		packet.setParametersForCommand(Command.updateUserByuId, userl);
		
		
		//updating Customer
		for(Membership mem:memshipList)//getting the orginal membership name (we got only membership id)
		{
			if(mem.getNum()==cList.get(0).getMembershipId())
				orginalmemship=mem.getMembershipType().toString();
			if(mem.getMembershipType().toString().equals(newmembership))
				choosedmemid=mem.getNum();
		}
		if(orginalmemship.isEmpty()==false&&choosedmemid!=-1)
			if(newmembership.equals(orginalmemship)==false)//he changed the membership
			{
				packet.addCommand(Command.updateCustomerByuId);
				cusl.add(new Customer(uList.get(0).getuId(),choosedmemid));
				packet.setParametersForCommand(Command.updateCustomerByuId, cusl);
			}
		
		
		//updating Account
		if(accList.isEmpty()==false) 
		{
			//finding what changed
			if(newcreditcard.isEmpty()==false)
				if(newcreditcard.equals(accList.get(0).getCreditCard())==false)
					accountinfochanged=1;//creditcard changed
			if(newstatus.equals(accList.get(0).getAccountStatus().toString())==false)
				if(accountinfochanged==1)
					accountinfochanged=3;//both are  changed
				else
					accountinfochanged=2;//only status
			if(accountinfochanged>0)
			{
				packet.addCommand(Command.updateAccountsBycId);
				
				AccountStatus accstatusvar;
				if(newstatus.equals(AccountStatus.Active.toString()))
					accstatusvar=AccountStatus.Active;
				else if(newstatus.equals(AccountStatus.Blocked.toString()))
					accstatusvar=AccountStatus.Blocked;
				else
					accstatusvar=AccountStatus.Closed;
				
				switch(accountinfochanged)
				{
				case 1://only creditcard changed
					accl.add(new Account(accList.get(0).getCustomerId(), accList.get(0).getBranchId(), accList.get(0).getBalance(), accList.get(0).getAccountStatus(), newcreditcard));
					break;
				case 2://only status changed
					accl.add(new Account(accList.get(0).getCustomerId(), accList.get(0).getBranchId(), accList.get(0).getBalance(),accstatusvar, accList.get(0).getCreditCard()));
					break;
				case 3://only status changed
					accl.add(new Account(accList.get(0).getCustomerId(), accList.get(0).getBranchId(), accList.get(0).getBalance(),accstatusvar, newcreditcard));
					break;
				}
				packet.setParametersForCommand(Command.updateAccountsBycId, accl);
			}
		}

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
							"Update Success", 
			                "Success", 
			                JOptionPane.CLOSED_OPTION);

				}
				else
					System.out.println("Fail: " + p.getExceptionMessage());
			}
		});
		send.start();
		
		
		
		
		
		
		
		
		
		
	}
	public void changePasswordNow()
	{
		appassword.setVisible(true);
		btnchangePassword.setVisible(false);
		btnCancelUpdatingPassword.setVisible(true);
	
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
