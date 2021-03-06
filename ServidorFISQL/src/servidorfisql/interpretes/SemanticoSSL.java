package servidorfisql.interpretes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class SemanticoSSL {
    
    
    private static final HashMap<String, Variable> TS = new HashMap<>();
    private static int codigo;
    private static String cadUsql;
    private static String idMetodo;
    private static String tipoMetodo;
    
    public static final ArrayList<String> erroresSSL = new ArrayList<>();
    
    private static Stack<Boolean> display;
    
    public static void analizarMetodo(Nodo metodo, int cod, String cad, String tipoMet){
        codigo = cod;
        cadUsql = cad;
        erroresSSL.clear();
        display = new Stack<>();
        
        idMetodo = metodo.getHijo(0).valor;
        tipoMetodo = tipoMet;
        Nodo lpar = metodo.getHijo(1);
        Nodo lsent = metodo.getHijo(2);
        
        llenarTS(lpar, lsent);
        
        if(!erroresSSL.isEmpty())
            return;
        
        for(Nodo sent : lsent.hijos){
            analisisSemantico(sent);
        }

        idMetodo = null;
    }
    
    public static String analizarSentenciaSSL(Nodo sent, int cod, String cad){
        codigo = cod;
        cadUsql = cad;
        erroresSSL.clear();
        display = new Stack<>();
        
        idMetodo = "GLOBAL";
        
        analisisSemantico(sent);
        
        idMetodo = null;
        
        if(!erroresSSL.isEmpty())
            return cadenaErrores();
        else
            return null;
    }
    
    private static void llenarTS(Nodo lpar, Nodo lsent){
        
        //Parametros
        for(Nodo atr : lpar.hijos){
            evaluarSentencias(atr);
        }
        
        for(Nodo sent : lsent.hijos){
            evaluarSentencias(sent);
        }
        
    }
    
    private static void evaluarSentencias(Nodo sent){
        String response;
        Nodo lsent;

        switch (sent.token) {
            case "ARG":
                {
                    String tipo = sent.getHijo(0).valor;
                    Nodo id = sent.getHijo(1);
                    Variable var = new Variable(tipo, id.valor);
                    if(TS.containsKey(id.valor)){
                        response = Error.lenguaje(
                                codigo,
                                "USQL",
                                cadUsql,
                                "Semantico",
                                id.row,
                                id.col,
                                "Multiple declaracion del atributo [ " + id.valor + " ]");
                        erroresSSL.add(response);
                    }else{
                        TS.put(id.valor, var);
                    }       
                    break;
                }
            case "DEC":
                {
                    Nodo lvar = sent.getHijo(0);
                    Nodo tipo = sent.getHijo(1);
                    for(Nodo id : lvar.hijos){
                        
                        Variable var = new Variable(EjecucionSSL.ejecutar.getTipo(tipo.valor), id.valor);
                        if(TS.containsKey(id.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    id.row,
                                    id.col,
                                    "Multiple declaracion de la variable [ " + id.valor + " ]");
                            erroresSSL.add(response);
                        }else
                            TS.put(id.valor, var);
                    }   
                    break;
                }
                
            case "INST":
                Nodo lvar = sent.getHijo(0);
                Nodo tipo = sent.getHijo(1);
                
                for(Nodo obj : lvar.hijos){
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, tipo.valor)){
                        response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    tipo.row,
                                    tipo.col,
                                    "No se puede instanciar la variable ["+ obj.valor +"]"
                                            + " porque no existe el objeto [" + tipo.valor + "]"
                                            + " en a Base de Datos");
                            erroresSSL.add(response);
                    }else{
                        Variable var = new Variable(tipo.valor, obj.valor);
                        if(TS.containsKey(obj.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    obj.row,
                                    obj.col,
                                    "Multiple declaracion de la variable [ " + obj.valor + " ]");
                            erroresSSL.add(response);
                        }else
                            TS.put(obj.valor, var);
                    }
                }
                
                break;
            case "IF":
                Nodo lsentv = sent.getHijo(1);
                Nodo lsentf = sent.hijos.size() == 3 ? sent.getHijo(2) : null;
                for(Nodo s : lsentv.hijos)
                    evaluarSentencias(s);
                if(lsentf != null)
                    for(Nodo s : lsentf.hijos)
                        evaluarSentencias(s);
                break;
            case "SWITCH":
                
                Nodo lcase = sent.getHijo(1);
                for(Nodo caso : lcase.hijos){
                    
                    if(caso.token.equals("CASE")){
                        lsent = caso.getHijo(1);
                        for(Nodo s : lsent.hijos)
                            evaluarSentencias(s);
                    }else{
                        lsent = caso.getHijo(0);
                        for(Nodo s : lsent.hijos)
                            evaluarSentencias(s);
                    }
                }   
                break;
                
            case "FOR":
                Nodo dec = sent.getHijo(0);
                lsent = sent.getHijo(3);
                
                if(dec.getHijo(1).valor.equals("integer")){
                    evaluarSentencias(dec);
                    for(Nodo s : lsent.hijos)
                        evaluarSentencias(s);
                }else{
                    response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    dec.getHijo(1).row,
                                    dec.getHijo(1).col,
                                    "La variable de control debe ser de tipo INTEGER");
                    erroresSSL.add(response);
                }
                
                break;
                
            case "WHILE":
                lsent = sent.getHijo(1);
                for(Nodo s : lsent.hijos)
                    evaluarSentencias(s);
                break;
                
            
                
                
                
            case "CREATE_TABLE":
            case "CREATE_OBJECT":
            case "PROC":
            case "FUNC":
                response = SemanticoUSQL.analizar(sent, codigo, cadUsql);
                if(response != null)
                    erroresSSL.add(response);
                else
                    EjecucionUSQL.ejecutar(sent);
                break;
                
            case "INSERT":
            case "SELECT":
            case "UPDATE":
            case "DELETE_FROM_TABLE":
                
                response = SemanticoUSQL.analizar(sent, codigo, cadUsql);
                if(response != null)
                    erroresSSL.add(response);
                
                break;
                
            case "CREATE_USER":
            case "CREATE_DB":
            case "USE":
            case "ALTER_TABLE_ADD":
            case "ALTER_TABLE_QUIT":
            case "ALTER_OBJECT_ADD":
            case "ALTER_OBJECT_QUIT":
            case "ALTER_USER":
            case "DELETE":
                
                response = Error.lenguaje(
                        codigo, 
                        "USQL", 
                        cadUsql, 
                        "Semantico", 
                        sent.row, 
                        sent.col, 
                        "Sentencia [" + sent.token + "] invalida "
                                + "en el ambito [" + idMetodo + "]");
                erroresSSL.add(response);
                break;
                
                
            default:
                break;
        }
    }
    
    private static void analisisSemantico(Nodo sent){
        String tipo, tipoResult;
        Nodo cond, lsent;
        String response;
        
        
        switch(sent.token){
            case "DEC":
                if(sent.hijos.size() == 3){
                    tipo = EjecucionSSL.ejecutar.getTipo(sent.getHijo(1).valor);
                    tipoResult = evaluarExpresion(sent.getHijo(2));
                    
                    if(!casteoAsignacion(tipo, tipoResult)){
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.row, 
                                            sent.col, 
                                            "Tipos incompatibles");
                        erroresSSL.add(response);
                    }
                }
                break;
            case "=":
                tipoResult = evaluarExpresion(sent.getHijo(1));
                
                //Es una variable normal
                if(sent.getHijo(0).token.equals("VAR")){
                    String idVar = sent.getHijo(0).valor;
                    if(!TS.containsKey(idVar)){
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.getHijo(0).row, 
                                            sent.getHijo(0).col, 
                                            "No existe la variable [" + sent.getHijo(0).valor + "]");
                        erroresSSL.add(response);
                        break;
                    }else{
                        tipo = TS.get(idVar).tipo;
                    }
                }else{//Es una instancia de un objeto
                    
                    String idObjeto = sent.getHijo(0).getHijo(0).valor;
                    
                    //Existe el ebjeto
                    if(TS.containsKey(idObjeto)){
                        String tipoObjeto = TS.get(idObjeto).tipo;
                        String idAtr = sent.getHijo(0).getHijo(1).valor;
                        
                        //Existe el atributo referenciado
                        if(Archivos.bbdd.existeAtributo(Server.actualDB, tipoObjeto, idAtr)){
                            tipo = Archivos.bbdd.getTipoAtributo(Server.actualDB, tipoObjeto, idAtr);
                        }else{
                            response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.getHijo(0).getHijo(1).row, 
                                            sent.getHijo(0).getHijo(1).col, 
                                            "No existe el atributo [" + idAtr + "]");
                            erroresSSL.add(response);
                            break;
                        }
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.getHijo(0).row, 
                                            sent.getHijo(0).col, 
                                            "No existe la variable [" + sent.getHijo(0).valor + "]");
                        erroresSSL.add(response);
                        break;
                    }
                }
                
                if(!casteoAsignacion(tipo, tipoResult)){
                    response = Error.lenguaje(
                                        codigo, 
                                        "USQL", 
                                        cadUsql, 
                                        "Semantico", 
                                        sent.row, 
                                        sent.col, 
                                        "Tipos incompatibles");
                    erroresSSL.add(response);
                }
                
                break;
            case "CALL":
                Nodo idFunc = sent.getHijo(0);
                Nodo larg = sent.hijos.size() == 2 ? sent.getHijo(1) : null;
                
                //Existe el metodo
                if(Archivos.bbdd.existeMetodo(Server.actualDB, idFunc.valor)){
                    //Es una funcion
                    int cantPar = Archivos.bbdd.getCantParametros(Server.actualDB, idFunc.valor);
                     
                    //Se envian parametros
                    if(larg != null){
                        
                        //Se envia y recibe la misma cantidad de parametros
                        if(cantPar == larg.hijos.size()){

                            for(int i = 0; i < cantPar; i++){
                                String tipoParam = Archivos.bbdd.getTipoParam(Server.actualDB, idFunc.valor, i);
                                String tipoArg = evaluarExpresion(larg.getHijo(i));
                                
                                if(!tipoParam.equals(tipoArg)){
                                    response = Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    larg.row, 
                                    larg.col, 
                                    "Tipos incompatibles en parametros");
                                erroresSSL.add(response);
                                }
                            }

                        }else{
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                larg.row, 
                                larg.col, 
                                "No coincidela cantidad de parametros.");
                            erroresSSL.add(response);
                        }
                    }else{  //No se envian parametros
                        if(cantPar > 0){//Se deben recibir parametros
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                larg.row, 
                                larg.col, 
                                "No coincidela cantidad de parametros.");
                            erroresSSL.add(response);
                        }
                    }
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            idFunc.row, 
                            idFunc.col, 
                            "No existe la funcion [" + idFunc.valor + "] en la Base de Datos [" + Server.actualDB + "]");
                    erroresSSL.add(response);
                    tipoResult = "NULO";
                }
                break;
            case "RETURN":
                //String tipoMet = Archivos.bbdd.getTipoMetodo(Server.actualDB, idMetodo);
                if(!tipoMetodo.equals("VOID")){
                    
                    tipoResult = evaluarExpresion(sent.getHijo(0));
                    if(!tipoMetodo.equals(tipoResult)){
                        response = Error.lenguaje(
                                codigo,
                                "USQL",
                                cadUsql,
                                "Semantico",
                                sent.row,
                                sent.col,
                                "Tipos de funcion [" + tipoMetodo +"] y de retorno [" + tipoResult + "] incompatibles");
                        erroresSSL.add(response);
                    }else{
                        
                    }                
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Sentencia return invalida en un Procedimiento.");
                    erroresSSL.add(response);
                }
                
                break;

            case "IF":
                cond = sent.getHijo(0);
                lsent = sent.getHijo(1);
                
                if(evaluarExpresion(cond).equals("bool")){
                    for(Nodo s : lsent.hijos){
                        analisisSemantico(s);
                    }
                    
                    if(sent.hijos.size() == 3){
                        lsent = sent.getHijo(2);
                        for(Nodo s : lsent.hijos){
                            analisisSemantico(s);
                        }
                    }
                    
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Se requiere una expresion condicional en la sentencia IF");
                    erroresSSL.add(response);
                }
                break;
                
            case "SWITCH":
                Nodo exp = sent.getHijo(0);
                Nodo lcase = sent.getHijo(1);
                
                tipo = evaluarExpresion(exp);
                
                for(Nodo caso : lcase.hijos){
                        display.push(true);
                        if(caso.token.equals("CASE")){
                            
                            String tipoCaso = evaluarExpresion(caso.getHijo(0));
                            if(tipoCaso.equals(tipo)){
                                lsent = caso.getHijo(1);
                                for(Nodo s : lsent.hijos){
                                    analisisSemantico(s);
                                }
                            }else{
                                response = Error.lenguaje(
                                        codigo, 
                                        "USSQL", 
                                        cadUsql, 
                                        "Semantico", 
                                        sent.row, 
                                        sent.col, 
                                        "Tipos incompatibles de expresion y caso de evaluacion.");
                                erroresSSL.add(response);
                            }
                        }else{
                            lsent = caso.getHijo(0);
                            for(Nodo s : lsent.hijos){
                                analisisSemantico(s);
                            }
                        }
                        
                        display.pop();
                    }
                break;
            
            case "FOR":
                cond = sent.getHijo(1);
                lsent = sent.getHijo(3);
                
                if(evaluarExpresion(cond).equals("bool")){
                    for(Nodo s : lsent.hijos){
                        analisisSemantico(s);
                    }                    
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Se requiere una expresion condicional en la sentencia FOR");
                    erroresSSL.add(response);
                }
                
                break;
            case "WHILE":
                cond = sent.getHijo(0);
                lsent = sent.getHijo(1);
                
                if(evaluarExpresion(cond).equals("bool")){
                    for(Nodo s : lsent.hijos){
                        analisisSemantico(s);
                    }                    
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Se requiere una expresion condicional en la sentencia WHILE");
                    erroresSSL.add(response);
                }
                break;
                
            case "BREAK":
                if(display.empty()){
                    response = Error.lenguaje(
                            codigo, 
                            "USSQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Sentencia DETENER no permitida en el contexto actual.");
                    erroresSSL.add(response);
                    }
                break;
                
            case "PRINT":
                break;
        }
    }
    
    
    public static String evaluarExpresion(Nodo exp){
        String tipoResult;
        String izq, der;
        String response;
        
        switch(exp.token){
            case "||":
            case "&&":
                izq = evaluarExpresion(exp.getHijo(0));
                der = evaluarExpresion(exp.getHijo(1));
                tipoResult = compararTiposExp(izq, der, "COND", exp);
                break;
                
            case "!":
                izq = evaluarExpresion(exp.getHijo(0));
                tipoResult= compararTiposExp(izq, null, "COND", exp);
                break;
                
            case "==":
            case "!=":
            case "<":
            case "<=":
            case ">":
            case ">=":
                izq = evaluarExpresion(exp.getHijo(0));
                der = evaluarExpresion(exp.getHijo(1));
                tipoResult = compararTiposExp(izq, der, "REL", exp);
                break;
                
            case "+":
            case "-":
            case "*":
            case "/":
            case "^":
                izq = evaluarExpresion(exp.getHijo(0));
                der = evaluarExpresion(exp.getHijo(1));
                tipoResult = compararTiposExp(izq, der, "AR", exp);
                break;
                
            case "_":
                izq = evaluarExpresion(exp.getHijo(0));
                tipoResult = compararTiposExp(izq, null, "AR", exp);
                break;
                
            case "bool":
                tipoResult = "bool";
                break;
            case "ENT":
                tipoResult = "integer";
                break;
            case "DOB":
                tipoResult = "double";
                break;
            case "CAD":
                tipoResult = "text";
                break;
            case "FECHA":
                tipoResult = "date";
                break;
            case "FECHAHORA":
                tipoResult = "datetime";
                break;
                
            case "VAR":
                String idVar = exp.valor;
                if(idMetodo.equals("GLOBAL")){
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            exp.row, 
                            exp.col, 
                            "Variable [" + idVar + "] en ambito no permitido");
                    erroresSSL.add(response);
                    tipoResult = "NULO";
                    break;
                }
                    
                
                if(TS.containsKey(idVar))
                    tipoResult = TS.get(idVar).tipo;
                else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            exp.row, 
                            exp.col, 
                            "No se ha declarado la variable [" + exp.valor + "] en el ambito [" + idMetodo + "]");
                    erroresSSL.add(response);
                    tipoResult = "NULO";
                }
                break;
                
            case ".":
                Nodo var = exp.getHijo(0);
                Nodo atr = exp.getHijo(1);
                
                //Existe la instancia
                if(TS.containsKey(var.valor)){
                    
                    String tipoObj = TS.get(var.valor).tipo;
                    
                    //Existe el atributo en el objeto
                    if(Archivos.bbdd.existeAtributo(Server.actualDB, tipoObj, atr.valor)){
                        tipoResult = Archivos.bbdd.getTipoAtributo(Server.actualDB, tipoObj, atr.valor);
                    }else{
                        response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            atr.row, 
                            atr.col, 
                            "No existe el atributo [" + atr.valor + "] en el objeto [" + tipoObj + "]");
                        erroresSSL.add(response);
                        tipoResult = "NULO";
                    }
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            var.row, 
                            var.col, 
                            "No se ha declarado la variable [" + var.valor + "] en el ambito [" + idMetodo + "]");
                    erroresSSL.add(response);
                    tipoResult = "NULO";
                }
                break;
                
            case "->":
                Nodo tabla = exp.getHijo(0);
                Nodo columna = exp.getHijo(1);
                
                //Existe la tabla
                if(Archivos.bbdd.existeTabla(Server.actualDB, tabla.valor)){
                    
                    //Existe la columna en la tabla
                    if(Archivos.bbdd.existeColumna(Server.actualDB, tabla.valor, columna.valor)){
                        tipoResult = Archivos.bbdd.getTipoColumna(Server.actualDB, tabla.valor, columna.valor);
                    }else{
                        response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            columna.row, 
                            columna.col, 
                            "No existe la columna [" + columna.valor + "] en la tabla [" + tabla.valor + "]");
                        erroresSSL.add(response);
                        tipoResult = "NULO";
                    }
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            tabla.row, 
                            tabla.col, 
                            "No existe la tabla [" + tabla.valor + "] en la Base de Datos [" + Server.actualDB + "]");
                    erroresSSL.add(response);
                    tipoResult = "NULO";
                }
                break;
                
            case "GET_DATE":
                tipoResult = "date";
                break;
                
            case "GET_DATE_TIME":
                tipoResult = "datetime";
                break;
                
            case "CONTAR":
                tipoResult = "integer";
                break;
                
            case "CALL":
                Nodo idFunc = exp.getHijo(0);
                Nodo larg = exp.hijos.size() == 2 ? exp.getHijo(1) : null;
                
                //Existe la funcion
                if(Archivos.bbdd.existeMetodo(Server.actualDB, idFunc.valor)){
                    //Es una funcion
                    if(!Archivos.bbdd.getTipoMetodo(Server.actualDB, idFunc.valor).equals("VOID")){
                        
                        int cantPar = Archivos.bbdd.getCantParametros(Server.actualDB, idFunc.valor);
                        
                        if(larg != null){
                            if(cantPar == larg.hijos.size()){
                                
                                boolean compatibles = true;
                                
                                for(int i = 0; i < cantPar; i++){
                                    String tipoParam = Archivos.bbdd.getTipoParam(Server.actualDB, idFunc.valor, i);
                                    String tipoArg = evaluarExpresion(larg.getHijo(i));
                                    if(!tipoParam.equals(tipoArg)){
                                        compatibles = false;
                                        break;
                                    }
                                }
                                
                                if(compatibles)
                                    tipoResult = Archivos.bbdd.getTipoMetodo(Server.actualDB, idFunc.valor);
                                else{
                                    response = Error.lenguaje(
                                        codigo, 
                                        "USQL", 
                                        cadUsql, 
                                        "Semantico", 
                                        larg.row, 
                                        larg.col, 
                                        "Tipos incompatibles en parametros");
                                    erroresSSL.add(response);
                                    tipoResult = "NULO";
                                }
                                
                            }else{
                                response = Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    larg.row, 
                                    larg.col, 
                                    "No coincidela cantidad de parametros.");
                                erroresSSL.add(response);
                                tipoResult = "NULO";
                            }
                        }else{
                            if(cantPar > 0){
                                response = Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    larg.row, 
                                    larg.col, 
                                    "No coincidela cantidad de parametros.");
                                erroresSSL.add(response);
                                tipoResult = "NULO";
                            }else{
                                tipoResult = Archivos.bbdd.getTipoMetodo(Server.actualDB, idFunc.valor);
                            }
                        }
                        
                    }else{
                        response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            idFunc.row, 
                            idFunc.col, 
                            "[" + idFunc.valor + "] es un procedimiento.");
                        erroresSSL.add(response);
                        tipoResult = "NULO";
                    }
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            idFunc.row, 
                            idFunc.col, 
                            "No existe la funcion [" + idFunc.valor + "] en la Base de Datos [" + Server.actualDB + "]");
                    erroresSSL.add(response);
                    tipoResult = "NULO";
                }
                
                break;
                
            default:
                tipoResult = null;
                break;
        }
        
        return tipoResult;
    }
    
    private static String compararTiposExp(String izq, String der, String clase, Nodo op){
        String tipo = "NULO";
        String response;
        
        switch(clase){
            case "COND":
                if(op.token.equals("!")){
                    if(izq.equals("bool"))
                        tipo = "bool";
                    else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion !. "
                                    + "Se esperaba [BOOL], se ha encontrado [" + izq + "]");
                        erroresSSL.add(response);
                    }
                }else{
                    if(izq.equals("bool") && der.equals("bool"))
                        tipo = "bool";
                    else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token + ". "
                                + "Se esperaba [BOOL], se ha encontrado [" + izq + "] y [" + der + "]");
                        erroresSSL.add(response);
                    }
                }
                break;
                
            case "REL":
