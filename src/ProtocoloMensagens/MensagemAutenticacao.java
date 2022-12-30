package ProtocoloMensagens;


public class MensagemAutenticacao extends Mensagem
{
    private final String username;
    private final String password;
    private final boolean login;
    public MensagemAutenticacao(int id,String username, String passoword, boolean login)
    {
        super(id);
        this.username = username;
        this.password = passoword;
        this.login = login;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public boolean getLogin()
    {
        return login;
    }
    public Frame createFrame()
    {
        String total = (login ? "LOGIN: " : "REGISTO: ") + "___" + username + "___" + password;
        return new Frame(getId(),getTipo("Autenticacao"),total.getBytes());
    }
    public Frame createFrameResponse(boolean bool)
    {
        byte [] msg_failure = "INVALIDO".getBytes();
        byte [] msg_sucess = ((login ? "Login " : "Registo ") + "válido! Bem-vindo " + username).getBytes(); 
        if(!bool)
            return new Frame(getId(),getTipo("Autenticacao"),msg_failure);
        else
            return new Frame(getId(),getTipo("Autenticacao"),msg_sucess);
    }
    public static MensagemAutenticacao receive(Frame frame)
    {
        byte []b = frame.data;
        String total = new String(b);
        String []up = total.split("___");
        String login_str = up[0];
        boolean login = (login_str.equals("LOGIN: ") ? true : false);
        String user = up[1];
        String pass = up[2];
        return new MensagemAutenticacao(frame.tag,user,pass,login);
    }
}
