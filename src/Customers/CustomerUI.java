package Customers;

import Products.CatalogProductController;
import javafx.application.Application;
import javafx.stage.Stage;

public class CustomerUI extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
	CustomerController customerController = new CustomerController();
		customerController.start(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
