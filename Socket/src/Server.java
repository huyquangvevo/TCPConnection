import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
	
	public String message;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
			System.out.println("The SocketReceive Server is running!");
			int clientNumber = 0;
			try {
				ServerSocket listener = new ServerSocket(9898);
				
				while (true) {
					 new SocketReceive(listener.accept(), clientNumber++).start();
				}
						
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}		


	private static class SocketReceive extends Thread {
			 private Socket socket;
			 private int clientNumber;
			 public SocketReceive(Socket socket, int clientNumber) {
				 this.socket = socket;
				 this.clientNumber = clientNumber;
				 System.out.println("New client #" + clientNumber + " connected at " + socket);
			 }
			 public void run() {
				 
				 try {
					 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					 PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
					 out.println("Hello, you are client #" + clientNumber);
				 while (true) {
					// PrintWriter outStream = new PrintWriter(socket.getOutputStream(),true);
					 BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));	 				 
					 System.out.println(inClient.readLine());
					// Scanner scanner = new Scanner(System.in);
					// String message = scanner.nextLine();
					// outStream.println(message);
				 } 
				 } catch (IOException e) {
					 System.out.println("Error handling client #" + clientNumber);
				 } finally {
					 try { socket.close(); } catch (IOException e) {}
					 System.out.println("Connection with client # " + clientNumber + " closed");
				 }
			 }
		}
	
	private static class SocketSend extends Thread {
		private Socket socket;
		private int clientNumber;
		
		public SocketSend(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New client #" + clientNumber + " connected at " + socket);
		}
		
		public void run() {
			try {
				 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				 PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
				 out.println("Hello, you are client #" + clientNumber);
			 while (true) {
				 PrintWriter outStr = new PrintWriter(socket.getOutputStream(),true);
				 
				// BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));	 
				// System.out.println(inClient.readLine());
				// Scanner scanner = new Scanner(System.in);
				// String message = scanner.nextLine();
				// outStream.println(message);
			 } 
			 } catch (IOException e) {
				 System.out.println("Error handling client #" + clientNumber);
			 } finally {
				 try { socket.close(); } catch (IOException e) {}
				 System.out.println("Connection with client # " + clientNumber + " closed");
			 }
		}
		
	}
}
