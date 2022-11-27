import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Ver """erro""" de acordar a thread das recompensas.
 * Server - Cliente
 * Notificações
 * Melhorar os testes
 * Melhorar o código
 * Fazer relatório
 */
public class Server
{
    private final Map<String,Cliente> clientes;
    private Map<String,Cliente> notificacoes;
    private final Map<String,Trotinete> trotinetes;
    private final Map<String,Reserva> reservas;
    private final Map<String,Recompensa> recompensas;
    private final List<List<Integer>> mapa; // número de trotinetes em cada posição
    private final Map<String, ReentrantReadWriteLock> locks;
    private final Map<String, Integer> codigos;
    private final Thread recompensaThread;
    private final ThreadAcordaThread acorda;
    private final Thread acordaThread;
    private final int raio;
    private static final String trotinestesSTR = "Trotinetes";

    Server(int raio, int tamanho)
    {
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
        Lock lockthread = new ReentrantLock();
        Condition cond = lockthread.newCondition();
        this.acorda = new ThreadAcordaThread(lockthread,cond);
        this.acordaThread = new Thread(this.acorda);
        this.acordaThread.start();
        recompensaThread = new Thread(new ThreadRecompensas(this,lockthread,cond));
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

    public void reverRecompensas()
    {
        this.locks.get("Recompensas").writeLock().lock();
        this.locks.get("Mapa").readLock().lock();
        List<String> removeRec = new ArrayList<>();
        for(Recompensa recompensa : this.recompensas.values())
        {
            int xi = recompensa.getXi();
            int yi = recompensa.getYi();
            if(this.mapa.get(yi).get(xi) < 2)
                removeRec.add(recompensa.getCod());
            else
            {
                int xf = recompensa.getXf();
                int yf = recompensa.getYf();
                if(this.mapa.get(yf).get(xf) > 0)
                {
                    boolean alterou = false;
                    int x, y=0;
                    for(List<Integer> l : this.mapa)
                    {
                        x=0;
                        for(Integer nrTroti : l)
                        {
                            if(nrTroti == 0)
                            {
                                recompensa.setXf(x);
                                recompensa.setYf(y);
                                alterou = true;
                            }
                            x++;
                        }
                        y++;
                    }
                    if(!alterou)
                        removeRec.add(recompensa.getCod());
                }
            }
        }
        for(String cod : removeRec)
        {
            this.recompensas.remove(cod);
        }
        this.locks.get("Mapa").readLock().unlock();
        this.locks.get("Recompensas").writeLock().unlock();
    }
    /**
     * QUANDO ESTACIONA -> REVER RECOMPENSAS :
     * Acordar threadRecompensa -> criar método de analise Recompensa e verifica se é preciso eliminar alguma
     */
    public float estacionamento(String cod, int x, int y)
    {
        this.locks.get(trotinestesSTR).writeLock().lock();
        this.locks.get("Reservas").readLock().lock();
        this.locks.get("Mapa").writeLock().lock();
        Reserva reserva = this.reservas.get(cod);
        this.locks.get("Reservas").readLock().unlock();
        int n = this.mapa.get(y).get(x);
        this.mapa.get(y).set(x,n+1);
        this.locks.get("Mapa").writeLock().unlock();
        this.trotinetes.get(reserva.getTrotinete()).liberta();
        this.locks.get(trotinestesSTR).writeLock().unlock();
        float res = reserva.geraCusto(x,y);
        this.acorda.incrementa();
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
            String c = this.getCodigo("Reservas");
            this.acorda.incrementa();
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
    public boolean avaliaMapa()
    {
        Recompensa res = null;
        try
        {
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
                            this.addRecompensa(res);
                            return true;
                        }
                    }
                    if(elem > 1)
                    {
                        res = new Recompensa(xi,yi,xf,yf,30);
                        if(xf != -1)
                        {
                            this.addRecompensa(res);
                            return true;
                        }
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
        return false;
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
        this.acordaThread.stop();
    }

    public Map<String, Cliente> getClientes() {
        return clientes;
    }

    public Map<String, Trotinete> getTrotinetes() {
        return trotinetes;
    }

    public Map<String, Reserva> getReservas() {
        return reservas;
    }

    public Map<String, Recompensa> getRecompensas() {
        return recompensas;
    }
}