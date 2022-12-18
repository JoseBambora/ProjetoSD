package Testes;

import ScooterServer.*;

public class ThreadAddTrotinete implements Runnable
{
    private ScooterServer server;
    private Integer id;
    public ThreadAddTrotinete(ScooterServer server, int id)
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
