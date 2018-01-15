package Users;

import javafx.application.Application;
import javafx.stage.Stage;

public class UserManagementUI extends Application{
		
		public static void main(String[] args) {
			launch(args);
		}

		@Override
		public void start(Stage primaryStage) throws Exception {
			// TODO Auto-generated method stub
			UsersManagementController sc = new UsersManagementController();
			sc.start(primaryStage);
		}
}
