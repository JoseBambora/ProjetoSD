package ScooterServer;

public class Trotinete
{
    private String codigo;
    private int x;
    private int y;
    private boolean reservada;

    public Trotinete(String codigo, int x, int y)
    {
        this.codigo = codigo;
        this.x = x;
        this.y = y;
    }
    public void reserva()
    {
        this.reservada = true;
    }
    public void liberta(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.reservada = false;
    }

    public boolean isReservada() {
        return reservada;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return  !isReservada() ? "ID Trotinete: " + codigo + ", Coordenadas (" + x + "," + y + ")\n" : "";
    }
}
