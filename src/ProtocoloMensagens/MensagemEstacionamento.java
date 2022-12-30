package ProtocoloMensagens;
import java.nio.ByteBuffer;
import java.util.*;

public class MensagemEstacionamento extends Mensagem
{
    private final String codigo;
    private final int x;
    private final int y;
    private final String resultado_estacionamento;

    public MensagemEstacionamento(int id,int x,int y, String codigo, String resultado_estacionamento)
    {
        super(id);
        this.codigo = codigo;
        this.x = x;
        this.y = y;
        this.resultado_estacionamento = resultado_estacionamento;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public String getCodigo()
    {
        return codigo;
    }
    public Frame createFrame()
    {
        byte[] bytes = ByteBuffer.allocate(8 + this.getCodigo().getBytes().length)
                                 .putInt(this.getX()).putInt(this.getY()).put(this.getCodigo().getBytes()).array();

        return new Frame(getId(),getTipo("Estacionamento"),bytes);
    }
    public Frame createFrameResponse(float custo_viagem)
    {
        String s = ("\nPreço:\n------" + String.format("%.02f", custo_viagem) + "\n");
        return new Frame(getId(),getTipo("Estacionamento"),s.getBytes());
    }
    public static MensagemEstacionamento receive(Frame frame)
    {
        byte []b = frame.data;
        ByteBuffer bb = ByteBuffer.wrap(b);
        int x = bb.getInt();
        int y = bb.getInt();
        byte [] strBytes = bb.array();
        String cod = new String(strBytes);
        return new MensagemEstacionamento(frame.tag,x,y,cod,"");
    }
}
