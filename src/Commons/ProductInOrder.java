package Commons;

import java.io.Serializable;

import Orders.Order;
import Products.Product;
/***
 * 
 * Entity class to define a Product in Order
 *
 */
public class ProductInOrder implements Serializable {
	
	/***
	 * Instance Variables
	 */
	private int orderId;
	private int productId;
	private int quantity;
	
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
	/***
	 * 
	 * @return quantity
	 */
	public int getQuantity() {
		return quantity;
	}
	/***
	 * 
	 * @param quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
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
