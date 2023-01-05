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
        try
        {
            demultiplexer.send(f);
            System.out.println("Pedido enviado. À espera de receber resposta");
            byte[] dados = demultiplexer.receive(mensagem.getId());
            System.out.println("=======================================");
            System.out.println("Resposta recebida");
            System.out.println(new String(dados));
            System.out.println("=======================================");
        } catch (IOException  | InterruptedException e) {
            System.out.println("Conexão fechada");
        }
        
    }
}
