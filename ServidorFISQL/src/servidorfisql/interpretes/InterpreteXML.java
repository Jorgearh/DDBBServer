package servidorfisql.interpretes;

import servidorfisql.Constantes;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Grafica;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParserXML;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class InterpreteXML implements Constantes{
    
    ParserXML parserXML;
    Grafica grafica;
    
    public InterpreteXML(){
        grafica = new Grafica();
    }
    
    public void analizar(String xml){
        Nodo astXML;
        
        try{
            parserXML = new ParserXML(new StringReader(xml));
            astXML = parserXML.INI();
            grafica.graficar(astXML, FILE_AST_XML);
            
            interpretar(astXML);
            
        } catch (ParseException ex) {
            Consola.write(ex.getLocalizedMessage());
            Consola.write("ERROR EN EL PARSEO DE ARCHIVO XML");
        } catch (IOException ex) {
            Consola.write(ex.getLocalizedMessage());
        }
    }
    
    
    private void interpretar(Nodo ast){
        String fileName;
        
        fileName = ast.token;
        
        switch(fileName){
            case "UsersFile":
                cargarUsuarios(ast);
                break;
            case "MasterFile":
                break;
            case "DataBaseFile":
                break;
            default:
                Consola.write("Archivo no reconocido: " + fileName);
                break;
        }
    }
    
    
    private void cargarUsuarios(Nodo ast){
        
        for (Nodo user : ast.hijos) {
            String username = user.getHijo(0).getHijo(0).valor;
            String password = user.getHijo(1).getHijo(0).valor;
            
            Archivos.usuarios.agregarUsuario(username, password);
        }
    }
    
}
