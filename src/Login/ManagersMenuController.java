package Login;

import java.net.URL;
import java.util.ResourceBundle;

import Customers.CustomerController;
import Survey.AnswerSurveyController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ManagersMenuController implements Initializable {


	@FXML
    private Hyperlink linkChangeBranch;

    @FXML
    private Hyperlink linkLogout;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnCatalog;

    @FXML
    private Button btnSurvey;

    @FXML
    private Button btnFlower;

    @FXML
    private ComboBox<String> cmbBranch;

    @FXML
    private Button btnUpdateCatalog;

    @FXML
    private Button btnAccount;

    @FXML
    private Button btnCreateClient;

	private static Stage primaryStage;
	
	
	
	private static LoginController loginController;
    
    
	public void setLoginController(LoginController login)
	{
	   loginController = login;
	}
	public void start(Stage mainStage) throws Exception {
			String title = "Main Menu";
			String srcFXML = "/Login/ManagersMenu.fxml";
			
		
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource(srcFXML));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				mainStage.setTitle(title);
				mainStage.setScene(scene);
				mainStage.setResizable(false);
				mainStage.show();
				primaryStage=mainStage;
				
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				loginController.performLoggedOut(LoginController.userLogged);
				System.exit(0);
			}
		});
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void onClickInsertSurveyResults()
	{
		AnswerSurveyController sc = new AnswerSurveyController();
		primaryStage.close();
		sc.start(new Stage());
	}
	
	public void onClickCreateCustomer()
	{
		CustomerController customerController = new CustomerController();
		try {
			primaryStage.close();
			customerController.start(new Stage());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
