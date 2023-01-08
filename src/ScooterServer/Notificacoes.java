package ScooterServer;

import Comunicacao.TaggedConnection;
import ProtocoloMensagens.Frame;
import ProtocoloMensagens.MensagemNotificacoes;
import java.io.IOException;

public class Notificacoes
{
    private final String codigo;
    private final int x;
    private final int y;
    private final TaggedConnection toClient;
    private final int tag;
    public Notificacoes(String codigo,int x, int y, int tag,TaggedConnection toClient)
    {
        this.codigo = codigo;
        this.x = x;
        this.y = y;
        this.tag = tag;
        this.toClient = toClient;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void escreveMensagem()
    {
        try
        {
            MensagemNotificacoes mensagemNotificacoes = new MensagemNotificacoes(this.tag,x,y);
            Frame frame = mensagemNotificacoes.createFrameResponse();
            this.toClient.send(frame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
