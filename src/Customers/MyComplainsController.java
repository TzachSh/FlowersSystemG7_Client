package Customers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Branch;
import Commons.Refund;
import Login.CustomerMenuController;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Products.ConstantData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/***
 * Controller
 * Allows to customer to follow his complains and see the response
 *
 */

public class MyComplainsController implements Initializable{
	@FXML private ListView<Complain> cListView;
	/**customer complains*/
	private ArrayList<Complain> complainsList;
	/**possible branches*/
	private ArrayList<Branch> branchesList;
	/**refunds from complains*/
	private ArrayList<Refund> refundsList;
	/**complain's employee reply*/
	private ArrayList<Reply> replyList;
	/**
	 * Observable collection to be set in to the views
	 */
	private ObservableList<Complain> complainsData;
	
	/**
	 * Customer to be initalized after login
	 */
	private Customer customer = (Customer)LoginController.userLogged;
	/**
	 * Save the stage to be changed during runtime
	 */
	private Stage primaryStage;
	
	/**
	 * 
	 * @param primaryStage - main stage to be opened
	 */
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setResizable(false);
		String title = "Follow Complains";
		String srcFXML = "/Customers/MyComplainsUI.fxml";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {
		        	  
		        	  primaryStage.close();
					  CustomerMenuController menu = new CustomerMenuController();
					  try {
						menu.start(new Stage());
					} catch (Exception e) {
						ConstantData.displayAlert(AlertType.ERROR, "Error", "Exception when trying to open Menu Window", e.getMessage());
					}

		          }
		      }); 
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	

	/**
	 * Gather data of the customer's complains and display them in the list view
	 */
	private void displayComplains()
	{
		ArrayList<Complain> customerComplainsList = new ArrayList<>();
		
		for(Complain complain : complainsList)
			if(complain.getCustomerId() == customer.getId())
				customerComplainsList.add(complain);
		
		complainsData = FXCollections.observableArrayList(customerComplainsList);
		
		cListView.setItems(complainsData);
	}
	
	/***
	 * 
	 * @param complain search by
	 * @return the branch which this complain relies to 
	 */
	private Branch getBranchByComplain(Complain complain)
	{
		Branch retBranch = null;
		
		for(Branch branch : branchesList)
			if(branch.getbId() == complain.getBranchId())
				retBranch = branch;
		return retBranch;
	}
	
	/**
	 * 
	 * @param complain search by
	 * @return the refund, if exists, for this complain
	 */
	private Refund getRefundByComplain(Complain complain)
	{
		Refund retRefund = null;
		
		for(Refund refund : refundsList)
			if(refund.getRefundAbleId() == complain.getId())
				retRefund = refund;
		
		return retRefund;
		
	}
	
	/**
	 * 
	 * @param complain search by
	 * @return the reply, if exists, for this complain
	 */
	private Reply getReplyByComplain(Complain complain)
	{
		Reply retReply = null;

		for (Reply reply : replyList)
			if (reply.getComplainId() == complain.getId())
				retReply = reply;

		return retReply;
	}
	
	/**
	 * Set the handle to be defined of each cell in the list view
	 */
	private void setListCellFactory()
	{
		cListView.setCellFactory(new Callback<ListView<Complain>, ListCell<Complain>>() {
			
			/**
			 * Create handler for each cell of the list view
			 */
			@Override
			public ListCell<Complain> call(ListView<Complain> param) {
				// TODO Auto-generated method stub
				return new ListCell<Complain>() {
					
				/**
				 * 	Set handler for each row, is a corresponding to the status of the complain, if it's active will show a "Reply" button near to it, else will be shown "Done"
				 * @param complain - show the complain's details in the fields such as date , subject and content
				 */
				private void setCellHandler(Complain complain)
				{
					Branch branch = getBranchByComplain(complain);
					Refund refund = getRefundByComplain(complain);
					Reply reply = getReplyByComplain(complain);
					
					String textDate = "Date: ";
					String textTitle = "Subject: ";
					String textContent = "Content: ";
					String textReply = "Replyment: ";
					String textStatus = "Status: ";
					String textRefund = "Refund: ";
					String textBranch = "In branch: ";
					
					VBox detailsElements = null;
					
					HBox dateElement = new HBox(new Label(textDate), new Text(String.format("%s", complain.getCreationDate().toString())));
					HBox titleElement = new HBox (new Label(textTitle) , new Text(complain.getTitle()));
					HBox infoElement = new HBox (new Label(textContent) , new Text(complain.getDetails()));
					HBox statusElement = new HBox(new Label(textStatus),new Text( complain.isActive() ? "Pending" : "Closed" ));
					HBox branchElement = null;
					if(!complain.isActive() && refund != null) {
						HBox refundElement = new HBox(new Label(textRefund) , new Text( String.format("%.2f$", refund.getAmount())));
							if (branch != null) {
								branchElement = new HBox(new Label(textBranch), new Text(branch.getName()));
								branchElement.setPadding(new Insets(0, 10, 5, 20));
								refundElement.getChildren().add(branchElement);
								refundElement.setPadding(new Insets(5, 10, 5, 20));
								detailsElements = new VBox(statusElement, dateElement, titleElement, infoElement,refundElement);
								if(reply != null)
								{
									HBox replyElement = new HBox(new Label(textReply), new Text(reply.getReplyment()));
									replyElement.setPadding(new Insets(5, 10, 5, 20));
									detailsElements.getChildren().add(replyElement);
								}
							}
							else if (reply != null) {
									HBox replyElement = new HBox(new Label(textReply), new Text(reply.getReplyment()));
									branchElement = new HBox(new Label(textBranch), new Text(branch.getName()));
									branchElement.setPadding(new Insets(0, 10, 5, 20));
									refundElement.getChildren().add(branchElement);
									refundElement.setPadding(new Insets(5, 10, 5, 20));
									replyElement.setPadding(new Insets(5, 10, 5, 20));
									detailsElements = new VBox(statusElement, dateElement, titleElement, infoElement,
											replyElement, refundElement);
								}
							}
					else
					{
						if(complain.isActive()) {
							detailsElements = new VBox(statusElement,dateElement,titleElement,infoElement);
							if(branch != null)
							{
								branchElement = new HBox(new Label(textBranch), new Text(branch.getName()));
								branchElement.setPadding(new Insets(5, 10, 5, 20));
								detailsElements.getChildren().add(branchElement);
							}
						}
						else {
								detailsElements = new VBox(statusElement,dateElement,titleElement,infoElement);
								if (branch != null) {
									branchElement = new HBox(new Label(textBranch), new Text(branch.getName()));
									branchElement.setPadding(new Insets(5, 10, 5, 20));
									detailsElements.getChildren().add(branchElement);
								}
								if (reply != null) {
									HBox replyElement = new HBox(new Label(textReply), new Text(reply.getReplyment()));
									replyElement.setPadding(new Insets(5, 10, 5, 20));
									detailsElements = new VBox(statusElement, dateElement, titleElement, infoElement,replyElement);
									detailsElements.getChildren().add(branchElement);
								}
								else
								{
									detailsElements = new VBox(statusElement, dateElement, titleElement, infoElement);
									if(branch != null)
									{
										branchElement = new HBox(new Label(textBranch), new Text(branch.getName()));
										branchElement.setPadding(new Insets(5, 10, 5, 20));
										detailsElements.getChildren().add(branchElement);
									}
								}
						}
					}
				
				 	dateElement.setPadding(new Insets(5,10,5,20));
				 	titleElement.setPadding(new Insets(5,10,5,20));
				 	infoElement.setPadding(new Insets(5,10,5,20));
				 	detailsElements.setStyle("-fx-border-style:solid inside;"+
                    			  "-fx-border-width:1;"+
                    			  "-fx-border-insets:5;"+
                    			  "-fx-border-radius:5;");
				 	detailsElements.setPadding(new Insets(10));
                    setGraphic(detailsElements);
				}

				/**
				 * Update each row of the list view by the received item
				 * @param item - the complain to show it's details
				 * @param empty - to check if the item is null or not
				 */
					@Override
				protected void updateItem(Complain item, boolean empty) {
					// TODO Auto-generated method stub
					super.updateItem(item, empty);
					 if (item != null) 	
						 setCellHandler(item);                        
					 else
						 setGraphic(null);
				}};
			}
		});
	}
	
	/***
	 * Initialization of the relevant lists: branches, complains, refunds and replies
	 */
	private void initData()
	{
		ArrayList<Object> paramListBranches = new ArrayList<>();
		ArrayList<Object> paramListComplains = new ArrayList<>();
		ArrayList<Object> paramListRefund = new ArrayList<>();
		ArrayList<Object> paramListReply = new ArrayList<>();
		
		Packet packet = new Packet();
		packet.addCommand(Command.getBranches);
		packet.addCommand(Command.getComplains);
		packet.addCommand(Command.getRefunds);
		packet.addCommand(Command.getReplies);
		
		packet.setParametersForCommand(Command.getBranches, paramListBranches);
		packet.setParametersForCommand(Command.getComplains, paramListComplains);
		packet.setParametersForCommand(Command.getRefunds, paramListRefund);
		packet.setParametersForCommand(Command.getReplies, paramListReply);
		
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
					complainsList = p.<Complain>convertedResultListForCommand(Command.getComplains);
					branchesList = p.<Branch>convertedResultListForCommand(Command.getBranches);
					refundsList = p.<Refund>convertedResultListForCommand(Command.getRefunds);
					replyList = p.<Reply>convertedResultListForCommand(Command.getReplies);
					displayComplains();
				}
			}
		});
		sender.start();
	}

	/**
	 * Called at the stage creation level to initialize to data and the list view cells handler 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		setListCellFactory();
		initData();
	}
	
}
