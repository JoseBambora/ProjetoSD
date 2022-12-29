package g8;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ProtocoloMensagens.*;

public class Demultiplexer implements AutoCloseable {

    private TaggedConnection taggedConnection;
    private Lock l = new ReentrantLock();

    private IOException exception = null;

    private Map<Integer, Value> map= new HashMap<>();

    private class Value{
        int waiters = 0;
        Queue<byte[]> deque = new ArrayDeque<>();
        Condition c = l.newCondition();
        public Value(){
        }

    }

    public Demultiplexer(TaggedConnection conn) {
        this.taggedConnection = conn;

    }
    public void start(){
        new Thread(()->{
                while (true) {
                    try {
                        Frame frame = taggedConnection.receive();
                        l.lock();
                        try {
                            Value value = map.get(frame.tag);
                            value.waiters++;
                            if (value == null) {
                                value = new Value();
                                map.put(frame.tag, value);
                            }
                            value.deque.add(frame.data);
                            value.c.signal();
                        } finally {
                            l.unlock();
                        }
                    } catch (IOException e) {
                       this.exception = e;
                    }
                }
        }).start();
    }

    public void send(Frame frame) throws IOException {
        taggedConnection.send(frame);

    }
    public void send(Mensagem mensagem) throws IOException {
        taggedConnection.send(mensagem);
    }
    public byte[] receive(int tag) throws IOException, InterruptedException {
        l.lock();
        Value v;
        try {
            v = map.get(tag);
            if(v == null){
                v = new Value();
                map.put(tag, v);
            }
            while (v.deque.isEmpty()){
                    v.c.await();
                }
            byte[] reply = v.deque.poll();
            if(v.deque.isEmpty() && v.waiters == 0){
                map.remove(tag);
            }
            return  reply;
        }finally {
            l.unlock();
        }
    }

    public void close() throws IOException {
        taggedConnection.close();
    }

}