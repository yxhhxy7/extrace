/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: 
 * License Type: Evaluation
 */
package extrace.misc.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.google.gson.annotations.Expose;

public class TransPackage  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3050390478904210174L;

	public TransPackage() {
	}
	
	@Expose private String ID;
	@Expose private String sourceNode;
	@Expose private String targetNode;
	@Expose private HashSet<PackageRoute> route;
	@Expose private Date createTime;
	@Expose private Date acceptTime;
	@Expose private int status;
	
	public void setID(String value) {
		this.ID = value;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getORMID() {
		return getID();
	}

	public void setSourceNode(String value) {
		this.sourceNode = value;
	}
	
	public String getSourceNode() {
		return sourceNode;
	}

	public void setTargetNode(String value) {
		this.targetNode = value;
	}
	
	public String getTargetNode() {
		return targetNode;
	}
	
	public void setCreateTime(Date value) {
		this.createTime = value;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setAcceptTime(Date value) {
		this.acceptTime = value;
	}

	public Date getAcceptTime() {
		return acceptTime;
	}

	public void setStatus(int value) {
		this.status = value;
	}
	
	public int getStatus() {
		return status;
	}

	public String toString() {
		return toString(false);
	}

	public HashSet<PackageRoute> getRoute() {
		return route;
	}

	public void setRoute(HashSet<PackageRoute> route) {
		this.route = route;
	}

	public String toString(boolean idOnly) {
		if (idOnly) {
			return String.valueOf(getID());
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append("TransPackage[ ");
			sb.append("ID=").append(getID()).append(" ");
			sb.append("SourceNode=").append(getSourceNode()).append(" ");
			sb.append("TargetNode=").append(getTargetNode()).append(" ");
			sb.append("CreateTime=").append(getCreateTime()).append(" ");
			sb.append("AcceptTime=").append(getAcceptTime()).append(" ");
			sb.append("Status=").append(getStatus()).append(" ");
			sb.append("Route=").append(getRoute()).append(" ");
			sb.append("]");
			return sb.toString();
		}
	}
	
	private boolean _saved = false;
	
	public void onSave() {
		_saved=true;
	}
	
	
	public void onLoad() {
		_saved=true;
	}
	
	
	public boolean isSaved() {
		return _saved;
	}
	
	
}
