package ProtocoloMensagens;
import java.nio.ByteBuffer;
import java.util.*;

public class MensagemEstacionamento extends Mensagem
{
    private final String codigo;
    private final int x;
    private final int y;
    private boolean recompensa;

    public MensagemEstacionamento(int id,int x,int y, String codigo, boolean recompensa)
    {
        super(id);
        this.codigo = codigo;
        this.x = x;
        this.y = y;
        this.recompensa = recompensa;
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
        byte b = (byte) (recompensa ? 1 : 0);
        byte[] bytes = ByteBuffer.allocate(9 + this.getCodigo().getBytes().length)
                                 .putInt(this.getX()).putInt(this.getY())
                                 .put(b)
                                 .put(this.getCodigo().getBytes()).array();

        return new Frame(getId(),getTipo("Estacionamento"),bytes);
    }
    public Frame createFrameResponse(float custo_viagem)
    {
        String s = String.format("%.02f", custo_viagem) + "\n";
        if(recompensa)
            s = "Prémio\n" + s;
        else
            s = "Preço\n" + s;
        return new Frame(getId(),getTipo("Estacionamento"),s.getBytes());
    }

    public boolean isRecompensa() {
        return recompensa;
    }

    public static MensagemEstacionamento receive(Frame frame)
    {
        byte []b = frame.data;
        ByteBuffer bb = ByteBuffer.wrap(b);
        int x = bb.getInt();
        int y = bb.getInt();
        byte rec = bb.get();
        byte [] strBytes = new byte[b.length-9];
        bb.get(strBytes);
        String cod = new String(strBytes);
        return new MensagemEstacionamento(frame.tag,x,y,cod,rec == 1);
    }
}
