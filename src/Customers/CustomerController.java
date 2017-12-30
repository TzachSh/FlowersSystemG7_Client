package Customers;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Users.Permission;
import javax.swing.JOptionPane;

import PacketSender.Packet;
import javafx.application.Platform;
import javafx.beans.Observable;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.invoke.util.BytecodeName;

public class CustomerController implements Initializable {
	
	private String user,password,confirmpassword,creditcard,status,membership,discount,permission;
	
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
	
	@FXML
	private ComboBox<String> cbMemberShip;
	public void start(Stage primaryStage) throws Exception {

		String title = "Add Customer UI";
		String srcFXML = "/Customers/addCustomerUI.fxml";
		String srcCSS = "/Customers/application.css";
		
		//initial text fields and buttons 
		btnRegister=new Button();
		btnCancel=new Button();
		btnFinish=new Button();
		btnAddCreditCard=new Button();
		btnAddCreditCard=new Button();
		txtDiscount=new TextField();
		txtUser=new TextField();
		txtPassword=new TextField();
		txtConfirmPassword=new TextField();
		txtCreditCard=new TextField();
		anchorpane1=new AnchorPane();
		anchorpane2=new AnchorPane();
		
		

	
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
	private void initComboBox()
	{
		ObservableList<String> observelistAccountStatus,observelistMembership,observePermission;
		ArrayList<String> accountstatus = new ArrayList<>();
		accountstatus.add(AccountStatus.Active.toString());
		accountstatus.add(AccountStatus.Blocked.toString());
		accountstatus.add(AccountStatus.Closed.toString());
		observelistAccountStatus = FXCollections.observableArrayList(accountstatus);
		cbStatus.setItems(observelistAccountStatus);
		cbStatus.getSelectionModel().select(0);
		
		ArrayList<String> membership = new ArrayList<>();
		membership.add(MembershipType.Normal.toString());
		membership.add(MembershipType.Monthly.toString());
		membership.add(MembershipType.Yearly.toString());
		observelistMembership = FXCollections.observableArrayList(membership);
		cbMemberShip.setItems(observelistMembership);
		cbMemberShip.getSelectionModel().select(0);
		
		ArrayList<String> permission = new ArrayList<>();
		permission.add(Permission.Administrator.toString());
		permission.add(Permission.Limited.toString());
		permission.add(Permission.Blocked.toString());
		permission.add(Permission.Client.toString());
		observePermission = FXCollections.observableArrayList(permission);
		cbPermission.setItems(observePermission);
		cbPermission.getSelectionModel().select(3);
		
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
			JOptionPane.showMessageDialog(null, 
                    "Please Fill Credit Card Number", 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		creditcard=txtCreditCard.getText();
		txtCreditCard.setDisable(true);
		btnAddCreditCard.setDisable(true);
		
	}
	private void getInputFromComboBox()
	{
		int index;
		index=cbStatus.getSelectionModel().getSelectedIndex();
		status=AccountStatus.values()[index].toString();
		index=cbMemberShip.getSelectionModel().getSelectedIndex();
		membership=MembershipType.values()[index].toString();
		index=cbPermission.getSelectionModel().getSelectedIndex();
		permission=Permission.values()[index].toString();
	}
	
	public void registerNow()
	{
		if(txtDiscount.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, 
                    "Please Fill Discount", 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
			return;

		}
		
		discount=txtDiscount.getText();
		//getting the status and memebrship from comboboxes
		getInputFromComboBox();
		
		

		
	}
	public void registerNextInformation()
	{
		
		if(txtPassword.getText().isEmpty()||txtUser.getText().isEmpty()||txtConfirmPassword.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, 
                    "Please Fill All Information", 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		user=txtUser.getText();
		password=txtPassword.getText();
		confirmpassword=txtConfirmPassword.getText();
		//disapling old information
		txtUser.setDisable(true);
		txtConfirmPassword.setDisable(true);
		txtPassword.setDisable(true);
		btnRegister.setDisable(true	);
		//show additional information
		anchorpane2.setVisible(true);
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Packet packet = new Packet();
		initComboBox();
		
	}
	
}
