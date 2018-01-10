package Products;

import java.io.Serializable;

public class FlowerIsAlreadyExistsException extends Exception implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FlowerIsAlreadyExistsException(String msg)
	{
		super(msg);
	}
	
	public FlowerIsAlreadyExistsException()
	{
		
	}
}
