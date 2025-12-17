package org.abprojeto.sockets.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Representa a resposta enviada do servidor para o cliente.
 *
 * <p>
 * Esta mensagem faz parte do protocolo de comunicação do serviço de mensagens.
 * A resposta do servidor informa ao cliente o resultado da requisição realizada,
 * podendo conter uma mensagem válida ou uma mensagem de erro.
 * </p>
 *
 * <p>
 * Formato da resposta (tamanho fixo):
 * <ul>
 *   <li>1 byte   - Status da resposta</li>
 *   <li>127 bytes - Texto da mensagem</li>
 * </ul>
 * Totalizando 128 bytes.
 * </p>
 */
public class Response {

    /**
     * Indica que a requisição foi processada com sucesso.
     */
    public static final byte STATUS_OK = 0;

    /**
     * Indica que ocorreu um erro ao processar a requisição.
     */
    public static final byte STATUS_ERROR = 1;

    /**
     * Tamanho fixo do campo de texto da resposta.
     */
    private static final int TEXT_SIZE = 127;

    /**
     * Status da resposta (STATUS_OK ou STATUS_ERROR).
     */
    private final byte status;

    /**
     * Texto da mensagem retornada pelo servidor.
     * Pode representar uma mensagem válida ou uma mensagem de erro.
     */
    private final String message;

    /**
     * Construtor da resposta.
     *
     * @param status status da resposta
     * @param message texto da mensagem
     */
    public Response(byte status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Retorna o status da resposta.
     *
     * @return status da resposta
     */
    public byte getStatus() {
        return status;
    }

    /**
     * Retorna o texto da mensagem da resposta.
     *
     * @return mensagem retornada pelo servidor
     */
    public String getMessage() {
        return message;
    }

    /**
     * Serializa e envia a resposta pelo stream de saída.
     *
     * <p>
     * O texto da mensagem é convertido para bytes utilizando UTF-8 e ajustado
     * para um tamanho fixo de 127 bytes. Caso a mensagem possua menos caracteres,
     * o restante do campo é preenchido com espaços.
     * </p>
     *
     * @param out stream de saída associado ao socket
     * @throws IOException caso ocorra erro de escrita
     */
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(status);

        byte[] textBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[TEXT_SIZE];

        Arrays.fill(buffer, (byte) ' ');

        int length = Math.min(textBytes.length, TEXT_SIZE);
        System.arraycopy(textBytes, 0, buffer, 0, length);

        out.write(buffer);
        out.flush();
    }

    /**
     * Lê e desserializa uma resposta a partir do stream de entrada.
     *
     * @param in stream de entrada associado ao socket
     * @return objeto Response construído a partir dos dados recebidos
     * @throws IOException caso ocorra erro de leitura
     */
    public static Response read(DataInputStream in) throws IOException {
        byte status = in.readByte();

        byte[] buffer = new byte[TEXT_SIZE];
        in.readFully(buffer);

        String message = new String(buffer, StandardCharsets.UTF_8).trim();
        return new Response(status, message);
    }
}
