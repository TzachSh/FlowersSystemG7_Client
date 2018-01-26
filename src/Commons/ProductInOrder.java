package Commons;

import java.io.Serializable;

/**
 * @author VladimirB
 * Storage products in order in it's quantity
 */
import Orders.Order;
import Products.Product;
/***
 * 
 * Entity class to define a Product in Order
 *
 */
public class ProductInOrder implements Serializable {
	
	/**
	 * order number
	 */
	private int orderId;
	/**
	 * product number
	 */
	private int productId;
	/**
	 * quantity in order line
	 */
	private int quantity;
	/**
	 * 
	 * @return order number
	 */
	/***
	 * 
	 * @return orderId
	 */
	public int getOrderId() {
		return orderId;
	}
	/**
	 * 
	 * @param orderId - to set
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	/***
	 * 
	 * @return productId
	 */
	public int getProductId() {
		return productId;
	}
	/***
	 * 
	 * @param productId to set
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}
	/**
	 * 
	 * @return amount in the order
	 */
	public int getQuantity() {
		return quantity;
	}
	/**
	 * 
	 * @param quantity in order
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	/**
	 * Constructor
	 * @param orderId order number
	 * @param productId product number
	 * @param quantity amount in order
	 */
	/***
	 * Constructor
	 * @param orderId
	 * @param productId
	 * @param quantity
	 */
	public ProductInOrder(int orderId, int productId, int quantity) {
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
	}
	
}
