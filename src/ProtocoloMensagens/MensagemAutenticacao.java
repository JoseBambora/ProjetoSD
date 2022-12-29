package ProtocoloMensagens;


public class MensagemAutenticacao extends Mensagem
{
    private final String username;
    private final String password;
    public MensagemAutenticacao(int id,String username, String passoword)
    {
        super(id);
        this.username = username;
        this.password = passoword;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public Frame createFrame()
    {
        String total = username + "___" + password;
        return new Frame(getId(),getTipo("Autenticacao"),total.getBytes());
    }
    public Frame createFrameResponse(boolean bool)
    {
        byte []b = new byte[1];
        if(!bool)
            b[0] = (byte) 1;
        return new Frame(getId(),getTipo("Autenticacao"),b);
    }
    public static MensagemAutenticacao receive(Frame frame)
    {
        byte []b = frame.data;
        String total = new String(b);
        String []up = total.split("___");
        String user = up[0];
        String pass = up[1];
        return new MensagemAutenticacao(frame.tag,user,pass);
    }
}
