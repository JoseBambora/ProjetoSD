package ScooterServer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Reserva
{
    private final String trotinete;
    private final int xinicial;
    private final int yinicial;
    private final LocalDateTime dataDaReserva;
    private String codigo;
    private String cliente;

    public Reserva(String trotinete, int xinicial, int yinicial, LocalDateTime dataDaReserva, String codigo, String cliente)
    {
        this.trotinete = trotinete;
        this.xinicial = xinicial;
        this.yinicial = yinicial;
        this.dataDaReserva = dataDaReserva;
        this.codigo = codigo;
        this.cliente = cliente;
    }

    public String getCliente() {
        return cliente;
    }

    public String getTrotinete() {
        return trotinete;
    }

    public int getXinicial() {
        return xinicial;
    }

    public int getYinicial() {
        return yinicial;
    }

    public void setCod(String cod)
    {
        this.codigo = cod;
    }
    public String getCodigo()
    {
        return codigo;
    }
    public float geraCusto(int xf, int yf)
    {
        return Math.abs(xf - xinicial) + Math.abs(yf - yinicial) * (ChronoUnit.SECONDS.between(dataDaReserva, LocalDateTime.now())+1);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return codigo.equals(reserva.codigo);
    }

    @Override
    public int hashCode()
    {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return "Codigo da reserva: " + codigo + "\nTrotinete: " + trotinete + "\nCoordenadas: (" + xinicial + "," + yinicial + ")";
    }
}
