package Comunicacao;

import ScooterServer.ScooterServer;
import ScooterServer.IScooterServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String [] args) throws IOException
    {
        ServerSocket server = new ServerSocket(1584);
        IScooterServer scooterServer = new ScooterServer(4,20);
        System.out.println("Servidor inicializado");
        while (true)
        {
            Socket socket = server.accept();
            System.out.println("Novo cliente");
            TaggedConnection taggedConnection = new TaggedConnection(socket);
            Thread atende = new Thread(new AtendeCliente(taggedConnection, scooterServer));
            atende.start();
        }
    }
}
