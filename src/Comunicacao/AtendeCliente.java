package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.Mensagem;
import ProtocoloMensagens.MensagemAutenticacao;
import ScooterServer.IScooterServer;

import java.io.*;
import java.net.Socket;

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
