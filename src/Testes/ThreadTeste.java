package Testes;

import ScooterServer.*;

public class ThreadTeste implements Runnable
{
    private ScooterServer server;
    private Integer id;
    public ThreadTeste(ScooterServer server, int id)
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
        Reserva cod = server.addReserva(id,id);
        float preco = server.estacionamento(cod.getCodigo(), id + 1, id + 1);
        if(preco == 0)
            System.out.println("Erro estacionamento preço");

    }
}
