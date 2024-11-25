package Servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class PreguntaContra extends Thread{
	private Socket s;
	private HashMap<String,AuxiliarSocket> j;
	public PreguntaContra(Socket socket, HashMap<String,AuxiliarSocket> jugadores) {
		s=socket;
		j=jugadores;
	}
	public void run() {
		try {
			ObjectOutputStream oos= new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			String contra=ois.readLine(); //contraseña vacia no vale (es la de partida pública)
			synchronized(j){
				AuxiliarSocket s2=j.get(contra);
				if(s2!=null) {
					System.out.println("Partida encontrada con contraseña: "+contra);
					j.remove(contra);
					AtenderJugadores p = new AtenderJugadores (oos,s2.getOOS(),ois, s, s2.getSocket());
					p.start();
				}
				else {
					System.out.println("Esperando para contraseña: "+contra);
					j.put(contra, new AuxiliarSocket(s, ois, oos));
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