//                if(izq.equals(der)){
//                    if(izq.equals("bool")){
//                        response = Error.lenguaje(
//                                codigo, 
//                                "USQL", 
//                                cadUsql, 
//                                "Semantico", 
//                                op.row, 
//                                op.col, 
//                                "Tipos incompatibles en operacion relacional " + op.token + ". "
//                                + "Los tipos de datos no pueden ser BOOL");
//                        erroresSSL.add(response);
//                    }else{
//                        tipo = "bool";
//                    }
//                }else{
//                    response = Error.lenguaje(
//                                codigo, 
//                                "USQL", 
//                                cadUsql, 
//                                "Semantico", 
//                                op.row, 
//                                op.col, 
//                                "Tipos incompatibles en operacion relacional " + op.token + ". "
//                                + "Los tipos deben ser iguales.");
//                        erroresSSL.add(response);
//                }
                switch(op.token){
                    case "==":
                        if(izq.equals(der))
                            tipo = "bool";
                        break;
                        
                    case "!=":
                        if(izq.equals(der))
                            tipo = "bool";
                        break;
                        
                    case "<":
                    case ">":
                    case "<=":
                    case ">=":
                        if((izq.equals("integer") || izq.equals("double") || izq.equals("bool")) && (der.equals("integer") || der.equals("double") || der.equals("bool"))){
                            tipo = "bool";
                        }if((izq.equals("date")|| izq.equals("datetime")|| izq.equals("text")) && (der.equals("date")||der.equals("datetime")|| der.equals("text"))){
                            tipo = "bool";
                        }else {
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion relacional " + op.token + ". "
                                + "Los tipos deben ser iguales.");
                                erroresSSL.add(response);
                        }
                        break;

                }
                break;
                
            case "AR":
                switch(op.token){
                    case "+":
//                        if(izq.equals("text") || der.equals("text"))
//                            tipo = "text";
//                        else if(izq.equals("bool"))
//                            tipo = der;
//                        else if(izq.equals("integer")){
//                            if(der.equals("bool") || der.equals("integer"))
//                                tipo = "integer";
//                            else
//                                tipo = der;
//                        }else if(izq.equals("double")){
//                            
//                        }else{
//                            response = Error.lenguaje(
//                                codigo, 
//                                "USQL", 
//                                cadUsql, 
//                                "Semantico", 
//                                op.row, 
//                                op.col, 
//                                "Tipos incompatibles en operacion " + op.token + ". "
//                                + "Los tipos no pueden ser DATE/DATETIME");
//                            erroresSSL.add(response);
//                        }
                        
                        if ((izq.equals("integer")) && der.equals("double")) {
                            tipo = "double";
                        } else if (izq.equals("integer") && der.equals("integer")) {
                            tipo = "integer";
                        }else if (izq.equals("integer") && der.equals("bool")) {
                            tipo = "integer";
                        }else if (izq.equals("integer") && der.equals("text")) {
                            tipo = "text";
                        }else if (izq.equals("double") && der.equals("integer")) {
                            tipo = "double";
                        }  else if (izq.equals("double") && der.equals("bool")) {
                            tipo = "double";
                        } else if (izq.equals("double") && der.equals("double")) {
                            tipo = "double";
                        } else if (izq.equals("double") && der.equals("text")) {
                            tipo = "text";
                        }else if (izq.equals("bool") && der.equals("double")) {
                            tipo = "double";
                        }else if (izq.equals("bool") && der.equals("integer")) {
                            tipo = "integer";
                        }  else if (izq.equals("bool") && der.equals("bool")) {
                            tipo = "bool";
                        } else if (izq.equals("bool") && der.equals("text")) {
                            tipo = "text";

                        }else if ((izq.equals("datetime") || izq.equals("date"))  && der.equals("text")) {
                            tipo = "text";

                        }  else if (izq.equals("text") && ( der.equals("text") ||der.equals("integer") ||der.equals("double") ||der.equals("bool") )) {
                            tipo = "text";

                        } else {
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token);
                            erroresSSL.add(response);
                        }
                        
                        break;
                        
                    case "-":
                        if ((izq.equals("integer")) && der.equals("double")) {
                            tipo = "double";

                        } else if (izq.equals("integer") && der.equals("integer")) {
                            tipo = "integer";

                        }else if (izq.equals("integer") && der.equals("bool")) {
                            tipo = "integer";

                        }else if (izq.equals("double") && der.equals("integer")) {
                            tipo = "double";

                        }  else if (izq.equals("double") && der.equals("bool")) {
                            tipo = "double";

                        } else if (izq.equals("double") && der.equals("double")) {
                            tipo = "double";

                        }else if (izq.equals("bool") && der.equals("double")) {
                            tipo = "double";

                        }else if (izq.equals("bool") && der.equals("integer")) {
                            tipo = "integer";

                        } else {
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token + ". "
                                + "Los tipos no pueden ser DATE/DATETIME");
                            erroresSSL.add(response);
                        }

                        break;
                    case "^":
