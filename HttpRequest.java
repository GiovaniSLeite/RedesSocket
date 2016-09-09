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
        tokens.nextToken(); // pular o primeiro método, que deve ser “GET”
        String fileName = tokens.nextToken();
        
        // Acrescente um “.” de modo que a requisição do arquivo esteja dentro do diretório atual.
        fileName = "." + fileName;

        System.out.println("Nome do Arquivo buscado: " + fileName);

        // Abrir o arquivo requisitado.
        FileInputStream fis = null;
        Boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }
        
        // Define as strings das mensagens de resposta.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        
        //Se o arquivo requisitado existir
        if (fileExists) {
            
            //Escreve que o Status está OK
            statusLine = "HTTP/1.1 OK"; 
            
            //Recupera o tipo do arquivo atraves do metodo contentType
            contentTypeLine = "Content - type: " + contentType(fileName) + CRLF; 
            
          //Se o arquivo nao existir  
        } else {
            
            //Escreve que o Status eh "Nao encontrado"
            statusLine = "HTTP/1.1 404 Not Found";
            
            //Define a mensagem padrao de "Not found"
            contentTypeLine = "Content-Type: text/html" + CRLF;//Arrumar
            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
        
        }
        
        // Obter e exibir as linhas de cabeçalho.
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
        System.out.println(headerLine);
}

        // Enviar a linha de status.
        os.writeBytes(statusLine);

        // Enviar a linha de tipo de conteúdo.
        os.writeBytes(contentTypeLine);

        // Enviar uma linha em branco para indicar o fim das linhas de cabeçalho.
        os.writeBytes(CRLF);

        // Enviar o corpo da entidade, para ambos os casos (arquivo existir ou nao)
        if (fileExists) {
            sendBytes(fis, os);
            fis.close(); //Fecha o FileInputStream
        } else {
            os.writeBytes(entityBody);
        }
        
        //Fecha todas as conexoes restantes
        os.close();
        br.close();
        socket.close();
    }

    //Recupera o tipo do conteudo do arquivo requisitado
    private String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
                return "text/html";
	}
        if (fileName.endsWith(".gif")) {
                return "image/gif"; 
        }
        if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
                return "image/jpeg"; 
        }
        
        //Este metodo pode ser incrementado para o reconhecimento de outros tipos de arquivos
        
        return "application/octet-stream";
    }

    //Metodo de envio de Bytes
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
