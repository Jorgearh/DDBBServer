package servidorfisql.interpretes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Grafica;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.interpretes.Analizadores.PlyCS.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.PlyCS.analizador.ParserPlyCS;
import static servidorfisql.Constantes.FILE_AST_PLYCS;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class InterpretePlyCS {
    
    ParserPlyCS parserPlyCS;
    InterpreteUSQL interpreteUSQL;
    
    Grafica grafica;
    
    public InterpretePlyCS(){
        this.grafica = new Grafica();
        this.interpreteUSQL = new InterpreteUSQL();
    }
    
    public String analizar(PrintWriter pw, String request){
        Nodo astPlyCS;
        String response;
        
        try{
            
            parserPlyCS = new ParserPlyCS(new StringReader(request));
            astPlyCS = parserPlyCS.INI();
            grafica.graficar(astPlyCS, FILE_AST_PLYCS);
            
            response = interpretar(pw, astPlyCS);
            
        }catch(ParseException pe){
            Consola.writeln(pe.getMessage());
            response = "ERROR EN EL PARSEO";
        } catch (IOException ex) {
            Consola.writeln(ex.getMessage());
            response = "ERROR DE IO";
        }
        
        return response;
    }
    
    private String interpretar(PrintWriter pw, Nodo ast){
        String response, paquete, usql;
        int codigo;
        
        
        paquete = ast.getHijo(0).valor;
        
        switch(paquete){
            case "login":
                String user, password;
                
                codigo = Integer.parseInt(ast.getHijo(1).valor);
                user = ast.getHijo(2).getHijo(0).valor;
                password = ast.getHijo(2).getHijo(1).valor;
                
                response = login(codigo, user, password, pw);
                break;
                
            case "logout":
                logout(pw);
                response = "LOGOUT";
                break;
                
            case "usql":
                codigo = Integer.parseInt(ast.getHijo(1).valor);
                usql = ast.getHijo(2).valor;
                response = usql(codigo, usql);
                break;
                
            case "reporte":
                codigo = Integer.parseInt(ast.getHijo(1).valor);
                usql = ast.getHijo(2).valor;
                response = reporte(usql);
                break;
                
            default:
                Consola.writeln("Request invalida. Paquete recibido [" + paquete + "]");
                response = "[\"paquete\": \"error\", \"descripcion\": \"request invalida\"]";
                break;
        }
        
        
        return response;
    }
    
    /***
     * Carga a memoria la informacion asociada al usuario si las credenciales son correctas
     * @param codigo
     * @param user
     * @param password
     * @param pw
     * @return Respuesta del servidor de login exitoso o error detallado.
     */
    private String login(int codigo, String user, String password, PrintWriter pw){
        String response;
        
        if(Archivos.usuarios.existsUser(user)){
            
            if(Archivos.usuarios.matchesPassword(user, password)){
                
                Server.clientes.setClient(pw, user);
                Server.user = user;
                
                response = "[\n" +
                            "	\"paquete\": \"login\",\n" +
                            "	\"validar\": " + codigo + ",\n" +
                            "	\"datos\": [\n" +
                            "		\"user\": \"" + user + "\",\n" +
                            "		\"login\": true\n" +
                            "	]\n" +
                            "]";
                
            }else{
                
                response = Error.logico(codigo, "login", "El usuario [" + user + "] invalido.");
            }
        }else{
            response = Error.logico(codigo, "login", "Password invalida para usuario [" + user + "]");
        }
        
        
        return response;
    }
    
    
    
    /***
     * Guarda en memoria secundaria la informacion en memoria y elimina la conexion del cliente
     * @param pw 
     */
    private void logout(PrintWriter pw){
                Consola.writeln("Cerrada sesion de usuario [" + Server.clientes.getUsername(pw) + "]. "
                    + "Cliente [" + pw.toString() + "] desconectado...");
        Server.clientes.removeClient(pw);
        Server.user = "ddbbuser";
    }
    
    
    
    
    private String usql(int codigo, String cadUsql){
        //return interpreteUSQL.analizar(usql);
        Nodo astUSQL = Archivos.parsearUSQL(cadUsql);
        return interpreteUSQL.interpretar(codigo, astUSQL, cadUsql);
    }
    
    
    
    private String reporte(String usql){
        return null;
    }
}
