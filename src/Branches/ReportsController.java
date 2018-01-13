package Branches;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import Customers.Membership;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ReportsController implements Initializable{
	
	@FXML private Label lblBranchName;
	@FXML private Label lblBranchNumber;
	@FXML private Label lblEmployeeName;
	@FXML private ComboBox<String> cbReports;
	@FXML private TableView<String> table1;
	@FXML private TableView<String> table2;
	@FXML private ComboBox<String> cbYear;
	@FXML
	private ComboBox<String> cbQuarterly1;
	@FXML
	private ComboBox<String> cbQuarterly2;
	@FXML
	private ComboBox<String> cbBranches;
	@FXML
	private ComboBox<String> cbBranchesName;
	@FXML
	private ComboBox<String> cbBranchTwoName;
	@FXML
	private ComboBox<String> cbBranchTwoNumber;
	@FXML
	private Label lbQuarterlycomp;
	@FXML
	private RadioButton rbcomp;
	@FXML
	private RadioButton rbcompbranch;
	@FXML
	private Button btnGenerateReport;
	private ArrayList<Branch> branchlist;

	private Employee employee;
	
	/**
	 * 
	 * @param useremployee the manager / branch manager
	 */
	public ReportsController(Employee useremployee)
	{
		super();
		this.employee=useremployee;
	}
	public void start(Stage primaryStage) {
		
		String title = "Report";
		String srcFXML = "/Branches/ReportsUI.fxml";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle(title);
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void generateReport()
	{
		String report,year,quartely1,quartely2;
		
		
		report=cbReports.getSelectionModel().getSelectedItem();
		year=cbYear.getSelectionModel().getSelectedItem();
		quartely1=cbQuarterly1.getSelectionModel().getSelectedItem();
		if(employee.getRole().toString().equals(Role.BranchesManager.toString()))
		{
			quartely2=cbQuarterly1.getSelectionModel().getSelectedItem();
		}
	}
	/**
	 * This function show error message 
	 * @param str error message
	 */
	public void showError(String str)
	{
		JOptionPane.showMessageDialog(null, 
				str, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
	}
	
	public void handleBranchNumber()
	{
		cbBranchesName.getSelectionModel().select(cbBranches.getSelectionModel().getSelectedIndex());
	}
	public void handleBranchTwoNumber()
	{
		cbBranchTwoName.getSelectionModel().select(cbBranchTwoNumber.getSelectionModel().getSelectedIndex());

	}
	public void handleBranchTwoName()
	{
		cbBranchTwoNumber.getSelectionModel().select(cbBranchTwoName.getSelectionModel().getSelectedIndex());
	}
	public void handleBranchName()
	{
		cbBranches.getSelectionModel().select(cbBranchesName.getSelectionModel().getSelectedIndex());
	}
	public void handleBranchTwoComp()
	{

		if(rbcompbranch.isSelected())
		{
			cbBranchTwoName.setVisible(true);
			cbBranchTwoNumber.setVisible(true);
		}
		else
		{
			cbBranchTwoName.setVisible(false);
			cbBranchTwoNumber.setVisible(false);
		}
	}
	public void handleRadioButton()
	{

		if(rbcomp.isSelected())
		{
			lbQuarterlycomp.setVisible(true);
			cbQuarterly2.setVisible(true);
		}
		else
		{
			lbQuarterlycomp.setVisible(false);
			cbQuarterly2.setVisible(false);
		}
	}
	private void initComboBox()
	{
		ObservableList<String> observelistReport,observelistYear,observelistQuarterly,observelistBranch;
		//adding reports to combo box
		ArrayList<String> report = new ArrayList<>();
		report.add("Orders");
		report.add("Complains");
		report.add("Income");
		report.add("Statisfaction");
		observelistReport = FXCollections.observableArrayList(report);
		cbReports.setItems(observelistReport);
		cbReports.getSelectionModel().selectFirst();

		//adding Years to combobox
		ArrayList<String> yearlist = new ArrayList<>();
		for(int i=Calendar.getInstance().get(Calendar.YEAR);i>=2015;i--)
		{
			yearlist.add(i+"");
		}
		observelistYear = FXCollections.observableArrayList(yearlist);
		cbYear.setItems(observelistYear);
		cbYear.getSelectionModel().selectFirst();

		//adding Quarterlies
		ArrayList<String> Quarterlieslist = new ArrayList<>();
		Quarterlieslist.add(1+"");
		Quarterlieslist.add(2+"");
		Quarterlieslist.add(3+"");
		Quarterlieslist.add(4+"");
		observelistQuarterly = FXCollections.observableArrayList(Quarterlieslist);
		cbQuarterly1.setItems(observelistQuarterly);
		cbQuarterly2.setItems(observelistQuarterly);
		cbQuarterly1.getSelectionModel().selectFirst();
		cbQuarterly2.getSelectionModel().selectFirst();
		ArrayList<String> Branchnumber = new ArrayList<>();
		ArrayList<String> BranchName = new ArrayList<>();
		if(employee.getRole().toString().equals((Role.BranchesManager).toString()))
		{
			for(Branch br:branchlist)
			{
				Branchnumber.add(br.getbId()+"");
				BranchName.add(br.getName());
			}
			observelistBranch = FXCollections.observableArrayList(Branchnumber);
			cbBranches.setItems(observelistBranch);
			cbBranches.getSelectionModel().selectFirst();
			cbBranchTwoNumber.setItems(observelistBranch);
			cbBranchTwoNumber.getSelectionModel().select(1);

			observelistBranch = FXCollections.observableArrayList(BranchName);
			cbBranchesName.setItems(observelistBranch);
			cbBranchesName.getSelectionModel().selectFirst();
			cbBranchTwoName.setItems(observelistBranch);
			cbBranchTwoName.getSelectionModel().select(1);
			
		}
	

	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// hiding table 2 that is needed for branches manager and hiding both of them until the required information gathered from the user
		table1.setVisible(false);
		table2.setVisible(false);
		rbcomp.setVisible(false);
		cbQuarterly2.setVisible(false);
		lbQuarterlycomp.setVisible(false);
		cbBranches.setVisible(false);
		cbBranchesName.setVisible(false);
		cbBranchTwoName.setVisible(false);
		cbBranchTwoNumber.setVisible(false);
		rbcompbranch.setVisible(false);
		
		
		Packet packet = new Packet();
		packet.addCommand(Command.getBranches);
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {
			
			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				 branchlist = p.<Branch>convertedResultListForCommand(Command.getBranches);
				 if (p.getResultState()) 
				 {
					initComboBox(); 
					 if(branchlist.isEmpty()==true)
					 {
						 showError("Error , Please Try Again");
							return;
					 }
					 if(employee.getRole().toString().equals((Role.BranchManager).toString()))
					 {
						 lblBranchNumber.setVisible(true);
						
						 for(Branch br:branchlist)
							 if(br.getbId()==employee.getBranchId())
							 {
								lblBranchName.setText("Branch Name : "+br.getName().toString());
								lblBranchNumber.setText("Branch Number : "+br.getbId());
							 }
						 
					 }
					 else		 
					 {
						//its the branches manager
						lblBranchNumber.setVisible(true);
						cbBranches.setVisible(true);
						cbBranchesName.setVisible(true);
						rbcomp.setVisible(true);
						String name=employee.getUser().toString();
						lblEmployeeName.setText("Manager : "+name);
						rbcompbranch.setVisible(true);
						
					 }
					 String name=employee.getUser().toString();
					 lblEmployeeName.setText("Manager : "+name);

		
				}
				else
					System.out.println("Fail: " + p.getExceptionMessage());	
				 
			}
		});
		send.start();


	}
}
