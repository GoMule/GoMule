package TestyMcTest;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class testClient {

public testClient() throws MalformedURLException, XmlRpcException{
    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
    XmlRpcClient client = new XmlRpcClient();
    client.setConfig(config);
    Object[] params = new Object[]{new Integer(33), new Integer(9)};
    Integer result = (Integer) client.execute("Calculator.add", params);
    System.out.println(result);
}

public static void main(String[] args) throws MalformedURLException, XmlRpcException{
	new testClient();
}

}
