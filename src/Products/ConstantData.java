package Products;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import Customers.Membership;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;

public class ConstantData {
	public static ArrayList<ProductType> productTypeList = new ArrayList<>();
	public static ArrayList<ColorProduct> productColorList = new ArrayList<>();
	public static ArrayList<Membership> memberShipList = new ArrayList<>();
	public static boolean isInit=false;
	public static void initColorsAndTypes() {
		Packet packet = new Packet();//create packet to send
		packet.addCommand(Command.getColors);//add command
		packet.addCommand(Command.getProductTypes);//add command
		packet.addCommand(Command.getMemberShip);
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
				if (p.getResultState())
				{
					memberShipList = p.<Membership>convertedResultListForCommand(Command.getMemberShip);
					productColorList = p.<ColorProduct>convertedResultListForCommand(Command.getColors);
					productTypeList = p.<ProductType>convertedResultListForCommand(Command.getProductTypes);
					isInit=true;
				}
				else//if it was error in connection
					JOptionPane.showMessageDialog(null,"Connection error","Error",JOptionPane.ERROR_MESSAGE);
			}
		});
		send.start();
	}
}
