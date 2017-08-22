package servidorfisql.comunicacion;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import servidorfisql.gui.Consola;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class ServerStart extends Thread{
    
    

    @Override
    public void run() 
    { 

        try 
        {
            ServerSocket serverSock = new ServerSocket(2222);
            Consola.write("Servidor escuchando en el puerto 2222");

            while (true) 
            {
                            Socket clientSock = serverSock.accept();
                            PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                            //Server.clientOutputStreams.add(writer);
                            Server.clientes.agregarCliente(writer);

                            Thread listener = new Thread(new ClientHandler(clientSock, writer));
                            listener.start();
                            Consola.write("Se ha conectado el cliente [" + writer.toString() + "]");
            }
        }
        catch (Exception ex)
        {
            Consola.write("Error estableciendo una conexion...");
        }
    }
}
