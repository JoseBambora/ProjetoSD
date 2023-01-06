package ProtocoloMensagens;

import ScooterServer.Recompensa;
import ScooterServer.Reserva;

import java.nio.ByteBuffer;

public class MensagemAceitaRecompensa extends Mensagem
{

    private String recompensa;

    public MensagemAceitaRecompensa(int id,String recompensa)
    {
        super(id);
        this.recompensa = recompensa;
    }

    public Frame createFrame()
    {
        return new Frame(getId(),getTipo("AceitaRecompensa"),recompensa.getBytes());
    }

    public String getRecompensa() {
        return recompensa;
    }

    public Frame createFrameResponse(Recompensa recompensa)
    {
        String s = "Recompensa Aceite\n" + recompensa.toString();
        return new Frame(getId(),getTipo("AceitaRecompensa"),s.getBytes());
    }

    public static MensagemAceitaRecompensa receive(Frame frame)
    {
        return new MensagemAceitaRecompensa(frame.tag, new String(frame.data));
    }
}
