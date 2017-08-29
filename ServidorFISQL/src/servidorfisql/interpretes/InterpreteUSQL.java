package servidorfisql.interpretes;

import servidorfisql.Constantes;
import servidorfisql.interpretes.Analizadores.Nodo;

/**
 *
 * @author jorge
 */
public class InterpreteUSQL implements Constantes{
    
    public InterpreteUSQL(){
    }

    
    public String interpretar(Nodo lsent){
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
