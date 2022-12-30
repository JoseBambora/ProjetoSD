package ProtocoloMensagens;

import java.nio.ByteBuffer;

public class MensagemNotificacoes extends Mensagem {
    private int x;
    private int y;
    private String notificacao;

    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public String getNotificacao()
    {
        return notificacao;
    }

    public MensagemNotificacoes(int id,int x,int y, String notificacao)
    {
        super(id);
        this.x = x;
        this.y = y;
        this.notificacao = notificacao;
    }

    public Frame createFrame()
    {
        byte[] bytes = ByteBuffer.allocate(8 + this.getCodigo().getBytes().length)
                                 .putInt(this.getX()).putInt(this.getY()).put(this.getCodigo().getBytes()).array();
                                 
        return new Frame(getId(),getTipo("Notificacoes"),bytes); 
    }
    public Frame createFrameResponse();
    public static MensagemEstacionamento receive(Frame frame);

}
