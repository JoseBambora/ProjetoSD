package ScooterServer;

public class Recompensa
{
    private final int xi;
    private final int yi;
    private int xf;
    private int yf;
    private float premio;
    private String cod;
    private boolean aceite;
    private final String troti;

    public Recompensa(int xi, int yi, int xf, int yf,String troti)
    {
        this.xi = xi;
        this.yi = yi;
        this.xf = xf;
        this.yf = yf;
        calculaPremio();
        this.troti = troti;
        this.aceite = false;
    }
    public Recompensa(Recompensa r)
    {
        this.xi = r.xi;
        this.yi = r.yi;
        this.xf = r.xf;
        this.yf = r.yf;
        calculaPremio();
        this.troti = r.troti;
        this.aceite = r.aceite;
    }

    public String getTroti() {
        return troti;
    }

    public float getPremio() {
        return premio;
    }

    public boolean isAceite() {
        return aceite;
    }

    public void calculaPremio()
    {
        this.premio = Math.abs(xf - xi) + Math.abs(yf - xi);
    }

    public void aceite()
    {
        this.aceite = true;
    }
    public void setCod(String cod)
    {
        this.cod = cod;
    }
    public String getCod()
    {
        return cod;
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recompensa that = (Recompensa) o;
        return cod.equals(that.cod);
    }

    public void setXf(int xf) {
        this.xf = xf;
    }

    public void setYf(int yf) {
        this.yf = yf;
    }

    @Override
    public int hashCode()
    {
        return cod.hashCode();
    }

    public int getXi() {
        return xi;
    }

    public int getYi() {
        return yi;
    }

    public int getXf() {
        return xf;
    }

    public int getYf() {
        return yf;
    }

    @Override
    public String toString() {
        return "Codigo recompensa: " + this.cod + "\nInício: (" + xi + "," + yi + ")\nDestino: (" + xf + "," + yf + ")\nPrémio: " + premio + "\n";
    }
}