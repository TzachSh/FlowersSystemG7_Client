package JUnit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Before;
import org.junit.BeforeClass;
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

public class UnitTest extends TestCase {

	private Order pendingOrder,cancelledOrder;
	private OrderDetailsController orderDetails;
	static Date date;
	private static Timestamp curTimeStamp;
	private Timestamp NHrLater;
	private static int customerIdBefore; 
	private static int membership;
	private static int branchIdBefore;
	public UnitTest(String name) {
		super(name);
	}
	//run before each test
	@Before
	protected void setUp() throws Exception {
       //Get current time
		date = new Date(Calendar.getInstance().getTime().getTime());
		java.util.Date today = new java.util.Date();
		curTimeStamp = new Timestamp(today.getTime());
		
		customerIdBefore =333; 
	    membership =2;
		branchIdBefore=11;

	   orderDetails = new OrderDetailsController();
       //initialize pending order
       pendingOrder = new Order(0, date ,new Timestamp(System.currentTimeMillis()), 1, Status.Pending, 1, 100);
       //initialize cancel order
       cancelledOrder = new Order(0, date ,new Timestamp(System.currentTimeMillis()), 1, Status.Canceled, 1, 100); 


	}
	//check if order not changing with the same status

	@Test
	public void testChangeOrderStatusTestCancelToCancel() 
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
	//check if order has been changed from pending to cancelled
	@Test
	public void testchangeOrderStatusTestPendingToCancel()  {
		try
		{
		Method method = OrderDetailsController.class.getDeclaredMethod("changeOrderStatus", Order.class,Status.class);
        method.setAccessible(true);
        method.invoke(orderDetails, pendingOrder,Status.Canceled);
        assertTrue(pendingOrder.getStatus()==Status.Canceled);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	//get full refund
	@Test
	public void testGetCancelRefundFull()
	{
		//get timestamp 3 hours later
 		Calendar cal = Calendar.getInstance();
 	    cal.setTimeInMillis(curTimeStamp.getTime());
 	    cal.add(Calendar.HOUR, 3);
 		NHrLater = new Timestamp(cal.getTime().getTime());
 		setPayment();
		try
		{
			//get access to private function
			Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
	        method.setAccessible(true);
	        //set order
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class,pendingOrder );
	        //get refund
	        Refund refund =(Refund) method.invoke(orderDetails, NHrLater,curTimeStamp);
	        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments",Order.class);
	        method.setAccessible(true);
	        //get amount in refund
	        double amount = (double)method.invoke(orderDetails,pendingOrder);
	
	        assertTrue(refund.getAmount()==amount);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	//check full refund more than one year
	@Test
	public void testGetCancelRefundFullMoreThanDay()
	{
		//get timestamp 1 day later
 		Calendar cal = Calendar.getInstance();
 	    cal.setTimeInMillis(curTimeStamp.getTime());
 	    cal.add(Calendar.HOUR, 24);
 		NHrLater = new Timestamp(cal.getTime().getTime());
 		setPayment();
		try
		{
		//get access to private method
		Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
        method.setAccessible(true);
        //set order
        Field field = OrderDetailsController.class.getDeclaredField("order");
        field.setAccessible(true);
        field.set(Order.class,pendingOrder );
        //get refund
        Refund refund =(Refund) method.invoke(orderDetails, NHrLater,curTimeStamp);
        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments",Order.class);
        method.setAccessible(true);
        //get amount
        double amount = (double)method.invoke(orderDetails,pendingOrder);

        assertTrue(refund.getAmount()==amount);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	//check half refund
	@Test
	public void testGetCancelRefundHalf()
	{
		//get timestamp 2 hours later
 		Calendar cal = Calendar.getInstance();
 	    cal.setTimeInMillis(curTimeStamp.getTime());
 	    cal.add(Calendar.HOUR, 2);
 		NHrLater = new Timestamp(cal.getTime().getTime());
 		setPayment();
		try
		{
			//get access to private function
		Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
        method.setAccessible(true);
        //set order
        Field field = OrderDetailsController.class.getDeclaredField("order");
        field.setAccessible(true);
        field.set(Order.class,pendingOrder );
        //get refund
        Refund refund =(Refund) method.invoke(orderDetails, NHrLater,curTimeStamp);
        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments",Order.class);
        method.setAccessible(true);
        //get amount
        double amount = (double)method.invoke(orderDetails,pendingOrder);
        amount*=0.5;
        assertTrue(refund.getAmount()==amount);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	@Test
	public void testGetCancelRefundZero()
	{
		//get timestamp less than 1 hour
 		Calendar cal = Calendar.getInstance();
 	    cal.setTimeInMillis(curTimeStamp.getTime());
 	    cal.add(Calendar.MINUTE, 30);
 		NHrLater = new Timestamp(cal.getTime().getTime());
 		setPayment();
		try
		{
			//get access to  private method
		Method method = OrderDetailsController.class.getDeclaredMethod("getCancelRefund", Timestamp.class,Timestamp.class);
        method.setAccessible(true);
        //set order
        Field field = OrderDetailsController.class.getDeclaredField("order");
        field.setAccessible(true);
        field.set(Order.class,pendingOrder );
        //get refund
        Refund refund =(Refund) method.invoke(orderDetails, NHrLater,curTimeStamp);
        method = OrderDetailsController.class.getDeclaredMethod("getOrderPayments",Order.class);
        method.setAccessible(true);
        //check if no refund
        assertTrue(refund==null);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	//help function to set payments
	private void setPayment()
	{
	   ArrayList<OrderPayment> payment = new ArrayList<>();
       payment.add(new OrderPayment(PaymentMethod.Cash,40,date));
       payment.add(new OrderPayment(PaymentMethod.CreditCard,80,date));
       pendingOrder.setOrderPaymentList(payment);
	}
	//check if no charges for order
	@Test
	public void testIfOrderChargerFalse()
	{
		ArrayList<OrderPayment> payment = new ArrayList<>();
        payment.add(new OrderPayment(PaymentMethod.Cash,40,null));
        payment.add(new OrderPayment(PaymentMethod.CreditCard,80,null));
        pendingOrder.setOrderPaymentList(payment);
		try {
			Method method = OrderDetailsController.class.getDeclaredMethod("isCharged");
	        method.setAccessible(true);
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class,pendingOrder );
	        boolean isCharged = (boolean) method.invoke(orderDetails);
	        assertTrue(isCharged==false);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	//check if one or more charges for order
	@Test
	public void testIfOrderChargerTrue()
	{
		setPayment();
		try {
			Method method = OrderDetailsController.class.getDeclaredMethod("isCharged");
	        method.setAccessible(true);
	        Field field = OrderDetailsController.class.getDeclaredField("order");
	        field.setAccessible(true);
	        field.set(Order.class,pendingOrder );
	        boolean isCharged = (boolean) method.invoke(orderDetails);
	        assertTrue(isCharged==true);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
	//build packet to test
	private void preparePacketByOrderDetails() throws Exception
	{
		//set account
		Account ac = new Account(1, customerIdBefore, membership, 20, branchIdBefore, AccountStatus.Active, null);
 		Calendar cal = Calendar.getInstance();
 	    cal.setTimeInMillis(curTimeStamp.getTime());
 	    cal.add(Calendar.HOUR, 3);
 	    //set new requested date
 		pendingOrder.setRequestedDate(new Timestamp(cal.getTime().getTime()));
 		CustomerMenuController.currentAcc=ac;
 		setPayment();
 		//set order
        Field field = OrderDetailsController.class.getDeclaredField("order");
        field.setAccessible(true);
        field.set(Order.class,pendingOrder );
        
	}
	
	//help function to validate  packet
	private void packetValidation(Packet packetToValidate)
	{
		 pendingOrder = (Order) packetToValidate.convertedResultListForCommand(Command.updateOrder).get(0);
	        ArrayList<Object> paramList = packetToValidate.getParameterForCommand(Command.updateAccountBalance);
	        int branchId = (Integer)paramList.get(0);
	        int customerId = (Integer)paramList.get(1);
	        double amount = (Double)paramList.get(2);	        
	        Refund refund = (Refund)packetToValidate.getParameterForCommand(Command.addOrderRefund).get(0);
	        //check if creation date exists amount is bigger than 0 and refund is correspondence to the order
	        assertTrue(refund.getCreationDate()!= null && refund.getAmount()>0 && refund.getRefundAbleId()==pendingOrder.getoId());
	        //check if order related to the specific customer
	        assertTrue(customerId==customerIdBefore && refund.getAmount()==amount && branchId==branchIdBefore);
	        //check if order in the packet with updated status
	        assertTrue(pendingOrder.getStatus()==Status.Canceled);
	}
	//check packet build correctly
	@Test
	public void testIfPacketCorrect()
	{
		
		try
		{
	        // prepare packet of order
	        preparePacketByOrderDetails();
	        Packet packet = orderDetails.initPacket();
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
	public void testSentPacketSuccessfully()
	{
		try
		{
			// prepare packet of order
			preparePacketByOrderDetails();
			
			//checking this function before on correctness
			Packet packet = orderDetails.initPacket();
			//create mock SystemSender
			MockSystemSernder mockSystemSender = new MockSystemSernder();
			orderDetails.saveToServer(mockSystemSender, packet);
			
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
