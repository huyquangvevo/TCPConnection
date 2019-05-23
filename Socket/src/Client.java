import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		Socket socket;
		try {
			socket = new Socket("192.168.43.164", 9898);
			while(true) {
			//	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//	String msg = in.readLine();
			//	if(msg!=null)
			//		System.out.println(msg);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				Scanner scanner = new Scanner(System.in);
				String msgOut = scanner.nextLine();
				out.println(msgOut);
			}
		//	Scanner scanner = new Scanner(System.in);
		//	String message = scanner.nextLine(); 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
	}	
	
}
