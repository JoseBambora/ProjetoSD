package Comunicacao;

import java.io.IOException;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.Mensagem;
import Comunicacao.Demultiplexer;

public class ProcessaPedido implements Runnable
{
    private final Demultiplexer demultiplexer;
    private final Mensagem mensagem;
    
    public ProcessaPedido(Demultiplexer demultiplexer, Mensagem mensagem)
    {
        this.demultiplexer = demultiplexer;
        this.mensagem = mensagem;

    }
    @Override
    public void run()
    {
        try {
            demultiplexer.send(mensagem);
        } catch (IOException e) {
            
        }
        try {
            Frame f = demultiplexer.receive();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(f);
    }
}
