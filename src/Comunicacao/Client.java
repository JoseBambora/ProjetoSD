package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.*;

import java.io.IOException;
import java.net.Socket;

public class Client
{
    public static void main(String [] args) throws IOException {
        Socket server = new Socket("localhost",1584);
        TaggedConnection taggedConnection = new TaggedConnection(server);
        MensagemAutenticacao m = new MensagemAutenticacao(0,"seila","seila");
        MensagemTrotinetes m2  = new MensagemTrotinetes(1, 3, 3,new String[1]);
        MensagemReservar m3 = new MensagemReservar(3, 1, 0,"");
        System.out.println("Pedido enviado");
        //taggedConnection.send(m.createFrame());
        taggedConnection.send(m3.createFrame());
        Frame f = taggedConnection.receive();
        System.out.println(f.tag);
        System.out.println(f.tipo);
        String s = new String(f.data);
        System.out.println(s);

    }
}
