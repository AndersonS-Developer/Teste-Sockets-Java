package org.abprojeto.sockets.server;

import org.abprojeto.sockets.protocol.Request;
import org.abprojeto.sockets.protocol.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 * Thread responsável por atender um cliente conectado ao servidor.
 *
 * <p>
 * Cada instância desta classe é associada a um único cliente e executada
 * em uma thread independente. Essa abordagem permite que o servidor atenda
 * múltiplos clientes simultaneamente, caracterizando uma arquitetura paralela.
 * </p>
 *
 * <p>
 * A thread permanece ativa enquanto a conexão for persistente, processando
 * múltiplas requisições do cliente. O encerramento da conexão ocorre quando
 * o cliente indica explicitamente que deseja finalizar a comunicação.
 * </p>
 */
public class ClientHandler implements Runnable {

    /**
     * Socket associado ao cliente conectado.
     */
    private final Socket socket;

    /**
     * Repositório contendo as mensagens disponíveis no servidor.
     * Este objeto é compartilhado entre todas as threads e utilizado
     * apenas para leitura.
     */
    private final MessageRepository repository;

    /**
     * Gerador de números aleatórios utilizado para seleção de mensagens.
     */
    private final Random random = new Random();

    /**
     * Construtor do manipulador de cliente.
     *
     * @param socket socket do cliente conectado
     * @param repository repositório de mensagens do servidor
     */
    public ClientHandler(Socket socket, MessageRepository repository) {
        this.socket = socket;
        this.repository = repository;
    }

    /**
     * Método executado pela thread responsável pelo atendimento do cliente.
     *
     * <p>
     * Este método realiza a leitura das requisições enviadas pelo cliente,
     * valida o número da mensagem solicitada e envia a resposta apropriada
     * conforme o protocolo definido.
     * </p>
     */
    @Override
    public void run() {
        System.out.println("Cliente conectado: " + socket.getInetAddress());

        try (
                // Stream para leitura das requisições do cliente
                DataInputStream in = new DataInputStream(socket.getInputStream());

                // Stream para envio das respostas ao cliente
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {

            boolean keepRunning = true;

            // Loop de atendimento (conexão persistente)
            while (keepRunning) {

                Request request = Request.read(in);
                int number = request.getMessageNumber();

                Response response;

                int totalMessages = repository.getTotalMessages();

                // Validação do número solicitado
                if (number < 0 || number > totalMessages) {

                    String errorMsg = "Numero invalido. Valores validos: 0 a " + totalMessages;
                    response = new Response(Response.STATUS_ERROR, errorMsg);

                } else {

                    String message;

                    // Seleção da mensagem solicitada
                    if (number == 0) {
                        message = repository.getRandomMessage();
                    } else {
                        message = repository.getMessageByNumber(number);
                    }

                    response = new Response(Response.STATUS_OK, message);
                }

                // Envia a resposta ao cliente
                response.write(out);

                // Verifica se a conexão deve ser encerrada
                if (request.shouldCloseConnection()) {
                    keepRunning = false;
                }
            }

        } catch (IOException e) {
            System.out.println("Erro na comunicacao com o cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Conexao encerrada com: " + socket.getInetAddress());
            } catch (IOException ignored) {
            }
        }
    }
}
