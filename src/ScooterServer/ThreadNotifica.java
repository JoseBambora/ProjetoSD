package ScooterServer;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ThreadNotifica implements Runnable
{
    private IScooterServer server;
    private Lock lock;
    private Condition condition;
    public ThreadNotifica(IScooterServer servidor, Lock lock, Condition condition)
    {
        this.server = servidor;
        this.lock = lock;
        this.condition = condition;
    }
    @Override
    public void run()
    {
        try
        {
            lock.lock();
            while (true)
            {
                condition.await();
                List<Notificacoes> l = server.notifica();
                l.forEach(n -> n.escreveMensagem());
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
