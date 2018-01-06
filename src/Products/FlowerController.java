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
	Label lblColor;
	@FXML
	TextField txtName;
	@FXML
	TextField txtprice;
	@FXML
	ComboBox<String> cmbColor;
	private ArrayList<String> col = new ArrayList<>();
	private ArrayList<ColorProduct> cList ;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cmbColor.valueProperty().addListener(new ChangeListener<String>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void changed(ObservableValue arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				lblColor.setStyle("-fx-background-color: "+arg2+";");
			}
		});
		
		
		Packet packet = new Packet();
		packet.addCommand(Command.getColors);
	
		
		// create the thread for send to server the message
		SystemSender send = new SystemSender(packet);

		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			@Override
			public void onWaitingForResult() {
				System.out.println("Waiting");
			}

			@Override
			public void onReceivingResult(Packet p)
			{
				if (p.getResultState())
				{
					cList = p.<ColorProduct>convertedResultListForCommand(Command.getColors);
					
					for (ColorProduct color : cList)
					{
						col.add(color.getColorName());
					}
					cmbColor.getItems().addAll(col);
				}
				else
					System.out.println("Fail: " + p.getExceptionMessage());
			}
		});
		send.start();
	}
		
	public void onCreateClicked(ActionEvent event)
	{
		
		try {
			isValid();
			Packet packet = new Packet();
			packet.addCommand(Command.addFlower);
			ArrayList<Object> flower = new ArrayList<Object>();
			ColorProduct codeColor = cList.stream().filter(c->c.getColorName().equals(cmbColor.getValue())).findFirst().orElse(null);
			flower.add(new Flower(txtName.getText(),Double.parseDouble(txtprice.getText()),codeColor.getColId()));
			packet.setParametersForCommand(Command.addFlower,flower);
			SystemSender sender = new SystemSender(packet);
			sender.registerHandler(new IResultHandler() {
				
				@Override
				public void onWaitingForResult() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onReceivingResult(Packet p) {
					if(p.getResultState())
					{
						JOptionPane.showMessageDialog(null,"The flower has been saved","Success",JOptionPane.INFORMATION_MESSAGE);
						txtName.setText("");
						txtprice.setText("");
						cmbColor.setValue("Choose color");;
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

	private void isValid() throws Exception {
		if(txtName.getText().length() <0)
			throw new Exception("Please fill flower name before");
		if(Double.valueOf(txtprice.getText())<0)
			throw new Exception("Please fill price correctly");
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
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