//                        if((izq.equals("integer") || der.equals("double") && (der.equals("integer") || der.equals("double")))){
//                            if(izq.equals("double") || der.equals("double"))
//                                tipo = "double";
//                            else
//                                tipo = "integer";
//                        }else{
//                            response = Error.lenguaje(
//                                codigo, 
//                                "USQL", 
//                                cadUsql, 
//                                "Semantico", 
//                                op.row, 
//                                op.col, 
//                                "Tipos incompatibles en operacion " + op.token + ". "
//                                + "Los tipos solo pueden ser INTEGER/DOUBLE");
//                            erroresSSL.add(response);
//                        }
                        if ((izq.equals("integer")) && der.equals("double")) {
                            tipo = "double";

                        } else if (izq.equals("integer") && der.equals("integer")) {
                            tipo = "double";

                        }else if (izq.equals("integer") && der.equals("bool")) {
                            tipo = "double";

                        }else if (izq.equals("double") && der.equals("integer")) {
                            tipo = "double";

                        }  else if (izq.equals("double") && der.equals("bool")) {
                            tipo = "double";

                        } else if (izq.equals("double") && der.equals("double")) {
                            tipo = "double";

                        } else if (izq.equals("bool") && der.equals("double")) {
                            tipo = "double";

                        }else if (izq.equals("bool") && der.equals("integer")) {
                            tipo = "integer";

                        } else {
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token);
                            erroresSSL.add(response);
                        }

                        break;
                        
                    case "*":
