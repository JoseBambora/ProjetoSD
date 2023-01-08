package Comunicacao;

import ProtocoloMensagens.*;
import ScooterServer.IScooterServer;
import ScooterServer.Recompensa;
import ScooterServer.Reserva;
import ScooterServer.Trotinete;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            String cliente = "";
            boolean continua = true;
            while(continua)
            {
                Mensagem mensagem = Mensagem.getMessage(taggedConnection.receive());
                if (mensagem instanceof MensagemAutenticacao m)
                {
                    System.out.println("Pedido de autenticação");
                    boolean b =  true;
                    if(m.getLogin())
                         b = server.verificaCredenciais(m.getUsername(), m.getPassword());
                    else
                        server.addCliente(m.getUsername(),m.getPassword());
                    taggedConnection.send(m.createFrameResponse(b));
                    continua = b;
                    if(b)
                        cliente = m.getUsername();
                }
                else if (mensagem instanceof MensagemTrotinetes m)
                {
                    System.out.println("Pedido de trotinetes recebido");
                    List<String> trotinetesStr = server.getTrotinetes(m.getX(), m.getY())
                            .stream()
                            .map(Trotinete::toString)
                            .toList();
                    taggedConnection.send(m.createFrameResponse(trotinetesStr));
                    System.out.println("Resposta enviada");
                }
                else if (mensagem instanceof MensagemRecompensas m)
                {
                    System.out.println("Pedido de recompensas recebido");
                    List<Recompensa> recompensas = server.getRecompensas(m.getX(), m.getY());
                    taggedConnection.send(m.createFrameResponse(recompensas));
                }
                else if (mensagem instanceof MensagemReservar m)
                {
                    System.out.println("Pedido de reservar recebido");
                    Reserva r = server.addReserva(m.getX(), m.getY(),cliente,true);
                    taggedConnection.send(m.createFrameResponse(r));
                }
                else if (mensagem instanceof MensagemEstacionamento m)
                {
                    System.out.println("Pedido de estacionamento recebido");
                    float custo = server.estacionamento(m.getCodigo(), m.getX(), m.getY(),cliente,m.isRecompensa());
                    taggedConnection.send(m.createFrameResponse(custo));

                }
                else if (mensagem instanceof MensagemNotificacoes m)
                {
                    System.out.println("Pedido de notificações recebido");
                    server.addNotificacao(m.getX(), m.getY(), m.getId(), taggedConnection);
                    //inserir trigger para as notificacoes aqui
                    //taggedConnection.send(m.createFrameResponse());

                }
                else if(mensagem instanceof MensagemAceitaRecompensa m)
                {
                    System.out.println("Pedido de aceitação de recompensa recebido");
                    Recompensa res = server.aceitarRecompensa(m.getRecompensa(),cliente);
                    taggedConnection.send(m.createFrameResponse(res));
                }
                else
                {
                    System.out.println("Pedido inválido recebido, fechar conexão com cliente");
                    continua = false;
                }
            }
            taggedConnection.close();
        }
        catch (IOException e)
        {
            System.out.println("Conexão fechada por parte do cliente");
        }
    }
}
