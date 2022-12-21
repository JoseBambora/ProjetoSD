package ProtocoloMensagens;
import java.nio.ByteBuffer;
import java.util.List;

public class MensagemTrotinetes extends Mensagem {

    private String[] trotinetes; //Cada elemento é uma trotinete(toString)

    private int x; // coordenadas que o cliente forneceu
    private int y; 

    public MensagemTrotinetes(int id,int x, int y, String[] trotinetes)
    {
        super(id);
        this.trotinetes = trotinetes;
        this.x = x;
        this.y = y;
    }

    public String[] getTrotinetes()
    {
        return this.trotinetes;
    }

    public int getX()
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    public Frame createFrame()
    {
        byte[] bytes = ByteBuffer.allocate(8).putInt(this.getX()).putInt(this.getY()).array();
        return new Frame(getId(),getTipo("Trotinetes"),bytes); 
    }
     public Frame createFrameResponse(List<String> trotinetes)
    {
        String s = "TROTINETES:\n";
        for(String t : trotinetes)
            s += (t + "_____");
        
        return new Frame(getId(),getTipo("Trotinetes"),s.getBytes());
    }

    public static MensagemTrotinetes receive(Frame frame) 
    {
        byte []b = frame.data;
        ByteBuffer bb = ByteBuffer.wrap(b);
        int x = bb.getInt();
        int y = bb.getInt();
        byte [] strBytes = bb.array();
        String total = new String(strBytes);
        String [] trotinetes = total.split("_____"); 

        return new MensagemTrotinetes(frame.tag,x,y,trotinetes);
    }





    
}
