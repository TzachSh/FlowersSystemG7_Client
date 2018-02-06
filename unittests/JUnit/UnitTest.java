package JUnit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import Commons.Refund;
import Commons.Status;
import Customers.Account;
import Customers.AccountStatus;
import Login.CustomerMenuController;
import Orders.Order;
import Orders.OrderDetailsController;
import Orders.OrderPayment;
import Orders.PaymentMethod;
import PacketSender.Command;
import PacketSender.Packet;
import junit.framework.TestCase;

public class UnitTest extends TestCase 
{
	private static Timestamp currentTimeStamp;
	private Timestamp anotherTimeStamp;
	static Date currentDate;
	
	private Order pendingOrder, cancelledOrder;
	private OrderDetailsController orderDetailsController;
	
	private static int customerId; 
	private static int membership;
	private static int branchId;
	
	public UnitTest(String name) {
		super(name);
	}
	
	//run before each test
	@Before
	protected void setUp() throws Exception
	{
       //Get current time
		currentDate = new Date(Calendar.getInstance().getTime().getTime());
		java.util.Date today = new java.util.Date();
		currentTimeStamp = new Timestamp(today.getTime());
		
		customerId = 333; 
	    membership = 2;
		branchId = 11;

	   orderDetailsController = new OrderDetailsController();
	   
       //initialize pending order
	   int orderId = 0;
	   Date creationDate = currentDate;
	   Timestamp reqestedDate = new Timestamp(System.currentTimeMillis());
	   Status pendingStatus = Status.Pending;
	   double total = 100;
	   
       pendingOrder = new Order(orderId, creationDate ,reqestedDate, customerId, pendingStatus, branchId, total);
      
       //initialize cancel order
       Status cancelledStatus = Status.Canceled;
       
       cancelledOrder = new Order(orderId, creationDate ,reqestedDate, customerId, cancelledStatus, branchId, total); 
	}
	
	/** help function to set payments */
	private void setPaymentForPendingOrder()
	{
	   ArrayList<OrderPayment> payment = new ArrayList<>();
	   double paymentAmount = 40;
       payment.add(new OrderPayment(PaymentMethod.Cash, paymentAmount, currentDate));
       paymentAmount = 80;
       payment.add(new OrderPayment(PaymentMethod.BalancePayment, paymentAmount, currentDate));
       pendingOrder.setOrderPaymentList(payment);
	}
	
	/** build packet to test **/
	private void preparePacketByOrderDetails() throws Exception
	{
		//set account
		int accountNumber = 1;
		double accountBalance = 20;
		AccountStatus activeStatus = AccountStatus.Active;
		String creditCard = null;
		
		Account account = new Account(accountNumber, customerId, membership, accountBalance, branchId, activeStatus, creditCard);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTimeStamp.getTime());
		int hoursToAdd = 3;
		calendar.add(Calendar.HOUR, hoursToAdd);
		
		// set new requested date
		pendingOrder.setRequestedDate(new Timestamp(calendar.getTime().getTime()));
		
		// define the account that ordering
		CustomerMenuController.currentAcc = account;
		
		setPaymentForPendingOrder();
		
