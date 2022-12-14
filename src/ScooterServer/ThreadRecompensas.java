package ScooterServer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ThreadRecompensas implements Runnable
{
    private Server server;
    private Lock lock;
    private Condition condition;

    public ThreadRecompensas(Server server, Lock lock, Condition condition)
    {
        this.server = server;
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run()
    {
        lock.lock();
        while(true)
        {
            try
            {
                condition.await();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            server.reverRecompensas();
        }
    }
}
