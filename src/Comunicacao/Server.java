package Comunicacao;

import ScooterServer.IScooterServer;
import ScooterServer.ScooterServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String [] args) throws IOException {
        ServerSocket server = new ServerSocket(1584);
        IScooterServer scooterServer = new ScooterServer(2,30);
        scooterServer.addCliente("seila","seila");
        System.out.println("Servidor inicializado");
        while (true)
        {
            Socket socket = server.accept();
            System.out.println("Pedido recebido");
            TaggedConnection taggedConnection = new TaggedConnection(socket);
            Thread atende = new Thread(new AtendeCliente(taggedConnection, scooterServer));
            atende.start();
        }
    }
}
