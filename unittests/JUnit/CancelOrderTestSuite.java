package JUnit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import junit.framework.Test;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({ UnitTest.class })
public class CancelOrderTestSuite {
	public static Test suite() {  
		TestSuite suite = new TestSuite("Cancel order Tests");   
		  suite.addTest(new UnitTest("Cancel order Methods") { 
			  protected void runTest() { 
				  testChangeOrderStatusTestCancelToCancel();
				  testchangeOrderStatusTestPendingToCancel();
				  testIfOrderChargerFalse();
				  testIfOrderChargerTrue();
			  	}  
			  }    
		  ); 
		  suite.addTest(new UnitTest("Refund Methods") { 
			  protected void runTest() { 
				  testGetCancelRefundFull();
				  testGetCancelRefundFullMoreThanDay();
				  testGetCancelRefundHalf();
				  testGetCancelRefundZero();
			  	}  
			  }    
		  );  
		suite.addTest(new UnitTest("Packet validation") { 
			  protected void runTest() { 
				  testIfPacketCorrect();
				  testSentPacketSuccessfully();
			  	}  
			  }    
		  ); 
		return suite; 
		} 
	 

}