package Survey;
import Branches.Employee;
import Branches.Role;
import Users.Permission;
import javafx.application.Application;
import javafx.stage.Stage;


public class AnswerSurveyUI extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub

		Employee branchEmployee = new Employee(1,"user1","123,",true,Permission.Limited,1,Role.Branch,1);
		AnswerSurveyController sc = new AnswerSurveyController();
		sc.branchEmployee = branchEmployee;
		sc.start(arg0);
	}
}
