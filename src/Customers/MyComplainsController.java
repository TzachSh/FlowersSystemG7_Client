package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import PacketSender.Command;
import PacketSender.Packet;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class MyComplainsController implements Initializable{

	@FXML private ListView<Complain> cListView;
	@FXML private ArrayList<Complain> complainsList;
	@FXML private ObservableList<Complain> complainsData;
	
	public void start(Stage primaryStage) {
		
		String title = "Follow Complains";
		String srcFXML = "/Customers/MyComlainsUI.fxml";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void displayComplains()
	{
		
	}
	
	private void initData()
	{
		ArrayList<Object> paramListBranches = new ArrayList<>();
		ArrayList<Object> paramListComplains = new ArrayList<>();
		ArrayList<Object> paramListRefund = new ArrayList<>();
		
		Packet packet = new Packet();
		packet.addCommand(Command.getBranches);
		packet.addCommand(Command.getComplains);
		packet.addCommand(Command.getRefunds);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		initData();
	}
	
}