//                        if(!izq.equals("date") && !izq.equals("datetime") && !izq.equals("text") &&
//                                !der.equals("date") && der.equals("datetime") && !der.equals("text")){
//                            
//                            if(izq.equals("bool"))
//                                tipo = der;
//                            else{
//                                if(izq.equals("double") || der.equals("double"))
//                                    tipo = "double";
//                                else
//                                    tipo = "integer";
//                            }
//                        }else{
//                            response = Error.lenguaje(
//                                codigo, 
//                                "USQL", 
//                                cadUsql, 
//                                "Semantico", 
//                                op.row, 
//                                op.col, 
//                                "Tipos incompatibles en operacion " + op.token + ". "
//                                + "Los tipos solo pueden ser INTEGER/DOUBLE/BOOL");
//                            erroresSSL.add(response);
//                        }
                        if ((izq.equals("integer")) && der.equals("double")) {
                            tipo = "double";

                        } else if (izq.equals("integer") && der.equals("integer")) {
                            tipo = "integer";

                        }else if (izq.equals("integer") && der.equals("bool")) {
                            tipo = "integer";

                        }else if (izq.equals("double") && der.equals("integer")) {
                            tipo = "double";

                        }  else if (izq.equals("double") && der.equals("bool")) {
                            tipo = "double";

                        } else if (izq.equals("double") && der.equals("double")) {
                            tipo = "double";

                        } else if (izq.equals("bool") && der.equals("double")) {
                            tipo = "double";

                        }else if (izq.equals("bool") && der.equals("integer")) {
                            tipo = "integer";

                        }  else if (izq.equals("bool") && der.equals("bool")) {
                            tipo = "bool";
                        } else {
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token);
                            erroresSSL.add(response);
                        }
                        break;
                        
                    case "/":
