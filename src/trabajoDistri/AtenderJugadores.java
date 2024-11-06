package trabajoDistri;

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
			ObjectInputStream in1 = new ObjectInputStream(s1.getInputStream());
            ObjectOutputStream out2 = new ObjectOutputStream(s2.getOutputStream());
			ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());
        ) {
			System.out.println("Entra");
            // Enviar orden
			out1.writeObject(true);
			out1.flush();
			out2.writeObject(false);
			// Envia host a j2
            out2.writeObject((String)in1.readObject());
			// Enviar puerto a j2
            out2.writeObject((int)in1.readObject());
			
            out2.flush();
		}
		catch(IOException | ClassNotFoundException e) {
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
