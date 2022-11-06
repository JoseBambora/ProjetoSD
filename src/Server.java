import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server
{
    /**
     * 0 2 0 1
     * 0 0 0 0
     * 0 0 0 0
     *
     * 0 1 0 1
     * 1 0 0 0
     * 0 0 0 0
     */
    private Map<String,Cliente> clientes;
    private Map<String,Cliente> notificacoes;
    private Map<String,Trotinete> trotinetes;
    private Map<String,Reserva> reservas;
    private Map<String,Recompensa> recompensas;
    private Integer[][] mapa; // número de trotinetes em cada posição
    private Map<String, ReentrantReadWriteLock> locks;
    private Lock lockthread;
    private Condition recThread;
    private int raio;

    Server(int raio)
    {
        this.lockthread = new ReentrantLock();
        this.recThread = this.lockthread.newCondition();
        this.locks = new HashMap<>();
        this.locks.put("Clientes",new ReentrantReadWriteLock());
        this.locks.put("Notificacoes",new ReentrantReadWriteLock());
        this.locks.put("Trotinetes",new ReentrantReadWriteLock());
        this.locks.put("Reservas",new ReentrantReadWriteLock());
        this.locks.put("Recompensas",new ReentrantReadWriteLock());
        this.locks.put("Mapa",new ReentrantReadWriteLock());
        Thread recompensaThread = new Thread(new ThreadRecompensas(recompensas,mapa,this.locks.get("Recompensas"),this.locks.get("Mapa"),lockthread));
        recompensaThread.start();
        this.raio = raio;
    }
    public float estacionamento(String cod, int x, int y)
    {
        this.locks.get("Mapa").writeLock().lock();
        this.mapa[y][x]++;
        this.locks.get("Mapa").writeLock().unlock();
        this.lockthread.lock();
        this.recThread.signal();
        this.lockthread.unlock();
        // this.reservas.get(cod)  comparar coordenadas, comparar tempo e calcular custo
        // notificar thread das recompensas
        return 0;
    }
    public void addReserva(Reserva reserva)
    {
        this.lockthread.lock();
        this.lockthread.newCondition().signal();
        this.lockthread.unlock();
        // notificar thread das recompensas
    }
    public void addRecompensa(Recompensa recompensa)
    {
        // notificar todos os clientes em notificacoes
    }
    public void addCliente(String nome, String pass)
    {
        this.locks.get("Clientes").writeLock().lock();
        this.clientes.put(nome,new Cliente(nome,pass));
        this.locks.get("Clientes").writeLock().unlock();
    }
    public boolean verificaCredenciais(String nome, String pass)
    {
        try
        {
            this.locks.get("Clientes").readLock().lock();
            Cliente cliente = this.clientes.get(nome);
            if(cliente == null)
                return false;
            else
                return cliente.getNomeUtilizador().equals(nome) && cliente.getPassword().equals(pass);
        }
        finally
        {
            this.locks.get("Clientes").readLock().unlock();
        }
    }
}
