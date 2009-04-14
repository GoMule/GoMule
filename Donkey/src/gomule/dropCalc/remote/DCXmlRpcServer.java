package gomule.dropCalc.remote;

import gomule.dropCalc.DCNew;

import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;



public class DCXmlRpcServer{

	private final int port = 8080;
	public static final DCNew DC = new DCNew();
	
	
	public DCXmlRpcServer()  throws Exception{

	          XmlRpcServlet servlet = new XmlRpcServlet();
	          ServletWebServer webServer = new ServletWebServer(servlet, port);
	          
	          webServer.start();
	      }

	
	
	public static void main(String[] args) throws Exception {

		new DCXmlRpcServer();
		
		
	}

}
