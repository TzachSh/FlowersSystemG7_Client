package Customers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class ReplyController implements Initializable {
	@FXML
	private Button btnSend;
	@FXML
	private Button btnCancel;
	@FXML
	private TextField txtTitle;
	@FXML
	private TextArea txtDesc;
	@FXML
	private TextArea txtReply;
	@FXML
	private TextField txtRefund;
	
	private Complain complain;
	
	public void setComplain(Complain complain)
	{
		this.complain = complain;
	}
	
	public void start() {
		String title = "Replyment";
		String srcFXML = "/Customers/ReplyToComplain.fxml";

		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource(srcFXML));
			fxmlLoader.setController(this);

			Scene scene = new Scene(fxmlLoader.load());
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	@FXML
	private void handleCancelPressed(Event event)
	{
		((Node) event.getSource()).getScene().getWindow().hide();
		ComplainsController complainsController = new ComplainsController();
		complainsController.start(new Stage());
		
	}

	public void setComponents(Complain complain)
	{
		txtTitle.setText(complain.getTitle());
		txtDesc.setText(complain.getDetails());
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	
	}
}
