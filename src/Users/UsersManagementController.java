package Users;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.CellRendererPane;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;

import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class UsersManagementController implements Initializable{

	@FXML private TableView<User> tblUsers;
	@FXML private TableColumn<User, String> colUser;
	@FXML private TableColumn<User, String> colPass;
	@FXML private TableColumn<User, String> colPerm;
	
	private ArrayList<User> usersList;
	private ObservableList<User> usersData;
	
	public void start(Stage primaryStage) {
		
		String title = "Users Management";
		String srcFXML = "/Users/UsersManagementUI.fxml";
		
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

	private void initData()
	{
		ArrayList<Object> paramListUsers = new ArrayList<>();
		
		Packet packet = new Packet();
		packet.addCommand(Command.getUsers);
		packet.setParametersForCommand(Command.getUsers, paramListUsers);
		
		SystemSender sender = new SystemSender(packet);
		
		sender.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{
					usersList = p.<User>convertedResultListForCommand(Command.getUsers);
					usersData = FXCollections.observableArrayList(usersList);
					initColumns(usersData);
					tblUsers.setItems(usersData);
				}
			}
		});
		sender.start();
	}
	

	private void initColumns(ObservableList<User> usersData)
	{
		colUser.setCellValueFactory(new PropertyValueFactory<User,String>("user"));
		colPass.setCellValueFactory(new PropertyValueFactory<User,String>("password"));
		//colPerm.setCellValueFactory(new PropertyValueFactory<User,String>("permission"));
		ObservableList<String> cbValues = FXCollections.observableArrayList("1", "2", "3");
		colPerm.setCellFactory(ComboBoxTableCell.forTableColumn(cbValues));
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initData();
	
	}
	
	
}
