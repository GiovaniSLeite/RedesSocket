import java.net.*;

public final class WebServer {

    public static void main(String arvg[]) throws Exception {
        // Ajustar o número da porta.
        int port = 6789;

        // Estabelecer o socket de escuta.
        ServerSocket servidor = new ServerSocket(port);
        System.out.println("Porta " + port + " aberta!");

        // Processar a requisição de serviço HTTP em um laço infinito.
        while (true) {
            // Escutar requisição de conexão TCP.
            Socket cliente = servidor.accept();

            //Construir um objeto para processar a mensagem de requisição HTTP.
            HttpRequest request = new HttpRequest(cliente);
            
            // Criar um novo thread para processar a requisição.
            Thread thread = new Thread(request);
            
            //Iniciar o thread.
            thread.start();

        }
    }

}

