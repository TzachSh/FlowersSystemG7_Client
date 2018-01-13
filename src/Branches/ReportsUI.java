package Branches;

import Customers.ComplainsController;
import Users.Permission;
import javafx.application.Application;
import javafx.stage.Stage;

public class ReportsUI extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		//ReportsController reportsController = new ReportsController(new Employee(2, "user2", "1", true, Permission.Administrator, 1, Role.BranchManager, 1));
		ReportsController reportsController = new ReportsController(new Employee(2, "user2", "1", true, Permission.Administrator, 1, Role.BranchesManager, 1));
		reportsController.start(arg0);
	}

}
