package trabajoDistri;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Jugador {
    public static void main(String[] args) {
		//meter menú de buscar partida / instrucciones etc

		buscaPartida("localhost",12345);
	}
	
	private static void buscaPartida(String hostServ, int portServ){
		System.out.println("Buscando partida...");
		
		try(Socket servidor = new Socket(hostServ, portServ);
			ObjectInputStream is = new ObjectInputStream(servidor.getInputStream());
			ObjectOutputStream os = new ObjectOutputStream(servidor.getOutputStream());
		){
			
			boolean ordenJug=(boolean) is.readObject(); //true implica que va primero, false segundo. También decide cuál es servidor
			System.out.println("¡PARTIDA ENCONTRADA!");

			if(ordenJug){
				System.out.println("Jugador 1");
				try(ServerSocket ss= new ServerSocket()){
					
					os.writeObject(ss.getInetAddress().getHostAddress());
					os.writeObject(ss.getLocalPort());
					os.flush();
					Socket s= ss.accept();
					System.out.println("conectado");
				}catch(IOException e) {e.printStackTrace();}
			}else{
				System.out.println("Jugador 2");
				String hostJug=(String) is.readObject();
				int portJug=(int) is.readObject();
				Socket s= new Socket(hostJug,portJug);
				System.out.println("Conectado");
			}

		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
