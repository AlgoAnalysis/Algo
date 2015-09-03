package com.algotrado.interactive.brokers.tws;

import java.util.Date;

import com.ib.controller.NewTickType;

public class TickData {

	private NewTickType newTickType;
	private double price;
	private int size;
	private Date timestamp;
	
	public TickData(NewTickType newTickType, double price, int size, Date timestamp) {
		super();
		this.newTickType = newTickType;
		this.price = price;
		this.size = size;
		this.timestamp= timestamp;
	}

	public NewTickType getNewTickType() {
		return newTickType;
	}

	public void setNewTickType(NewTickType newTickType) {
		this.newTickType = newTickType;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
