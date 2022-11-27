public class Cliente
{
    private final String nomeUtilizador;
    private final String password;

    public Cliente(String nomeUtilizador, String password)
    {
        this.nomeUtilizador = nomeUtilizador;
        this.password = password;
    }

    public String getNomeUtilizador()
    {
        return nomeUtilizador;
    }

    public String getPassword()
    {
        return password;
    }

    public boolean verificaPassword(String password)
    {
        return this.password.equals(password);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return nomeUtilizador.equals(cliente.nomeUtilizador);
    }

    @Override
    public String toString() {
        return nomeUtilizador + " " + password;
    }
}
