package co.ritiriwaj.android.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Riwaj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String imageURL;
	private String title;
	private String date;
	private String desc;
	private int riwajId;
	private int status;
	private List<HashMap<String, String>> eventList;

	public Riwaj(String imageURL, String title, String date, String desc,
			int riwajId, int status) {
		this.imageURL = imageURL;
		this.title = title;
		this.date = date;
		this.desc = desc;
		this.riwajId = riwajId;
		this.setStatus(status);

	}
	
	public Riwaj(String imageURL, String title, int riwajId) {
		this.imageURL = imageURL;
		this.title = title;
		this.riwajId = riwajId;
		this.date = null;
		this.setEventList(null);
	}
	/*
	 * Create new constructor for offline with "status" field
	 */
	public Riwaj(String imageURL, String title, int riwajId, int status) {
		this.imageURL = imageURL;
		this.title = title;
		this.riwajId = riwajId;
		this.status = status;
		this.date = null;
		this.setEventList(null);
	}
	
	public Riwaj(String imageURL, String title, int riwajId, List<HashMap<String, String>> eventList) {
		this.imageURL = imageURL;
		this.title = title;
		this.riwajId = riwajId;
		this.date = null;
		this.setEventList(eventList);
	}

	public int getRiwajId() {
		return riwajId;
	}

	public void setRiwajId(int riwajId) {
		this.riwajId = riwajId;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return title + "\n" + desc;
	}

	public List<HashMap<String, String>> getEventList() {
		return eventList;
	}

	public void setEventList(List<HashMap<String, String>> eventList) {
		this.eventList = eventList;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}