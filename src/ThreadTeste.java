public class ThreadTeste implements Runnable
{
    private Server server;
    private Integer id;
    public ThreadTeste(Server server, int id)
    {
        this.server = server;
        this.id = id;
    }
    @Override
    public void run()
    {
        server.addCliente(id.toString(),id.toString());
        if(server.verificaCredenciais(id.toString(),Integer.toString(id+1)))
            System.out.println("Erro verifica credenciais 1 " + id);
        if(!server.verificaCredenciais(id.toString(),Integer.toString(id)))
            System.out.println("Erro verifica credenciais 2 " + id);
        String cod = server.addReserva(String.valueOf(id));
        float preco = server.estacionamento(cod, id + 1, id + 1);
        if(preco == 0)
            System.out.println("Erro estacionamento preço");

    }
}
