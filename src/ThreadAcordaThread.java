import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadAcordaThread implements Runnable
{
    private int contador = 0;
    private final Lock lock;
    private final Condition condition;
    private final Lock lockContador;
    public ThreadAcordaThread(Lock lock, Condition condition)
    {
        this.lockContador = new ReentrantLock();
        this.lock = lock;
        this.condition = condition;
    }
    public void incrementa()
    {
        lockContador.lock();
        contador++;
        lockContador.unlock();
    }
    @Override
    public void run()
    {
        int cont = 0;
        while (true)
        {
            lockContador.lock();
            while (contador > 0)
            {
                lock.lock();
                condition.signal();
                System.out.println("Acordei a tal " + cont++);
                try
                {
                    condition.await();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                lock.unlock();
                contador--;
            }
            lockContador.unlock();
        }
    }
}