//                        if((izq.equals("integer") || der.equals("double") && (der.equals("integer") || der.equals("double")))){
//                            tipo = "double";
//                        }else{
//                            response = Error.lenguaje(
//                                codigo, 
//                                "USQL", 
//                                cadUsql, 
//                                "Semantico", 
//                                op.row, 
//                                op.col, 
//                                "Tipos incompatibles en operacion " + op.token + ". "
//                                + "Los tipos solo pueden ser INTEGER/DOUBLE");
//                            erroresSSL.add(response);
//                        }
                        if ((izq.equals("integer")) && der.equals("double")) {
                            tipo = "double";

                        } else if (izq.equals("integer") && der.equals("integer")) {
                            tipo = "integer";

                        }else if (izq.equals("integer") && der.equals("bool")) {
                            tipo = "integer";

                        }else if (izq.equals("double") && der.equals("integer")) {
                            tipo = "double";

                        }  else if (izq.equals("double") && der.equals("bool")) {
                            tipo = "double";

                        } else if (izq.equals("double") && der.equals("double")) {
                            tipo = "double";

                        } else if (izq.equals("bool") && der.equals("double")) {
                            tipo = "double";

                        }else if (izq.equals("bool") && der.equals("integer")) {
                            tipo = "integer";

                        } else {
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token);
                            erroresSSL.add(response);
                        }
                        break;
                        
                    case "_":
                        if(izq.equals("integer") || izq.equals("double"))
                            tipo = izq;
                        else{
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipo incompatibles en operacion - unario. "
                                + "El tipo solo pueden ser INTEGER/DOUBLE");
                            erroresSSL.add(response);
                        }
                        break;
                }
                break;
        }
        
        
        
        return tipo;
    }
    
    private static boolean casteoAsignacion(String izq, String der) {

        if (izq.equals("integer") && der.equals("integer")) {
            return true;
        } else if (izq.equals("integer") && der.equals("bool")) {
            return true;
        } else if (izq.equals("integer") && der.equals("double")) {
            return true;
        } else if (izq.equals("bool") && der.equals("bool")) {
            return true;
        } else if (izq.equals("bool") && (der.equals("integer") || der.equals("text"))) {
            return true;
        } else if (izq.equals("double") && der.equals("integer")) {
            return true;
        } else if (izq.equals("double") && der.equals("double")) {
            return true;
        } else if (izq.equals("datetime") && der.equals("datetime")) {
            return true;
        } else if (izq.equals("date") && der.equals("date")) {
            return true;
        }else if(izq.equals("text")){
            return true;
        } else {
            return false;
        }
    }
    
    
    
    public static String cadenaErrores(){
        String cad = "";
        
        for(String error : erroresSSL){
            cad += error;
        }
        
        return cad;
    }
}


class Variable{
    String tipo;
    String id;
    
    public Variable(String tipo, String id){
        this.tipo = tipo;
        this.id = id;
    }
}
