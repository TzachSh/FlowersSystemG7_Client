package Branches;

import Customers.ComplainsController;
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
		ReportsController reportsController = new ReportsController();
		reportsController.start(arg0);
	}

}
