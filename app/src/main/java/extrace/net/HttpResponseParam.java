package extrace.net;


//HTTP应答返回参数

public class HttpResponseParam{
	RETURN_STATUS statusCode;	//状态码
	String responseClassName;	//当有多种返回的对象可能时,用这个名字来区分
	String responseString;		//响应的实体JSON字符串
	//String responseMessage;		//响应的消息字符串
	public HttpResponseParam(){
		statusCode = RETURN_STATUS.Ok;
		responseClassName = "";
	}

	public enum RETURN_STATUS{
		Ok,
		Saved,
		RequestException,
		ResponseException,
		ServerException,
		ObjectNotFoundException,
		NetworkException,
		Unknown
	}
}
