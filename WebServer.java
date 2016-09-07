import java.net.ServerSocket;
import java.net.Socket;
 
public final class WebServer {
 
    
   public static void main(String argv[]) throws Exception
 {
     int port =6789;
     ServerSocket server = new ServerSocket(port);
     
     while (true) {
      // Listen for a TCP connection request.
      Socket conexao = server.accept();
      //Construct object to process HTTP request message
      HttpRequest request = new HttpRequest(conexao);
      
      
      Thread thread = new Thread(request);
      thread.start(); //start thread
     }
 }
}