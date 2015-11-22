package co.ritiriwaj.android.model;

import java.io.Serializable;
import java.util.List;

public class SubProcedure implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id;
	String orderNo;
	String paraText;
	String blockQuote;
	String imageListCSV;
	String reasonWhyId;

	public SubProcedure(String id, String orderNo, String paraText,
			String blockQuote, String imageListCSV, String reasonWhyId) {
		this.id = id;
		this.orderNo = orderNo;
		this.paraText = paraText;
		this.blockQuote = blockQuote;
		this.imageListCSV = imageListCSV;
		this.reasonWhyId = reasonWhyId;
		
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setParaText(String paraText) {
		this.paraText = paraText;
	}

	public void setBlockQuote(String blockQuote) {
		this.blockQuote = blockQuote;
	}

	public void setImageListCSV(String imageListCSV) {
		this.imageListCSV = imageListCSV;
	}

	public String getId() {
		return this.id;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public String getParaText() {
		return this.paraText;
	}

	public String getBlockQuote() {
		return this.blockQuote;
	}

	public String getImageListCSV() {
		return this.imageListCSV;
	}
	
	public String getReasonWhyId() {
		return reasonWhyId;
	}

	public void setReasonWhyId(String reasonWhyId) {
		this.reasonWhyId = reasonWhyId;
	}
}
