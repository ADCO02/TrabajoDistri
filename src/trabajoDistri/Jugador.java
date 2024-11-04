package trabajoDistri;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Jugador {
    public static void main(String[] args) {
		try(Socket s = new Socket("localhost", 12345);
				ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream is = new ObjectInputStream(s.getInputStream());){
			//gestion
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}
