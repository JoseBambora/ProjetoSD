package Testes;

import ScooterServer.*;

public class ThreadTeste1 implements Runnable
{
    private IScooterServer server;
    private Integer id;
    public ThreadTeste1(IScooterServer server, int id)
    {
        this.server = server;
        this.id = id;
    }
    @Override
    public void run()
    {
        server.addCliente(id.toString(),id.toString());
        if(server.verificaCredenciais(id.toString(),Integer.toString(id+1)))
            System.out.println("Erro verifica credenciais 1 " + id);
        if(!server.verificaCredenciais(id.toString(),Integer.toString(id)))
            System.out.println("Erro verifica credenciais 2 " + id);
        server.addReserva(id-10,id-10);
    }
}