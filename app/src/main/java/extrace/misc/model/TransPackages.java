package extrace.misc.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class TransPackages {
	@Expose String telCode;
	@Expose ArrayList<String> transpackages;
	
	public TransPackages() {
		super();
	}
	
	public String getTelCode() {
		return telCode;
	}
	
	public void setTelCode(String telCode) {
		this.telCode = telCode;
	}
	
	public ArrayList<String> getTranspackages() {
		return transpackages;
	}
	
	public void setTranspackages(ArrayList<String> transpackages) {
		this.transpackages = transpackages;
	}
	
	@Override
	public String toString() {
		return "TransPackages [telCode=" + telCode + ", transpackages=" + transpackages + "]";
	}
}
