package ProtocoloMensagens;

public class Frame
{
    public final int tag;
    public final int tipo;
    public final byte[] data;
    public Frame(int tag, int tipo, byte[] data)
    {
        this.tag = tag; this.data = data;
        this.tipo = tipo;
    }
}
