package ScooterServer;

import java.util.List;

public interface ScooterServer
{
    public void reverRecompensas();
    public float estacionamento(String cod, int x, int y);
    public Reserva addReserva (int x, int y);
    public void aceitarRecompensa(String cod);
    public void addCliente(String nome, String pass);
    public boolean verificaCredenciais(String nome, String pass);
    public List<Trotinete> getTrotinetes(int x, int y);
    public List<Recompensa> getRecompensas(int x, int y);
}
