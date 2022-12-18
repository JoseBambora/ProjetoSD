package ProtocoloMensagens;

public abstract class Mensagem
{
    // 0 -> Autenticacao
    private int id;
    public Mensagem(int id)
    {
        this.id = id;
    }
    public abstract Frame createFrame();

    public static int getTipo(String t)
    {
        int r;
        switch (t)
        {
            case "Autenticacao":
                r = 0;
                break;
            default :
                r = 1;
                break;
        }
        return r;
    }

    public int getId() {
        return id;
    }

    public static Mensagem getMessage(Frame f)
    {
        Mensagem r = null;
        if(f.tipo == 0)
        {
            r = MensagemAutenticacao.receive(f);
        }
        return r;
    }
}
