package Products;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Flower implements Serializable {
	private int id;
	private String name;
	private double price;
	private Color color;
	private ArrayList<FlowerInProduct> flowerInProduct;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public ArrayList<FlowerInProduct> getFlowerInProduct() {
		return flowerInProduct;
	}
	public void setFlowerInProduct(ArrayList<FlowerInProduct> flowerInProduct) {
		this.flowerInProduct = flowerInProduct;
	}
	public Flower(int id, String name, double price, Color color, ArrayList<FlowerInProduct> flowerInProduct) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.color = color;
		this.flowerInProduct = flowerInProduct;
	}
	
	
	
}
