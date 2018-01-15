package Login;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServiceMenuController 
{

	private static LoginController loginController;
	    
	    
	public void setLoginController(LoginController login)
	{
	   loginController = login;
	}
	    
	public void onClickingSurveyManagment()
	{
		
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
	
	public void start(Stage primaryStage) throws Exception {
		String title = "Main Menu";
		String srcFXML = "/Login/ServiceMenu.fxml";
		
	
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			
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
}
