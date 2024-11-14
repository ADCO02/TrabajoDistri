package Cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import Partida.Tablero;

public class Jugador {
    public static void main(String[] args) {
		//meter menú de buscar partida / instrucciones etc
    	menu();
		buscaPartida("localhost",12345);
	}
    
    
    private static void menu() {
    	Scanner in=new Scanner(System.in);
    	int seleccion=0;
    	System.out.println("BIENVENIDO");
    	while(seleccion!=4) {
    		muestraOpciones();
    		String respuesta= in.nextLine();
    		try {
    			seleccion=Integer.parseInt(respuesta);
    			switch(seleccion) {
    			case 1:
    				buscaPartida("localhost",12345);
    			case 2:
    				System.out.println("Esta opción aún está en desarrollo");
    			case 3:
    				mostrarInstrucciones();
    			case 4:
    				System.out.println("HASTA LA PRÓXIMA");
    			default:
    				System.out.println("¡Esa opción no existe!");
    			}
    		}catch(NumberFormatException e) {
    			System.out.println("¡Introduce un número!");
    			seleccion=0;
    		}
    		
    	}
    	
    }
	
	private static void buscaPartida(String hostServ, int portServ){
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
					os.writeBytes(ss.getInetAddress().getHostAddress()+"\n");
					os.writeInt(ss.getLocalPort());
					os.flush();
					Socket s= ss.accept();
					jugador1(s);
				}catch(IOException e) {e.printStackTrace();}
			}else{
				System.out.println("Jugador 2");
				String hostJug= is.readLine();
				int portJug=is.readInt();
				Socket s=new Socket(hostJug,portJug);
				jugador2(s);
			}

		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void jugador1(Socket s) {
		try(ObjectInputStream is = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
		){
			Tablero t = new Tablero();
			while(!t.verificarJuegoTerminado()) {
				juegaTurno(t,os);
				t=esperaTurno(is);
			}
			t.mostrarResultados();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void jugador2(Socket s) {
		try(ObjectInputStream is = new ObjectInputStream(s.getInputStream());
				ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
			){
				Tablero t;
				while(!t.verificarJuegoTerminado()) {
					t=esperaTurno(is);
					juegaTurno(t,os);
				}
				t.mostrarResultados();
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
	
	private static void juegaTurno(Tablero t,ObjectOutputStream os) {
		t.iniciarTurno();
		while(!t.turnoTerminado()) {
			t.tiraDados();
			os.writeObject(t);
			if(t.vuelveATirar()) {
				t.bloqueaDados();
			}else {
				t.eligeCasilla();
				t.terminaTurno();
			}
			os.writeObject(t);
		}
	}
	
	private static Tablero esperaTurno(ObjectInputStream is) {
		try {
			Tablero t = (Tablero) is.readObject();
			while(!t.turnoTerminado()) {
				t.mostrarDados();
				t = (Tablero) is.readObject();
				if(!t.turnoTerminado()) {
					t.mostrarBloqueados();
					t = (Tablero) is.readObject();
				}
			}
			return t;
		}catch(ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void muestraOpciones() {
    	System.out.println("¿QUÉ DESEAS HACER?");
    	System.out.println(" 1. Jugar partida pública");
    	System.out.println(" 2. Jugar partida privada");
    	System.out.println(" 3. ¿Cómo se juega?");
    	System.out.println(" 4. SALIR");
    }
    
    private static void mostrarInstrucciones() {
    	
    }

}
