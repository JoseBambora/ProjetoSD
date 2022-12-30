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
        Frame f = mensagem.createFrame();
        try {
            demultiplexer.send(f);
        } catch (IOException e) {
            
        }
        try {
            byte[] dados = demultiplexer.receive(mensagem.getId());
            System.out.println(new String(dados));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
    }
}
