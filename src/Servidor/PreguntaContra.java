package Servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PreguntaContra extends Thread{
	private Socket s;
	private HashMap<String,Socket> j;
	public PreguntaContra(Socket socket, HashMap<String,Socket> jugadores) {
		s=socket;
		j=jugadores;
	}
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			String contra=ois.readLine(); //contraseña vacia no vale
			if(j.containsKey(contra)) {
				AtenderJugadores p = new AtenderJugadores (j.get(contra),s);
			}
			else {
				j.put(contra, s);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
