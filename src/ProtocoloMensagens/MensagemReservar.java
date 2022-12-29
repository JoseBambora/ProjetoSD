package ProtocoloMensagens;

import java.nio.ByteBuffer;
import ScooterServer.Reserva;

public class MensagemReservar extends Mensagem{

    private String reserva;

    private int x;
    private int y;


    public MensagemReservar(int id,int x, int y, String reserva)
    {
        super(id);
        this.x = x;
        this.y = y;
        this.reserva = reserva;
    }
    
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }

    public Frame createFrame()
    {
        byte[] bytes = ByteBuffer.allocate(8).putInt(this.getX()).putInt(this.getY()).array();
        return new Frame(getId(),getTipo("Reservar"),bytes); 
    }
    
    public Frame createFrameResponse(Reserva reserva)
    {
        String s = "RESERVA:\n------";
        if (reserva == null)
            s+= "Nao existe trotinete no local.";
        else
            s += s.toString();
        
        return new Frame(getId(),getTipo("Reservar"),s.getBytes());
    }

    public static MensagemReservar receive(Frame frame)
    {
        byte []b = frame.data;
        ByteBuffer bb = ByteBuffer.wrap(b);
        int x = bb.getInt();
        int y = bb.getInt();
        byte [] strBytes = bb.array();
        String reserva = new String(strBytes);
        

        return new MensagemReservar(frame.tag, x, y, reserva);

    } 

    







    
}
