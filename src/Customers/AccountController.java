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
	private Employee currentUser;
	
	
	public AccountController(User user)
	{
		this.currentUser=(Employee)user;
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	
		
	}

	
	
	public void checkUserExist()
	{
		Packet packet = new Packet();
		
		packet.addCommand(Command.getCustomersKeyByuId);
		ArrayList<Object> userl=new ArrayList<>();
		userl.add(new User(Integer.parseInt(txtID.getText())));
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
					return;
				}
				apAddAccount.setVisible(true);
				Customer currentCustomer=cList.get(0);

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
