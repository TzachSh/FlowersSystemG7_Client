package Login;

import javafx.application.Application;
import javafx.stage.Stage;

public class LoginApp extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		LoginController loginController = new LoginController();
		loginController.start(arg0);
	}

}
