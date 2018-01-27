package Products;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import Customers.Membership;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 * constant fields and usable functions
 */
public class ConstantData {
	/**
	 * list of all product types
	 */
	public static ArrayList<ProductType> productTypeList = new ArrayList<>();
	/**
	 * list of all colors
	 */
	public static ArrayList<ColorProduct> productColorList = new ArrayList<>();
	/**
	 * list of all memberships
	 */
	public static ArrayList<Membership> memberShipList = new ArrayList<>();
	/**
	 * value if constantData is already initialized
	 */
	public static boolean isInit=false;
	/**
	 * get colors,types and memberships from server
	 */
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
	
	/**
	 * Get the instance of color by color id
	 * 
	 * @param colorId
	 *            The color Id to search for index
	 * @return The instance of the color in the collection that found, If not found,
	 *         return null
	 */
	public static ColorProduct getColorOfFlowerByColorId(int colorId) {
		for (ColorProduct color : ConstantData.productColorList) {
			if (color.getColId() == colorId)
				return color;
		}

		return null;
	}
	
	/**
	 * Show an Alert dialog with custom info
	 * @param type type alert
	 * @param title title window
	 * @param header header of the message
	 * @param content message
	 */
	public static void displayAlert(AlertType type , String title , String header , String content)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
