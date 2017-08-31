package servidorfisql.server;

import servidorfisql.server.manejador.Archivos;
import servidorfisql.comunicacion.Clientes;
import servidorfisql.comunicacion.ServerStart;
import servidorfisql.gui.Consola;

/**
 *
 * @author jorge
 */
public class Server {
    
    public static String user = "ddbbserver";
    public static Clientes clientes;
    public static String actualDB = "";
    
    public static void iniciarServidor(){
        
        Consola.writeln("Iniciando servidor...");
        
        clientes = new Clientes();
        
        Thread starter = new Thread(new ServerStart());
        starter.start();
        
        /*Iniciar sistema de archivos*/
        Archivos.inicializarSistemaDeArchivos();
        
        /*Cargar usuarios*/
        Archivos.cargarUsuarios();
        
        /*Cargar BBDD*/
        Archivos.cargarInformacion();
        
        Consola.writeln("Servidor iniciado...\n");
    }
    
    public static void detenerServidor(){
        try 
        {
            Consola.writeln("Deteniendo el servidor...");
            
            Archivos.guardarInformacion();
            Thread.sleep(1500);
            
            Consola.writeln("Servidor detenido...\n");
        } 
        catch(InterruptedException ex) 
        {
            Thread.currentThread().interrupt();
            Consola.writeln("Error deteniendo el servidor!!!");
        }
    }
    
    public static void reiniciarServidor(){
        detenerServidor();
        iniciarServidor();
    }
    
    
    
    
    
}
