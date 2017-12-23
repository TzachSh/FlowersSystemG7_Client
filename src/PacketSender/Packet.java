package PacketSender;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This Class uses for sends requests to the server with parameters as optional
 * And also for receive the expected result
 *
 */
public class Packet<T> implements Serializable
{
	/**
	 * private attributes
	 */
	private static final long serialVersionUID = 1L;
	private String msgKey;
	private ArrayList<Object> paramList = new ArrayList<Object>();
	private ArrayList<T> resultList = new ArrayList<T>();
	
	private boolean resultSuccess = false;
	private Exception resultException = null;
	/**
	 * Constructor
	 * 
	 * @param msgKey The key for the specific request
	 */
	public Packet(String msgKey)
	{
		this.msgKey = msgKey;
	}
	
	/**
	 * Check if there is a result for the request from the server
	 * 
	 * @return if result has been received
	 */
	public boolean hasResultFromServer()
	{
		return resultList.size() > 0;
	}
	
	/**
	 * Add parameter for sending to server for client uses
	 * 
	 * @param param the parameter to add
	 */
	public void addParameter(Object param)
	{
		paramList.add(param);
	}
	
	/**
	 * Getter for msgKey attribute for server uses
	 * 
	 * @return message key that sent to server
	 */
	public String getmsgKey()
	{
		return msgKey;
	}
	
	/**
	 * Getter for paramList attribute for client uses
	 * 
	 * @return collection of parameters that sent to server
	 */
	public ArrayList<Object> getParameterList()
	{
		return new ArrayList<Object>(paramList);
	}
	
	/**
	 * Setter for resultList attribute for server uses
	 * 
	 * @param resultList the result collection that generated from the server
	 */
	public void setResultList(ArrayList<T> resultList)
	{
		this.resultList = resultList;
		setResultSuccess();
	}
	
	/**
	 * Get the result state from the server
	 * 
	 */
	public boolean getResultState()
	{
		return resultSuccess;
	}
	
	/**
	 * Set the result state as success
	 * 
	 */
	public void setResultSuccess()
	{
		resultSuccess = true;
	}
	
	/**
	 * Set an exception that server throw for the request
	 * @param e Exception instance
	 */
	public void setException(Exception e)
	{
		this.resultException = e;
		resultSuccess = false;
	}
	
	/**
	 * Get an exception if received from the server
	 * @return Exception instance
	 */
	public Exception getException()
	{
		return resultException;
	}
	
	/**
	 * Getter for resultList attribute for client uses
	 * 
	 * @return collection of result that sent from server
	 */
	public ArrayList<T> getResultList()
	{
		return new ArrayList<T>(resultList);
	}
}
