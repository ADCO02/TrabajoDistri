package Servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AtenderJugadores extends Thread {
    private Socket s1;
	private Socket s2;
	public AtenderJugadores(Socket s1, Socket s2) {this.s1= s1;this.s2=s2;}
	public void run() {
		System.out.println("Emparejando...");
		try (
            ObjectOutputStream out1 = new ObjectOutputStream(s1.getOutputStream());
            ObjectOutputStream out2 = new ObjectOutputStream(s2.getOutputStream());
        ) {
            // Enviar orden
			out1.writeObject(true);
			out1.flush();
			out2.writeObject(false);
			out2.flush();
			try(ObjectInputStream in1 = new ObjectInputStream(s1.getInputStream())){
				//Enviar ip
				out2.writeBytes(in1.readLine()+"\n");
				// Enviar puerto a j2
	            out2.writeInt(in1.readInt());
	            out2.flush();
			}
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
