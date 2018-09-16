package server;

import java.net.Socket;
import java.util.StringTokenizer;
import java.io.*;

final class HttpRequest implements Runnable
{
	final static String CRLF = "\r\n";
	Socket socket;
	
	// Constructor
	public HttpRequest(Socket socket) throws Exception
	{
		this.socket = socket;
	}
	
	// Implement the run() method of the Runnable interface.
	public void run()
	{
		try {
			processRequest();
		} 
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void processRequest() throws Exception
	{
		// Get a reference to the socket's input and output streams.
		InputStream is = this.socket.getInputStream();
		DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
		
		// Set up input stream filters.
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		// Get the request line of the HTTP request message.
		String requestLine = br.readLine();
		
		// Display the request line.
		System.out.println();
		System.out.println(requestLine);
		
		// Get and display the header lines.
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		
		// ===== Part B

		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();
		// Prepend a "." so that file request is within the current directory.
		fileName = "." + fileName;
		
		// Open the requested file.
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
			System.out.printf("==> Arquivo %s Existe! <==\n",fileName);
		} 
		catch (FileNotFoundException e) {
			System.out.printf("==> Arquivo %s Nao Existe! <==\n",fileName);
			fileExists = false;
		}
		
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-type: " +
			contentType( fileName ) + CRLF;
		} 
		else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-type: text/html" + CRLF;
			entityBody = "<HTML>" +
			"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
			"<BODY>Not Found <br> This is not the webpage you are looking for. </BODY></HTML>";
		}
		
		
		// Send the status line.
		os.writeBytes(statusLine);
		// Send the content type line.
		os.writeBytes(contentTypeLine);
		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);
		
		// Send the entity body.
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} 
		else {
			os.writeBytes(entityBody);
		}
		
		
		
		// Close streams and socket.
		os.close();
		br.close();
		socket.close();
		
	}
	
	private static void sendBytes(FileInputStream fis, OutputStream os)
	throws Exception
	{
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		// Copy requested file into the socket's output stream.
		while((bytes = fis.read(buffer)) != -1 ) {
			os.write(buffer, 0, bytes);
		}
	}
	
	private static String contentType(String fileName)
	{
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if(fileName.endsWith(".jpeg") ) {
			return "image/jpeg";
		}
		if( fileName.endsWith(".gif")) {
			return "image/gif";
		}
		if( fileName.endsWith(".txt")) {
			return "text/txt";
		}
		return "application/octet-stream";
	}
}