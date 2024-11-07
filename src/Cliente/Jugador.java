package Cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Jugador {
    public static void main(String[] args) {
		//meter menú de buscar partida / instrucciones etc
    	menu();
		buscaPartida("localhost",12345);
	}
    
    private static void menu() {
    	int seleccion=0;
    	System.out.println("BIENVENIDO");
    	while(seleccion!=6) {
    		
    	}
    	System.out.println("HASTA LA PRÓXIMA");
    }
	
	private static Socket buscaPartida(String hostServ, int portServ){
		System.out.println("Buscando partida...");
		
		try(Socket servidor = new Socket(hostServ, portServ);
			ObjectInputStream is = new ObjectInputStream(servidor.getInputStream());
		){
			
			boolean ordenJug=(boolean) is.readObject(); //true implica que va primero, false segundo. También decide cuál es servidor
			System.out.println("¡PARTIDA ENCONTRADA!");

			if(ordenJug){
				System.out.println("Jugador 1");
				try(ServerSocket ss= new ServerSocket(0);
					ObjectOutputStream os = new ObjectOutputStream(servidor.getOutputStream());
				){
					System.out.println(ss.getLocalPort());
					os.writeInt(ss.getLocalPort());
					os.flush();
					Socket s= ss.accept();
					jugador1(s);
				}catch(IOException e) {e.printStackTrace();}
			}else{
				System.out.println("Jugador 2");
				//String hostJug=(String) is.readObject();
				int portJug=is.readInt();
				Socket s=new Socket("localhost",portJug);
				jugador2(s);
			}

		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void jugador1(Socket s) {
		
	}
	
	private static void jugador2(Socket s) {
		
	}

}
