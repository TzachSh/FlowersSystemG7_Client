package Products;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FlowerController extends Application implements Initializable {

	@FXML
	Label lblColor;//display color
	@FXML
	TextField txtName;
	@FXML
	TextField txtprice;
	@FXML
	Label lblErrPrice;
	@FXML
	Label lblErrName;
	@FXML
	ComboBox<String> cmbColor;//display possible color names
	private ArrayList<ColorProduct> cList ;//all possible colors for flower
	/**
	 * init data for form
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// set listener to combobox when other item has been choosed
		cmbColor.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				lblColor.setStyle("-fx-background-color: "+arg2+";");
			}
		});
		
		
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.getColors);//add command
	
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			@Override
			public void onWaitingForResult() {//waiting when send
			}

			@Override
			public void onReceivingResult(Packet p)//set combobox values
			{
				ArrayList<String> col = new ArrayList<>();
				if (p.getResultState())
				{
					cList = p.<ColorProduct>convertedResultListForCommand(Command.getColors);
					
					for (ColorProduct color : cList)
					{
						col.add(color.getColorName());
					}
					cmbColor.getItems().addAll(col);
				}
				else//if it was error in connection
					JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
			}
		});
		send.start();
	}
	/**
	 * 	when user click on create checking validationand sending it to the server
	 */
	public void onCreateClicked(ActionEvent event)
	{
		
		try {
			isValid();//check validation
			Packet packet = new Packet();//create packet to send
			packet.addCommand(Command.addFlower);//add command
			ArrayList<Object> flower = new ArrayList<Object>();//create array list for send to server
			
			ColorProduct codeColor = cList.stream().filter(c->c.getColorName().equals(cmbColor.getValue())).findFirst().orElse(null);//get color id for colorList
			
			String name=txtName.getText().replaceAll("'", "\\'");//remove special character
			
			flower.add(new Flower(name,Double.parseDouble(txtprice.getText()),codeColor.getColId()));//add new flower
			packet.setParametersForCommand(Command.addFlower,flower);//set flower list to command
			SystemSender sender = new SystemSender(packet);//create sender
			sender.registerHandler(new IResultHandler() {
				
				@Override
				public void onWaitingForResult() {//waiting
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onReceivingResult(Packet p) {//notify user about an answer from the server
					if(p.getResultState())
					{
						JOptionPane.showMessageDialog(null,"The flower has been saved","Success",JOptionPane.INFORMATION_MESSAGE);
						txtName.setText("");
						txtprice.setText("");
						cmbColor.setValue("Choose color");
					}
					else
						JOptionPane.showMessageDialog(null,"The flower hasn't been saved","Error",JOptionPane.ERROR_MESSAGE);
					
				}
			});
			sender.start();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}

	/***
	 *  check if all fields in the form is valid
	 * @throws Exception with error
	 */
	private void isValid() throws Exception {
		if(txtName.getText().length() == 0)
		{
			lblErrName.setText("*");
			throw new Exception("Please fill flower name before");
		}
		if(cmbColor.getValue()==null)
		{
			throw new Exception("Please choose color");
		}
		if(txtprice.getText().length()==0 || Double.parseDouble(txtprice.getText())<0) {
			lblErrPrice.setText("*");
			throw new Exception("Please fill price correctly");
		}
	}
	/**
	 * load fxml Flower GUI
	 */
	@Override
	public void start(Stage arg0) throws Exception {
		String title = "Products";
		String srcFXML = "/Products/FlowerUI.fxml";
		String srcCSS = "/Products/application.css";
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			arg0.setTitle(title);
			arg0.setScene(scene);
			arg0.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
		
		arg0.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				Platform.exit();
			}
		});
	}
}
