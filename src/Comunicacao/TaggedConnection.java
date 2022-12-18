package Comunicacao;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ProtocoloMensagens.*;

public class TaggedConnection implements AutoCloseable
{
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Lock readlock = new ReentrantLock();
    private final Lock writelock = new ReentrantLock();
    public TaggedConnection(Socket socket) throws IOException
    {
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

    }
    public void send(Frame frame) throws IOException {
        writelock.lock();
        try
        {
            this.dos.writeInt(frame.tag);
            this.dos.writeInt(frame.tipo);
            this.dos.writeInt(frame.data.length);
            this.dos.write(frame.data);
            this.dos.flush();
        }
        finally {
            writelock.unlock();
        }
    }
    public void send(Mensagem mensagem) throws IOException {
        this.send(mensagem.createFrame());
    }

    public Frame receive() throws IOException {
        readlock.lock();
        try {
            int tag = this.dis.readInt();
            int tipo = this.dis.readInt();
            int size = this.dis.readInt();
            byte[] data;
            data = new byte[size];
            this.dis.readFully(data);
            return new Frame(tag,tipo,data);
        }
        finally {
            readlock.unlock();
        }
    }

    public void close() throws IOException {
        this.dis.close();
        this.dos.close();
    }
}
