package Cliente;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import Partida.Tablero;

public class Jugador {

    public static void main(String[] args) {
    	menu();
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
				System.out.println("Has elegido la opción: "+seleccion);
    			switch(seleccion) {
    			case 1:
    				buscaPartida("localhost",12345);
    				break;
    			case 2:
    				partidaPrivada("localhost",12345,in);
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
    			System.out.println("¡Introduce un número!\n");
    			seleccion=0;
    		}
    	}
    	//in.close();
    }
    
    private static void buscaPartida(String hostServ, int portServ){
		introduceContra(hostServ, portServ, "");
	}
    
    private static void partidaPrivada(String hostServ, int portServ, Scanner in) {
    	System.out.println("Introduce una contraseña para la partida");
    	String contras = "";
		while (contras.equals("")) {
			contras=in.nextLine();
			if(contras.equals("")){
				System.out.println("Introduzca una contraseña!");
			}
		}
		introduceContra(hostServ, portServ, contras);
    }

	private static void introduceContra(String hostServ, int portServ, String contra){
		System.out.println("Buscando partida...");
		try(Socket servidor = new Socket(hostServ, portServ)){
			DataOutputStream dos = new DataOutputStream(servidor.getOutputStream());
			dos.writeBytes(contra + "\n");
			dos.flush();
			
			
			ObjectOutputStream os = new ObjectOutputStream(servidor.getOutputStream());
			ObjectInputStream is = new ObjectInputStream(servidor.getInputStream());
			
			boolean ordenJug=is.readBoolean(); //true implica que va primero, false segundo. También decide cuál es servidor
			System.out.println("¡PARTIDA ENCONTRADA!");

			if(ordenJug){
				System.out.println("Jugador 1");
				try(ServerSocket ss= new ServerSocket(0);){
					os.writeBytes(ss.getInetAddress().getHostAddress()+"\n");
					os.writeInt(ss.getLocalPort());
					os.flush();
					servidor.close();
					try(Socket s= ss.accept()){
						jugador1(s);
					}catch(IOException e) {e.printStackTrace();}
				}catch(IOException e) {e.printStackTrace();}
			}else{
				System.out.println("Jugador 2");
				String hostJug= is.readLine();
				int portJug=is.readInt();
				servidor.close();
				try(Socket s=new Socket(hostJug,portJug)){
					jugador2(s);
				}catch(IOException e) {e.printStackTrace();}
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static void jugador1(Socket s) {
		try (ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
			 ObjectInputStream is = new ObjectInputStream(s.getInputStream())) {
			
			Tablero t = new Tablero(); 
	
			while (!t.verificarJuegoTerminado()) {
				juegaTurno(t, os);  
				t = esperaTurno(is);  
			}
			t.mostrarResultados();
		} catch (IOException e) {
			System.out.println("Error de IO: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	private static void jugador2(Socket s) {
		try (ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
			 ObjectInputStream is = new ObjectInputStream(s.getInputStream())) {
			
			Tablero t = null; 

			while (t == null || !t.verificarJuegoTerminado()) {
				t = esperaTurno(is); 
				juegaTurno(t, os); 
			}
			t.mostrarResultados();
		} catch (IOException e) {
			System.out.println("Error de IO: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void juegaTurno(Tablero t, ObjectOutputStream os) throws IOException {
	
			t.iniciarTurno(); 
			System.out.println("\nTU TURNO\n");
	
			while (!t.turnoTerminado()) {
				t.tiraDados();									//tira los dados
				os.reset();
				os.writeObject(t);								//le pasa el tablero al contrincante para que vea qué dados le han salido
				if (t.intentosLibres()){
					t.mostrarTablero();  						//muestra el tablero
					t.mostrarDados();
					if(t.vuelveATirar()){
						t.bloqueaDados();							//elige los dados a bloquear para la siguiente tirada
						os.reset();
						os.writeObject(t);							//le pasa el tablero al contrincante para que vea qué dados ha bloqueado
					}else{
						t.eligeCasilla();							//elige la casilla de puntuación
						t.terminaTurno();
					}
				} else {
					t.eligeCasilla();							//elige la casilla de puntuación
					t.terminaTurno();
				}
			}
	
			os.reset();
			os.writeObject(t);
			os.flush();  
	}
	
	private static  Tablero esperaTurno(ObjectInputStream is) {
		System.out.println("\nTURNO DE TU OPONENTE\n");
		try {
			Tablero t = null;
	
			while (t==null||!t.turnoTerminado()) {
				t=(Tablero) is.readObject();
				System.out.println("Le han salido los siguientes dados:");
				t.mostrarDados();
				t = (Tablero) is.readObject(); 
				if (!t.turnoTerminado()) {
					System.out.println("El oponente ha bloqueado los siguientes dados:");
					t.mostrarBloqueados();  
				}
			}
			System.out.println("El turno del oponente ha terminado.");
			return t;
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Error al recibir el tablero: " + e.getMessage());
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