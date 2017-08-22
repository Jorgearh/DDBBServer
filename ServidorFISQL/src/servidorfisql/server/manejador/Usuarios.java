package servidorfisql.server.manejador;

import java.util.HashMap;
import java.util.Map;
import servidorfisql.gui.Consola;

/**
 *
 * @author jorge
 */
public class Usuarios {
    private HashMap<String, Usuario> usuarios;
    
    public Usuarios(){
        this.usuarios = new HashMap<>();
    }
    
    public void agregarUsuario(String user, String pass){
        if(!this.usuarios.containsKey(user))
            this.usuarios.put(user, new Usuario(user, pass));
        else
            Consola.write("El usuario [" + user + "] ya existe.");
    }
    
    
    public void imprimirUsuarios(){
        Consola.write("Usuarios en memoria:");
        for (Map.Entry<String, Usuario> user : usuarios.entrySet()) {
            Usuario usuario = user.getValue();
            
            Consola.write("    usuario: " + usuario.getUsername() + " password: " + usuario.getPassword());
        }
    }
}


class Usuario{
    private final String username;
    private final String password;
    
    public Usuario(String user, String pass){
        this.username = user;
        this.password = pass;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    
    
}