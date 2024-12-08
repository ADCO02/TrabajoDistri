
package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    private static final int puerto = 12345;
	public static void main(String[] args) {
		HashMap<String,Socket> jugadores = new HashMap<String,Socket>();
		System.out.println("SERVIDOR ACTIVO");
		try(ServerSocket ss= new ServerSocket(puerto)){
			ExecutorService pool = Executors.newCachedThreadPool();
			while(true){
				try{
					Socket s=ss.accept();
					pool.execute(new PreguntaContra(s,jugadores,pool));
				}catch(IOException e) {e.printStackTrace();}
			}
		}catch(IOException e) {e.printStackTrace();}

	}
}
