package Servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
    private static final int puerto = 12345;
	public static void main(String[] args) {
		Map<String,Socket> jugadores = new HashMap<String,Socket>();
		System.out.println("SERVIDOR ACTIVO");
		try(ServerSocket ss= new ServerSocket(puerto)){
			while(true){
				try{
//					Socket s=ss.accept();
//					ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
//					Object contra =(String) ois.readObject();
//					if(jugadores.containsKey(contra)) {
//						AtenderJugadores p = new AtenderJugadores(,s);
//						p.start();
//					}
					//Crear hilo que pregunte la contraseña
					Socket s=ss.accept();
					PreguntaContra p = new PreguntaContra(s,jugadores);
					p.start();
					
					
					
//					Socket s1= ss.accept();
//					System.out.println("J1");
//                    Socket s2= ss.accept();
//					System.out.println("J2");
//                    AtenderJugadores p = new AtenderJugadores(s1,s2);
//					p.start();
				}catch(IOException e) {e.printStackTrace();}
			}
		}catch(IOException e) {e.printStackTrace();}

	}
}