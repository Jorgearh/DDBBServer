package servidorfisql.interpretes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.PlyCS.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.PlyCS.analizador.ParserPlyCS;
import servidorfisql.interpretes.Analizadores.PlyCS.analizador.TokenMgrError;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class InterpretePlyCS {
    
    ParserPlyCS parserPlyCS;
    //InterpreteUSQL interpreteUSQL;
    
    Grafica grafica;
    
    public InterpretePlyCS(){
        this.grafica = new Grafica();
        //this.interpreteUSQL = new InterpreteUSQL();
    }
    
    public String analizar(PrintWriter pw, String request){
        Nodo astPlyCS;
        String response;
        
        try{
            
            parserPlyCS = new ParserPlyCS(new StringReader(request));
            astPlyCS = parserPlyCS.INI();
            grafica.graficar(astPlyCS, "/home/jorge/Escritorio/ASTs/plycs/request");
            
            response = interpretar(pw, astPlyCS);
            
        }catch(TokenMgrError ex){
            response = "Error lexico en el analisis del paquete PlyCS\n    " + ex.getMessage() + "\n" + request;
            Consola.writeln(response);
            
        }catch(ParseException pe){
            response = "Error sintactico en el analisis del paquete PlyCS\n    " + pe.getMessage() + "\n" + request;
            Consola.writeln(response);
            
        } catch (IOException ex) {
            Consola.writeln(ex.getMessage());
            response = "ERROR DE IO";
            
        }
        
        return response;
    }
    
    private String interpretar(PrintWriter pw, Nodo ast) throws IOException{
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
                usql = ast.getHijo(2).valor.replace("~", "\"");
                response = usql(codigo, usql);
                break;
                
            case "reporte":
                codigo = Integer.parseInt(ast.getHijo(1).valor);
                usql = ast.getHijo(2).valor;
                response = reporte(codigo, usql);
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
                response = Error.logico(codigo, "login", "Password invalida para usuario [" + user + "]");
                
            }
        }else{
            response = Error.logico(codigo, "login", "El usuario [" + user + "] es invalido.");
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
    
    
    
    
    private String usql(int codigo, String cadUsql) throws IOException{
        Nodo astUSQL = Archivos.parsearUSQL(codigo, cadUsql);
        
        if(astUSQL.token.equals("ERROR"))
            return astUSQL.valor;
        else{
            return InterpreteUSQL.interpretarSentenciasUsql(codigo, astUSQL, cadUsql);
        }
    }
    
    
    
    private String reporte(int codigo, String cadUsql) throws IOException{
        Nodo astUSQL = Archivos.parsearUSQL(codigo, cadUsql);
        
        if(astUSQL.token.equals("ERROR"))
            return astUSQL.valor;
        else{
            return InterpreteUSQL.interpretarSentenciasUsql(codigo, astUSQL, cadUsql);
        }
    }
}
