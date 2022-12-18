package Comunicacao;

import ProtocoloMensagens.Frame;
import ProtocoloMensagens.MensagemAutenticacao;

import java.io.IOException;
import java.net.Socket;

public class Client
{
    public static void main(String [] args) throws IOException {
        Socket server = new Socket("localhost",1584);
        TaggedConnection taggedConnection = new TaggedConnection(server);
        MensagemAutenticacao m = new MensagemAutenticacao(1,"seila","seila");
        System.out.println("Pedido enviado");
        taggedConnection.send(m.createFrame());
        Frame f = taggedConnection.receive();
        System.out.println(f.tag);
        System.out.println(f.tipo);
        System.out.println(f.data[0] == 0);
    }
}
