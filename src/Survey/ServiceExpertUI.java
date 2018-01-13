package Survey;

import Branches.CustomerService;
import Branches.Employee;
import Branches.Role;
import Branches.ServiceExpert;
import Users.Permission;
import javafx.application.Application;
import javafx.stage.Stage;

public class ServiceExpertUI extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub

		Employee serviceExpert = new ServiceExpert(1,"user1","123,",true,Permission.Limited,1,Role.CustomerService,1,null);
		ServiceExpertController sc = new ServiceExpertController();
		sc.serviceExpert = serviceExpert;
		sc.start(arg0);
	}
}
