package ScooterServer;

import Comunicacao.TaggedConnection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Ver """erro""" de acordar a thread das recompensas.
 * ScooterServer.ScooterServer.Comunicacao.Server - ScooterServer.Cliente
 * Notificações
 * Melhorar os testes
 * Melhorar o código
 * Fazer relatório
 */
public class ScooterServer implements IScooterServer
{
    private final Map<String, Cliente> clientes;
    private final Map<String, Notificacoes> notificacoes;
    private final Map<String, Trotinete> trotinetes;
    private final Map<String, Reserva> reservas;
    private final Map<String, Recompensa> recompensas;
    private final List<List<Integer>> mapa; // número de trotinetes em cada posição
    private final Map<String, ReentrantReadWriteLock> locks;
    private final Map<String, Integer> codigos;
    private final Lock lockthread;
    private final Condition cond;
    private final Lock notilockthread;
    private final Condition noticond;
    private final int raio;
    private final int tamanho;
    public ScooterServer(int raio, int tamanho)
    {
        this.locks = new HashMap<>();
        this.locks.put("Clientes",new ReentrantReadWriteLock());
        this.locks.put("Notificacoes",new ReentrantReadWriteLock());
        this.locks.put("Trotinetes",new ReentrantReadWriteLock());
        this.locks.put("Reservas",new ReentrantReadWriteLock());
        this.locks.put("Recompensas",new ReentrantReadWriteLock());
        this.locks.put("Mapa",new ReentrantReadWriteLock());
        this.locks.put("Codigos",new ReentrantReadWriteLock());
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
        this.notificacoes = new HashMap<>();
        this.lockthread = new ReentrantLock();
        this.cond = lockthread.newCondition();
        this.notilockthread = new ReentrantLock();
        this.noticond = notilockthread.newCondition();
        Thread recompensaThread = new Thread(new ThreadRecompensas(this,this.lockthread,this.cond));
        recompensaThread.start();
        Thread notificacoesThread = new Thread(new ThreadNotifica(this,notilockthread,noticond));
        notificacoesThread.start();
        this.mapa = new ArrayList<>(tamanho);
        for(int i = 0; i < tamanho; i++)
        {
            this.mapa.add(new ArrayList<>(tamanho));
            for(int j = 0; j < tamanho; j++)
                this.mapa.get(i).add(0);
        }
        this.tamanho = tamanho;
        this.raio = raio;
        this.geraMap(tamanho);
    }

