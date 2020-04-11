package extrace.net;

import extrace.misc.model.UserInfo;

public interface LoginDataAdapter<T> extends IDataAdapter {
    public void setUserInfo(UserInfo userInfo);
}
