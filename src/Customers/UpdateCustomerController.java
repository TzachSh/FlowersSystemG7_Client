package Customers;

import java.net.URL;
import java.util.ResourceBundle;

import Users.User;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

class UpdateCustomerController implements Initializable{

	public UpdateCustomerController(User user) {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	public void start(Stage primaryStage)  throws Exception{
		
			String title = "Update Customer Information";
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
