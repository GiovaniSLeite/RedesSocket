
import java.io.*;
import java.net.*;
import java.util.*;

final class HttpRequest implements Runnable {

    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implemente o método run() da interface Runnable.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {

        // Obter uma referência para os trechos de entrada e saída do socket.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Ajustar os filtros do trecho de entrada.
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // Obter a linha de requisição da mensagem de requisição HTTP.
        String requestLine = br.readLine();

        //  Exibir a linha de requisição.
        System.out.println();
        System.out.println(requestLine);

        // Extrair o nome do arquivo a linha de requisição.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        //tokens.nextToken();
        tokens.nextToken(); // pular o método, que deve ser “GET”
        String fileName = tokens.nextToken();
        // Acrescente um “.” de modo que a requisição do arquivo esteja dentro do diretório atual.
        fileName = "." + fileName;
        System.out.println("Filename to Get: " + fileName);
   
        
        // Abrir o arquivo requisitado.
        FileInputStream fis = null;
        Boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        /*
        Existem três partes para a mensagem de resposta: a linha de status, 
        os cabeçalhos da resposta e o corpo da entidade. A linha de status e 
        os cabeçalhos da resposta são terminados pela de seqüência de caracteres 
        CRLF. Iremos responder com uma linha de status, que armazenamos na 
        variável statusLine, e um único cabeçalho de resposta, que armazenamos 
        na variável contentTypeLine. No caso de uma requisição de um arquivo 
        não existente, retornamos 404 Not Found na linha de status da mensagem 
        de resposta e incluímos uma mensagem de erro no formato de um documento
        HTML no corpo da entidade.
         */
        // Construir a mensagem de resposta.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK: ";
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.1 404 Not Found: ";
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Nao encontrado</TITLE></HEAD>" + "<BODY>O arquivo requisitado nao foi encontrado.</BODY></HTML>";
        }
        
        // Enviar a linha de status.
        os.writeBytes(statusLine);

        // Enviar a linha de tipo de conteúdo.
        os.writeBytes(contentTypeLine);

        // Enviar uma linha em branco para indicar o fim das linhas de cabeçalho.
        os.writeBytes(CRLF);

        // Enviar o corpo da entidade.
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        // Obter e exibir as linhas de cabeçalho.     
   String headerLine = null;
   while ((headerLine = br.readLine()).length() != 0) { 
    System.out.println(headerLine);
   }
        
        // Feche as cadeias e socket.
        os.close();
        br.close();
        socket.close();
    }

    private String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
                return "text/html";
	}
        //Implementar para outros tipos
           if (fileName.endsWith(".gif")) {
                return "image/gif"; 
        }
        if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
                return "image/jpeg"; 
        }
        return "application/octet-stream";
    }

    private void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception {
        // Construir um buffer de 1K para comportar os bytes no caminho para o socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // Copiar o arquivo requisitado dentro da cadeia de saída do socket.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

}
