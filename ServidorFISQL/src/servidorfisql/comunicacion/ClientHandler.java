package servidorfisql.comunicacion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.InterpretePlyCS;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class ClientHandler extends Thread{
    
    BufferedReader reader;
    Socket sock;
    PrintWriter pwClient;
    
    InterpretePlyCS interpretePlyCS;
    

    public ClientHandler(Socket clientSocket, PrintWriter user) 
    {
        interpretePlyCS = new InterpretePlyCS();
        pwClient = user;
        try 
        {
            sock = clientSocket;
            InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(isReader);
        }
        catch (Exception ex) 
        {
            Consola.writeln("Error inesperado en la comunicacion...");
        }

    }

    @Override
    public void run() 
    {
        String request, response;
        
        try{
            while ((request = reader.readLine()) != null) {
                /*ANALIZAR SOLICITUD DE LENGUAJE DE COMUNICACION PLYCS*/
                Consola.writeln("request original: \n" + request);
                request = request.replace("!@#", "\n");
                Consola.writeln("request transformada: \n" + request);
                
                response = interpretePlyCS.analizar(pwClient, request);
                
                if(response != null){
                    
                    response = response.replace("\n", "!@#").replace("\r", "$%^").replace("\t", "&*(");
                    System.out.println("Respondiendo...: \n" + response);
                    
                    if(!response.equals("LOGOUT"))
                        responderCliente(pwClient, response);
                }else{
                    Consola.writeln("interpretePlyCS retorno null a request " + request);
                }
            
            }

            
          } 
          catch (Exception ex) 
          {
             Consola.writeln("Conexion perdida..." + ex.getLocalizedMessage());
             ex.printStackTrace();
             Server.clientes.removeClient(pwClient);
          } 
     }
    
    private void responderCliente(PrintWriter cliente, String respuesta){
        
        if(Server.clientes.existsClient(cliente)){
            String user = Server.clientes.getUsername(cliente);
            
            try{
                
                cliente.println(respuesta);
                //Consola.writeln("Respondiendo a [" + user + "]" + respuesta + "\n");
                cliente.flush();
                
            }catch(Exception ex){
                Consola.writeln("Error respondiendo a [" + user + "]");
            }
        }else{
            Consola.writeln("Error, el cliente no se encuentra logeado en el sistema...");
        }
    }
}
