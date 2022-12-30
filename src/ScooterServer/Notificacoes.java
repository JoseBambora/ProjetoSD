package ScooterServer;

import Comunicacao.TaggedConnection;
import ProtocoloMensagens.Frame;
import ProtocoloMensagens.MensagemNotificacoes;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.IOException;

public class Notificacoes
{
    private String codigo;
    private int x;
    private int y;
    private TaggedConnection toClient;
    private int tag;
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
            Frame frame = mensagemNotificacoes.createFrame();
            this.toClient.send(frame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
