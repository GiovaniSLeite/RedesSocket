
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

final class HttpRequest implements Runnable {

    final static String CRLF = "\r\n";
    Socket socket;

    // Construtor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }
    // Implementar o método run() da interface Runnable.

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println("ERROOR: "+ e);
        }
    }

    private void processRequest() throws Exception {
        // Obter uma referência para os trechos de entrada e saída do socket
        
        InputStream is = socket.getInputStream(); //INSTANCIAR
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());//INSTANCIAR
        // Ajustar os filtros do trecho de entrada.
        //ALGUMA COISA ACONTECE NISSO
        BufferedReader br = new BufferedReader(new InputStreamReader(is)); //INSTANCIAR
        //os.write(12);
        // Obter a linha de requisição da mensagem de requisição HTTP.
        String requestLine = br.readLine();
        
        //  Exibir a linha de requisição.
        System.out.println();
        System.out.println(requestLine);
        
        // Obter e exibir as linhas de cabeçalho.
        String headerLine = "";
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }
        
        // Feche as cadeias e socket.
        os.close();
        br.close();
        socket.close();
    }

}
