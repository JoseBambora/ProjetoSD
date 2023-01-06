package ProtocoloMensagens;

import java.nio.ByteBuffer;

public class MensagemNotificacoes extends Mensagem {
    private int x;
    private int y;

    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }

    public MensagemNotificacoes(int id,int x,int y)
    {
        super(id);
        this.x = x;
        this.y = y;
    }

    public Frame createFrame()
    {
        byte[] bytes = ByteBuffer.allocate(8)
                                 .putInt(this.getX()).putInt(this.getY()).array();
                                 
        return new Frame(getId(),getTipo("Notificacoes"),bytes); 
    }
    public Frame createFrameResponse()
    {
        byte[] strRes = ("Já existem recompensas perdo das coordenadas (" + x + "," + y + ")").getBytes();

        return new Frame(getId(),getTipo("Notificacoes"),strRes);
    }

    public static MensagemNotificacoes receive(Frame frame)
    { 
        byte []b = frame.data;
        ByteBuffer bb = ByteBuffer.wrap(b);
        int x = bb.getInt();
        int y = bb.getInt();

        return new MensagemNotificacoes(frame.tag, x, y);

    }

}
