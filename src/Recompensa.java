public class Recompensa
{
    private int xi;
    private int yi;
    private int xf;
    private int yf;
    private float premio;
    private String cod;

    public Recompensa(int xi, int yi, int xf, int yf, float premio)
    {
        this.xi = xi;
        this.yi = yi;
        this.xf = xf;
        this.yf = yf;
        this.premio = premio;
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
}