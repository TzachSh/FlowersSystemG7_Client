package Customers;

import Branches.Employee;
import Branches.Role;
import javafx.application.Application;
import javafx.stage.Stage;

public class AccountUI extends Application{

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		//AccountController accountController = new AccountController(new Employee(2, 3, Role.Branch, 1));
		AccountController accountController = new AccountController();

		accountController.start(arg0);

	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
