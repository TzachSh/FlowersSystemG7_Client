package JUnit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import junit.framework.Test;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({ UnitTest.class })
public class CancelOrderTestSuite 
{
	public static Test suite()
	{  
		TestSuite suite = new TestSuite("Cancel order Tests");   
		  suite.addTest(new UnitTest("Cancel order Methods")
		  { 
			  protected void runTest() 
			  { 
					  testChangeOrderStatusCancelToCancel();
					  testChangeOrderStatusPendingToCancel();
					  testIfOrderIsNotCharged();
					  testIfOrderIsCharged();
					  testCancelingOrder_EarlierThanOrderCreation();
			  	}  
			  }    
		  ); 
		  
		  suite.addTest(new UnitTest("Refund Methods") 
		  { 
			  protected void runTest() 
			  { 
					 testGetFullRefundByCancellingOrder_24HoursLater();
					 testGetFullRefundByCancellingOrder_3HoursLater();
					 testGetHalfRefundByCancellingOrder_2HoursLater();
					 testGetZeroRefundByCancellingOrder_1HourLeft();
					 testGetOrderPaymentsCorrect();
			  	}  
			  }    
		  );  
		  
		suite.addTest(new UnitTest("Packet validation")
		{ 
			  protected void runTest()
			  { 
					 testValidationOfPacketBeforeSendingToServer();
					 testValidationOfSendingPacketToTheServer();
			  	}  
			  }    
		  ); 
		
		return suite; 
	}
}