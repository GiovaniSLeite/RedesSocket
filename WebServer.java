
import java.net.ServerSocket;
import java.net.Socket;

public final class WebServer {

    public static void main(String argv[]) throws Exception {
        // Ajustar o numero da porta.
        int port = 6789;

        // Estabelecer o socket de escuta.
        ServerSocket server = new ServerSocket(port);
        System.out.println("Porta " + port + " aberta!");
        // Processar a requisicao de serviço HTTP em um laço infinito.
        while (true) {
            // Escutar requisicao de conexao TCP.
            Socket conexao = server.accept();
            //Construir um objeto para processar a mensagem de requisicao HTTP.
            HttpRequest request = new HttpRequest(conexao);

            Thread thread = new Thread(request); // Criar um novo thread para processar a requisicao.
            thread.start(); //Iniciar o thread.
        }
    }
}
