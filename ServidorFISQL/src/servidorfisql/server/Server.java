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
    
    public static void iniciarServidor(){
        
        clientes = new Clientes();
        
        Archivos.inicializarSistemaDeArchivos();
        Consola.write("Sistema de archivos inicializado exitosamente...");
        
        Thread starter = new Thread(new ServerStart());
        starter.start();
        
        /*Cargar usuarios*/
        Archivos.cargarUsuarios();
        Consola.write("Usuarios cargados exitosamente...");
        
        /*Cargar BBDD*/
        Archivos.cargarInformacion();
        
        Consola.write("Servidor iniciado...");
    }
    
    public static void detenerServidor(){
        try 
        {
            Archivos.guardarInformacion();
            Thread.sleep(5000);                 //5000 milliseconds is five second.
        } 
        catch(InterruptedException ex) 
        {
            Thread.currentThread().interrupt();
        }
        
        Consola.write("Servidor detenido...");
    }
    
    public static void reiniciarServidor(){
        Consola.write("Servidor reiniciando...");
        detenerServidor();
        iniciarServidor();
    }
    
    
    
    
    
}
