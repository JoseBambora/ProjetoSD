package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.Mensagem;
import ProtocoloMensagens.MensagemAutenticacao;
import ProtocoloMensagens.MensagemEstacionamento;
import ProtocoloMensagens.MensagemNotificacoes;
import ProtocoloMensagens.MensagemRecompensas;
import ProtocoloMensagens.MensagemReservar;
import ProtocoloMensagens.MensagemTrotinetes;
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
            while(true) {
                Mensagem mensagem = Mensagem.getMessage(taggedConnection.receive());
                if (mensagem instanceof MensagemAutenticacao) {
                    System.out.println("Pedido de autenticação");
                    MensagemAutenticacao m = (MensagemAutenticacao) mensagem;
                    boolean b = server.verificaCredenciais(m.getUsername(), m.getPassword());
                    taggedConnection.send(m.createFrameResponse(b));
                    if (b) {
                        // continuar a comunicar com o cliente
                    }
                } else if (mensagem instanceof MensagemTrotinetes) {
                    System.out.println("Pedido de trotinetes recebido");
                    MensagemTrotinetes m = (MensagemTrotinetes) mensagem;
                    List<String> trotinetesStr = server.getTrotinetes(m.getX(), m.getY()).stream()
                            .map(t -> t.toString()).toList();
                    taggedConnection.send(m.createFrameResponse(trotinetesStr));
                    System.out.println("Resposta enviada");
                } else if (mensagem instanceof MensagemRecompensas) {
                    System.out.println("Pedido de recompensas recebido");
                    MensagemRecompensas m = (MensagemRecompensas) mensagem;
                    List<Recompensa> recompensas = server.getRecompensas(m.getX(), m.getY());
                    taggedConnection.send(m.createFrameResponse(recompensas));
                } else if (mensagem instanceof MensagemReservar) {
                    System.out.println("Pedido de reservar recebido");
                    MensagemReservar m = (MensagemReservar) mensagem;
                    Reserva r = server.addReserva(m.getX(), m.getY());
                    taggedConnection.send(m.createFrameResponse(r));


                } else if (mensagem instanceof MensagemEstacionamento) {
                    System.out.println("Pedido de estacionamento recebido");
                    MensagemEstacionamento m = (MensagemEstacionamento) mensagem;
                    Float custo = server.estacionamento(m.getCodigo(), m.getX(), m.getY());

                    taggedConnection.send(m.createFrameResponse(custo));

                } else if (mensagem instanceof MensagemNotificacoes) {
                    System.out.println("Pedido de notificações recebido");
                    MensagemNotificacoes m = (MensagemNotificacoes) mensagem;
                    server.addNotificacao(m.getX(), m.getY(), m.getId(), taggedConnection);
                    //inserir trigger para as notificacoes aqui
                    //taggedConnection.send(m.createFrameResponse());

                } else {
                    System.out.println("Pedido inválido recebido, fechar conexão com cliente");
                    break;
                }
            }
            taggedConnection.close();
        }
        catch (IOException e) {
            System.out.println("Conexão fechada");
        }
    }
}
