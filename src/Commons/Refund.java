package Commons;

import java.sql.Date;

import Customers.Complain;
import Orders.Order;

public class Refund {
	private int id;
	private Date creationDate;
	private double amount;
	private Order order;
	private Complain complain;
	private Status status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Complain getComplain() {
		return complain;
	}
	public void setComplain(Complain complain) {
		this.complain = complain;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Refund(int id, Date creationDate, double amount, Order order, Complain complain, Status status) {
		super();
		this.id = id;
		this.creationDate = creationDate;
		this.amount = amount;
		this.order = order;
		this.complain = complain;
		this.status = status;
	}
}
