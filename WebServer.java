import java.net.ServerSocket;
import java.net.Socket;
 
public final class WebServer {
 
    
   public static void main(String argv[]) throws Exception
 {
     // Ajustar o número da porta.
     int port =6789;
     
     // Estabelecer o socket de escuta.
     ServerSocket server = new ServerSocket(port);
     System.out.println("Porta " + port + " aberta!");
     // Processar a requisição de serviço HTTP em um laço infinito.
     while (true) {
      // Escutar requisição de conexão TCP.
      Socket conexao = server.accept();
      //Construir um objeto para processar a mensagem de requisição HTTP.
      HttpRequest request = new HttpRequest(conexao);
      
      
      Thread thread = new Thread(request); // Criar um novo thread para processar a requisição.
      thread.start(); //Iniciar o thread.
     }
 }
}