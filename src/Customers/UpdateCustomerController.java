package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Branches.Branch;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UpdateCustomerController implements Initializable {
	/**the current user*/
	public User user;
	/**account list*/
	private ArrayList<Account> accList ;
	/**user list*/
	private ArrayList<User> uList;
	/**membership list*/
	private ArrayList<Membership> memshipList ;
	/**membership account list*/
	private ArrayList<MemberShipAccount> memshipAccount ;
	/**customer list*/
	private ArrayList<Customer> cList ;
	private Branch currentBranch;
	@FXML
	private Label lbHeader;
	@FXML
	private Label lbStatus;
	@FXML
	private Label lblmembership;
	@FXML
	private Label lbBalance;
	@FXML
	private Label lbCreditCard;
	@FXML
	private RadioButton rbdeleteMemberShip;
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
	private RadioButton rbMemberShip;
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
	/**the current employee */
	private static Employee currentuser;
	/**the current customer*/
	private static Customer currentcustomer;
	/**the current customer account*/
	private Account currentCustomerAccount;
	/** the current stage */
	private static Stage myStage;

	
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
				rbMemberShip.setVisible(false);
				rbdeleteMemberShip.setVisible(false);
				lbHeader.setText("Client Information");
		 }
	 }
	 /**deleting membership from account*/
	public void deleteMemberShipFromAccount()
	{
		if(rbdeleteMemberShip.isVisible()&&rbdeleteMemberShip.isSelected())
		{
			cbMemberShip.setVisible(false);
			lblmembership.setVisible(false);
		}
		if(rbdeleteMemberShip.isVisible()&&rbdeleteMemberShip.isSelected()==false) 
		{
			cbMemberShip.setVisible(true);
			lblmembership.setVisible(true);
		}
		
	}
	 /**
	 * This function show membership combo box if the radio button was selected , else it will hide it .
	 */
	public void showMemberShipComboBox()
	{
		//checking the status
		if(rbMemberShip.isSelected()==true) 
		{
			cbMemberShip.setVisible(true);
			lblmembership.setVisible(true);
		}
		else
		{
			cbMemberShip.setVisible(false);
			lblmembership.setVisible(false);
		}
	}
	 /**
	  * This function inistialize the settings
	  */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		if(LoginController.userLogged instanceof Employee)
			currentBranch=ManagersMenuController.currentBranch;
		else
			if(LoginController.userLogged instanceof Customer)
				currentBranch=CustomerMenuController.currentBranch;
			else
				ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to get branch id", null);

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
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Error Loading Information , Please Try Again Later", null);

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
		cbMemberShip.getSelectionModel().selectFirst();

		//if there is no account for the customer we should not set visible the account fields , else we show them.
		if(accList.isEmpty()==false)
		{
			//adding account status to the combo box
			ArrayList<String> AccStatus = new ArrayList<>();
			AccStatus.add(AccountStatus.Active.name());
			AccStatus.add(AccountStatus.Blocked.name());
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
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Insert Customer's ID", null);
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
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Customer Dose Not Exist", null);
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
					accl.add(currentBranch.getbId());
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
						txtCustomerID.setDisable(true);
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
									// if the result status is true , we can get the  membership from the returned information .
									if(p.getResultState())
									{
										//getting the information from the packet
										memshipAccount=p.<MemberShipAccount>convertedResultListForCommand(Command.getMemberShipAccountByAcNum);
										if(memshipAccount.isEmpty()==false)
										{
											cbMemberShip.setVisible(true);
											lblmembership.setVisible(true);
											rbMemberShip.setVisible(false);
											rbdeleteMemberShip.setVisible(false);
											for(Membership mem:memshipList)
											{
												//if the memberhsip number are the same 
												if(mem.getNum()==memshipAccount.get(0).getmId())
												{
													cbMemberShip.getSelectionModel().select(mem.getNum()-1);
													
													break;
												}
											}
											if(LoginController.userLogged instanceof Employee)
											{
												rbMemberShip.setVisible(false);
												rbdeleteMemberShip.setVisible(true);
											}
											
										}
										else
										{
											rbdeleteMemberShip.setVisible(false);
											cbMemberShip.setVisible(false);
											lblmembership.setVisible(false);
											rbMemberShip.setVisible(false);

											if(LoginController.userLogged instanceof Employee)
											{
												rbMemberShip.setVisible(true);
												rbdeleteMemberShip.setVisible(false);

											}
										}
										
									}
									else
										ConstantData.displayAlert(AlertType.ERROR, "Error", "Error Loading Information , Please Try Again Later", null);
		
								}
							});
							//sending the package
							send.start();	
						}
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
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Enter New User", null);
			return;
		}
		//validate credit card fields that they are not empty
		if(txtCreditCard1.getText().isEmpty()||txtCreditCard2.getText().isEmpty()||txtCreditCard3.getText().isEmpty()||txtCreditCard4.getText().isEmpty()||txtCreditCard5.getText().isEmpty())
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Enter Valid Credit Card Number", null);
			return;
		}
		//validate the number of digits in each section is 4 in the credit card 
		if(txtCreditCard1.getText().length()!=4||txtCreditCard2.getText().length()!=4||txtCreditCard3.getText().length()!=4||txtCreditCard4.getText().length()!=4||txtCreditCard5.getText().length()!=4)
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Insert 4 Digits For Credit Card Section", null);
			return;
		}
		//if we wanted to change the password , so we need to validate it
		if(appassword.isVisible())
		{
			if(txtPassword.getText().isEmpty()) //checking if the password field is empty
			{		
				ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Enter The Old Password ", null);
				return;
			}
			if(uList.get(0).getPassword().equals(txtPassword.getText())==false)//checking that the user password and the input password are the same 
			{
				ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Enter The Correct Old Password", null);
				return;
			}
			if(txtNewPassword.getText().isEmpty()) //checking that the new password field is not empty
			{		
				ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Enter The New Password ", null);
				return;
			}
			if(txtConfirmPassword.getText().isEmpty()) //checking that the new confirm password field is not empty
			{		
				ConstantData.displayAlert(AlertType.ERROR, "Error", "Please Enter The New Confirm Password  ", null);
				return;
			}
			if(txtNewPassword.getText().equals(txtConfirmPassword.getText())==false)//checking that the new password and the confirm password are the same
			{
				ConstantData.displayAlert(AlertType.ERROR, "Error", "New Password And Confirm Password Are Not Matched", null);
				return;
			}
		}
		//if therer is error fetching the user .
		if(uList.isEmpty())
		{
			ConstantData.displayAlert(AlertType.ERROR, "Error", "Error Loading Customer , Please Try Again", null);
			return;
		}
		
		String newuser,newstatus,newmembership,newcreditcard,newpassword;
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
		
		//updating membership for this account 
		if(cbMemberShip.isVisible()==true)
		{
			for(Membership mem:memshipList)//getting the orginal membership name (we got only membership id)
			{
				if(mem.getMembershipType().toString().equals(newmembership))
					choosedmemid=mem.getNum();//getting the choosed membership id
			}
			//adding the command and the array list for the query to use the informaiton , if he got already membership , we just update it 
			if(memshipAccount.isEmpty()==false)
			{
				packet.addCommand(Command.updateMemberShipAccountByAcNum);
				java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
				memshipacc.add(new MemberShipAccount(accList.get(0).getNum(), choosedmemid, sqlDate));
				packet.setParametersForCommand(Command.updateMemberShipAccountByAcNum, memshipacc);
			}
			else
			{
				//he dont have membership account , we must insert new membership
				packet.addCommand(Command.addMemberShipAccount);
				java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
				memshipacc.add(new MemberShipAccount(accList.get(0).getNum(), choosedmemid, sqlDate));
				packet.setParametersForCommand(Command.addMemberShipAccount, memshipacc);
			}
				
		}
		if(rbdeleteMemberShip.isVisible()&&rbdeleteMemberShip.isSelected())
		{
			//adding commands to the packet
			packet.addCommand(Command.deleteMemberShipAccountByacNum);
			ArrayList<Object> info=new ArrayList<>();
			info.add(new MemberShipAccount(accList.get(0).getNum()));
			packet.setParametersForCommand(Command.deleteMemberShipAccountByacNum, info);
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
				else 
					accstatusvar=AccountStatus.Blocked;

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
					ConstantData.displayAlert(AlertType.CONFIRMATION, "Success", "Update Success", null);

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
			primaryStage.setResizable(false);
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
