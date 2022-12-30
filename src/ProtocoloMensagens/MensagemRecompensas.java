package ProtocoloMensagens;

import java.nio.ByteBuffer;
import java.util.List;
import ScooterServer.Recompensa;

public class MensagemRecompensas extends Mensagem {
    
    private String [] recompensas;
    private int x;
    private int y;

    public int getX()
    {
        return this.x;
    }
    public int getY()
    {
        return this.y;
    }
    public String[] getRecompensas()
    {
        return this.recompensas;
    }

    public MensagemRecompensas(int id,int x, int y, String[] recompensas)
    {
        super(id);
        this.x = x;
        this.y = y;
        this.recompensas = recompensas;
    }
    

    public Frame createFrame()
    {
        byte[] bytes = ByteBuffer.allocate(8).putInt(this.getX()).putInt(this.getY()).array();
        return new Frame(getId(),getTipo("Recompensas"),bytes); 
    }
    public Frame createFrameResponse(List<Recompensa> recompensas)
    {
        StringBuilder s = new StringBuilder("RECOMPENSAS:\n");
        for(Recompensa r : recompensas)
            s.append("(").append(r.getXi()).append(",").append(r.getYi()).append(")").append("->").append("(").append(r.getXf()).append(",").append(r.getYf()).append(")").append("  Recompensa :").append(String.format("%.02f", r.getPremio())).append("\n").append("------");
        return new Frame(getId(),getTipo("Recompensas"), s.toString().getBytes());
    }

    public static MensagemRecompensas receive(Frame frame)
    {
        byte []b = frame.data;
        ByteBuffer bb = ByteBuffer.wrap(b);
        int x = bb.getInt();
        int y = bb.getInt();
        byte [] strBytes = bb.array();
        String total = new String(strBytes);
        String [] recompensas = total.split("------"); 

        return new MensagemRecompensas(frame.tag, x, y, recompensas);

    } 

}
