import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadRecompensas implements Runnable
{private Server server;
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Acordei!!");
            Recompensa recompensa = server.avaliaMapa();
            if(recompensa != null)
            {
                server.addRecompensa(recompensa);
            }
        }
    }
}
