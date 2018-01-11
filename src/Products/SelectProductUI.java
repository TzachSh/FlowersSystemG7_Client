package Products;

import Branches.Employee;
import Branches.Role;
import Customers.Customer;
import Users.Permission;
import Users.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class SelectProductUI extends Application {

	public static Customer customer;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		SelectProductController selectController = new SelectProductController();
		customer = new Customer(1, 2, 1);
		Employee employee = new Employee(1, "t", "1234", true, Permission.Administrator, 1, Role.BranchManager, 2);
		//selectController.setForUpdateCatalog(employee);
		//selectController.setForViewingCatalog(customer);
		selectController.setForUpdateSale(new Employee(1, "t", "1234", true, Permission.Administrator, 1, Role.BranchManager, 2));
		selectController.start(arg0);
	}
}
