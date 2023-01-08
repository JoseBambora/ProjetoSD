package ScooterServer;

import Comunicacao.TaggedConnection;

import java.io.DataInputStream;
import java.util.List;

public interface IScooterServer
{
    public void reverRecompensas();
    public float estacionamento(String cod, int x, int y, String cliente, boolean isRecompensa);
    public Reserva addReserva (int x, int y,String cliente, boolean aceitar);
    public Recompensa aceitarRecompensa(String cod, String cliente);
    public void addCliente(String nome, String pass);
    public boolean verificaCredenciais(String nome, String pass);
    public List<Trotinete> getTrotinetes(int x, int y);
    public List<Recompensa> getRecompensas(int x, int y);

    public List<Notificacoes> notifica();
    public void addNotificacao(int x, int y, int tag, TaggedConnection toClient);
}
