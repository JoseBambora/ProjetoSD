package ProtocoloMensagens;

public abstract class Mensagem
{
    // 0 -> Autenticacao
    // 1  ->Trotinetes 
    // 2  ->Recompensas
    // 3  ->Reservar
    // 4  ->Estacionamento
    // 5  ->Notificacoes  
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
            case "Trotinetes":
                r = 1;
                break;
            case "Recompensas":
                r = 2;
                break;
            case "Reservar":
                r = 3;
                break;
            case "Estacionamento":
                r = 4;
                break;
            case "Notificacoes":
                r = 5;
                break;
            default:
                r = -1;
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
            r = MensagemAutenticacao.receive(f);
        if(f.tipo == 1)
            r = MensagemTrotinetes.receive(f);
        if(f.tipo == 2)
            r = MensagemRecompensas.receive(f);
        if(f.tipo == 3)
            r = MensagemReservar.receive(f);
        return r;
    }
}
