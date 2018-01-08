package Customers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Branches.Branch;
import Commons.Refund;
import Commons.Status;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;


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
	@FXML 
	private ComboBox<Branch> cmbBranch;
	
	private ArrayList<Branch> branchList;
	private ObservableList<Branch> data;
	
	private Complain complain;
	private ArrayList<Account> customerAccList;
	
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
	
	private Account getCustomerAccountInBranch(int bId)
	{
		Account retAcc = null;
		for(Account account : customerAccList)
			if(account.getBranchId() == bId)
				retAcc = account;
		
		return retAcc;		
	}
	
	@FXML 
	private void handleSendPressed(Event event)
	{
		String replyment = txtReply.getText();

		if (replyment.isEmpty()) {
			System.out.println("Must fill replyment");
			return;
		}
		
		int complainId = complain.getId();
	
		final double amount = Double.parseDouble(txtRefund.getText());
		boolean isRefund = true;

		if (txtRefund.getText().equals("0.0"))
			isRefund = false;

		Packet packet = new Packet();
		ArrayList<Object> paramListReply = new ArrayList<>();

		if (isRefund) {
			java.sql.Date creationDate = new java.sql.Date(new java.util.Date().getTime());
			Refund refund = new Refund(creationDate, amount, complainId);
			
			int bId = (cmbBranch.getSelectionModel().getSelectedItem()).getbId();
			Account accountToUpdate = getCustomerAccountInBranch(bId);
			accountToUpdate.setBalance(accountToUpdate.getBalance() + amount);

			ArrayList<Object> paramListRefund = new ArrayList<>();
			packet.addCommand(Command.addComplainRefund);
			paramListRefund.add(refund);
			packet.setParametersForCommand(Command.addComplainRefund, paramListRefund);
			packet.addCommand(Command.updateAccountsBycId);
			ArrayList<Object> paramListUpdateAccount = new ArrayList<>();
			paramListUpdateAccount.add(accountToUpdate);
			packet.setParametersForCommand(Command.updateAccountsBycId, paramListUpdateAccount);
		}

		Reply reply = new Reply(complainId, replyment);

		packet.addCommand(Command.addReply);
		paramListReply.add(reply);
		packet.setParametersForCommand(Command.addReply, paramListReply);

		SystemSender sender = new SystemSender(packet);
		sender.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if (p.getResultState()) {

				}
			}
		});
		sender.start();
	}
	
	//*
	private void initCmb()
	{
		cmbSetConverter();
		
		Packet packet = new Packet();
		packet.addCommand(Command.getBranches);
		ArrayList<Object> paramList = new ArrayList<>();
		packet.setParametersForCommand(Command.getBranches, paramList);
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
					ArrayList<Branch> customerBranches = new ArrayList<>();
					for(Branch branch : branchList) {
						Account account = getCustomerAccountInBranch(branch.getbId());
						if(account != null)
							customerBranches.add(branch);
					}
					if(customerBranches.size() > 0) {
						data = FXCollections.observableArrayList(customerBranches);
						cmbBranch.setItems(data);
						cmbBranch.getSelectionModel().selectFirst();
					}
				}
			}
		});
		sender.start();
	}

	public void setComponents(Complain complain)
	{
		setComplain(complain);
		initCustomerDetails();
		txtTitle.setText(complain.getTitle());
		txtDesc.setText(complain.getDetails());
		txtRefund.setText("0.0");
	}
	
	private void cmbSetConverter()
	{
		cmbBranch.setConverter(new StringConverter<Branch>() {

			@Override
			public Branch fromString(String string) {
				return null;
			}

			@Override
			public String toString(Branch object) {
				return String.format("%s", object.getName());
			}
		});
	}
	
	private void initCustomerDetails()
	{
		Packet packet = new Packet();
		ArrayList<Object> paramList = new ArrayList<>();
		paramList.add(complain.getCustomerId());
		packet.setParametersForCommand(Command.getAccountBycId, paramList);
		packet.addCommand(Command.getBranches);
		ArrayList<Object> paramListBranches = new ArrayList<>();
		packet.setParametersForCommand(Command.getBranches, paramListBranches);
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
					customerAccList = p.<Account>convertedResultListForCommand(Command.getAccountBycId);
					branchList = p.<Branch>convertedResultListForCommand(Command.getBranches);
					initCmb();
				}
			}
		});
		sender.start();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
}
