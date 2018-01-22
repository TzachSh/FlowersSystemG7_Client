package Login;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import Branches.CustomerService;
import Branches.Employee;
import Branches.Role;
import Branches.ServiceExpert;
import Customers.Complain;
import Customers.ComplainsController;
import Customers.Reply;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Survey.SurveyManagementController;
import Users.Permission;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServiceMenuController implements Initializable
{
	
	private static Stage primaryStage;
	private static LoginController loginController;
	@FXML private Label lblNotification;    
	    
	public void setLoginController(LoginController login)
	{
	   loginController = login;
	}
	    
	/***
	 * Open the survey management view
	 */
	public void onClickingSurveyManagment()
	{
		primaryStage.close();
		SurveyManagementController sc = new SurveyManagementController();
		sc.start(new Stage());
		
	}
	/***
	 * Open the complains management view
	 */
	public void onClickComplainsManagement()
	{
		primaryStage.close();
		ComplainsController cc = new ComplainsController();
		cc.start(new Stage());
	}
	
	/**
	 * Event Logged out that occurs when clicking on logout
	 */
	public void performLoggedOutHandler()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Logged Out");
		alert.setContentText("Are you Sure?");
		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		
		alert.getButtonTypes().setAll(okButton, noButton);
		alert.showAndWait().ifPresent(type -> {
		        if (type == okButton)
		        {
		        	loginController.performLoggedOut(LoginController.userLogged);
		        } 
		});
	}
	
	/***
	 * Initialize a notification label with the number of the active complains
	 */
	private void initComplainsNotification()
	{
		Packet packet = new Packet();
		packet.addCommand(Command.getComplains);
		
		SystemSender sender = new SystemSender(packet);
		sender.registerHandler(new IResultHandler() {
			/**
			 * On waiting for a result
			 */
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			/***
			 * On receiving a result
			 * @param p packet with data from the server
			 */
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{
					int activeComplainsCount = 0;
					Employee curEmployee = ((Employee)LoginController.userLogged);
					
					ArrayList<Complain> allComplainsList = p.<Complain>convertedResultListForCommand(Command.getComplains);
					
					if (curEmployee.getRole() == Role.CustomerService) {

						for (Complain complain : allComplainsList)
							if (complain.isActive() && complain.getCustomerServiceId() == curEmployee.geteId())
								activeComplainsCount++;

					}
					else if(curEmployee.getRole() == Role.ServiceExpert) {
						
						for(Complain complain : allComplainsList)
							if(complain.isActive())
								activeComplainsCount++;
					}			
			
					if(activeComplainsCount > 0)
						lblNotification.setText(String.format("+%d", activeComplainsCount));
				}
				else
				{
					Alert alert = new Alert(AlertType.ERROR , p.getExceptionMessage());
					alert.show();
				}
			}
		});
		sender.start();
	}
	public void start(Stage mainStage) throws Exception {
		String title = "Main Menu";
		String srcFXML = "/Login/ServiceMenu.fxml";
		
	
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			mainStage.setTitle(title);
			mainStage.setScene(scene);
			mainStage.setResizable(false);
			mainStage.show();
			primaryStage=mainStage;
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {

		            Alert alert = new Alert(Alert.AlertType.WARNING);
		      		alert.setTitle("Logged Out");
		      		alert.setContentText("Are you Sure?");
		      		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		      		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		      		
		      		alert.getButtonTypes().setAll(okButton, noButton);
		      		alert.showAndWait().ifPresent(type -> {
		      		        if (type == okButton)
		      		        {
		      		        	loginController.performLoggedOut(LoginController.userLogged);
		      		        	System.exit(0);
		      		        } 
		      		        else
		      		        {
		      		        	we.consume();
		      		        }
		      		});
		          }
		      }); 
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		initComplainsNotification();
	}

}
