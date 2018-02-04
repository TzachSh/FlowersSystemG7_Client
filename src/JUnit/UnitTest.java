package JUnit;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import Commons.Status;
import Orders.Order;
import Orders.OrderDetailsController;
import junit.framework.TestCase;

public class UnitTest extends TestCase {

	private Order pendingOrder,cancelledOrder;
	private OrderDetailsController orderDetails;
	public UnitTest(String name) {
		super(name);
	}
	@Before
	protected void setUp() throws Exception {
	   orderDetails = new OrderDetailsController();
       Date date = new Date(Calendar.getInstance().getTime().getTime());
       pendingOrder = new Order(0, date ,new Timestamp(System.currentTimeMillis()), 1, Status.Pending, 1, 100);
       cancelledOrder = new Order(0, date ,new Timestamp(System.currentTimeMillis()), 1, Status.Canceled, 1, 100);  
	}

	@Test
	public void testOrder()  {
		try
		{
		Method method = OrderDetailsController.class.getDeclaredMethod("changeOrderStatus", Order.class,Status.class);
        method.setAccessible(true);
        method.invoke(orderDetails, pendingOrder,Status.Canceled);
        assertTrue(pendingOrder.getStatus()==Status.Canceled);
		}
		catch(Exception e)
		{
			fail();
		}
	}
	@Test
	public void changeOrderStatusTestCancelToCancel() 
	{
		try 
		{
			Method method = OrderDetailsController.class.getDeclaredMethod("changeOrderStatus", Order.class,Status.class);
	        method.setAccessible(true);
	        method.invoke(orderDetails, cancelledOrder,Status.Canceled);
	        assertTrue(cancelledOrder.getStatus()==Status.Canceled);
		}
		catch(Exception e)
		{
			fail();
		}
	}
	
}
