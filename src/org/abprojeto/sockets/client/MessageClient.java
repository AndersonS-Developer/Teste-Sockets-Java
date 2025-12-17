package org.abprojeto.sockets.client;

import org.abprojeto.sockets.protocol.Request;
import org.abprojeto.sockets.protocol.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente do serviço de mensagens distribuído.
 *
 * <p>
 * Esta aplicação cliente se conecta a um servidor de mensagens utilizando
 * sockets TCP/IP. O cliente permite ao usuário solicitar mensagens específicas
 * ou aleatórias, enviando requisições ao servidor conforme o protocolo definido.
 * </p>
 *
 * <p>
 * O cliente suporta conexões persistentes e transientes. A decisão de encerrar
 * ou manter a conexão após cada requisição é informada explicitamente pelo usuário.
 * </p>
 */
public class MessageClient {

    /**
     * Endereço do servidor.
     */
    private static final String HOST = "localhost";

    /**
     * Porta utilizada pelo servidor.
     */
    private static final int PORT = 5000;

    /**
     * Método principal do cliente.
     *
     * <p>
     * Estabelece a conexão com o servidor, envia requisições de mensagens e
     * recebe respostas até que o usuário opte por encerrar a conexão.
     * </p>
     *
     */
    public static void main(String[] args) {

        try (
                // Cria o socket de comunicação com o servidor
                Socket socket = new Socket(HOST, PORT);

                // Stream para leitura de dados enviados pelo servidor
                DataInputStream in = new DataInputStream(socket.getInputStream());

                // Stream para envio de dados ao servidor
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                // Scanner para leitura da entrada do usuário via console
                Scanner scanner = new Scanner(System.in)
        ) {

            System.out.println("Conectado ao servidor de mensagens.");

            boolean running = true;

            // Loop principal do cliente (conexão persistente)
            while (running) {

                System.out.print("Digite o numero da mensagem (0 para aleatoria): ");
                int number = scanner.nextInt();

                System.out.print("Encerrar conexao apos resposta? (s/n): ");
                String close = scanner.next();

                // Apenas a entrada "S" ou "s" encerra a conexão
                boolean closeConnection = close.equalsIgnoreCase("s");

                // Cria e envia a requisição ao servidor
                Request request = new Request(number, closeConnection);
                request.write(out);

                // Aguarda e lê a resposta do servidor
                Response response = Response.read(in);

                // Processa a resposta recebida
                if (response.getStatus() == Response.STATUS_OK) {
                    System.out.println("Mensagem: " + response.getMessage());
                } else {
                    System.out.println("Erro: " + response.getMessage());
                }

                // Verifica se o usuário solicitou o encerramento da conexão
                if (closeConnection) {
                    running = false;
                }

                System.out.println();
            }

            System.out.println("Cliente encerrado.");

        } catch (IOException e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
    }
}
