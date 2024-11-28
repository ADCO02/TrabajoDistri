
package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Servidor {
    private static final int puerto = 12345;
	public static void main(String[] args) {
		HashMap<String,Socket> jugadores = new HashMap<String,Socket>();
		System.out.println("SERVIDOR ACTIVO");
		try(ServerSocket ss= new ServerSocket(puerto)){
			while(true){
				try{
					Socket s=ss.accept();
					PreguntaContra p = new PreguntaContra(s,jugadores);
					p.start();
				}catch(IOException e) {e.printStackTrace();}
			}
		}catch(IOException e) {e.printStackTrace();}

	}
}
