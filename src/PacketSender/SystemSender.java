package PacketSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * This Class uses as thread that create a request to the server for the relevant data
 * and also wait for arrived data from the server if needed
 *
 */
public class SystemSender extends Thread
{
	/**
	 *  private attributes
	 */
	final int DEFUALT_PORT = 5555;
	private SystemClient client;
	private Packet<?> packet;
	private IResultHandler<?> handler;
	
	/**
	 * Constructor that initialize the handler object that implement the solution when data arrived
	 * @param packet instance of packet that we want send to the server
	 * @param handler instance of requested data class
	 */
	public SystemSender(Packet<?> packet, IResultHandler<?> handler)
	{
		this.client = initClient(handler);
		this.packet = packet;
		this.handler = handler;
	}
	
	/**
	 * Constructor that initialize the handler object that implement the solution when data arrived
	 * @param msgKey the message key that we want send to the server
	 * @param handler instance of requested data class
	 */
	public SystemSender(String msgKey, IResultHandler<?> handler)
	{
		this(new Packet<>(msgKey), handler);
	}
	
	/**
	 * Constructor that initialize the handler object that implement the solution when data arrived
	 * @param packet instance of packet that we want send to the server
	 */
	public SystemSender(Packet<?> packet)
	{
		this(packet, null);
	}
	
	/**
	 * Constructor that initialize the handler object that implement the solution when data arrived
	 * @param msgKey the message key that we want send to the server
	 */
	public SystemSender(String msgKey)
	{
		this(msgKey, null);
	}
	
	/**
	 * Register an handler for client
	 * 
	 * @param handler the handler implements
	 */
	public void registerHandler(IResultHandler<?> handler)
	{
		this.handler = handler;
		this.client.registerHandler(handler);
	}
	
	/**
	 * Initialize client connection from configuration file or uses of default parameters
	 * @return Instance of Client
	 */
	private SystemClient initClient(IResultHandler<?> handler) {
		String host;
		int port;
		try 
		{
			File cfgFile = new File(System.getProperty("user.dir")+"/" +"cfgClient.txt");
			FileReader fileReader = new FileReader(cfgFile);
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			host = bufferedReader.readLine();
			port = Integer.parseInt(bufferedReader.readLine());
		}
		catch (Exception e) 
		{
			host = "localhost";
			port = DEFUALT_PORT;
		}

		return new SystemClient(handler, host, port);
	}
	
	public void run()
	{
		try
		{
			client.sendToServer(packet);
			if (handler != null)
				handler.onWaitingForResult();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
