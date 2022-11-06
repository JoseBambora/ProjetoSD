import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadRecompensas implements Runnable
{
    private Map<String,Recompensa> recompensas;
    private Integer[][] mapa; // número de trotinetes em cada posição
    private ReentrantReadWriteLock recompensasLock;
    private ReentrantReadWriteLock mapaLock;
    private Lock lock;

    public ThreadRecompensas(Map<String, Recompensa> recompensas, Integer[][] mapa, ReentrantReadWriteLock recompensasLock, ReentrantReadWriteLock mapaLock, Lock lock)
    {
        this.recompensas = recompensas;
        this.mapa = mapa;
        this.mapaLock = mapaLock;
        this.recompensasLock = recompensasLock;
        this.lock = lock;
    }

    @Override
    public void run()
    {
        lock.lock();
        Condition condition = lock.newCondition();
        while(true)
        {
            try
            {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mapaLock.readLock().lock();
            // avaliar mapa
            // gerar recompensa se necessário
            if(true) // gera recompensa
            {
                // gera objeto recompensa
                recompensasLock.writeLock().lock();
                //recompensas.put(recompensa.getCod(),recompensa);
                recompensasLock.writeLock().unlock();
            }
            mapaLock.readLock().unlock();
        }
    }
}
