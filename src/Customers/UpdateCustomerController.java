package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.sun.scenario.effect.impl.state.LinearConvolveRenderState.PassType;

import Branches.Employee;
import Login.CustomerMenuController;
import Login.LoginController;
import Login.ManagersMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.ConstantData;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UpdateCustomerController implements Initializable {

	public User user;
	private ArrayList<Account> accList ;
	private ArrayList<User> uList;
	private ArrayList<Membership> memshipList ;
	private ArrayList<MemberShipAccount> memshipAccount ;
	private ArrayList<Customer> cList ;
	@FXML
	private Label lbHeader;
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
	private static Employee currentuser;
	private static Customer currentcustomer;
	private Account currentCustomerAccount;
	private static Stage myStage;

	
	 /*public UpdateCustomerController(User user) {
			// TODO Auto-generated constructor stub
		 this.user=user;
		}*/
	 /**
	  * This function hide and show relevant fields for client / manager
	  * @param loginUser 1 for manager 0 for client
	  */
	 public void HandleUIFields(int loginUser)
	 {
		 if(loginUser==1)
		 {
			 	//handling fields and buttons for the manager
				txtCustomerID.setEditable(true);
			 	btnSearch.setVisible(true);
				txtUser.setEditable(true);
				cbStatus.setEditable(true);
				cbMemberShip.setEditable(true);
				txtCreditCard1.setEditable(true);
				txtCreditCard2.setEditable(true);
				txtCreditCard3.setEditable(true);
				txtCreditCard4.setEditable(true);
				txtCreditCard5.setEditable(true);
				btnSave.setVisible(true);
				btnchangePassword.setVisible(true); 
				lbHeader.setText("Update Client Information");
		 }
		 else
		 {
			 //handling fields and buttons for the client
				txtCustomerID.setEditable(false);
			 	btnSearch.setVisible(false);
				txtBalance.setEditable(false);
				txtBalance.setDisable(false);
				txtUser.setEditable(false);
				cbStatus.setEditable(false);
				cbStatus.setDisable(true);
				cbMemberShip.setDisable(true);
				cbMemberShip.setEditable(false);
				txtCreditCard1.setEditable(false);
				txtCreditCard2.setEditable(false);
				txtCreditCard3.setEditable(false);
				txtCreditCard4.setEditable(false);
				txtCreditCard5.setEditable(false);
				btnSave.setVisible(false);
				btnchangePassword.setVisible(false);
				lbHeader.setText("Client Information");
		 }
	 }
	 /**
	  * This function inistialize the settings
	  */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		apnextinfo.setVisible(false);
		appassword.setVisible(false);
		//validate customer text field input 
		txtCustomerID.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			//checking the length
				if(newValue.length()>9)
					txtCustomerID.setText(oldValue);
				if(!newValue.equals("") && (newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCustomerID.setText(oldValue);
			}
		});
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
		//validate text for credit card
		txtCreditCard1.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				if(newValue.length()>4)
					txtCreditCard1.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard1.setText(oldValue);
			}
			
		});
		txtCreditCard2.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				if(newValue.length()>4)
					txtCreditCard2.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard2.setText(oldValue);
			}
			
		});
		txtCreditCard3.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO checking the length
				if(newValue.length()>4)
					txtCreditCard3.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard3.setText(oldValue);
			}
			
		});
		txtCreditCard4.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// checking the length
				if(newValue.length()>4)
					txtCreditCard4.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard4.setText(oldValue);
			}
			
		});
		txtCreditCard5.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				//checking the lenght
				if(newValue.length()>4)
					txtCreditCard5.setText(oldValue);
				if(!newValue.equals("") &&(newValue.charAt(newValue.length()-1)<'0'||newValue.charAt(newValue.length()-1)>'9'))
					txtCreditCard5.setText(oldValue);
			}
		});		
		//password validation
		txtPassword.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				//checking the length
				if(newValue.length()>50)
					txtPassword.setText(oldValue);
			}
		});
		txtConfirmPassword.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// checking the length
				if(newValue.length()>50)
					txtConfirmPassword.setText(oldValue);
			}
		});
		txtNewPassword.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * if the text changed
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// checking the length
				if(newValue.length()>50)
					txtNewPassword.setText(oldValue);
			}
		});
		//checking which type of user is logged
		if(LoginController.userLogged instanceof Employee) {
			currentuser=(Employee)LoginController.userLogged;
			HandleUIFields(1);
		}	
		else
			if(LoginController.userLogged instanceof Customer) {
			 	currentcustomer=(Customer) LoginController.userLogged;
			 	HandleUIFields(0);
			 	txtCustomerID.setText(currentcustomer.getuId()+"");
			 	
			 	//opening packet
				Packet packet = new Packet();
				//adding command to the packet
				packet.addCommand(Command.getAccountByuId);
				
				//adding array list to the packet's command so the Query can get information for statement .	
				ArrayList<Object> userl=new ArrayList<>();
				userl.add(currentcustomer.getuId());
				packet.setParametersForCommand(Command.getAccountByuId, userl);
				//sending the packet
				SystemSender send = new SystemSender(packet);
				send.registerHandler(new IResultHandler() {
					/**
					 * waiting for result from the server
					 */
					@Override
					public void onWaitingForResult() {
						
					}
					/**
					 * getting result p from the server
					 */
					@Override
					public void onReceivingResult(Packet p) {
						if(p.getResultState())
						{
							//getting information from the returned result
							ArrayList<Account> acco=new ArrayList<>();
							acco=p.<Account>convertedResultListForCommand(Command.getAccountByuId);
							currentCustomerAccount=acco.get(0);
						 	searchForCustomerByID();
						}
						else
							showError("Error Loading Information , Please Try Again Later");
					}
				});
				//sending the packet
				send.start();
			}
	}
	/**
	 * 
	 * This function hides the section of the password .
	 */
	public void hidePasswordSection()
	{
		appassword.setVisible(false);
		btnCancelUpdatingPassword.setVisible(false);
		btnchangePassword.setVisible(true);
	}
	/**
	 * This function initialize the combo boxes.
	 */
	private void initComboBox()
	{
		int i=0;
		//adding the membership types
		ObservableList<String> observelistMembership,observelistStatus;
		ArrayList<String> membership = new ArrayList<>();
		for(Membership mem: memshipList)
		{
			membership.add(mem.getMembershipType().toString());
		}
		//adding the membership arraylist to the combobox
		observelistMembership = FXCollections.observableArrayList(membership);
		cbMemberShip.setItems(observelistMembership);
		//checking which membership the customer got to show him in combo box (the shown value).
		for(Membership mem: memshipList) 
		{
			//if the ids are the same , we select it 
			/*if(mem.getNum()==cList.get(0).getMembershipId())
			{
				cbMemberShip.getSelectionModel().select(i);
				break;
			}
			i++;*/
		}
		//if there is no account for the customer we should not set visible the account fields , else we show them.
		if(accList.isEmpty()==false)
		{
			//adding account status to the combo box
			ArrayList<String> AccStatus = new ArrayList<>();
			AccStatus.add(AccountStatus.Active.name());
			AccStatus.add(AccountStatus.Blocked.name());
			AccStatus.add(AccountStatus.Closed.name());
			observelistStatus = FXCollections.observableArrayList(AccStatus);
			cbStatus.setItems(observelistStatus);
			//checking which status the customer got to show him in combo box (the shown value).
			if(accList.get(0).getAccountStatus().toString().equals("0"))
				cbStatus.getSelectionModel().select(0);
			else if(accList.get(0).getAccountStatus().toString().equals("1"))
				cbStatus.getSelectionModel().select(1);
			else if(accList.get(0).getAccountStatus().toString().equals("2"))
				cbStatus.getSelectionModel().select(2);
		}
		else//there is no account for this user , we hide account fields
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
	/**
	 * This function search for the customer by his id
	 */
	public void searchForCustomerByID()
	{
		if(txtCustomerID.getText().isEmpty()) {
			showError("Please Insert Customer's ID");
			return;
		}
		//opening packet
		Packet packet = new Packet();
		//adding command to the packet
		packet.addCommand(Command.getCustomersKeyByuId);
		
		//adding array list to the packet's command so the Query can get information for statement .	
		ArrayList<Object> userl=new ArrayList<>();
		userl.add(Integer.parseInt(txtCustomerID.getText()));
		packet.setParametersForCommand(Command.getCustomersKeyByuId, userl);
		
		
		//sending the packet
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			/**
			 * waiting for result from the server
			 */
			@Override
			public void onWaitingForResult() {
				
			}
			/**
			 * getting the result p from the server
			 */
			@Override
			public void onReceivingResult(Packet p) {
				//getting the result from the Query
				cList=p.<Customer>convertedResultListForCommand(Command.getCustomersKeyByuId);
				
				//if there is no customer with this id , we show error
				if(cList.isEmpty())
				{
					showError("Customer Dose Not Exist");
					txtCustomerID.setText("");
					return;
				}
				
				//opening packet
				Packet packet = new Packet();
				//adding commands to the packet
				packet.addCommand(Command.getAccountbycIDandBranch);
				packet.addCommand(Command.getUserByuId);
				packet.addCommand(Command.getMemberShip);
				//adding array list to the packet's command so the Query can get information for statement .	
				ArrayList<Object> accl=new ArrayList<>();
				accl.add(cList.get(0).getId());
				if(currentuser!=null)
					accl.add(currentuser.getBranchId());
				else
					accl.add(currentCustomerAccount.getBranchId());
				packet.setParametersForCommand(Command.getAccountbycIDandBranch, accl);
				//adding array list to the packet's command so the Query can get information for statement .	
				ArrayList<Object> cusl=new ArrayList<>();
				cusl.add(Integer.parseInt(txtCustomerID.getText()));
				packet.setParametersForCommand(Command.getUserByuId, cusl);
				
				//sending the packet
				SystemSender send = new SystemSender(packet);
				send.registerHandler(new IResultHandler() {
					/**
					 * waiting for result from the server
					 */
					@Override
					public void onWaitingForResult() {						
					}
					/**
					 * getting the result p from the server
					 */
					@Override
					public void onReceivingResult(Packet p) {
						//getting the result from the Query
						accList=p.<Account>convertedResultListForCommand(Command.getAccountbycIDandBranch);
						uList=p.<User>convertedResultListForCommand(Command.getUserByuId);
						memshipList=p.<Membership>convertedResultListForCommand(Command.getMemberShip);
						
						//handle fields after the result
						btnSearch.setDisable(true);
						apnextinfo.setVisible(true);
						initComboBox();
						txtUser.setText(uList.get(0).getUser());
						//check if there is account for this customer;
						if(accList.isEmpty()==false)
						{
							//adding information to the fields
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
						//opening packet
						Packet packet = new Packet();
						//adding commands to the packet
						packet.addCommand(Command.getMemberShipAccountByAcNum);
						ArrayList<Object> info=new ArrayList<>();
						info.add(accList.get(0).getNum());
						packet.setParametersForCommand(Command.getMemberShipAccountByAcNum, info);
						//sending the packet
						SystemSender send = new SystemSender(packet);
						send.registerHandler(new IResultHandler() {
							/**
							 * waiting for result from the server
							 */
							@Override
							public void onWaitingForResult() {
								
							}
							/**
							 * getting the result p from the server
							 */
							@Override
							public void onReceivingResult(Packet p) {
								//getting the information from the packet
								memshipAccount=p.<MemberShipAccount>convertedResultListForCommand(Command.getMemberShipAccountByAcNum);
								if(p.getResultState())
								{
									// if the result status is true , we can get the  membership from the returned information .
									for(Membership mem:memshipList)
									{
										//if the memberhsip number are the same 
										if(mem.getNum()==memshipAccount.get(0).getmId())
										{
											cbMemberShip.getSelectionModel().select(mem.getNum()-1);
											break;
										}
									}
								}
								else
									showError("Error Loading Information , Please Try Again Later");			
							}
						});
						//sending the package
						send.start();				
				}
				});		
				//sending the package
				send.start();
				}
		});
		//sending the package
		send.start();
	}
	/**
	 * This function saves the new information from the fields to the customer .
	 */
	public void saveNewCustomerInformation()
	{
		//validate user
		if(txtUser.getText().isEmpty())//check if the user field is empty
		{
			showError("Please Enter New User");
			return;
		}
		//validate credit card fields that they are not empty
		if(txtCreditCard1.getText().isEmpty()||txtCreditCard2.getText().isEmpty()||txtCreditCard3.getText().isEmpty()||txtCreditCard4.getText().isEmpty()||txtCreditCard5.getText().isEmpty())
		{
			showError("Please Enter Valid Credit Card Number");
			return;
		}
		//validate the number of digits in each section is 4 in the credit card 
		if(txtCreditCard1.getText().length()!=4||txtCreditCard2.getText().length()!=4||txtCreditCard3.getText().length()!=4||txtCreditCard4.getText().length()!=4||txtCreditCard5.getText().length()!=4)
		{
			showError("Please Insert 4 Digits For Credit Card Section");
			return;
		}
		//if we wanted to change the password , so we need to validate it
		if(appassword.isVisible())
		{
			if(txtPassword.getText().isEmpty()) //checking if the password field is empty
			{		
				showError("Please Enter The Old Password ");
				return;
			}
			if(uList.get(0).getPassword().equals(txtPassword.getText())==false)//checking that the user password and the input password are the same 
			{
				showError("Please Enter The Correct Old Password");
				return;
			}
			if(txtNewPassword.getText().isEmpty()) //checking that the new password field is not empty
			{		
				showError("Please Enter The New Password ");
				return;
			}
			if(txtConfirmPassword.getText().isEmpty()) //checking that the new confirm password field is not empty
			{		
				showError("Please Enter The New Confirm Password ");
				return;
			}
			if(txtNewPassword.getText().equals(txtConfirmPassword.getText())==false)//checking that the new password and the confirm password are the same
			{
				showError("New Password And Confirm Password Are Not Matched");
				return;
			}
		}
		//if therer is error fetching the user .
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
		//opening packet
		Packet packet = new Packet();

		//updating the information
		newuser=txtUser.getText();
		newstatus=cbStatus.getSelectionModel().getSelectedItem();
		newmembership=cbMemberShip.getSelectionModel().getSelectedItem();
		newcreditcard=txtCreditCard1.getText()+txtCreditCard2.getText()+txtCreditCard3.getText()+txtCreditCard4.getText()+txtCreditCard5.getText();
		newpassword=txtNewPassword.getText();
		
		ArrayList<Object> accl=new ArrayList<>();
		ArrayList<Object> userl=new ArrayList<>();
		ArrayList<Object> memshipacc=new ArrayList<>();
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
			if(mem.getNum()==memshipAccount.get(0).getmId())
				orginalmemship=mem.getMembershipType().toString();//getting original member ship
			if(mem.getMembershipType().toString().equals(newmembership))
				choosedmemid=mem.getNum();//getting the choosed membership id
		}
		if(orginalmemship.isEmpty()==false&&choosedmemid!=-1)//validate the membership id and field
			if(newmembership.equals(orginalmemship)==false)//he changed the membership
			{
				//adding the command and the array list for the query to use the informaiton
				packet.addCommand(Command.updateMemberShipAccountByAcNum);
				java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
				memshipacc.add(new MemberShipAccount(accList.get(0).getNum(), choosedmemid, sqlDate));
				packet.setParametersForCommand(Command.updateMemberShipAccountByAcNum, memshipacc);
			}
		
		//updating Account if there is account for the user , ther user filled the information
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
				//adding the command for updating user's account information
				packet.addCommand(Command.updateAccountsBycId);
				
				AccountStatus accstatusvar;
				if(newstatus.equals(AccountStatus.Active.name()))
					accstatusvar=AccountStatus.Active;
				else if(newstatus.equals(AccountStatus.Blocked.name()))
					accstatusvar=AccountStatus.Blocked;
				else
					accstatusvar=AccountStatus.Closed;
				//checking what changed so we can add only what changed
				switch(accountinfochanged)
				{
				case 1://only creditcard changed
					accl.add(new Account(accList.get(0).getBranchId(),accList.get(0).getCustomerId(), accList.get(0).getBalance(), accList.get(0).getAccountStatus(), newcreditcard));
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
		//sending the packet
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			/**
			 * waiting for result from the server
			 */
			@Override
			public void onWaitingForResult() {				
			}
			/**
			 * getting the result p from he server 
			 */
			@Override
			public void onReceivingResult(Packet p) {
				//checking the result
				if (p.getResultState())
				{
					JOptionPane.showMessageDialog(null, 
							"Update Success", 
			                "Success", 
			                JOptionPane.CLOSED_OPTION);
					//closing this window
						myStage.close();
					  ManagersMenuController menu = new ManagersMenuController();
					  try {
						  //returning the menu window
						menu.start(new Stage());
					} catch (Exception e) {
						ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
					}
				}
				else
					System.out.println("Fail: " + p.getExceptionMessage());
			}
		});
		//sending the package
		send.start();
	
	}
	/**
	 * This function show the password section for changing it .
	 */
	public void changePasswordNow()
	{
		appassword.setVisible(true);
		btnchangePassword.setVisible(false);
		btnCancelUpdatingPassword.setVisible(true);
	
	}
	/**
	 * This function show the error pop out with error message
	 * @param str the error message
	 */
	public void showError(String str)
	{
		JOptionPane.showMessageDialog(null, 
				str, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
	}
	/**
	 * This 
	 * @param primaryStage 
	 * @throws Exception
	 */
	public void start(Stage primaryStage)  throws Exception{
		// TODO Auto-generated method stub
		String title = "Add Account UI";
		String srcFXML = "/Customers/updateCustomerUI.fxml";
		String srcCSS = "/Customers/application.css";
		myStage=primaryStage;

		//tyrying to init the window
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
			/**
			 * handling closing the window event
			 */
			@Override
			public void handle(WindowEvent event) {
				  primaryStage.close();
				  //checking if the user is manager/branch worker / client
				   ManagersMenuController menumanager;
				   CustomerMenuController menuclient;
				if(currentuser!=null) {
					//its manager / branch manager /branch worker
					menumanager = new ManagersMenuController();
					  try {
						  menumanager.start(new Stage());
						} catch (Exception e) {
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
						}
				}
				else {
					//its the client
					menuclient=new CustomerMenuController();
					  try {
						  menuclient.start(new Stage());
						} catch (Exception e) {
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
						}		
				}				
			}
		});
	}
}
