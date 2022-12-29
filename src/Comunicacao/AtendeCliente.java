package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.Mensagem;
import ProtocoloMensagens.MensagemAutenticacao;
import ProtocoloMensagens.MensagemRecompensas;
import ProtocoloMensagens.MensagemReservar;
import ProtocoloMensagens.MensagemTrotinetes;
import ScooterServer.IScooterServer;
import ScooterServer.Recompensa;
import ScooterServer.Reserva;
import ScooterServer.Trotinete;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class AtendeCliente implements Runnable
{
    private final TaggedConnection taggedConnection;
    private final IScooterServer server;
    public AtendeCliente(TaggedConnection taggedConnection, IScooterServer server)
    {
        this.taggedConnection = taggedConnection;
        this.server = server;
    }
    @Override
    public void run()
    {
        try
        {
            Mensagem mensagem = Mensagem.getMessage(taggedConnection.receive());
            if(mensagem instanceof MensagemAutenticacao)
            {
                MensagemAutenticacao m = (MensagemAutenticacao) mensagem;
                boolean b = server.verificaCredenciais(m.getUsername(),m.getPassword());
                taggedConnection.send(m.createFrameResponse(b));
                if (b)
                {
                    // continuar a comunicar com o cliente
                }
            }
            else if (mensagem instanceof MensagemTrotinetes)
            {
                MensagemTrotinetes m = (MensagemTrotinetes) mensagem;    
                List<String> trotinetesStr  = server.getTrotinetes(m.getX(), m.getY()).stream()
                                                    .map(t -> t.toString()).toList();
                taggedConnection.send(m.createFrameResponse(trotinetesStr));

            }
            else if (mensagem instanceof MensagemRecompensas) 
            {
                MensagemRecompensas m = (MensagemRecompensas) mensagem;
                List<Recompensa> recompensas = server.getRecompensas(m.getX(), m.getY());
                taggedConnection.send(m.createFrameResponse(recompensas));
            }
            else if (mensagem instanceof MensagemReservar)
            {
                MensagemReservar m = (MensagemReservar) mensagem;
                Reserva r = server.addReserva(m.getX(), m.getY());
                taggedConnection.send(m.createFrameResponse(r));


            }       
            else
            {
                MensagemAutenticacao m = new MensagemAutenticacao(mensagem.getId(),"","");
                taggedConnection.send(m.createFrameResponse(false));
            }

            

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
