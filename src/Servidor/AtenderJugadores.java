package Servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AtenderJugadores extends Thread {
    private ObjectOutputStream out1;
	private ObjectOutputStream out2;
	private ObjectInputStream in1;
	private Socket s1;
	private Socket s2;
	public AtenderJugadores(ObjectOutputStream out1, ObjectOutputStream out2, ObjectInputStream in1, Socket s1, Socket s2) {
		this.out1= out1;
		this.in1=in1;
		this.out2=out2;
		this.s1=s1;
		this.s2=s2;
	}
	public void run() {
		System.out.println("Emparejando...");
		try {
            // Enviar orden
			out1.writeBoolean(true);
			out1.flush();
			out2.writeBoolean(false);
			out2.flush();
			//Enviar ip
			out2.writeBytes(in1.readLine()+"\n");
			// Enviar puerto a j2
			out2.writeInt(in1.readInt());
			out2.flush();
			System.out.println("Partida iniciada");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if (s1 != null && !s1.isClosed()) {
					s1.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (s2 != null && !s2.isClosed()) {
					s2.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
