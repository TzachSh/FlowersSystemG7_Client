package PacketSender;

import java.io.IOException;

import javafx.application.Platform;
import ocsf.client.AbstractClient;

/**
 * This class is a subclass of AbstractClient from ocsf
 *  and uses as last layer for send the data to the server
 *
 */
public class SystemClient extends AbstractClient
{
	/**
	 *  handler result behavior
	 */
	private IResultHandler handler; 
	
	
	
	/**
	 * Constructor to initialize the host name and the port that used for the connection to the server
	 * @param handler the instance of requested data class
	 * @param host the host name of the server
	 * @param port the port number of the server
	 */
	public SystemClient(IResultHandler handler, String host, int port) {
		super(host, port);
		
		registerHandler(handler);
		try
		{
			openConnection();
		}
		catch (IOException e) 
		{
			//e.printStackTrace();
		}
	}
	
	/**
	 * Register an handler for client
	 * 
	 * @param handler the handler implements
	 */
	public void registerHandler(IResultHandler handler)
	{
		this.handler = handler;
	}
	/**
	 * receive message from server
	 */
	@Override
	protected void handleMessageFromServer(Object msg) 
	{
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (handler != null)
					handler.onReceivingResult((Packet)msg);
				
				
					try
					{
						closeConnection();
					}
					catch (Exception e) { }
				}
		});
		
	}
}
