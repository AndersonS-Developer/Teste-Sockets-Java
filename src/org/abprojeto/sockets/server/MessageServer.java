package org.abprojeto.sockets.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor principal do serviço de mensagens.
 *
 * <p>
 * Esta classe é responsável por inicializar o servidor, carregar o conjunto
 * de mensagens a partir de um arquivo texto e aguardar conexões de clientes.
 * O servidor utiliza o modelo cliente/servidor com comunicação baseada em
 * sockets TCP/IP.
 * </p>
 *
 * <p>
 * Para cada cliente que se conecta, o servidor cria uma nova thread de atendimento,
 * permitindo o processamento simultâneo de múltiplos clientes. Essa abordagem
 * caracteriza uma arquitetura paralela, conforme os requisitos do trabalho.
 * </p>
 */
public class MessageServer {

    /**
     * Porta na qual o servidor ficará aguardando conexões.
     */
    private static final int PORT = 5000;

    /**
     * Caminho do arquivo contendo as mensagens do servidor.
     */
    private static final String MESSAGE_FILE = "src/Messages.txt";

    /**
     * Método principal do servidor.
     *
     * <p>
     * Realiza a inicialização do repositório de mensagens, cria o socket do servidor
     * e permanece em execução aguardando conexões de clientes. Cada conexão aceita
     * resulta na criação de uma nova thread responsável pelo atendimento do cliente.
     * </p>
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {

        try {
            // Carrega as mensagens a partir do arquivo texto
            MessageRepository repository = new MessageRepository(MESSAGE_FILE);

            // Cria o socket do servidor na porta especificada
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("Servidor iniciado na porta " + PORT);

            // Loop principal do servidor: aguarda conexões de clientes
            while (true) {

                // Aguarda um cliente se conectar (chamada bloqueante)
                Socket clientSocket = serverSocket.accept();

                // Cria um manipulador para o cliente conectado
                ClientHandler handler =
                        new ClientHandler(clientSocket, repository);

                // Cria e inicia uma nova thread para atendimento do cliente
                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
