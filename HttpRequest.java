
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

final class HttpRequest implements Runnable {

    //Contantes de final de linha e instacia do socket
    final static String CRLF = "\r\n";
    Socket socket;

    //Senha que autoriza os acessos ao diretorio
    String ADMINISTRADOR = "admin:admin";
    ArrayList<String> DIRETORIOSOCULTOS;
    ArrayList<String> DIRETORIOSRESTRITOS;

    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
        DIRETORIOSRESTRITOS = new ArrayList<String>();
        DIRETORIOSOCULTOS = new ArrayList<String>();
        DIRETORIOSOCULTOS.add("./src");
        DIRETORIOSRESTRITOS.add("./test");
    }

    // Implemente o método run() da interface Runnable.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
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

        //  Exibir a linha de requisição e grava no log
        System.out.println();
        System.out.println(requestLine);

        //Grava no Log a operação requisitada
        Log.geraLog("Operacao requisitada: " + requestLine);

        // Extrair o nome do arquivo a linha de requisição.
        StringTokenizer tokens = new StringTokenizer(requestLine);

        // pular o método, que deve ser “GET”
        tokens.nextToken();

        //Define o nome do arquivo a ser procurado
        String fileName = tokens.nextToken();

        // Acrescente um “.” de modo que a requisição do arquivo esteja dentro do diretório atual.
        fileName = "." + fileName;

        //Imprime na tela qual eh o arquivo/diretorio solicitado
        System.out.println("Filename to Get: " + fileName);

        //Variavel para verificacao de autenticacao do usuario.
        boolean autorizado = false;

        // Trecho que obtem e exibe na tela as linhas de cabeçalho.     
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            
            //Exibe na tela cada linha do cabeçalho
            System.out.println(headerLine);

            //Grava no Log qual o endereço da origem da requisição
            if (headerLine.contains("Host:")) {
                Log.geraLog("Endereco: " + headerLine.substring(5));
            }

            //Apenas faz a verificacao de login em diretorios restritos
            if (!fileName.equals("./") && DIRETORIOSRESTRITOS.contains(fileName)) {

                //Bloco que faz a autenticacao do usuario. Primeiro, verifica no cabecalho o usuario e senha requisitados
                if (headerLine.contains("Authorization")) {
                    //Pega a string que contem o user e a senha criptografadas
                    String userSenha = headerLine.split(" ")[2];

                    //Descriptografa o usuario e a senha
                    byte[] decodedBytes = Base64.getDecoder().decode(userSenha.getBytes());

                    //Se o usuario e senha possuem permissao, entao autoriza o acesso
                    if (new String(decodedBytes, Charset.forName(("UTF-8"))).equals(ADMINISTRADOR)) {
                        autorizado = true;
                    }
                }
            } else {
                autorizado = true;
            }
        }

        //Criacao de um file, para identificar os arquivos / diretorios requisitados       
        File targetFile = new File(new File("."), fileName);

        //Instancia as linhas de cabeçalho da requisição
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        //Variavel para conferencia se o arquivo requisitado existe ou nao
        Boolean fileExists = true;

        //Instacia o FileInputStream e o StringBuffer
        FileInputStream fis = null;
        StringBuffer sb = new StringBuffer();

        //Se o arquivo requisitado existe...
        if (targetFile.exists()) {

            //Se é um arquivo e nao um diretorio...
            if (targetFile.isFile()) {
                fis = new FileInputStream(targetFile);

                //Caso contrario, se nao for um arquivo pode ser um diretorio    
            } else if (targetFile.isDirectory()) {

                //Se o usuario nao foi autorizado, retorna a mensagem no cabeçalho seguindo o padrao
                if (!autorizado) {
                    statusLine = "HTTP/1.1 401 UNATHORIZED" + CRLF;
                    contentTypeLine = "WWW-Authenticate: Basic realm=\\\"RestrictedAccess\\\"";
                }

                //Se for um diretorio, cria um vetor com uma lista de Files do diretório
                File files[] = targetFile.listFiles();

                //Cria a pagina HTML de listagem de diretorios
                sb = listaDiretoriosEmHTML(sb, files, targetFile);

            }

            //Caso nenhum dos casos acima for satisfeito, o arquivo / diretorio requisitado nao existe    
        } else {
            fileExists = false;
        }

        // Construir a mensagem de resposta, caso a requisicao é valida e o usuario esta validado
        if (fileExists && autorizado && !DIRETORIOSOCULTOS.contains(fileName)) {

            //Define a mensagem de status
            statusLine = "HTTP/1.1 200 OK" + CRLF;

            //Se o que é requisitado nao é um diretorio...
            if (!targetFile.isDirectory()) {
                contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;

                //Caso contrario, é um diretorio...    
            } else {
                contentTypeLine = "Content-Type: text/html" + CRLF;
            }

        //Se o arquivo / diretorio requisitado nao existe, cria a mensagem em HTML de "Not Found"  
        } else if (autorizado && !DIRETORIOSOCULTOS.contains(fileName)) {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Nao encontrado</TITLE></HEAD>" + "<BODY>O arquivo requisitado nao foi encontrado.</BODY></HTML>";
        }
         
        //Caso o diretorio seja oculto...
        else if (DIRETORIOSOCULTOS.contains(fileName)){
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>O conteúdo do diretório não pode ser listado</TITLE></HEAD>" + "<BODY>O conteúdo do diretório não pode ser listado.</BODY></HTML>";
        }
        

        // Enviar a linha de status.
        os.writeBytes(statusLine);

        // Enviar a linha de tipo de conteúdo.
        os.writeBytes(contentTypeLine);

        // Enviar uma linha em branco para indicar o fim das linhas de cabeçalho.
        os.writeBytes(CRLF);

        //Envia a pagina que lista os diretorios
        if (targetFile.isDirectory()) {
            os.writeBytes(sb.toString());
        }

        // Enviar o corpo da entidade.
        if (fileExists) {
            if (fis != null) {
                sendBytes(fis, os);
                fis.close();
            }
        } else {
            os.writeBytes(entityBody);
        }

        // Fecha as cadeias e o socket.
        os.close();
        br.close();
        socket.close();
    }

    //Metodo que retorna o tipo do conteudo (pode ser incrementado para o reconhecimento de mais tipos)
    private String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html") || fileName.endsWith("//")) {
            return "text/html";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    //Metodo que envia os Bytes de tranferencia da requisição 
    private void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception {

        // Construir um buffer de 1K para comportar os bytes no caminho para o socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copiar o arquivo requisitado dentro da cadeia de saída do socket.
        while ((bytes = fis.read(buffer)) != -1) {
            Log.geraLog("Bytes transmitidos: " + Integer.toString(bytes)); //Grava no Log
            os.write(buffer, 0, bytes);
        }
    }

    //Metodo que cria a pagina HTML que lista os diretorios
    public StringBuffer listaDiretoriosEmHTML(StringBuffer sb, File[] files, File targetFile) {
        //Trecho HTML da pagina
        sb.append("\n<html>");
        sb.append("\n<head>");
        sb.append("\n<style>");
        sb.append("\n</style>");
        sb.append("\n<title>List of files/dirs under /scratch/mseelam/view_storage/mseelam_otd1/otd_test/./work</title>");
        sb.append("\n</head>");
        sb.append("\n<body>");
        sb.append("\n<div class=\"datagrid\">");
        sb.append("\n<table>");
        sb.append("\n<caption>Directory Listing</caption>");
        sb.append("\n<thead>");
        sb.append("\n	<tr>");
        sb.append("\n		<th>File</th>");
        sb.append("\n		<th>Dir ?</th>");
        sb.append("\n		<th>Size</th>");
        sb.append("\n		<th>Date</th>");
        sb.append("\n	</tr>");
        sb.append("\n</thead>");
        sb.append("\n<tfoot>");
        sb.append("\n	<tr>");
        sb.append("\n		<th>File</th>");
        sb.append("\n		<th>Dir ?</th>");
        sb.append("\n		<th>Size</th>");
        sb.append("\n		<th>Date</th>");
        sb.append("\n	</tr>");
        sb.append("\n</tfoot>");
        sb.append("\n<tbody>");

        //Recupera o numero de arquivos
        int numberOfFiles = files.length;

        //Laço que percorre todos os itens do diretorio
        for (int i = 0; i < numberOfFiles; i++) {

            //Define tabulacao
            if (i % 2 == 0) {
                sb.append("\n\t<tr class='alt'>");
            } else {
                sb.append("\n\t<tr>");
            }

            //Caso for diretorio, define como tal na coluna Dir, alem de recuperar a data de modificação
            if (files[i].isDirectory()) {
                sb.append("\n\t\t<td><a href='" + files[i].getName() + "/'>" + files[i].getName() + "</a></td>"
                        + "<td>Y</td>" + "<td>" + files[i].length()
                        + "</td>" + "<td>" + (new Date(files[i].lastModified())) + "</td>\n\t</tr>");

                //Caso contrario, é um arquivo. Tambem recupera a data de modificacao
            } else {
                sb.append("\n\t\t<td><a href='" + targetFile.getParent() + "/" + files[i].getName() + "'>" + files[i].getName() + "</a></td>"
                        + "<td>N</td>" + "<td>" + files[i].length()
                        + "</td>" + "<td>" + (new Date(files[i].lastModified())) + "</td>\n\t</tr>");
            }
        }

        //Finaliza a pagina HTML
        sb.append("\n</tbody>");
        sb.append("\n</table>");
        sb.append("\n</div>");
        sb.append("\n</body>");
        sb.append("\n</html>");

        //Retorna o StringBuffer com a pagina em HTML armazenada
        return sb;
    }

}
