package server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public final class WebServer
{
	public static void main(String argv[]) throws Exception
	{
		// Set the port number.
		int port = 6789;
		
		// Establish the listen socket.
		ServerSocket server = new ServerSocket(port);
		
		// Process HTTP service requests in an infinite loop.
		while (true) {
			// Listen for a TCP connection request.
			Socket client = server.accept();
			
			// Construct an object to process the HTTP request message.
			HttpRequest request = new HttpRequest(client);//( ? );
			// Create a new thread to process the request.
			Thread thread = new Thread(request);
			// Start the thread.
			thread.start();
		}
	}
}