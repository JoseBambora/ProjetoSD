package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client
{
    public boolean login(BufferedReader reader, Demultiplexer demultiplexer) throws IOException{
        String nome = reader.readLine();
        String pass = reader.readLine();
        MensagemAutenticacao mensagem = new MensagemAutenticacao(0,nome,pass);
        demultiplexer.send(mensagem.createFrame());
        Frame f = demultiplexer.receive();
        return f.data[0]==(byte)1;
    }
    public static void main(String [] args) throws IOException {
        Socket server = new Socket("localhost",1584);
        Demultiplexer demultiplexer = new Demultiplexer(new TaggedConnection(server));
        
        int counter = 0;
        //MensagemAutenticacao m = new MensagemAutenticacao(0,"seila","seila");
        //MensagemTrotinetes m2  = new MensagemTrotinetes(1, 3, 3,new String[1]); recebe reserva
        //MensagemReservar m3 = new MensagemReservar(3, 1, 0,""); 
        //System.out.println("Pedido enviado");
        //taggedConnection.send(m.createFrame());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("-----MENU------\nRESERVA-->1\nTROTINETES-->2\nRECOMPENSA-->3\nESTECIONAMENTO-->4");
        while(true){
            String tipomsg = reader.readLine();
            if(tipomsg != null){
                switch(tipomsg){
                    case "1":
                        int x_reserva = Integer.parseInt(reader.readLine()); 
                        int y_reserva = Integer.parseInt(reader.readLine());
                        Mensagem mensagem_reserva  = new MensagemTrotinetes(counter, x_reserva, y_reserva,new String[1]);
                        counter++;
                        Thread atende_reserva = new Thread(new ProcessaPedido(demultiplexer, mensagem_reserva));
                        atende_reserva.start();
                        break;
                    case "2":
                        int x_trotinete = Integer.parseInt(reader.readLine()); 
                        int y_trotinete = Integer.parseInt(reader.readLine());
                        Mensagem mensagem_trotinete  = new MensagemTrotinetes(counter, x_trotinete, y_trotinete,new String[1]);
                        counter++;
                        Thread atende_trotinete = new Thread(new ProcessaPedido(demultiplexer, mensagem_trotinete));
                        atende_trotinete.start();
                        break;
                    case "3":
                        int x_recompensa = Integer.parseInt(reader.readLine()); 
                        int y_recompensa = Integer.parseInt(reader.readLine());
                        Mensagem mensagem_recompensa  = new MensagemTrotinetes(counter, x_recompensa, y_recompensa,new String[1]);
                        counter++;
                        Thread atende_recompensa = new Thread(new ProcessaPedido(demultiplexer, mensagem_recompensa));
                        atende_recompensa.start();
                        break;
                    case "4":
                        int x_estacionamento = Integer.parseInt(reader.readLine()); 
                        int y_estacionamento = Integer.parseInt(reader.readLine());
                        String codigo = reader.readLine();
                        Mensagem mensagem_estacionamento  = new MensagemEstacionamento(counter, x_estacionamento, y_estacionamento, codigo, "");
                        counter++;
                        Thread atende_estacionamento = new Thread(new ProcessaPedido(demultiplexer, mensagem_estacionamento));
                        atende_estacionamento.start();
                        break;
                    default:
                        System.out.println("escreva um valor correto");
                        break;
                }      
            }else
                break;
        }
        demultiplexer.close();
        server.close();
    }
}