    private String getCodigo(String campo)
    {
        this.locks.get("Codigos").writeLock().lock();
        Integer c = this.codigos.get(campo);
        this.codigos.put(campo,c+1);
        this.locks.get("Codigos").writeLock().unlock();
        return c.toString();
    }
    private double calculaDist(int x1, int y1, int x2, int y2)
    {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    private void geraMap(int tamanho)
    {
        Random r = new Random();
        for(int y = 0; y < tamanho; y++)
        {
            for(int x = 0; x < tamanho; x++)
            {
                int random = r.nextInt(2);
                if(random == 1 && this.getRaio(x,y).stream().allMatch(n -> n == 0))
                {
                    this.mapa.get(y).set(x,random);
                    Trotinete t = new Trotinete(this.getCodigo("Trotinetes"),x,y);
                    this.trotinetes.put(t.getCodigo(),t);
                }
            }
        }
    }

    // TESTADO
    private void addRecompensa(Recompensa recompensa)
    {
        Recompensa add = new Recompensa(recompensa);
        String c = this.getCodigo("Recompensas");
        add.setCod(c);
        this.recompensas.put(c,add);
        System.out.println("Recompensa adicionada");
        signalNT();
    }
    private void signalNT()
    {
        notilockthread.lock();
        noticond.signal();
        notilockthread.unlock();
    }

    private void signalRT()
    {
        this.lockthread.lock();
        this.cond.signal();
        this.lockthread.unlock();
    }

    public List<List<Integer>> getMapa() {
        return mapa;
    }

    public List<Integer> getRaio(int xt, int yt)
    {
        List<Integer> raio = new ArrayList<>();
        int maxy = Math.min(yt + this.raio,20);
        int maxx = Math.min(xt + this.raio, this.tamanho);
        int inity = Math.max(0 , yt - this.raio);
        int initx = Math.max(0 , xt - this.raio);
        for(int y = inity; y < maxy; y++)
        {
            for (int x = initx; x < maxx; x++)
            {
                if(this.calculaDist(xt,yt,x,y) <= this.raio)
                    raio.add(this.mapa.get(y).get(x));
            }
        }
        return raio;
    }
    // USAR O RAIO
    private void addRecompensas()
    {
        Recompensa res = null;
        int xi, yi, xf = -1, yf = -1;
        for(yi = 0;yi < mapa.size(); yi++)
        {
            for(xi = 0; xi < mapa.get(yi).size(); xi++)
            {
                List<Trotinete> trotinetes = this.getTrotinetes(xi,yi);
                int num = trotinetes.size();
                if(num == 0)
                {
                    xf = xi;
                    yf = yi;
                    int finalXf = xf;
                    int finalYf = yf;
                    if(res != null && this.recompensas.values().stream().noneMatch(r -> calculaDist(r.getXf(),r.getYf(),finalXf,finalYf) <= this.raio) && res.getXi() != finalXf && res.getYi() != finalYf)
                    {
                        res.setXf(xf);
                        res.setYf(yf);
                        this.addRecompensa(res);
                        res = null;
                        xf = yf = -1;
                    }
                }
                else if(num > 1 && this.mapa.get(yi).get(xi) > 0)
                {
                    int finalXi = xi;
                    int finalYi = yi;
                    Trotinete troti = trotinetes.stream().filter(t -> t.getX() == finalXi && t.getY() == finalYi).findFirst().orElse(null);
                    if(troti != null)
                    {
                        res = new Recompensa(xi, yi, xf, yf, troti.getCodigo());
                        if (xf != -1) {
                            this.addRecompensa(res);
                            xf = yf = -1;
                            res = null;
                        }
                    }
                }
            }
        }
    }
    // TESTADO
    private void removeRecompensas()
    {
        Set<String> removeRec = new HashSet<>();
        this.recompensas.values().stream()
                .filter(r -> !r.isAceite())
                .filter(r -> this.getTrotinetes(r.getXf(),r.getYf()).size() > 0)
                .forEach(r -> removeRec.add(r.getCod()));
        this.recompensas.values().stream()
                .filter(r -> !r.isAceite())
                .filter(r -> this.getTrotinetes(r.getXi(),r.getYi()).size() <= 1)
                .forEach(r -> removeRec.add(r.getCod()));
        removeRec.forEach(this.recompensas::remove);
    }
    // TESTADO
    public void reverRecompensas()
    {
        this.locks.get("Recompensas").writeLock().lock();
        this.locks.get("Mapa").readLock().lock();
        System.out.println("Rever recompensas");
        this.removeRecompensas();
        this.addRecompensas();
        System.out.println("Recompensa revistas");
        this.locks.get("Mapa").readLock().unlock();
        this.locks.get("Recompensas").writeLock().unlock();
    }
    private void estacionaTroti(int x, int y, String troti)
    {
        this.locks.get("Trotinetes").writeLock().lock();
        this.locks.get("Mapa").writeLock().lock();
        int n = this.mapa.get(y).get(x);
        this.mapa.get(y).set(x, n + 1);
        this.locks.get("Mapa").writeLock().unlock();
        this.trotinetes.get(troti).liberta(x, y);
        this.locks.get("Trotinetes").writeLock().unlock();
    }
    // TESTAR PARA VERIFICAR CUSTOS RECOMPENSAS
    public float estacionamento(String cod, int x, int y, boolean isRecompensa) {
        float res = 0;
        if (isRecompensa)
        {
            this.locks.get("Recompensas").readLock().lock();
            Recompensa r = this.recompensas.get(cod);
            if(r != null)
            {
                res = r.getPremio();
                estacionaTroti(r.getXf(),r.getYf(),r.getTroti());
            }
            this.locks.get("Recompensas").readLock().unlock();
        }
        else
        {
            this.locks.get("Reservas").writeLock().lock();
            Reserva reserva = this.reservas.get(cod);
            if (reserva != null)
            {
                this.reservas.remove(cod);
                estacionaTroti(x,y,reserva.getTrotinete());
                this.locks.get("Reservas").writeLock().unlock();
                res = reserva.geraCusto(x, y);
                this.signalRT();
            }
            else
            {
                this.locks.get("Reservas").writeLock().unlock();
            }
        }
        return res;
    }
    private Reserva reservaTrotinete (Trotinete trotinete)
    {
        trotinete.reserva();
        int n = this.mapa.get(trotinete.getY()).get(trotinete.getX());
        this.mapa.get(trotinete.getY()).set(trotinete.getX(), n - 1);
        return new Reserva(trotinete.getCodigo(), trotinete.getX(), trotinete.getY(), LocalDateTime.now(), "");
    }
    private void addReservaMap(Reserva reserva)
    {
        String c = this.getCodigo("Reservas");
        reserva.setCod(c);
        this.reservas.put(reserva.getCodigo(), reserva);
    }

    public Reserva addReserva (int x, int y, boolean acordar)
    {
        this.locks.get("Trotinetes").writeLock().lock();
        List<Trotinete> trotinetes = new ArrayList<>(this.getTrotinetes(x,y));
        trotinetes.sort((t1, t2) -> (int) (calculaDist(x, y, t1.getX(), t1.getY()) - calculaDist(x, y, t2.getX(), t2.getY())));
        Reserva reserva = null;
        if(trotinetes.size() > 0)
        {
            this.locks.get("Mapa").writeLock().lock();
            reserva = this.reservaTrotinete(trotinetes.get(0));
            this.locks.get("Reservas").writeLock().lock();
            this.locks.get("Trotinetes").writeLock().unlock();
            this.locks.get("Mapa").writeLock().unlock();
            this.addReservaMap(reserva);
            this.locks.get("Reservas").writeLock().unlock();
            if(acordar)
                this.signalRT();
        }
        else
        {
            this.locks.get("Trotinetes").writeLock().unlock();
        }
        return reserva;
    }

    public Map<String, Recompensa> getRecompensas() {
        return recompensas;
    }

    public Recompensa aceitarRecompensa(String cod)
    {
        this.locks.get("Recompensas").writeLock().lock();
        Recompensa recompensa = this.recompensas.get(cod);
        if(recompensa != null && !recompensa.isAceite())
        {
            this.locks.get("Trotinetes").writeLock().lock();
            this.locks.get("Mapa").writeLock().lock();
            recompensa.aceite();
            this.locks.get("Recompensas").writeLock().unlock();
            this.trotinetes.get(recompensa.getTroti()).reserva();
            this.locks.get("Trotinetes").writeLock().unlock();
            int n = this.mapa.get(recompensa.getYi()).get(recompensa.getXi());
            this.mapa.get(recompensa.getYi()).set(recompensa.getXi(),n-1);
            this.locks.get("Mapa").writeLock().unlock();
            return recompensa;
        }
        else
            this.locks.get("Recompensas").writeLock().unlock();
        return null;
    }

    private void addClienteMap (String nome, String pass)
    {
        if(!this.clientes.containsKey(nome))
        {
            Cliente cliente = new Cliente(nome,pass);
            this.clientes.put(nome,cliente);
        }
    }
    // TESTADO
    public void addCliente(String nome, String pass)
    {
        this.locks.get("Clientes").writeLock().lock();
        this.addClienteMap(nome,pass);
        this.locks.get("Clientes").writeLock().unlock();
    }

    private boolean verificaCredenciaisAux(String nome, String pass)
    {
        boolean res = false;
        Cliente cliente = this.clientes.get(nome);
        if(cliente != null)
            res = cliente.verificaPassword(pass);
        return res;
    }

    // TESTADO
    public boolean verificaCredenciais(String nome, String pass)
    {
        this.locks.get("Clientes").readLock().lock();
        boolean res = this.verificaCredenciaisAux(nome,pass);
        this.locks.get("Clientes").readLock().unlock();
        return res;
    }

    @Override
    public String toString() {
        return "ScooterServer.ScooterServer.Comunicacao.Server{" + "\n" +
                "clientes=" + clientes + "\n" +
                ", notificacoes=" + notificacoes + "\n" +
                ", trotinetes=" + trotinetes + "\n" +
                ", reservas=" + reservas + "\n" +
                ", recompensas=" + recompensas +
                '}';
    }

    private List<Trotinete> getTrotinetesAux(int x, int y)
    {
        return this.trotinetes.values()
                .stream()
                .filter(t -> !t.isReservada())
                .filter(t -> calculaDist(x,y,t.getX(),t.getY()) <= this.raio)
                .toList();
    }
    private List<Recompensa> getRecompensasAux(int x, int y)
    {
        return this.recompensas.values()
                .stream()
                .filter(r -> !r.isAceite())
                .filter(r -> calculaDist(x,y,r.getXi(),r.getYi()) <= this.raio)
                .toList();
    }

    public List<Trotinete> getTrotinetes(int x, int y)
    {
        this.locks.get("Trotinetes").readLock().lock();
        List<Trotinete> result = this.getTrotinetesAux(x,y);
        this.locks.get("Trotinetes").readLock().unlock();
        return result;
    }
    public List<Recompensa> getRecompensas(int x, int y)
    {
        this.locks.get("Recompensas").readLock().lock();
        List<Recompensa> result = this.getRecompensasAux(x,y);
        this.locks.get("Recompensas").readLock().unlock();
        return result;
    }

    @Override
    public List<Notificacoes> notifica()
    {
        this.locks.get("Notificacoes").writeLock().lock();
        this.locks.get("Recompensas").readLock().lock();
        List<Notificacoes> list = this.notificacoes.values().stream()
                .filter(n -> this.recompensas.values().stream()
                        .anyMatch(r -> this.calculaDist(r.getXi(),r.getYi(),n.getX(),n.getY()) <= this.raio))
                .toList();
        this.locks.get("Recompensas").readLock().unlock();
        list.forEach(n -> this.notificacoes.remove(n.getCodigo()));
        this.locks.get("Notificacoes").writeLock().unlock();
        return list;
    }

    @Override
    public void addNotificacao(int x, int y, int tag, TaggedConnection toClient)
    {
        String cod = this.getCodigo("Notificacoes");
        this.locks.get("Notificacoes").writeLock().lock();
        this.notificacoes.put(cod,new Notificacoes(cod,x,y,tag,toClient));
        this.locks.get("Notificacoes").writeLock().unlock();
        signalNT();
    }
}