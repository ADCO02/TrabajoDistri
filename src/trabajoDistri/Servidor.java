package trabajoDistri;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int puerto = 12345;
	public static void main(String[] args) {
		System.out.println("SERVIDOR ACTIVO");
		try(ServerSocket ss= new ServerSocket(puerto)){
			while(true){
				try{
					Socket s1= ss.accept();
					System.out.println("J1");
                    Socket s2= ss.accept();
					System.out.println("J2");
                    AtenderJugadores p = new AtenderJugadores(s1,s2);
					p.start();
				}catch(IOException e) {e.printStackTrace();}
			}
		}catch(IOException e) {e.printStackTrace();}

	}
}