		//set order
		Field field = OrderDetailsController.class.getDeclaredField("order");
		field.setAccessible(true);
		field.set(Order.class,pendingOrder);
	}
	
	/** help function to validate  packet */
	private void packetValidation(Packet packetToValidate)
	{
		 pendingOrder = (Order) packetToValidate.convertedResultListForCommand(Command.updateOrder).get(0);
	     ArrayList<Object> paramList = packetToValidate.getParameterForCommand(Command.updateAccountBalance);
	    
	     int paramBranchId = (Integer)paramList.get(0);
	     int paramCustomerId = (Integer)paramList.get(1);
	     double paramAmount = (Double)paramList.get(2);	        
	     Refund paramRefund = (Refund)packetToValidate.getParameterForCommand(Command.addOrderRefund).get(0);
	        
	     // check if creation date exists amount is bigger than 0 and refund is correspondence to the order
	     boolean refundValidation = paramRefund.getCreationDate() != null &&
	    		 					paramRefund.getAmount() > 0 && 
	    		 					paramRefund.getRefundAbleId() == pendingOrder.getoId() &&
	    		 					paramRefund.getAmount() == paramAmount;
	     
	     // check if order related to the specific customer
	     boolean customerVaildation = paramCustomerId == customerId && paramBranchId == branchId;
	     
	     // check if order in the packet with updated status
	     boolean orderStatusValidation = pendingOrder.getStatus() == Status.Canceled;
	    
         assertEquals("Refund Details are not correspondence to the order", true, refundValidation);
	     
         assertEquals("Customer Details are not belongs to the relevant customer", true, customerVaildation);

         assertEquals("Order Status does not changed to canceled", true, orderStatusValidation);
	}
	
	//check if order not changing with the same status
	@Test
	public void testChangeOrderStatusCancelToCancel() 
	{
		try 
		{
			Method method = OrderDetailsController.class.getDeclaredMethod("changeOrderStatus", Order.class,Status.class);
	       
			method.setAccessible(true);
	        method.invoke(orderDetailsController, cancelledOrder,Status.Canceled);
	       
	        assertTrue(cancelledOrder.getStatus() == Status.Canceled);
		}
		catch(Exception e)
		{
			fail();
		}
	}
	
	//check if order has been changed from pending to cancelled
	@Test
	public void testChangeOrderStatusPendingToCancel()  
	{
		try
		{
			 assertFalse(pendingOrder.getStatus() == Status.Canceled);
			
			//get access to private function
			Method method = OrderDetailsController.class.getDeclaredMethod("changeOrderStatus", Order.class,Status.class);
	      
			method.setAccessible(true);
	        method.invoke(orderDetailsController, pendingOrder,Status.Canceled);
	        
	        assertTrue(pendingOrder.getStatus() == Status.Canceled);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	//get full refund
	@Test
	public void testGetFullRefundByCancellingOrder_3HoursLater()
	{
		//get timestamp 3 hours later
 		Calendar calendar = Calendar.getInstance();
 	    calendar.setTimeInMillis(currentTimeStamp.getTime());
 	    int hoursToAdd = 3;
 	    calendar.add(Calendar.HOUR, hoursToAdd);
 	    
 	    anotherTimeStamp = new Timestamp(calendar.getTime().getTime());
 	    
 	   setPaymentForPendingOrder();
 		
		try
		{
			//get access to private function
			Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
	        method.setAccessible(true);
	        
	        //set order
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class, pendingOrder);
	        
	        //get refund
	        Refund refund = (Refund)method.invoke(orderDetailsController, anotherTimeStamp,currentTimeStamp);
	        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments",Order.class);
	        method.setAccessible(true);
	        
	        //get amount in refund
	        double amount = (double)method.invoke(orderDetailsController, pendingOrder);
	
	        assertTrue(refund.getAmount() == amount);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	//check full refund more than one day
	@Test
	public void testGetFullRefundByCancellingOrder_24HoursLater()
	{
		//get timestamp 1 day later
 		Calendar calendar = Calendar.getInstance();
 	    calendar.setTimeInMillis(currentTimeStamp.getTime());
 	    calendar.add(Calendar.HOUR, 24);
 	    
 	    anotherTimeStamp = new Timestamp(calendar.getTime().getTime());
 	    
 	    setPaymentForPendingOrder();
 		
		try
		{
			//get access to private method
			Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
	        method.setAccessible(true);
	        
	        //set order
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class, pendingOrder);
	        
	        //get refund
	        Refund refund = (Refund)method.invoke(orderDetailsController, anotherTimeStamp,currentTimeStamp);
	        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments", Order.class);
	        method.setAccessible(true);
	        
	        //get amount
	        double amount = (double)method.invoke(orderDetailsController, pendingOrder);
	
	        assertTrue(refund.getAmount() == amount);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	//check half refund
	@Test
	public void testGetHalfRefundByCancellingOrder_2HoursLater()
	{
		//get timestamp 2 hours later
 		Calendar calendar = Calendar.getInstance();
 	    calendar.setTimeInMillis(currentTimeStamp.getTime());
 	    int hoursToAdd = 2;
 	    calendar.add(Calendar.HOUR, hoursToAdd);
 	    
 	    anotherTimeStamp = new Timestamp(calendar.getTime().getTime());
 	    
 	   setPaymentForPendingOrder();
 		
		try
		{
			//get access to private function
			Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
	        method.setAccessible(true);
	        
	        //set order
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class, pendingOrder);
	        
	        //get refund
	        Refund refund = (Refund)method.invoke(orderDetailsController, anotherTimeStamp, currentTimeStamp);
	        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments", Order.class);
	        method.setAccessible(true);
	        
	        //get half amount
	        double amount = (double)method.invoke(orderDetailsController, pendingOrder);
	        amount *= 0.5;
	        
	        assertTrue(refund.getAmount() == amount);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetZeroRefundByCancellingOrder_1HourLeft()
	{
		//get timestamp less than 1 hour
 		Calendar calendar = Calendar.getInstance();
 	    calendar.setTimeInMillis(currentTimeStamp.getTime());
 	    calendar.add(Calendar.MINUTE, 30);
 	    
 	    anotherTimeStamp = new Timestamp(calendar.getTime().getTime());
 	    
 	    setPaymentForPendingOrder();
 		
		try
		{
			//get access to  private method
			Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
	        method.setAccessible(true);
	        
	        //set order
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class, pendingOrder);
	        
	        //get refund
	        Refund refund = (Refund)method.invoke(orderDetailsController, anotherTimeStamp, currentTimeStamp);
	        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments",Order.class);
	        method.setAccessible(true);
	        
	        //check if no refund
	        assertTrue(refund == null);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	//check if no charges for order
	@Test
	public void testIfOrderIsNotCharged()
	{
		ArrayList<OrderPayment> payment = new ArrayList<>();
		double paymentAmount = 40;
		Date paymentDate = null;
        payment.add(new OrderPayment(PaymentMethod.Cash, paymentAmount, paymentDate));
        
        pendingOrder.setOrderPaymentList(payment);
        
		try 
		{
			//get access to  private method
			Method method = OrderDetailsController.class.getDeclaredMethod("isCharged");
	        method.setAccessible(true);
	        
	        // set order
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class, pendingOrder);
	        
	        // check if not charged
	        boolean isCharged = (boolean)method.invoke(orderDetailsController);
	        
	        assertTrue(isCharged == false);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	//check if one or more charges for order
	@Test
	public void testIfOrderIsCharged()
	{
		setPaymentForPendingOrder();
		
		try 
		{
			// get access to  private method
			Method method = OrderDetailsController.class.getDeclaredMethod("isCharged");
	        method.setAccessible(true);
	        
	        // set order
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class, pendingOrder);
	        
	        boolean isCharged = (boolean) method.invoke(orderDetailsController);
	        
	        assertTrue(isCharged == true);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	//check packet build correctly
	@Test
	public void testValidationOfPacketBeforeSendingToServer()
	{
		try
		{
	        // prepare packet of order
	        preparePacketByOrderDetails();
	        
	        Packet packet = orderDetailsController.initPacket();
	        
	        // validate packet
	        packetValidation(packet);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	//packet validation on send to server
	@Test
	public void testValidationOfSendingPacketToTheServer()
	{
		try
		{
			// prepare packet of order
			preparePacketByOrderDetails();
			
			//checking this function before on correctness
			Packet packet = orderDetailsController.initPacket();
			
			//create mock SystemSender
			MockSystemSender mockSystemSender = new MockSystemSender();
			orderDetailsController.saveToServer(mockSystemSender, packet);
			
			// validate packet from system sender
			Packet serverPacket = mockSystemSender.getPacket();
			
	        packetValidation(serverPacket);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
}
