package Testes;

import ScooterServer.*;

public class ThreadAddTrotinete implements Runnable
{
    private Server server;
    private Integer id;
    public ThreadAddTrotinete(Server server, int id)
    {
        this.server = server;
        this.id = id;
    }
    @Override
    public void run()
    {
        server.addTrotinete(id,id);
    }
}
