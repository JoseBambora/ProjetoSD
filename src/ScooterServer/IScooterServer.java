package ScooterServer;

import Comunicacao.TaggedConnection;

import java.io.DataInputStream;
import java.util.List;

public interface IScooterServer
{
    public void reverRecompensas();
    public float estacionamento(String cod, int x, int y, boolean isRecompensa);
    public Reserva addReserva (int x, int y, boolean aceitar);
    public Recompensa aceitarRecompensa(String cod);
    public void addCliente(String nome, String pass);
    public boolean verificaCredenciais(String nome, String pass);
    public List<Trotinete> getTrotinetes(int x, int y);
    public List<Recompensa> getRecompensas(int x, int y);

    public List<Notificacoes> notifica();
    public void addNotificacao(int x, int y, int tag, TaggedConnection toClient);
}
