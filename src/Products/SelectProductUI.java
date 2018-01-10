package Products;

import Branches.Employee;
import Branches.Role;
import Customers.Customer;
import Users.Permission;
import Users.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class SelectProductUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		SelectProductController selectController = new SelectProductController();
		Customer customer = new Customer(1, 2, 1);
		//selectController.setForUpdateSale(new Employee(1, "t", "1234", true, Permission.Administrator, 1, Role.BranchManager, 2));
		selectController.setForViewingCatalog(customer);
		//selectController.setForUpdateCatalog();
		selectController.start(arg0);
	}
}
