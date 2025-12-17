package org.abprojeto.sockets.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Representa uma requisição enviada do cliente para o servidor.
 *
 * <p>
 * Esta mensagem faz parte do protocolo de comunicação definido para o serviço
 * de mensagens. A requisição contém as informações necessárias para que o
 * servidor identifique qual mensagem deve ser retornada ao cliente, além de
 * indicar se a conexão deve ser encerrada após o atendimento.
 * </p>
 *
 * <p>
 * Formato da requisição (tamanho fixo):
 * <ul>
 *   <li>1 byte  - Tipo da mensagem</li>
 *   <li>4 bytes - Número da mensagem solicitada</li>
 *   <li>1 byte  - Indicador de encerramento da conexão</li>
 * </ul>
 * </p>
 */
public class Request {

    /**
     * Identificador do tipo de mensagem "requisição".
     */
    public static final byte TYPE_REQUEST = 1;

    /**
     * Número da mensagem solicitada pelo cliente.
     * Valores válidos: 0 a M, onde 0 indica solicitação aleatória.
     */
    private final int messageNumber;

    /**
     * Indica se a conexão deve ser encerrada após o envio da resposta.
     * true  → conexão transiente
     * false → conexão persistente
     */
    private final boolean closeConnection;

    /**
     * Construtor da requisição.
     *
     * @param messageNumber número da mensagem solicitada (0 para aleatória)
     * @param closeConnection indica se a conexão deve ser encerrada
     */
    public Request(int messageNumber, boolean closeConnection) {
        this.messageNumber = messageNumber;
        this.closeConnection = closeConnection;
    }

    /**
     * Retorna o número da mensagem solicitada.
     *
     * @return número da mensagem
     */
    public int getMessageNumber() {
        return messageNumber;
    }

    /**
     * Indica se a conexão deve ser encerrada após a resposta.
     *
     * @return true se a conexão deve ser encerrada, false caso contrário
     */
    public boolean shouldCloseConnection() {
        return closeConnection;
    }

    /**
     * Serializa e envia a requisição pelo stream de saída.
     *
     * @param out stream de saída associado ao socket
     * @throws IOException caso ocorra erro de escrita
     */
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(TYPE_REQUEST);       // Tipo da mensagem
        out.writeInt(messageNumber);       // Número da mensagem solicitada
        out.writeBoolean(closeConnection); // Indicador de encerramento
        out.flush();
    }

    /**
     * Lê e desserializa uma requisição a partir do stream de entrada.
     *
     * @param in stream de entrada associado ao socket
     * @return objeto Request construído a partir dos dados recebidos
     * @throws IOException caso ocorra erro de leitura ou tipo inválido
     */
    public static Request read(DataInputStream in) throws IOException {
        byte type = in.readByte();

        if (type != TYPE_REQUEST) {
            throw new IOException("Tipo de mensagem inválido");
        }

        int messageNumber = in.readInt();
        boolean closeConnection = in.readBoolean();

        return new Request(messageNumber, closeConnection);
    }
}
