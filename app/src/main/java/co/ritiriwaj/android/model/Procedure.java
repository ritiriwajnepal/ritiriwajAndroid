package co.ritiriwaj.android.model;

import java.io.Serializable;

public class Procedure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String id;
	private String orderNo;

	public Procedure(String title, String id, String orderNo) {
		this.title = title;
		this.id = id;
		this.orderNo = orderNo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		return "title: " + title + " id: " + id;
	}
}