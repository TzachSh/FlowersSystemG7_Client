package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import Branches.Branch;
import Branches.Employee;
import Login.LoginController;
import Login.ManagersMenuController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.ConstantData;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 
 * Controller
 * Handling Account managing options
 *   
 */

public class AccountController implements Initializable{
	
	@FXML
	private TextField txtID;
	@FXML
	private Button btnCheckCustomersID;
	@FXML
	private ComboBox<String> cbMemberShip;
	@FXML
	private Label lblMembership;
	@FXML
	private Label lblDiscount;
	@FXML
	private AnchorPane apAddAccount;
	@FXML
	private TextField txtDiscount;
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
	private RadioButton rbAddMemberShip;
	@FXML
	private Button btnFinish;
	/**
	 * membership name
	 */
	private String membership;
	/**
	 * membership code
	 */
	private int mId;
	/**
	 * all possible memberships
	 */
	private ArrayList<Membership> memshipList ;
	/**
	 * Active employee
	 */
	private Employee currentEmployee=(Employee)LoginController.userLogged;
	/**
	 * current customer to check
	 */
	private Customer currentCustomer;
	private Branch currentBranch=ManagersMenuController.currentBranch;
	private static Stage myStage;
	/**
	 * Initialize the combo box information
	 */
	private void initComboBox()
	{
		ObservableList<String> observelistMembership;
		//adding the membership types
		ArrayList<String> membership = new ArrayList<>();
		membership.add(MembershipType.Monthly.toString());
		membership.add(MembershipType.Yearly.toString());
		observelistMembership = FXCollections.observableArrayList(membership);
		cbMemberShip.setItems(observelistMembership);
	
		
	}
	/**
	 * show membership combo box if the radio button was selected , else it will hide it .
	 */
	public void showMemberShipComboBox()
	{
		//checking the status
		if(rbAddMemberShip.isSelected()==true) 
		{
			cbMemberShip.setVisible(true);
			lblDiscount.setVisible(true);
			lblMembership.setVisible(true);
			txtDiscount.setVisible(true);
		}
		else
		{
			cbMemberShip.setVisible(false);
			lblDiscount.setVisible(false);
			lblMembership.setVisible(false);
			txtDiscount.setVisible(false);
		}
	}
	/**
	 * initialize the settings
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initComboBox();
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
		lblDiscount.setVisible(false);
		lblMembership.setVisible(false);
		txtDiscount.setVisible(false);
	}
	

	/**
	 * add the account to the database 
	 */
	public void addAccountNow()
	{
		//checking that the credit card text fields is not empty
		if(txtCreditCard1.getText().isEmpty()||txtCreditCard2.getText().isEmpty()||txtCreditCard3.getText().isEmpty()||txtCreditCard4.getText().isEmpty()||txtCreditCard5.getText().isEmpty())
		{
			ConstantData.displayAlert(AlertType.ERROR, "Missing Field", "Credit Card", "Please Insert Valid Credit Card");
			return;
		}
		//checking if the digits is 4 in the credit card fields
		if(txtCreditCard1.getText().length()!=4||txtCreditCard2.getText().length()!=4||txtCreditCard3.getText().length()!=4||txtCreditCard4.getText().length()!=4||txtCreditCard5.getText().length()!=4)
		{
			ConstantData.displayAlert(AlertType.ERROR, "Missing Field", "Credit Card", "Please Insert 4 Digits For Credit Card Section");
			return;
		}
		if(rbAddMemberShip.isSelected()==true&&txtDiscount.getText().isEmpty())
		{
			ConstantData.displayAlert(AlertType.ERROR, "Missing Field", "MemberShip", "Please Pick MemberShip");
			return;
		}
		//getting the information
		String fullCreditCar=txtCreditCard1.getText().toString()+txtCreditCard2.getText().toString()+txtCreditCard3.getText().toString()+txtCreditCard4.getText().toString()+txtCreditCard5.getText().toString();
		//opening packet
		Packet packet = new Packet();
		//adding command and the information for the query to use them
		packet.addCommand(Command.addAccounts);
		packet.addCommand(Command.getAccountbycIDandBranch);
		ArrayList<Object> acclist=new ArrayList<>();
		acclist.add(new Account(currentBranch.getbId(), currentCustomer.getId(), mId, 0, AccountStatus.Active, fullCreditCar));
		packet.setParametersForCommand(Command.addAccounts, acclist);
		ArrayList<Object> info=new ArrayList<>();
		info.add(currentCustomer.getId());
		info.add(currentBranch.getbId());
		packet.setParametersForCommand(Command.getAccountbycIDandBranch, info);
		//sending the packet
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			@Override
			public void onWaitingForResult() {				
			}
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if (p.getResultState())
				{
					ArrayList<Account> accL=new ArrayList<>();
					accL= p.<Account>convertedResultListForCommand(Command.getAccountbycIDandBranch);
					//if the user want to add membership and allready clicked on membership radio button
					if(rbAddMemberShip.isSelected()==true)
					{
						//opening packet
						Packet packet = new Packet();
						//adding command and the information for the query to use them
						packet.addCommand(Command.addMemberShipAccount);
						ArrayList<Object> memacc=new ArrayList<>();
						java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
						memacc.add(new MemberShipAccount(accL.get(0).getNum(), mId,sqlDate));
						packet.setParametersForCommand(Command.addMemberShipAccount, memacc);
						//sending the packet
						SystemSender send = new SystemSender(packet);
						send.registerHandler(new IResultHandler() {
							/**
							 * on waiting for results from server
							 */
							@Override
							public void onWaitingForResult() {
								
							}
							/**
							 * 
							 * @param p the result from the server 
							 */
							@Override
							public void onReceivingResult(Packet p) {
								if (p.getResultState())
								{
									ConstantData.displayAlert(AlertType.INFORMATION, "Account", "Adding Account", "The Account Has Been Added With MemberShip Successfully");
									//closing the window and returning to menu
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
						//sending the packet
						send.start();
					}
					else
					{
						
						JOptionPane.showMessageDialog(null, 
								"The Account Has Been Added Without MemberShip", 
				                "Success", 
				                JOptionPane.CLOSED_OPTION);
						//closing the window and returning to menu
						myStage.close();
						 ManagersMenuController menu = new ManagersMenuController();
						  try {
							menu.start(new Stage());
						} catch (Exception e) {
							ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
						}
					}
				}
				else
					ConstantData.displayAlert(AlertType.ERROR, "Error", "Loading Error", p.getExceptionMessage());
			}
		});
		//sending the packet
		send.start();
	}
	/**
	 * check that the user exist in the database
	 */
	public void checkUserExist()
	{
		//checking that the id field is not empty
		if(txtID.getText().isEmpty()) {
			ConstantData.displayAlert(AlertType.ERROR, "Missing Field", "Customer ID", "Please Insert Customer's ID");
			return;
		}
		//setting the id field and btn to be disable
		txtID.setDisable(true);
		btnCheckCustomersID.setDisable(true);
		//openeing packet
		Packet packet = new Packet();
		//adding getting customers
		packet.addCommand(Command.getCustomersKeyByuId);
		packet.addCommand(Command.getMemberShip);
		ArrayList<Object> userl=new ArrayList<>();
		//adding the information to the packet
		userl.add(Integer.parseInt(txtID.getText()));
		packet.setParametersForCommand(Command.getCustomersKeyByuId, userl);
		//sending the packet
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
		@Override
		public void onWaitingForResult() {	
		}
		@Override
		public void onReceivingResult(Packet p) {
			// TODO Auto-generated method stub
			//customer list
			ArrayList<Customer> cList = new ArrayList<>();
			if (p.getResultState())
			{
				//filling the list from the result that server returned
				cList = p.<Customer>convertedResultListForCommand(Command.getCustomersKeyByuId);
				memshipList= p.<Membership>convertedResultListForCommand(Command.getMemberShip);
				if(cList.isEmpty())//checking that there is customer with this id
				{
					ConstantData.displayAlert(AlertType.ERROR, "Customer ID", "Customer Not Exists", "Customer Not Exist In The DataBase Please Add Customer First");
					btnCheckCustomersID.setDisable(false);
					txtID.setDisable(false);
					txtID.setText("");
					return;
				}
				//the customer exist , so we add the information
				currentCustomer=cList.get(0);
				Packet packet = new Packet();
				packet.addCommand(Command.getAccountbycIDandBranch);
				//adding the information to the packet
				ArrayList<Object> accl=new ArrayList<>();
				accl.add(currentCustomer.getId());
				accl.add(currentBranch.getbId());
				packet.setParametersForCommand(Command.getAccountbycIDandBranch, accl);
				//sending the packet
				SystemSender send = new SystemSender(packet);
				send.registerHandler(new IResultHandler() {
					@Override
					public void onWaitingForResult() {
						
					}
					@Override
					public void onReceivingResult(Packet p) {
						ArrayList<Account> accList = new ArrayList<>();
						if (p.getResultState())
						{
							//checking if the customer got account
							accList=p.<Account>convertedResultListForCommand(Command.getAccountbycIDandBranch);
							if(!accList.isEmpty())
							{
								ConstantData.displayAlert(AlertType.ERROR, "Customer Account", "Customer Not Exists", "Customer Already Have Account");
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
				//sending the packet
				send.start();			
			}
			else
				System.out.println("Fail: " + p.getExceptionMessage());	
		}
		});
		//sending the packet
		send.start();
	}
	/**
	 * getting the discount and the membership type once clicked on combo box
	 */
	public void getMemberShipTypeDiscount()
	{
		int index,i;
		//getting the selected membership index
		index=cbMemberShip.getSelectionModel().getSelectedIndex();
		membership=MembershipType.values()[index].toString();
		//finding the membership in the membership list
		for(i=0;i<memshipList.size();i++)
		{
			if(memshipList.get(i).getMembershipType().toString().equals(membership)) {
				//setting the discount value to the text field
				txtDiscount.setText(""+memshipList.get(i).getDiscount()+"%");
				mId=memshipList.get(i).getNum();
			}
		}	
	}
	/**
	 * This function  initialize the window
	 * @param primaryStage - current stage to build
	 * @throws Exception error message
	 */
	public void start(Stage primaryStage)  throws Exception{
		// TODO Auto-generated method stub
		myStage=primaryStage;
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
				//lunching managers menu 
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

}
