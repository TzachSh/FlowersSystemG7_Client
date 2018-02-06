package Orders;

public class BadDateRangeException extends Exception
{
	public BadDateRangeException()
	{
	}
	
	public BadDateRangeException(String msg)
	{
		super(msg);
	}
}
