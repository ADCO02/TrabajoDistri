package Servidor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

public class PreguntaContra extends Thread{
	private Socket s;
	private HashMap<String,Socket> j;
	private ExecutorService pool;
	public PreguntaContra(Socket socket, HashMap<String,Socket> jugadores, ExecutorService pool) {
		super();
		s=socket;
		j=jugadores;
		this.pool=pool;
	}
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(s.getInputStream());
			String contra=dis.readLine(); //contraseña vacia no vale (es la de partida pública)
			
			
			synchronized(j){
				Socket s2=j.get(contra);
				
				//si ya estaba la contraseña, saca el socket y llama a juntarlos
				if(s2!=null) {
					if(contra.equals("")) {
						System.out.println("Partida pública encontrada");
					}else {
						System.out.println("Partida privada encontrada con contraseña: "+contra);
					}
					
					j.remove(contra);
					pool.execute(new AtenderJugadores (s, s2));
				}
				
				//si la contraseña no estaba ya, la mete
				else {
					if(contra.equals("")) {
						System.out.println("Esperando para partida pública");
					}else {
						System.out.println("Esperando para partida privada con contraseña: "+contra);
					}
					j.put(contra, s);
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}