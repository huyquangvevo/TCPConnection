import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerDemo {
public String message;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
			System.out.println("The SocketReceive Server is running!");
			int clientNumber = 0;
			try {
				ServerSocket receiver = new ServerSocket(6000);
				ServerSocket listener = new ServerSocket(9898);
				
				//ServerSocket receiver2 = new ServerSocket()
				
				while (true) {
					 new SocketReceive(receiver.accept(), listener.accept(), clientNumber++).start();
				}
						
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}		


	private static class SocketReceive extends Thread {
			 private Socket socket;
			 private Socket socketPhone;
			 private int clientNumber;
			 public SocketReceive( Socket socketPhone,Socket socket, int clientNumber) {
				 this.socket = socket;
				 this.socketPhone = socketPhone;
				 this.clientNumber = clientNumber;
				 System.out.println("New client #" + clientNumber + " connected at " + socket);
			 }
			 public void run() {
				 
				 try {
					 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					 PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
					 out.println("Hello, you are client #" + clientNumber);
				 while (true) {
					 if(socket.isClosed() || socketPhone.isClosed()) {
						 System.out.println("Closed");
						 break;
					 }
					// PrintWriter outStream = new PrintWriter(socket.getOutputStream(),true);
					 BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));	 				 
					 PrintWriter outPhone = new PrintWriter(socketPhone.getOutputStream(),true);
					 
				//	 String s;
				//	 while((System.currentTimeMillis()< 500)) {
				//		 if (inClient)
				//	 }
					// System.out.println("Start buffer");
					 if(inClient.ready()) {
						 String str = inClient.readLine();
						 outPhone.println(str);
						 System.out.println(str);
					 } else {
						 BufferedReader inPhone = new BufferedReader(new InputStreamReader(socketPhone.getInputStream(),"UTF-8"));
						 PrintWriter outClient = new PrintWriter(socket.getOutputStream(),true);
						 if(inPhone.ready()) {
							 outClient.println(inPhone.readLine());
							 System.out.println("Phone to sensor");
						 }
						// System.out.println("Not ready!");
					 }
					
					// Scanner scanner = new Scanner(System.in);
					// String message = scanner.nextLine();
					// outStream.println(message);
				 } 
				 } catch (IOException e) {
					 System.out.println("Error handling client #" + clientNumber);
				 } finally {
					 try { 
						 socket.close();
						 socketPhone.close();
					 } catch (IOException e) {}
					 System.out.println("Connection with client # " + clientNumber + " closed");
				 }
			 }
		}
	
}
