package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    public static void registo(BufferedReader reader, Demultiplexer demultiplexer) throws IOException, InterruptedException{
        System.out.println("Nome de utilizador");
        String nome = reader.readLine();
        System.out.println("Password");
        String pass = reader.readLine();
        MensagemAutenticacao mensagem = new MensagemAutenticacao(0,nome,pass,false);
        demultiplexer.send(mensagem.createFrame());
        byte[] data = demultiplexer.receive(mensagem.getId());
        System.out.println(new String(data));
    }
    private static List<String> geraMenu()
    {
        List<String> res = new ArrayList<>();
        res.add("-----------MENU-----------");
        res.add("| 1: TROTINETES          |");
        res.add("| 2: RECOMPENSAS         |");
        res.add("| 3: RESERVAR            |");
        res.add("| 4: ESTACIONAMENTO      |");
        res.add("| 5: NOTIFICACOES        |");
        res.add("| 6: ACEITAR RECOMPENSA  |");
        res.add("| 7: SAIR                |");
        res.add("--------------------------");
        res.add("Selecione uma opção");
        return res;
    }
    public static void main(String [] args) throws IOException
    {
        Socket server = new Socket("localhost",1584);
        Demultiplexer demultiplexer = new Demultiplexer(new TaggedConnection(server));
        demultiplexer.start();
        int counter = 1;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean pedidos = false;
        boolean login = true;
        while(login)
        {
            System.out.println("Login ou registar?");
            System.out.println("1 - Login");
            System.out.println("2 - Registar");
            int login_registo = Integer.parseInt(reader.readLine());
            try
            {
                if (login_registo == 1)
                {
                    login = false;
                    pedidos = login(reader, demultiplexer);
                }
                else
                {
                    registo(reader, demultiplexer);
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("Exceção login");
            }
        }
        List<String> menu = geraMenu();
        if(pedidos)
            menu.forEach(System.out::println);
        while(pedidos)
        {
            String tipomsg = reader.readLine();
            if(tipomsg != null)
            {
                switch(tipomsg)
                {
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
                        System.out.println("Recompensa ou Reserva?");
                        System.out.println("1 - Recompensa");
                        System.out.println("2 - Reserva");
                        int rec = Integer.parseInt(reader.readLine());
                        int x_estacionamento = 0, y_estacionamento = 0;
                        String codigo = "";
                        if(rec == 1)
                        {
                            System.out.println("Insira o código da recompensa");
                            codigo = reader.readLine();
                        }
                        else if(rec == 2)
                        {
                            System.out.println("Insira o código da reserva");
                            codigo = reader.readLine();
                            System.out.println("Insira o x:");
                            x_estacionamento = Integer.parseInt(reader.readLine());
                            System.out.println("Insira o y:");
                            y_estacionamento = Integer.parseInt(reader.readLine());
                        }
                        Mensagem mensagem_estacionamento  = new MensagemEstacionamento(counter, x_estacionamento, y_estacionamento, codigo,rec == 1);
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
                    case "6":
                        System.out.println("Insira o código da recompensa");
                        String cod = reader.readLine();
                        MensagemAceitaRecompensa mensagem_aceita = new MensagemAceitaRecompensa(counter,cod);
                        counter++;
                        Thread atende_aceita = new Thread(new ProcessaPedido(demultiplexer, mensagem_aceita));
                        atende_aceita.start();
                        break;
                    case "7":
                        pedidos = false;
                    default:
                        System.out.println("Escreva um valor correto");
                        break;
                }
                if(pedidos)
                    menu.forEach(System.out::println);
            }
            else
                pedidos = false;
        }
        demultiplexer.close();
        server.close();
    }
}
