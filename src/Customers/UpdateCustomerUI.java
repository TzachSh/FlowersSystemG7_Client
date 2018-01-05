package Customers;

import Users.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class UpdateCustomerUI extends Application {

	public User user=null;
	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		UpdateCustomerController updateCustomerController = new UpdateCustomerController(user);
		//UpdateCustomerController updateCustomerController = new UpdateCustomerController();
		updateCustomerController.start(arg0);

	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
