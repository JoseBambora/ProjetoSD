package ScooterServer;

import ProtocoloMensagens.Frame;

import javax.xml.crypto.Data;
import java.io.DataInputStream;

public class Notificacoes
{
    private String codigo;
    private int x;
    private int y;
    private DataInputStream toClient;
    private int tag;
    public Notificacoes(String codigo,int x, int y, int tag,DataInputStream toClient)
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
        // MensagemNotificacao
        // cria Frame
        // Escreve no dataInputStream
    }
}
