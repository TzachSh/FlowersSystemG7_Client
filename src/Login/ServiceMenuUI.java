package Login;

import javafx.application.Application;
import javafx.stage.Stage;

public class ServiceMenuUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		ServiceMenuController menu = new ServiceMenuController();
		menu.start(arg0);
	}
}
