package org.abprojeto.sockets.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Repositório responsável por carregar e fornecer as mensagens do servidor.
 */
public class MessageRepository {

    private final List<String> messages = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Construtor que carrega as mensagens a partir de um arquivo texto.
     *
     * @param filePath src/Messages.txt
     * @throws IOException caso ocorra erro de leitura
     */
    public MessageRepository(String filePath) throws IOException {
        loadMessages(filePath);
    }

    private void loadMessages(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Primeira linha: total de mensagens
            int total = Integer.parseInt(reader.readLine());

            for (int i = 0; i < total; i++) {
                String line = reader.readLine();
                if (line != null) {
                    messages.add(line);
                }
            }
        }
    }

    /**
     * Retorna o total de mensagens disponíveis.
     */
    public int getTotalMessages() {
        return messages.size();
    }

    /**
     * Retorna uma mensagem pelo número (1 a M).
     *
     * @param number número da mensagem
     * @return mensagem correspondente
     */
    public String getMessageByNumber(int number) {
        return messages.get(number - 1);
    }

    /**
     * Retorna uma mensagem aleatória.
     */
    public String getRandomMessage() {
        int index = random.nextInt(messages.size());
        return messages.get(index);
    }
}
