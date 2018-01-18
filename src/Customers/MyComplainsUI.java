package Customers;

import Users.Permission;
import javafx.application.Application;
import javafx.stage.Stage;

public class MyComplainsUI extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
	MyComplainsController mc = new MyComplainsController();
	mc.customer = new Customer(1,1,1);
	mc.start(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

}