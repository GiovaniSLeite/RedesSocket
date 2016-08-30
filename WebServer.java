
import java.net.ServerSocket;
import java.net.Socket;



public final class WebServer {

    public static void main(String arvg[]) throws Exception {
        // Ajustar o número da porta.
        int port = 6789;
        // Estabelecer o socket de escuta.
        ServerSocket server = new ServerSocket(port);
        
        
        // Processar a requisição de serviço HTTP em um laço infinito.
        while (true) {
            // Escutar requisição de conexão TCP.
            Socket s = server.accept();
           // System.out.println("Cliente conectado: " + s.getInetAddress().getHostAddress());
            //quando recebida
            //Construir um objeto para processar a mensagem de requisição HTTP.
            //if(s.isConnected()){
            HttpRequest request = new HttpRequest(s);//ARRUMAR
            // Criar um novo thread para processar a requisição.
            Thread thread = new Thread(request);
            //Iniciar o thread.
            thread.start();//}
            //s.close();
        }

    }

}
