package ScooterServer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Ver """erro""" de acordar a thread das recompensas.
 * ScooterServer.ScooterServer.Server - ScooterServer.Cliente
 * Notificações
 * Melhorar os testes
 * Melhorar o código
 * Fazer relatório
 */
public class Server implements ScooterServer
{
    private final Map<String, Cliente> clientes;
    private Map<String, Cliente> notificacoes;
    private final Map<String, Trotinete> trotinetes;
    private final Map<String, Reserva> reservas;
    private final Map<String, Recompensa> recompensas;
    private final List<List<Integer>> mapa; // número de trotinetes em cada posição
    private final Map<String, ReentrantReadWriteLock> locks;
    private final Map<String, Integer> codigos;
    private final Thread recompensaThread;
    private final Lock lockthread;
    private final Condition cond;
    private final int raio;
    private final Map<String,String> reservasRec;
    public Server(int raio, int tamanho)
    {
        this.locks = new HashMap<>();
        this.locks.put("Clientes",new ReentrantReadWriteLock());
        this.locks.put("Notificacoes",new ReentrantReadWriteLock());
        this.locks.put("Trotinetes",new ReentrantReadWriteLock());
        this.locks.put("Reservas",new ReentrantReadWriteLock());
        this.locks.put("Recompensas",new ReentrantReadWriteLock());
        this.locks.put("Mapa",new ReentrantReadWriteLock());
        this.locks.put("ReservasRec",new ReentrantReadWriteLock());
        this.codigos = new HashMap<>();
        this.codigos.put("Notificacoes",0);
        this.codigos.put("Trotinetes",0);
        this.codigos.put("Reservas",0);
        this.codigos.put("Recompensas",0);
        this.codigos.put("Mapa",0);
        this.clientes = new HashMap<>();
        this.reservas = new HashMap<>();
        this.recompensas = new HashMap<>();
        this.trotinetes = new HashMap<>();
        this.reservasRec = new HashMap<>();
        this.lockthread = new ReentrantLock();
        this.cond = lockthread.newCondition();
        recompensaThread = new Thread(new ThreadRecompensas(this,this.lockthread,this.cond));
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

    // TESTADO
    private String getCodigo(String campo)
    {
        Integer c = this.codigos.get(campo);
        this.codigos.put(campo,c+1);
        return c.toString();
    }

    // TESTADO
    public void addTrotinete(int x , int y)
    {
        this.locks.get("Trotinetes").writeLock().lock();
        String t = this.getCodigo("Trotinetes");
        this.trotinetes.put(t,new Trotinete(t,x,y));
        this.locks.get("Mapa").writeLock().lock();
        this.locks.get("Trotinetes").writeLock().unlock();
        int n = this.mapa.get(y).get(x);
        this.mapa.get(y).set(x,n+1);
        this.locks.get("Mapa").writeLock().unlock();

    }

    // TESTADO
    private void addRecompensa(Recompensa recompensa)
    {
        // notificar clientes
        String c = this.getCodigo("Reservas");
        recompensa.setCod(c);
        this.recompensas.put(recompensa.getCod(),recompensa);
    }

    // USAR O RAIO
    private void addRecompensas()
    {
        Recompensa res = null;
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
                        xf = yf = -1;
                    }
                }
                if(elem > 1)
                {
                    res = new Recompensa(xi,yi,xf,yf,30);
                    if(xf != -1)
                    {
                        this.addRecompensa(res);
                        xf = yf = -1;
                        res = null;
                    }
                }
                xi++;
            }
            xi = 0;
            yi++;
        }
    }
    // TESTADO
    private void removeRecompensas()
    {
        List<String> removeRec = new ArrayList<>();
        this.recompensas.values().stream()
                .filter(r -> this.mapa.get(r.getYi()).get(r.getXi()) < 2)
                .forEach(r -> removeRec.add(r.getCod()));
        this.recompensas.values().stream()
                .filter(r -> this.mapa.get(r.getYi()).get(r.getXi()) > 1)
                .filter(r -> this.mapa.get(r.getYf()).get(r.getXf()) > 0)
                .forEach(r -> removeRec.add(r.getCod()));
        removeRec.forEach(this.recompensas::remove);
    }
    // TESTADO
    public void reverRecompensas()
    {
        this.locks.get("Recompensas").writeLock().lock();
        this.locks.get("Mapa").readLock().lock();
        this.removeRecompensas();
        this.addRecompensas();
        this.locks.get("Mapa").readLock().unlock();
        this.locks.get("Recompensas").writeLock().unlock();
    }

    // TESTAR PARA VERIFICAR CUSTOS RECOMPENSAS
    public float estacionamento(String cod, int x, int y)
    {
        this.locks.get("Trotinetes").writeLock().lock();
        this.locks.get("Reservas").readLock().lock();
        this.locks.get("Mapa").writeLock().lock();
        this.locks.get("ReservasRec").readLock().lock();
        Reserva reserva = this.reservas.get(cod);
        this.locks.get("Reservas").readLock().unlock();
        int n = this.mapa.get(y).get(x);
        this.mapa.get(y).set(x,n+1);
        this.locks.get("Mapa").writeLock().unlock();
        this.trotinetes.get(reserva.getTrotinete()).liberta();
        this.locks.get("Trotinetes").writeLock().unlock();
        float res;
        if(this.reservasRec.containsKey(cod))
            res = this.recompensas.get(this.reservasRec.get(cod)).getPremio();
        else
            res = reserva.geraCusto(x,y);
        this.locks.get("ReservasRec").readLock().unlock();
        this.lockthread.lock();
        this.cond.signal();
        this.lockthread.unlock();
        return res;
    }

    private double calculaDist(int x1, int y1, int x2, int y2)
    {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    public Reserva addReserva (int x, int y)
    {
        this.locks.get("Trotinetes").writeLock().lock();
        List<Trotinete> trotinetes = this.trotinetes.values().stream()
                .filter(t -> calculaDist(x,y,t.getX(),t.getY()) < this.raio)
                .filter(t -> !t.isReservada())
                .sorted((t1, t2) -> (int) (calculaDist(x, y, t1.getX(), t1.getY()) - calculaDist(x, y, t2.getX(), t2.getY())))
                .collect(Collectors.toList());
        Reserva reserva = null;
        if(trotinetes.size() > 0)
        {
            Trotinete trotinete = trotinetes.get(0);
            trotinete.reserva();
            this.locks.get("Reservas").writeLock().lock();
            this.locks.get("Mapa").writeLock().lock();
            this.locks.get("Trotinetes").writeLock().unlock();
            int n = this.mapa.get(trotinete.getY()).get(trotinete.getX());
            this.mapa.get(trotinete.getY()).set(trotinete.getX(), n - 1);
            this.locks.get("Mapa").writeLock().unlock();
            String c = this.getCodigo("Reservas");
            reserva = new Reserva(trotinete.getCodigo(), trotinete.getX(), trotinete.getY(), LocalDateTime.now(), c);
            this.reservas.put(reserva.getCodigo(), reserva);
            reserva.setCod(c);
            this.locks.get("Reservas").writeLock().unlock();
            this.lockthread.lock();
            this.cond.signal();
            this.lockthread.unlock();
        }
        else
            this.locks.get("Trotinetes").writeLock().unlock();
        return reserva;
    }

    public void aceitarRecompensa(String cod)
    {
        this.locks.get("Recompensas").writeLock().lock();
        Recompensa recompensa = this.recompensas.get(cod);
        recompensa.aceite();
        Reserva res = addReserva(recompensa.getXi(), recompensa.getYi());
        this.locks.get("ReservasRec").writeLock().lock();
        this.locks.get("Recompensas").writeLock().unlock();
        this.reservasRec.put(res.getCodigo(),recompensa.getCod());
        this.locks.get("ReservasRec").writeLock().unlock();
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

    @Override
    public String toString() {
        return "ScooterServer.ScooterServer.Server{" + "\n" +
                "clientes=" + clientes + "\n" +
                ", notificacoes=" + notificacoes + "\n" +
                ", trotinetes=" + trotinetes + "\n" +
                ", reservas=" + reservas + "\n" +
                ", recompensas=" + recompensas +
                '}';
    }

    public void terminaServidor()
    {
        this.recompensaThread.stop();
    }

    public List<Trotinete> getTrotinetes(int x, int y)
    {
        this.locks.get("Trotinetes").readLock().lock();
        List<Trotinete> result = this.trotinetes.values().stream().filter(t -> !t.isReservada())
                .filter(t -> calculaDist(x,y,t.getX(),t.getY()) < this.raio)
                .collect(Collectors.toList());
        this.locks.get("Trotinetes").readLock().unlock();
        return result;
    }
    public List<Recompensa> getRecompensas(int x, int y)
    {
        this.locks.get("Recompensas").readLock().lock();
        List<Recompensa> result = this.recompensas.values().stream()
                .filter(r -> calculaDist(x,y,r.getXi(),r.getYi()) < this.raio)
                .collect(Collectors.toList());
        this.locks.get("Recompensas").readLock().unlock();
        return result;
    }

    public List<List<Integer>> getMapa() {
        return mapa;
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