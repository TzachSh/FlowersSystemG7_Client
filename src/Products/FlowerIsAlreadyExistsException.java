package Products;

import java.io.Serializable;
/**
 * exception if flower is exist
 */
public class FlowerIsAlreadyExistsException extends Exception implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	/**
	 * @param msg message error message
	 */
	public FlowerIsAlreadyExistsException(String msg)
	{
		super(msg);
	}
	
	public FlowerIsAlreadyExistsException()
	{
		
	}
}
