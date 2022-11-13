import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<List<Integer>> mapa; // número de trotinetes em cada posição
    private Map<String, ReentrantReadWriteLock> locks;
    private Map<String, Integer> codigos;
    private Lock lockthread;
    private Condition recThread;
    private Thread recompensaThread;
    private int raio;
    private static final String trotinestesSTR = "Trotinetes";

    Server(int raio, int tamanho)
    {
        this.lockthread = new ReentrantLock();
        this.recThread = this.lockthread.newCondition();
        this.locks = new HashMap<>();
        this.locks.put("Clientes",new ReentrantReadWriteLock());
        this.locks.put("Notificacoes",new ReentrantReadWriteLock());
        this.locks.put(trotinestesSTR,new ReentrantReadWriteLock());
        this.locks.put("Reservas",new ReentrantReadWriteLock());
        this.locks.put("Recompensas",new ReentrantReadWriteLock());
        this.locks.put("Mapa",new ReentrantReadWriteLock());
        this.codigos = new HashMap<>();
        this.codigos.put("Notificacoes",0);
        this.codigos.put(trotinestesSTR,0);
        this.codigos.put("Reservas",0);
        this.codigos.put("Recompensas",0);
        this.codigos.put("Mapa",0);
        this.clientes = new HashMap<>();
        this.reservas = new HashMap<>();
        this.recompensas = new HashMap<>();
        this.trotinetes = new HashMap<>();
        recompensaThread = new Thread(new ThreadRecompensas(this,lockthread,recThread));
        recompensaThread.start();
        this.mapa = new ArrayList<>(tamanho);
        for(int i = 0; i < tamanho; i++)
        {
            this.mapa.add(new ArrayList<>(tamanho));
            for(int j = 0; j < tamanho; j++)
                this.mapa.get(i).add(0);
        }

        this.raio = raio;
    }
    private String getCodigo(String campo)
    {
        Integer c = this.codigos.get(campo);
        this.codigos.put(campo,c+1);
        return c.toString();
    }
    // TESTADO
    public void addTrotinete(int x , int y)
    {
        this.locks.get(trotinestesSTR).writeLock().lock();
        String t = this.getCodigo(trotinestesSTR);
        this.trotinetes.put(t,new Trotinete(t,x,y));
        this.locks.get("Mapa").writeLock().lock();
        this.locks.get(trotinestesSTR).writeLock().unlock();
        int n = this.mapa.get(y).get(x);
        this.mapa.get(y).set(x,n+1);
        this.locks.get("Mapa").writeLock().unlock();

    }
    /**
     * QUANDO ESTACIONA -> REVER RECOMPENSAS :
     * Acordar threadRecompensa -> criar método de analis Recompensa e verifica se é preciso eliminar alguma
     */
    public float estacionamento(String cod, int x, int y)
    {
        this.locks.get(trotinestesSTR).writeLock().lock();
        this.locks.get("Reservas").readLock().lock();
        this.lockthread.lock();
        this.locks.get("Mapa").writeLock().lock();
        Reserva reserva = this.reservas.get(cod);
        this.locks.get("Reservas").readLock().unlock();
        int n = this.mapa.get(y).get(x);
        this.mapa.get(y).set(x,n+1);
        this.locks.get("Mapa").writeLock().unlock();
        this.trotinetes.get(reserva.getTrotinete()).liberta();
        this.locks.get(trotinestesSTR).writeLock().unlock();
        float res = reserva.geraCusto(x,y);
        this.recThread.signal();
        this.lockthread.unlock();
        return res;
    }
    // TESTADO
    public String addReserva(String trotinete)
    {
        this.locks.get(trotinestesSTR).writeLock().lock();
        this.locks.get("Reservas").writeLock().lock();
        this.locks.get("Mapa").writeLock().lock();
        Trotinete trotinete1 = this.trotinetes.get(trotinete);
        String res = "";
        if(!trotinete1.isReservada())
        {
            int n = this.mapa.get(trotinete1.getY()).get(trotinete1.getX());
            this.mapa.get(trotinete1.getY()).set(trotinete1.getX(), n-1);
            this.locks.get("Mapa").writeLock().unlock();
            this.lockthread.lock();
            String c = this.getCodigo("Reservas");
            this.recThread.signal();
            this.lockthread.unlock();
            trotinete1.reserva();
            Reserva reserva = new Reserva(trotinete,trotinete1.getX(), trotinete1.getY(), LocalDateTime.now(),c);
            this.reservas.put(reserva.getCodigo(),reserva);
            reserva.setCod(c);
            this.locks.get("Reservas").writeLock().unlock();
            res = c;
        }
        else
        {
            this.locks.get("Mapa").writeLock().unlock();
            this.locks.get("Reservas").writeLock().unlock();
        }
        this.locks.get(trotinestesSTR).writeLock().unlock();
        return res;
    }
    public void addRecompensa(Recompensa recompensa)
    {
        this.locks.get("Recompensas").writeLock().lock();
        // notificar clientes
        String c = this.getCodigo("Reservas");
        recompensa.setCod(c);
        this.recompensas.put(recompensa.getCod(),recompensa);
        this.locks.get("Recompensas").writeLock().unlock();
    }
    // TESTADO
    public void addCliente(String nome, String pass)
    {
        this.locks.get("Clientes").writeLock().lock();
        if(!this.clientes.containsKey(nome))
        {
            Cliente cliente = new Cliente(nome,pass);
            this.clientes.put(nome,cliente);
        }
        this.locks.get("Clientes").writeLock().unlock();
    }
    // TESTADO
    public boolean verificaCredenciais(String nome, String pass)
    {
        boolean res = false;
        this.locks.get("Clientes").readLock().lock();
        Cliente cliente = this.clientes.get(nome);
        if(cliente != null)
            res = cliente.verificaPassword(pass);
        this.locks.get("Clientes").readLock().unlock();
        return res;
    }
    public Recompensa avaliaMapa()
    {
        try
        {
            Recompensa res = null;
            this.locks.get("Mapa").readLock().lock();
            int xi = 0, yi= 0, xf = -1, yf = -1;
            for(List<Integer> linha : this.mapa)
            {
                for(Integer elem : linha)
                {
                    if(elem == 0)
                    {
                        xf = xi;
                        yf = yi;
                        if(res != null)
                        {
                            res.setXf(xf);
                            res.setYf(yf);
                            return res;
                        }
                    }
                    if(elem > 1)
                    {
                        res = new Recompensa(xi,yi,xf,yf,30);
                        if(xf != -1)
                            return res;
                    }
                    xi++;
                }
                xi = 0;
                yi++;
            }
        }
        finally
        {
            this.locks.get("Mapa").readLock().unlock();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Server{" + "\n" +
                "clientes=" + clientes + "\n" +
                ", notificacoes=" + notificacoes + "\n" +
                ", trotinetes=" + trotinetes + "\n" +
                ", reservas=" + reservas + "\n" +
                ", recompensas=" + recompensas +
                '}';
    }

    public List<List<Integer>> getMapa() {
        return mapa;
    }

    public void terminaServidor()
    {
        this.recompensaThread.stop();
    }
}