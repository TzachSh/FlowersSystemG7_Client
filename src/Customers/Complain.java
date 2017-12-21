package Customers;

import java.sql.Date;

import Branches.CustomerService;


public class Complain {
	private int id;
	private Date creationDate;
	private String title;
	private String details;
	private Customer customer;
	private CustomerService customerService;	
}
