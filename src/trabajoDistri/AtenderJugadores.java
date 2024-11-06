package trabajoDistri;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AtenderJugadores extends Thread {
    private Socket s1;
	private Socket s2;
	public AtenderJugadores(Socket s1, Socket s2) {this.s1= s1;this.s2=s2;}
	public void run() {
		try (
            ObjectOutputStream out1 = new ObjectOutputStream(s1.getOutputStream());
            ObjectOutputStream out2 = new ObjectOutputStream(s2.getOutputStream());
        ) {
            // Enviar host
            out1.writeObject(s2.getInetAddress().getHostAddress());
            out2.writeObject(s1.getInetAddress().getHostAddress());
			// Enviar servidor
			out1.writeObject(s2.getPort());
            out2.writeObject(s1.getPort());

            out1.flush();
            out2.flush();
		}
		catch(IOException e) {e.printStackTrace();}
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
