package Comunicacao;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ProtocoloMensagens.Frame;

public class Demultiplexer implements AutoCloseable
{
    private final TaggedConnection connection;
    private Map<Integer, Queue<byte[]>> queueMap;
    private Map<Integer, Condition> conditionMap;
    private Lock lockMap;
    public Demultiplexer(TaggedConnection conn)
    {
        this.connection = conn;
        this.queueMap = new HashMap<>();
        this.conditionMap = new HashMap<>();
        this.lockMap = new ReentrantLock();
    }
    public void start() throws IOException
    {
        // Iniciar thread para gerir os pedidos
        Runnable r = () ->
        {
            try
            {
                while (true)
                {
                    Frame f = this.connection.receive();
                    this.lockMap.lock();
                    this.queueMap.get(f.tag).add(f.data);
                    this.conditionMap.get(f.tag).signal();
                    this.lockMap.unlock();
                }
            } catch (Exception ignored)
            {
                this.lockMap.lock();
                // CASO DE EXCECAO -> PARAR SERVIDOR
                this.conditionMap.values().forEach(Condition::signal);
                this.lockMap.unlock();
            }
            System.out.println("acabou");
        };
        Thread t = new Thread(r);
        t.start();
    }
    public void send(Frame frame) throws IOException
    {
        lockMap.lock();
        int tag = frame.tag;
        if(!this.queueMap.containsKey(tag))
        {
            this.queueMap.put(tag,new LinkedList<>());
            this.conditionMap.put(tag, lockMap.newCondition());
        }
        lockMap.unlock();
        connection.send(frame);
    }
    public void send(int tag, byte[] data) throws IOException
    {
        lockMap.lock();
        if(!this.queueMap.containsKey(tag))
        {
            this.queueMap.put(tag,new LinkedList<>());
            this.conditionMap.put(tag, lockMap.newCondition());
        }
        lockMap.unlock();
        Frame f = new Frame(tag,0, data);
        connection.send(f);
    }
    public byte[] receive(int tag) throws IOException, InterruptedException
    {
        this.lockMap.lock();
        while(this.queueMap.get(tag).isEmpty())
        {
            this.conditionMap.get(tag).await();
        }
        this.lockMap.unlock();
        return this.queueMap.get(tag).remove();
    }
    public void close() throws IOException
    {
        connection.close();
    }
}
