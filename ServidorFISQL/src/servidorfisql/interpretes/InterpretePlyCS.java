package servidorfisql.interpretes;

import java.io.IOException;
import java.io.StringReader;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Grafica;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.interpretes.Analizadores.PlyCS.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.PlyCS.analizador.ParserPlyCS;
import static servidorfisql.Constantes.FILE_AST_PLYCS;

/**
 *
 * @author jorge
 */
public class InterpretePlyCS {
    
    ParserPlyCS parserPlyCS;
    Grafica grafica;
    
    public InterpretePlyCS(){
        grafica = new Grafica();
    }
    
    public String analizar(String request){
        Nodo astPlyCS;
        String response;
        
        try{
            
            parserPlyCS = new ParserPlyCS(new StringReader(request));
            astPlyCS = parserPlyCS.INI();
            grafica.graficar(astPlyCS, FILE_AST_PLYCS);
            
            response = interpretar(astPlyCS);
            
        }catch(ParseException pe){
            Consola.write(pe.getMessage());
            response = "ERROR EN EL PARSEO";
        } catch (IOException ex) {
            Consola.write(ex.getMessage());
            response = "ERROR DE IO";
        }
        
        return response;
    }
    
    private String interpretar(Nodo ast){
        String response, paquete, usql;
        int codigo;
        
        
        paquete = ast.getHijo(0).valor;
        
        switch(paquete){
            case "login":
                String user, password;
                
                codigo = Integer.parseInt(ast.getHijo(1).valor);
                user = ast.getHijo(2).getHijo(0).valor;
                password = ast.getHijo(2).getHijo(1).valor;
                response = login(user, password);
                
                break;
            case "logout":
                logout();
                response = "LOGOUT";
                
                break;
            case "reporte":
                codigo = Integer.parseInt(ast.getHijo(1).valor);
                usql = ast.getHijo(2).valor;
                response = reporte(usql);
                
                break;
            case "usql":
                codigo = Integer.parseInt(ast.getHijo(1).valor);
                usql = ast.getHijo(2).valor;
                response = usql(usql);
                
                break;
            default:
                Consola.write("Request invalida. Paquete recibido [" + paquete + "]");
                response = "[\"paquete\": \"error\", \"descripcion\": \"request invalida\"]";
                break;
        }
        
        
        return response;
    }
    
    private String login(String user, String password){
        return "[\"login\":\"true\"]";
    }
    
    private String logout(){
        return null;
    }
    
    private String reporte(String usql){
        return null;
    }
    
    private String usql(String usql){
        return null;
    }
    
}
