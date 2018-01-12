package Survey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import Branches.CustomerService;
import Branches.Employee;
import Branches.Role;
import Products.CatalogProductController;
import Users.Permission;
import Users.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


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