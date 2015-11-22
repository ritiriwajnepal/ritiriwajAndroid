package co.ritiriwaj.android.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class EthnicGroupAndCaste implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* ethinicGroup will contain (egId, egName) */
	HashMap<String, String> ethinicGroup;
	/* listCaste will contain a list of (egcId, cName) */
	List<HashMap<String, String>> listCaste;

	public EthnicGroupAndCaste(HashMap<String, String> ethinicGroup,
			List<HashMap<String, String>> listCaste) {
		this.ethinicGroup = ethinicGroup;
		this.listCaste = listCaste;
	}

	public void setEthinicGroup(HashMap<String, String> ethinicGroup) {
		this.ethinicGroup = ethinicGroup;
	}

	public void setCasteList(List<HashMap<String, String>> casteList) {
		this.listCaste = casteList;
	}

	public HashMap<String, String> getEthinicGroup() {
		return this.ethinicGroup;
	}

	public List<HashMap<String, String>> getCasteList() {
		return this.listCaste;
	}
}
