package Users;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.CellRendererPane;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;

import Customers.Complain;
import Customers.Customer;
import Customers.ReplyController;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import sun.security.util.Password;

public class UsersManagementController implements Initializable{

	@FXML private ListView<User> uListView;
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
					uListView.setItems(usersData);
				}
			}
		});
		sender.start();
	}
	
	
	private void setListCellFactory()
	{
		uListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
			
			/**
			 * Create handler for each cell of the list view
			 */
			@Override
			public ListCell<User> call(ListView<User> param) {
				// TODO Auto-generated method stub
				return new ListCell<User>() {
					
				private ComboBox<Permission> createPermsComboBox()
				{
					ComboBox<Permission> cmbPerm = new ComboBox<>();
					cmbPerm.getItems().addAll(Permission.Administrator,Permission.Limited,Permission.Blocked);
					return cmbPerm;
				}
					
				/**
				 * 	Set handler for each row, is a corresponding to the status of the complain, if it's active will show a "Reply" button near to it, else will be shown "Done"
				 * @param complain - show the complain's details in the fields such as date , subject and content
				 */
				private void setCellHandler(User user)
				{
					String textUid = "User Id: ";
					String textUserName = "Username ";
					String textPassword = "Password ";
					String textPerm="Permission ";
					String textLogged="Is Logged ";
					
					ComboBox<Permission> cmbPerms = createPermsComboBox();
					TextField txtUser = new TextField(user.getUser());
					TextField txtPassword = new TextField(user.getPassword());
					CheckBox cbLogged = new CheckBox();
					Button btnDelete = createDeleteButtonHandler(user);
					Button btnSave = createSaveButtonHandler(user);
					
					cbLogged.setSelected(user.isLogged());
					setSelectedPermission(user, cmbPerms);
					HBox uIdElement = new HBox(new Label(textUid), new Text(String.format("%s", user.getuId())));
					HBox userNameElement = new HBox(new Label(textUserName), txtUser);
					HBox passwordElement = new HBox (new Label(textPassword) , txtPassword);
					VBox userDetailsElement = new VBox(userNameElement,passwordElement);
					HBox cmbElement = new HBox(cmbPerms);
					HBox permissionElement = new HBox (new Label(textPerm) , cmbElement);
					HBox loggedElement = new HBox (new Label(textLogged) , cbLogged);
					HBox detailsElements = new HBox(uIdElement,userDetailsElement);
					HBox saveElement = new HBox(btnSave);
					HBox deleteElement = new HBox(btnDelete);
					HBox buttonsElement = new HBox(saveElement,deleteElement);
					Region r = new Region();
				    HBox.setHgrow(r, Priority.ALWAYS);
					HBox managementElements = new HBox(loggedElement,permissionElement,r,buttonsElement);
					
					VBox vBox = new VBox(detailsElements,new Separator(), managementElements);
					cmbElement.setPadding(new Insets(0,10,5,5));
					uIdElement.setPadding(new Insets(5,10,5,0));
					userNameElement.setPadding(new Insets(5,10,5,20));
					passwordElement.setPadding(new Insets(5,10,5,20));
					permissionElement.setPadding(new Insets(5,10,5,20));
					saveElement.setPadding(new Insets(10,10,0,20));
					deleteElement.setPadding(new Insets(10,10,0,0));
					managementElements.setPadding(new Insets(5,10,5,0));
					loggedElement.setPadding(new Insets(5,10,5,0));
					detailsElements.setPadding(new Insets(5,10,5,0));
					vBox.setStyle("-fx-border-style:solid inside;"+
                    			  "-fx-border-width:1;"+
                    			  "-fx-border-insets:5;"+
                    			  "-fx-border-radius:5;");
					vBox.setPadding(new Insets(10));
                    setGraphic(vBox);
				}
				/**
				 * Creating a button handler which is navigates to the relevant reply view for each complain
				 * @param complain - Create a new handler for this complain
				 * @return Button which handled to open a matching view of a specific complain
				 */
				private Button createDeleteButtonHandler(User user)
				{
					
						String textDelete = "Delete";
						Button btnDel;

						btnDel = new Button(textDelete);
						btnDel.setOnMouseClicked((event) -> {
							// Handle delete user
						});

						return btnDel;
				}
				
				private Button createSaveButtonHandler(User user)
				{
					
						String textSave = "Save";
						Button btnSave;

						btnSave = new Button(textSave);
						btnSave.setOnMouseClicked((event) -> {
							// Handle delete user
						});

						return btnSave;
				}
				
				private void deleteUser(User user)
				{
					Packet packet = new Packet();
					
					ArrayList<Object> paramList = new ArrayList<>();
					paramList.add(user);
					
					//packet.addCommand(Command.dele);
				}
				
				private void setSelectedPermission(User user,ComboBox<Permission> cmbPerms)
				{
						cmbPerms.getSelectionModel().select(user.getPermission());
				}

				/**
				 * Update each row of the list view by the received item
				 * @param item - the complain to show it's details
				 * @param empty - to check if the item is null or not
				 */
					@Override
				protected void updateItem(User item, boolean empty) {
					// TODO Auto-generated method stub
					super.updateItem(item, empty);
					 if (item != null) {	
						 	setCellHandler(item);
                        }
				}};
			}
		});
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		setListCellFactory();
		initData();
	
	}
	
	
}
