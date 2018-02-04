package JUnit;

import Orders.Order;
import junit.framework.TestCase;

public class UnitTest extends TestCase {

	private Order order;
	public UnitTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		order = new Order();
	}


	public void testOrder() {
		System.out.println(order);
	}
	
}
