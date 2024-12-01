package Servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AtenderJugadores extends Thread {
	private Socket s1;
	private Socket s2;
	public AtenderJugadores(Socket s1, Socket s2) {
		super();
		this.s1=s1;
		this.s2=s2;
	}
	public void run() {
		System.out.println("Emparejando...");
		try {
			ObjectInputStream in1= new ObjectInputStream(s1.getInputStream());
			ObjectOutputStream out1= new ObjectOutputStream(s1.getOutputStream());
			ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream()); //se crea para ir a la par con el jugador
			ObjectOutputStream out2= new ObjectOutputStream(s2.getOutputStream());
			
			
            // Enviar orden
			out1.writeBoolean(true);
			out1.flush();
			out2.writeBoolean(false);
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