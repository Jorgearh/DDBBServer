package servidorfisql.comunicacion;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Clientes {
    private final HashMap<PrintWriter, Cliente> clientes;
    
    public Clientes(){
        this.clientes = new HashMap<>();
    }
    
    /***
     * Carga un nuevo cliente a memoria, solo identificado por un objeto PrintWriter
     * @param pw 
     */
    public void agregarCliente(PrintWriter pw){
        this.clientes.put(pw, new Cliente(pw));
    }
    
    /***
     * 
     * @param pw 
     */
    public void removeClient(PrintWriter pw){
        if(this.clientes.containsKey(pw))
            this.clientes.remove(pw);
    }
    
    public void setClient(PrintWriter pw, String username){
        this.clientes.get(pw).login(username);
    }
    
    public String getUsername(PrintWriter pw){
        if(this.clientes.containsKey(pw))
            return this.clientes.get(pw).username;
        else
            return null;
    }
    
    public boolean existsClient(PrintWriter pw){
        return this.clientes.containsKey(pw);
    }
}


class Cliente{
    PrintWriter pw;
    String username;
    boolean loggedin;
    
    public Cliente(PrintWriter pw){
        this.username = "";
        this.loggedin = false;
    }
    
    public void login(String username){
        this.username = username;
        this.loggedin = true;
    }
}