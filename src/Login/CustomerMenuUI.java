package Login;

import javafx.application.Application;
import javafx.stage.Stage;

public class CustomerMenuUI  extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		CustomerMenuController menu = new CustomerMenuController();
		menu.start(arg0);
	}

}
