public class Main {
    public static void main(String[] args) throws InterruptedException {
        int num = 10;
        for(int j = 0; j < 1; j++)
        {
            Server server = new Server(10,30);
            Thread[] threads1 = new Thread[num];
            for(int i = 0; i < num; i++)
                threads1[i] = new Thread(new ThreadAddTrotinete(server,i));
            for(int i = 0; i < num; i++)
                threads1[i].start();
            for(int i = 0; i < num; i++)
                threads1[i].join();
            Thread[] threads = new Thread[num];
            for(int i = 0; i < num; i++)
                threads[i] = new Thread(new ThreadTeste(server,i));
            for(int i = 0; i < num; i++)
                threads[i].start();
            for(int i = 0; i < num; i++)
                threads[i].join();
            server.terminaServidor();
        }
    }
}