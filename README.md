# RedesSocket

Quando o servidor Web encontrar um erro, ele deve enviar uma mensagem de resposta com a fonte HTML apropriada, de forma que a informação do erro seja exibida na janela do browser.

Além destas funcionalidades básicas, o servidor Web deve:
1.	efetuar registros (logs) de todas as operações realizadas;
2.	permitir a configuração da listagem ou não de conteúdo de diretórios;
3.	permitir autenticação de acesso para o diretório restrito (sub-diretório do diretório principal do servidor web).

Logs:
O arquivo de log deve ser escrito em formato texto, e ser acessível através do servidor web. Ele deve conter as seguintes informações (mínimas): endereço origem, porta origem, horário da requisição, conteúdo requisitado, quantidade de bytes transmitidas em resposta a requisição.

Configuração da listagem ou não de conteúdo de diretórios:
Esta característica deve ser configurável (opção de execução do programa ou arquivo de configuração). Quando usuário acessa  uma url do tipo http://host.usp.br/diretorio/, as seguintes respostas são possíveis  (ou seja, opções de configuração):
1.	o conteúdo do diretório é listado em uma página html (opção que permite a listagem do diretório).
2.	uma página com uma mensagem do tipo “O conteúdo do diretório não pode ser listado” é retornada ao usuário (opção que não permite a listagem do diretório).
3.	uma página padrão é exibida: nesse caso, o servidor web exibe a página chamada index.html, e se esta não existir, exibe a mensagem de erro da opção 2.

Autenticação de acesso:
O servidor web deve as funções relativas a “Autenticação de Acesso” descritas no item 11 da RFC1945 [1]. Assim, quando o servidor receber uma requisição de acesso ao diretório restrito (sub-diretório do diretório principal do servidor web), ele deverá enviar mensagem ao cliente solicitando as informações de autenticação (usuário e senha).


Relatório:
O relatório deverá possuir formato condizente com um relatório científico, contendo uma introdução, desenvolvimento das seções, conclusão e referências bibliográficas.
O relatório deve conter a descrição da arquitetura do servidor, bem como justificativas para as decisões de projeto. Além disso, também deve incluir instruções de uso ou configurações, se necessário.

Referências:
[1] “RFC 1945 - Hypertext Transfer Protocol – HTTP/1.0”. T. Berners-Lee, R. Fielding, H. Frystyk.                                                               May 1996. Disponível em http://www.faqs.org/rfcs/rfc1945.html

