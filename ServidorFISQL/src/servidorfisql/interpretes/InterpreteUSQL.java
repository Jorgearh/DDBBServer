package servidorfisql.interpretes;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidorfisql.Constantes;
import servidorfisql.interpretes.Analizadores.Grafica;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.interpretes.Analizadores.USQL.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.USQL.analizador.ParserUSQL;

/**
 *
 * @author jorge
 */
public class InterpreteUSQL implements Constantes{
    
    Grafica grafica;
    
    public InterpreteUSQL(){
        this.grafica = new Grafica();
    }
    
    public String analizar(String instruccion){
        ParserUSQL parserUSQL;
        Nodo astUSQL;
        String response = "Instruccion procesada exitosamente";
        
        try{
            
            parserUSQL = new ParserUSQL(new StringReader(instruccion));
            astUSQL = parserUSQL.INI();
            grafica.graficar(astUSQL, FILE_AST_USQL);
            
            
        } catch (ParseException ex) {
            Logger.getLogger(InterpreteUSQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InterpreteUSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;
    }
    
    private String interpretar(Nodo lsent){
        String response = null;
        
        for(Nodo sent : lsent.hijos){
            String idSent = sent.token;
            
            switch(idSent){
                
            }
        }
        
        
        return response;
    }
    
    private void interpretarDDL(Nodo sent){
        
    }
    
    
}
