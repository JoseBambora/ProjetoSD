public class Cliente
{
    private String nomeUtilizador;
    private String password;

    public Cliente(String nomeUtilizador, String password) {
        this.nomeUtilizador = nomeUtilizador;
        this.password = password;
    }

    public String getNomeUtilizador() {
        return nomeUtilizador;
    }

    public String getPassword() {
        return password;
    }
}
