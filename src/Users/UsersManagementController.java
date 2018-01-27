package Users;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter.Chain;

import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/***
 * 
 * Controller class to handle the users management logic
 *
 */
public class UsersManagementController implements Initializable{
	/***
	 * FXML components to be shown and handled during runtime
	 */
	@FXML private ListView<User> uListView;
	@FXML private Button btnSearch;
	@FXML private TextField txtUid;
	/***
	 * Get the current logged in user
	 */
	private static LoginController loginController;
	
	/***
	 * Save the current stage
	 */
	private static Stage stage;
	
	/***
	 * Lists to be updated during runtime
	 */
	private ArrayList<User> usersList;
	private ObservableList<User> usersData;
	
	/***
	 * 
	 * @param login - current logged in user data
	 */
	public void setLoginController(LoginController login)
	{
	   loginController = login;
	}
	
	/**
	 * 
	 * @param primaryStage - stage to be initialized and showed
	 */
	public void start(Stage primaryStage) {
		stage = primaryStage;
		stage.setResizable(false);
		String title = "Users Management";
		String srcFXML = "/Users/UsersManagementUI.fxml";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setTitle(title);
			stage.setScene(scene);
			stage.show();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {

		            Alert alert = new Alert(Alert.AlertType.WARNING);
		      		alert.setTitle("Logged Out");
		      		alert.setContentText("Are you Sure?");
		      		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		      		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		      		
		      		alert.getButtonTypes().setAll(okButton, noButton);
		      		alert.showAndWait().ifPresent(type -> {
		      		        if (type == okButton)
		      		        {
		      		        	loginController.performLoggedOut(LoginController.userLogged);
		      		        	System.exit(0);
		      		        } 
		      		        else
		      		        {
		      		        	we.consume();
		      		        }
		      		});
		          }
		      }); 

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/***
	 * Get all the users from the server and show all of them in the list view
	 */
	private void displayUsers()
	{
		ArrayList<Object> paramListUsers = new ArrayList<>();
		
		Packet packet = new Packet();
		packet.addCommand(Command.getUsers);
		packet.setParametersForCommand(Command.getUsers, paramListUsers);
		
		SystemSender sender = new SystemSender(packet);
		
		sender.registerHandler(new IResultHandler() {
			/**
			 * While waiting for a result
			 */
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
			}
			/**
			 * While getting a result from the server
			 */
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if(p.getResultState())
				{
					usersList = p.<User>convertedResultListForCommand(Command.getUsers);
					usersData = FXCollections.observableArrayList(usersList);
					usersData.removeIf((User user) -> user.getuId() == LoginController.userLogged.getuId());
					uListView.setItems(usersData);
				}
				else {
					Alert alert = new Alert(AlertType.ERROR,p.getExceptionMessage());
					alert.show();
				}
			}
		});
		sender.start();
	}
	
	/***
	 * Set a cell handler for every cell in the list view
	 */
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
				
				/***
				 * Components to be updated
				 */
				private ComboBox<Permission> cmbPerms;
				private TextField txtUser;
				private TextField txtPassword;
				private CheckBox cbLogged;
				private Button btnDelete;
				private Button btnSave;
				
				/***
				 * 
				 * @return ComboBox with all of the permissions
				 */
				private ComboBox<Permission> createPermsComboBox()
				{
					ComboBox<Permission> cmbPerm = new ComboBox<>();
					cmbPerm.getItems().addAll(Permission.Administrator,Permission.Limited,Permission.Blocked);
					return cmbPerm;
				}
					
				/***
				 * 
				 * @param user to show his information in the components, such as user id, username, password, permission and his logged in state.
				 * Then create a button to handle each user deletion or update
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
			
				/***
				 * Creating a button to handle user deletion
				 * @param user to be deleted 
				 * @return Button which can delete the relevant user
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
				
				/***
				 * Create a button that handles an update of a user
				 * @param user to be updated
				 * @return Button which can update the specific user's information
				 */
				private Button createUpdateButtonHandler(User user)
				{
					
						String textUpdate = "Update";
						Button btnUpdate;

						btnUpdate = new Button(textUpdate);
						btnUpdate.setOnMouseClicked((event) -> {
							// Handle update user
							updateUser(user,txtUser.getText().replaceAll("'", "\\'"),txtPassword.getText().replaceAll("'", "\\'"),cbLogged.isSelected(),cmbPerms.getSelectionModel().getSelectedItem());
						});

						return btnUpdate;
				}
				/***
				 * Update a specific user with the inserted details in the FXML components.
				 * 
				 * @param user to be updated
				 * @param userName
				 * @param password
				 * @param isLogged
				 * @param permission
				 * Then updates the user and save the data in the server's database
				 */
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
						/***
						 * While waiting for result from the server
						 */
						@Override
						public void onWaitingForResult() {
							// TODO Auto-generated method stub
							
						}
						/***
						 * While getting a result from the server
						 */
						@Override
						public void onReceivingResult(Packet p) {
							// TODO Auto-generated method stub
							if(p.getResultState())
							{
								Alert alert = new Alert(AlertType.INFORMATION,"User updated successfully");
								alert.show();
								displayUsers();
							}
							else
							{
								Alert alert;
								if(p.getExceptionMessage().toLowerCase().contains("duplicate")) {
									alert = new Alert(AlertType.ERROR,"Username already exists");
									alert.show();
								}
								else
								{
									alert = new Alert(AlertType.ERROR,p.getExceptionMessage());
									alert.show();
								}
							}
						}
					});
					sender.start();

				}
				/***
				 * 
				 * @param user to be deleted.
				 * Delete the specific user from the server's database
				 */
				private void deleteUser(User user)
				{
					Packet packet = new Packet();
					
					ArrayList<Object> paramList = new ArrayList<>();
					paramList.add(user);
					
					packet.addCommand(Command.deleteUser);
					packet.setParametersForCommand(Command.deleteUser, paramList);
					
					SystemSender sender = new SystemSender(packet);
					sender.registerHandler(new IResultHandler() {
						/***
						 * While waiting for result from the server
						 */
						@Override
						public void onWaitingForResult() {
							// TODO Auto-generated method stub
							
						}
						/***
						 * While getting a result from the server
						 */
						@Override
						public void onReceivingResult(Packet p) {
							// TODO Auto-generated method stub
								if (p.getResultState()) {
									Alert alert = new Alert(AlertType.INFORMATION, "User deleted successfully");
									alert.show();
									displayUsers();
								}
								else {
									Alert alert = new Alert(AlertType.ERROR, p.getExceptionMessage());
									alert.show();
								}
							}
					});
					sender.start();
				}
				/***
				 * 
				 * @param user to set his permission by default
				 * @param cmbPerms to find the permission to set in
				 * Sets the relevant permission of every user
				 */
				private void setSelectedPermission(User user,ComboBox<Permission> cmbPerms)
				{
					cmbPerms.getSelectionModel().select(user.getPermission());
				}

				/**
				 * Update each row of the list view by the received item
				 * @param item - the user to show it's details
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
	
	/***
	 * 
	 * @param event of action
	 * Handle a search of a user by hid id the show him on the list by result
	 * 
	 */
	@FXML
	private void handleSearchPressed(Event event) {

		if (txtUid.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR,"Insert User Id!");
			alert.show();
			return;
		}

		int uId = Integer.parseInt(txtUid.getText());
		
		ArrayList<User> toLoad = new ArrayList<>();
		toLoad.add(getUserByUid(uId));
		usersData = FXCollections.observableArrayList(toLoad);
		uListView.setItems(usersData);
	}
	
	/***
	 * 
	 * @param uId to find 
	 * @return User with the specific uId
	 */
	private User getUserByUid(int uId)
	{
		User retUser = null;
		for(User user : usersList)
			if(user.getuId() == uId)
				retUser = user;
		
		return retUser;
	}
	/***
	 * Reset the list view when the search text is empty.
	 * Then shows all the users 
	 */
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
	/***
	 * Initialization of the data at the scene creation.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		setSearchOnTextChange();
		setListCellFactory();
		displayUsers();
	}
	
	
}
