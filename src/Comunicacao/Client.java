package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client
{
    public static boolean login(BufferedReader reader, Demultiplexer demultiplexer) throws IOException, InterruptedException{
        System.out.println("Nome de utilizador");
        String nome = reader.readLine();
        System.out.println("Password");
        String pass = reader.readLine();
        MensagemAutenticacao mensagem = new MensagemAutenticacao(0,nome,pass,true);
        demultiplexer.send(mensagem.createFrame());
        byte[] data = demultiplexer.receive(mensagem.getId());
        String resultado_login = new String(data);
        System.out.println(resultado_login);
        return (!resultado_login.equals("INVALIDO"));
        
    }
    public static void main(String [] args) throws IOException {
        Socket server = new Socket("localhost",1584);
        Demultiplexer demultiplexer = new Demultiplexer(new TaggedConnection(server));
        demultiplexer.start();
        
        int counter = 0;
        //MensagemAutenticacao m = new MensagemAutenticacao(0,"seila","seila");
        //MensagemTrotinetes m2  = new MensagemTrotinetes(1, 3, 3,new String[1]); recebe reserva
        //MensagemReservar m3 = new MensagemReservar(3, 1, 0,""); 
        //System.out.println("Pedido enviado");
        //taggedConnection.send(m.createFrame());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean pedidos;
        try {
            pedidos = login(reader,demultiplexer);
        } catch (InterruptedException e) {
            System.out.println("Exceção login");
            pedidos = false;
        }
        if(pedidos)
            System.out.println("-----MENU------\nTROTINETES-->1\nRECOMPENSAS-->2\nRESERVAR-->3\nESTACIONAMENTO-->4\nNOTIFICACOES-->5");
        while(pedidos)
        {
            String tipomsg = reader.readLine();
            if(tipomsg != null){
                switch(tipomsg){
                    case "1":
                        System.out.println("Insira o x:");
                        int x_trotinete= Integer.parseInt(reader.readLine()); 
                        System.out.println("Insira o y:");
                        int y_trotinete= Integer.parseInt(reader.readLine());
                        Mensagem  mensagem_trotinete = new MensagemTrotinetes(counter, x_trotinete, y_trotinete,new String[1]);
                        counter++;
                        Thread atende_trotinete = new Thread(new ProcessaPedido(demultiplexer, mensagem_trotinete));
                        atende_trotinete.start();
                        break;
                    case "2":
                        System.out.println("Insira o x:");
                        int x_recompensa  = Integer.parseInt(reader.readLine()); 
                        System.out.println("Insira o y:");
                        int y_recompensa = Integer.parseInt(reader.readLine());
                        Mensagem mensagem_recompensa  = new MensagemRecompensas(counter, x_recompensa, y_recompensa,new String[1]);
                        counter++;
                        Thread atende_recompensa = new Thread(new ProcessaPedido(demultiplexer, mensagem_recompensa));
                        atende_recompensa.start();
                        break;
                    case "3":
                        System.out.println("Insira o x:");
                        int x_reservar = Integer.parseInt(reader.readLine()); 
                        System.out.println("Insira o y:");
                        int y_reservar = Integer.parseInt(reader.readLine());
                        Mensagem  mensagem_reserva = new MensagemReservar(counter, x_reservar, y_reservar,"");
                        counter++;
                        Thread atende_reserva = new Thread(new ProcessaPedido(demultiplexer, mensagem_reserva));
                        atende_reserva.start();
                        break;
                    case "4":
                        System.out.println("Insira o código da reserva");
                        String codigo = reader.readLine();
                        System.out.println("Insira o x:");
                        int x_estacionamento = Integer.parseInt(reader.readLine()); 
                        System.out.println("Insira o y:");
                        int y_estacionamento = Integer.parseInt(reader.readLine());
                        Mensagem mensagem_estacionamento  = new MensagemEstacionamento(counter, x_estacionamento, y_estacionamento, codigo, "");
                        counter++;
                        Thread atende_estacionamento = new Thread(new ProcessaPedido(demultiplexer, mensagem_estacionamento));
                        atende_estacionamento.start();
                        break;
                    case "5":
                        System.out.println("Insira o x:");
                        int x_notifs = Integer.parseInt(reader.readLine()); 
                        System.out.println("Insira o y:");
                        int y_notifs = Integer.parseInt(reader.readLine());
                        Mensagem mensagem_notificacao  = new MensagemNotificacoes(counter, x_notifs, y_notifs);
                        counter++;
                        Thread atende_notificacao = new Thread(new ProcessaPedido(demultiplexer, mensagem_notificacao));
                        atende_notificacao.start();
                        break;
                    default:
                        System.out.println("escreva um valor correto");
                        break;
                }
                System.out.println("-----MENU------\nTROTINETES-->1\nRECOMPENSAS-->2\nRESERVAR-->3\nESTACIONAMENTO-->4\nNOTIFICACOES-->5");
            }
            else
                pedidos = false;
        }
        demultiplexer.close();
        server.close();
    }
}
