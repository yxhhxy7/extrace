package extrace.misc.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PackageRouteWrapper implements Serializable {

    private static final long serialVersionUID = -120165903841234592L;

    @Expose private ArrayList<String> packageId;
    @Expose private ArrayList<PackageRoute> positionInfo;

    public PackageRouteWrapper(){

    }

    public PackageRouteWrapper(ArrayList<String> packageId, ArrayList<PackageRoute> positionInfo) {
        this.packageId = packageId;
        this.positionInfo = positionInfo;
    }

    public ArrayList<String> getPackageId() {
        return packageId;
    }

    public void setPackageId(ArrayList<String> packageId) {
        this.packageId = packageId;
    }

    public ArrayList<PackageRoute> getPositionInfo() {
        return positionInfo;
    }

    public void setPositionInfo(ArrayList<PackageRoute> positionInfo) {
        this.positionInfo = positionInfo;
    }

    @Override
    public String toString() {
        return "PackageRouteWrapper{" +
                "packageId=" + packageId +
                ", positionInfo=" + positionInfo +
                '}';
    }
}
