package Users;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.CellRendererPane;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;

import Customers.Complain;
import Customers.ComplainsController;
import Customers.Customer;
import Customers.ReplyController;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
	@FXML private Button btnSearch;
	@FXML private TextField txtUid;
	
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

	private void displayUsers()
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
					
				private ComboBox<Permission> cmbPerms;
				private TextField txtUser;
				private TextField txtPassword;
				private CheckBox cbLogged;
				private Button btnDelete;
				private Button btnSave;
					
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
					String textPassword = "Password  ";
					String textPerm="Permission ";
					String textLogged="Is Logged ";
					
				    cmbPerms = createPermsComboBox();
					txtUser = new TextField(user.getUser());
					txtPassword = new TextField(user.getPassword());
					cbLogged = new CheckBox();
					btnDelete = createDeleteButtonHandler(user);
					btnSave = createUpdateButtonHandler(user);
					
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
							// Handle user delete
							 Alert alert = new Alert(Alert.AlertType.WARNING);
					      		alert.setTitle("Delete User");
					      		alert.setContentText("Are you Sure?");
					      		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
					      		ButtonType noButton = new ButtonType("No", ButtonData.NO);
					      		
					      		alert.getButtonTypes().setAll(okButton, noButton);
					      		alert.showAndWait().ifPresent(type -> {
					      		        if (type == okButton)
					      		        {
					      		        	deleteUser(user);
					      		        } 
					      		        else
					      		        {
					      		        	alert.close();
					      		        }
					      		});
						});

						return btnDel;
				}
				
				private Button createUpdateButtonHandler(User user)
				{
					
						String textUpdate = "Update";
						Button btnUpdate;

						btnUpdate = new Button(textUpdate);
						btnUpdate.setOnMouseClicked((event) -> {
							// Handle update user
							updateUser(user,txtUser.getText(),txtPassword.getText(),cbLogged.isSelected(),cmbPerms.getSelectionModel().getSelectedItem());
						});

						return btnUpdate;
				}
				
				private void updateUser(User user, String userName, String password, boolean isLogged, Permission permission)
				{
					int uId = user.getuId();
					user = new User(uId, userName, password, isLogged, permission);
					
					Packet packet = new Packet();
					
					ArrayList<Object> paramList = new ArrayList<>();
					paramList.add(user);
					
					packet.addCommand(Command.updateUserByuId);
					packet.setParametersForCommand(Command.updateUserByuId, paramList);
					
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
								Alert alert = new Alert(AlertType.INFORMATION,"User updated successfully");
								alert.show();
								displayUsers();
							}
						}
					});
					sender.start();

				}
				
				private void deleteUser(User user)
				{
					Packet packet = new Packet();
					
					ArrayList<Object> paramList = new ArrayList<>();
					paramList.add(user);
					
					packet.addCommand(Command.deleteUser);
					packet.setParametersForCommand(Command.deleteUser, paramList);
					
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
								Alert alert = new Alert(AlertType.INFORMATION,"User deleted successfully");
								alert.show();
								displayUsers();
							}
						}
					});
					sender.start();
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
					 else
						 setGraphic(null);
				}};
			}
		});
	}

	@FXML
	private void handleSearchPressed(Event event) {

		if (txtUid.getText().isEmpty()) {
			System.out.println("Enter user ID!");
			return;
		}

		int uId = Integer.parseInt(txtUid.getText());
		
		ArrayList<User> toLoad = new ArrayList<>();
		toLoad.add(getUserByUid(uId));
		usersData = FXCollections.observableArrayList(toLoad);
		uListView.setItems(usersData);
	}
	
	private User getUserByUid(int uId)
	{
		User retUser = null;
		for(User user : usersList)
			if(user.getuId() == uId)
				retUser = user;
		
		return retUser;
	}
	
	@FXML
	private void setSearchOnTextChange()
	{
		txtUid.textProperty().addListener((observable, oldValue, newValue) -> {
		   if(newValue.isEmpty())
		   {
			   usersData = FXCollections.observableArrayList(usersList);
			   uListView.setItems(usersData);
		   }
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		setSearchOnTextChange();
		setListCellFactory();
		displayUsers();
	}
	
	
}
