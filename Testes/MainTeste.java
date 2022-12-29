import ScooterServer.IScooterServer;
import ScooterServer.ScooterServer;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import ScooterServer.Reserva;

public class MainTeste
{
    static IScooterServer server = new ScooterServer(2,40);

    private void cliente1(int n)
    {
        server.addCliente("a" + n, "a" + n);
    }
    private void cliente2(int n)
    {
        server.addCliente("b" + n, "b" + n);

    }
    private void cliente3(int n)
    {
        server.addCliente("c" + n, "c" + n);

    }
    private void cliente4(int n)
    {
        server.addCliente("d" + n, "d" + n);

    }
    private void cliente5(int n)
    {
        server.addCliente("e" + n, "e" + n);

    }
    @Test
    public void testeAddClientes()
    {
        List<Thread> clientes = new ArrayList<>();
        for(int i =0 ; i < 6; i++)
        {
            int finalI = i;
            clientes.add(new Thread(() -> cliente1(finalI)));
            clientes.add(new Thread(() -> cliente2(finalI)));
            clientes.add(new Thread(() -> cliente3(finalI)));
            clientes.add(new Thread(() -> cliente4(finalI)));
            clientes.add(new Thread(() -> cliente5(finalI)));
        }
        clientes.forEach(Thread::start);
        clientes.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        for(int i = 0; i < 6;i++)
        {
            Assert.assertTrue(server.verificaCredenciais("a" + i, "a" + i));
            Assert.assertTrue(server.verificaCredenciais("b" + i, "b" + i));
            Assert.assertTrue(server.verificaCredenciais("c" + i, "c" + i));
            Assert.assertTrue(server.verificaCredenciais("d" + i, "d" + i));
            Assert.assertTrue(server.verificaCredenciais("e" + i, "e" + i));
        }
    }
    @Test
    public void testeAddMapa()
    {
        ScooterServer scooter = (ScooterServer) server;
        this.testeAddClientes();
        List<List<Integer>> lists = scooter.getMapa();
        for(List<Integer> list : lists)
            Assert.assertTrue(list.stream().noneMatch(n -> n > 1));
        for(int i = 0; i < 40; i++)
        {
            for(int j = 0; j < 40; j++)
            {
                int num = scooter.getRaio(j, i).stream().filter(n -> n == 1).toList().size();
                if(lists.get(i).get(j) == 1)
                {
                    Assert.assertEquals(1,num);
                }
            }
        }
    }
    @Test
    public void testeGeraRecompensas() throws InterruptedException {
        testeAddMapa();
        Map<Integer,List<Reserva>> map = new HashMap<>();
        List<Thread> clientes = new ArrayList<>();
        for(int i = 0; i < 40; i++)
        {
            for (int j = 0; j < 40; j++)
            {
                int finalI = i;
                int finalJ = j;
                List<Reserva> r = new ArrayList<>();
                clientes.add(new Thread(() -> r.add(server.addReserva(finalI, finalJ))));
                map.put(j+i*40,r);
            }
        }
        clientes.forEach(Thread::start);
        clientes.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        ScooterServer scooter = (ScooterServer) server;
        Thread.sleep(200);
        Assert.assertEquals(0,scooter.getRecompensas().size());
        List<List<Integer>> lists = scooter.getMapa();
        List<Reserva> list = new ArrayList<>();
        map.values().forEach(l -> l.forEach(r -> {if(r != null) list.add(r);}));
        Assert.assertTrue(list.stream().allMatch(reserva -> lists.get(reserva.getYinicial()).get(reserva.getXinicial()) == 0));
        Assert.assertTrue(lists.stream().allMatch(l -> l.stream().allMatch(n -> n == 0)));
        List<Thread> t2 = new ArrayList<>();
        for(Reserva r : list)
        {
            t2.add(new Thread(() -> server.estacionamento(r.getCodigo(),r.getXinicial(),r.getYinicial())));
        }
        t2.forEach(Thread::start);
        t2.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Assert.assertTrue(list.stream().allMatch(reserva -> lists.get(reserva.getYinicial()).get(reserva.getXinicial()) == 1));
        Thread.sleep(200);
        Assert.assertEquals(0,scooter.getRecompensas().size());
        t2.clear();
        for(int i = 0; i < list.size() && i < 6; i++)
        {
            Reserva r = list.get(i);
            t2.add(new Thread(() -> server.estacionamento(r.getCodigo(),r.getXinicial(),r.getYinicial())));
        }
        t2.forEach(Thread::start);
        t2.forEach(t ->
        {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread.sleep(3000);
        Assert.assertEquals(0, scooter.getRecompensas().size());
    }
}
