import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int num = 10;
        for(int j = 0; j < 100; j++)
        {
            Server server = new Server(10,30);
            for(int i = 0; i < num; i++)
                server.addTrotinete(i,i);
            Thread[] threads = new Thread[num];
            for(int i = 0; i < num; i++)
                threads[i] = new Thread(new ThreadTeste(server,i));
            for(int i = 0; i < num; i++)
                threads[i].start();
            for(int i = 0; i < num; i++)
                threads[i].join();
            Thread.sleep(100);
            Map<String,Cliente> clienteMap = server.getClientes();
            Map<String,Recompensa> recompensas = server.getRecompensas();
            Map<String,Reserva> reservas = server.getReservas();
            Map<String,Trotinete> troti = server.getTrotinetes();
            if(clienteMap.size() != 10)
                System.out.println("Erro add clientes");
            if(troti.size() != 10)
                System.out.println("Erro add troti");
            if(reservas.size() != 10)
                System.out.println("Erro reservas");
            if(recompensas.size() != 0)
            {
                System.out.println("Erro recompensas 1 " + recompensas.size());
                System.out.println("Erro recompensas 2 " + recompensas.size());
                List<List<Integer>> map = server.getMapa();
                for(Recompensa recompensa : recompensas.values())
                {
                    System.out.println("=============================");
                    System.out.println("(" + recompensa.getXi() + "," + recompensa.getYi() + ")");
                    System.out.println(map.get(recompensa.getYi()).get(recompensa.getXi()));
                    System.out.println("(" + recompensa.getXf() + "," + recompensa.getYf() + ")");
                    System.out.println(map.get(recompensa.getYf()).get(recompensa.getXf()));
                }
            }
            for(int i = 0; i < num; i++)
                threads[i] = new Thread(new ThreadTeste1(server,i+num));
            for(int i = 0; i < num; i++)
                threads[i].start();
            for(int i = 0; i < num; i++)
                threads[i].join();
            server.terminaServidor();
            if(reservas.size() != 20)
                System.out.println("Errado reservas");


        }
    }
}