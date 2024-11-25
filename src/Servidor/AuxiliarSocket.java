package Servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuxiliarSocket {
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public AuxiliarSocket(Socket s, ObjectInputStream ois, ObjectOutputStream oos){
        this.s=s;
        this.ois=ois;
        this.oos=oos;
    }

    public Socket getSocket(){
        return this.s;
    }

    public ObjectInputStream getOIS(){
        return this.ois;
    }

    public ObjectOutputStream getOOS(){
        return this.oos;
    }
}