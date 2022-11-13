import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class Reserva
{
    private String trotinete;
    private int xinicial;
    private int yinicial;
    private LocalDateTime dataDaReserva;
    private String codigo;

    public Reserva(String trotinete, int xinicial, int yinicial, LocalDateTime dataDaReserva, String codigo)
    {
        this.trotinete = trotinete;
        this.xinicial = xinicial;
        this.yinicial = yinicial;
        this.dataDaReserva = dataDaReserva;
        this.codigo = codigo;
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
        return Math.abs(xf - xinicial) + Math.abs(yf - yinicial) * ChronoUnit.MINUTES.between(dataDaReserva, LocalDateTime.now());
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
        return "Trot: " + trotinete + ", Cod: " + codigo ;
    }
}
