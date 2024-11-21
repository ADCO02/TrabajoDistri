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
    //hola prueba
    
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
    				break;
    			case 2:
    				System.out.println("Esta opción aún está en desarrollo\n");
    				break;
    			case 3:
    				mostrarInstrucciones();
    				break;
    			case 4:
    				System.out.println("HASTA LA PRÓXIMA");
    				break;
    			default:
    				System.out.println("¡Esa opción no existe!\n");
    				break;
    			}
    		}catch(NumberFormatException e) {
    			System.out.println("¡Introduce un número!");
    			seleccion=0;
    		}
    	}
    	in.close();
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
					System.out.println("Puerto: "+ss.getLocalPort());
					os.writeBytes(ss.getInetAddress().getHostAddress()+"\n");
					os.writeInt(ss.getLocalPort());
					os.flush();
					System.out.println("Esperando la conexión en jugador1...");
					Socket s= ss.accept();
					System.out.println("conexion aceptada");

					jugador1(s);
    				//System.out.println("Partida pública se ejecuta aquí");
				}catch(IOException e) {e.printStackTrace();}
			}else{
				System.out.println("Jugador 2");
				String hostJug= is.readLine();
				System.out.println("Host: "+hostJug);
				int portJug=is.readInt();
				System.out.println("Puerto: "+portJug);
				Socket s=new Socket(hostJug,portJug);
				jugador2(s);
				//System.out.println("Partida pública se ejecuta aquí");
			}

		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void jugador1(Socket s) {
	    System.out.println("entra en jugador1: ");
	    try (ObjectInputStream is = new ObjectInputStream(s.getInputStream());
	         ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream())) {

	        System.out.println("Flujo de entrada y salida creados con éxito");

	        Tablero t = new Tablero(2);
	        while (!t.verificarJuegoTerminado()) {
	            System.out.println("Jugando turno...");
	            
	            juegaTurno(t, os);
	            t = esperaTurno(is);
	        }
	        t.mostrarResultados();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	
	private static void jugador2(Socket s) {
		try(ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream is = new ObjectInputStream(s.getInputStream());
			){
				Tablero t=null;
				while(t==null || !t.verificarJuegoTerminado()) {
					t=esperaTurno(is);
					juegaTurno(t,os);
				}
				t.mostrarResultados();
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
	
	private static void juegaTurno(Tablero t,ObjectOutputStream os) throws IOException {
		t.iniciarTurno();
		System.out.println("entra en juega turno: ");

		while(!t.turnoTerminado()) {
			System.out.println("tira dados? ");
			t.mostrarTablero();
            t.tiraDados();
            t.mostrarDados();
            
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
    	System.out.println("\n-YAZY-");
    	System.out.println("Yazy es un juego de dados divertido y estratégico para 2 o más jugadores. "
    			+ "El objetivo del juego es obtener la mayor cantidad de puntos posible combinando los "
    			+ "resultados de los dados en diferentes categorías.\n");
    	System.out.println("-CÓMO SE JUEGA-");
    	System.out.println("El juego muestra el tablero de puntuación en pantalla, donde cada jugador "
    			+ "tiene su propia hoja de puntuación. También se cuenta con 5 dados.");
    	System.out.println("Cada jugador tiene tres tiradas por turno. Al inicio, lanza los 5 dados. "
    			+ "Después de cada tirada, puedes seleccionar los dados que deseas mantener. Los dados "
    			+ "no seleccionados se pueden volver a lanzar hasta un máximo de tres veces por turno.");
    	System.out.println("Después de la tercera tirada (o cuando decidas no lanzar más), debes escoger "
    			+ "una categoría de puntuación.\n");
    	System.out.println("-CATEGORÍAS DE PUNTUACIÓN-");
    	System.out.println("Unos, Dos, Tres, Cuatro, Cinco, Seis: Suma los valores de los dados que coincidan "
    			+ "con el número seleccionado.");
    	System.out.println("Trío o cuarteto: Tres o cuatro dados del mismo número. Suma el valor de todos los dados.");
    	System.out.println("Full: Tres dados del mismo número y dos dados de otro número. Suma 25 puntos.");
    	System.out.println("Escalera: cuando los 5 dados tienen números consecutivos diferentes (1-2-3-4-5 ó "
    			+ "2-3-4-5-6). Suma 40 puntos");
    	System.out.println("Yazy: Todos los dados deben ser del mismo número. Suma 50 puntos.");
    	System.out.println("\n");
    }

}