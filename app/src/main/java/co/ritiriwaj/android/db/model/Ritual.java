package co.ritiriwaj.android.db.model;

public class Ritual {

	private String id;
	private String name;
	private String date;
	private String nepDate;
	private String what;
	private String image;
	private String videoURL;
	private String history;
	private String significance;
	private String interestingFacts;
	private String source;
	private String type;
	private String lastModified;
	private String status;

	public Ritual(String id, String name, String image, String date,
			String nep_date, String type, String last_modified, String what,
			String history, String significance, String interestingFacts,
			String source, String videoURL, String status) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.date = date;
		this.nepDate = nep_date;
		this.type = type;
		this.lastModified = last_modified;
		this.what = what;
		this.history = history;
		this.significance = significance;
		this.interestingFacts = interestingFacts;
		this.source = source;
		this.videoURL = videoURL;
		this.setStatus(status);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNepDate() {
		return nepDate;
	}

	public void setNepDate(String nepDate) {
		this.nepDate = nepDate;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getVideoURL() {
		return videoURL;
	}

	public void setVideoURL(String videoURL) {
		this.videoURL = videoURL;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getSignificance() {
		return significance;
	}

	public void setSignificance(String significance) {
		this.significance = significance;
	}

	public String getInterestingFacts() {
		return interestingFacts;
	}

	public void setInterestingFacts(String interestingFacts) {
		this.interestingFacts = interestingFacts;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
