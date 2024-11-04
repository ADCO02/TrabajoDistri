package trabajoDistri;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AtenderJugadores extends Thread {
    private Socket s1;
	private Socket s2;
	public AtenderJugadores(Socket s1, Socket s2) {this.s1= s1;this.s2=s2;}
	public void run() {
		try
		{
			//hace que los dos jugadores se comuniquen entre ellos
		}
		catch(IOException e) {e.printStackTrace();}
	}
}
