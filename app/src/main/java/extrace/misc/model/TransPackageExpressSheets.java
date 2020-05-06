package extrace.misc.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransPackageExpressSheets implements Serializable {

	private static final long serialVersionUID = -120165903123234592L;

	@Expose private TransPackage tp;
	
	@Expose private ArrayList<ExpressSheet> expressSheets;

	public TransPackage getTp() {
		return tp;
	}

	public void setTp(TransPackage tp) {
		this.tp = tp;
	}

	public List<ExpressSheet> getExpressSheets() {
		return expressSheets;
	}

	public void setExpressSheets(ArrayList<ExpressSheet> expressSheets) {
		this.expressSheets = expressSheets;
	}

	@Override
	public String toString() {
		return "TransPackageExpressSheets [pID=" + tp.toString() + ", expressSheets=" + expressSheets + "]";
	}
	
}
