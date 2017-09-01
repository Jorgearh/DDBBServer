package servidorfisql.interpretes;

import java.util.ArrayList;
import servidorfisql.Constantes;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class InterpreteUSQL implements Constantes{
    
    int codigo;
    String cadUsql;
    
    
    public InterpreteUSQL(){
    }

    
    public String interpretar(int codigo, Nodo lsent, String cadUsql){
        String response = "";
        this.codigo = codigo;
        this.cadUsql = cadUsql;
        
        for(Nodo sent : lsent.hijos){
            String idSent = sent.token;
            
            switch(idSent){
                case "CREATE_DB":
                case "CREATE_TABLE":
                case "CREATE_OBJECT":
                case "CREATE_USER":
                case "PROC":
                case "FUNC":
                    response += create(sent);
                    break;
                    
                case "USE":
                    response += usar(sent);
                    break;
                    
                case "ALTER_TABLE_ADD":
                case "ALTER_TABLE_QUIT":
                case "ALTER_OBJECT_ADD":
                case "ALTER_OBJECT_QUIT":
                    response += alter(sent);
                    break;
                    
                case "DELETE":
                    response += delete(sent);
                    break;
                default:
                    response += Error.logico(codigo, "logico", "Sentencia " + idSent + "invalida");
            }
        }
        
        
        return response;
    }
    
    private String create(Nodo create){
        String response = "EXITO";
        Nodo nodoID;
        
        switch(create.token){
            case "CREATE_DB":
                String idDB;
                
                nodoID = create.getHijo(0);
                idDB = nodoID.valor;
                
                if(Archivos.bbdd.existeBD(idDB)){
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            this.cadUsql, 
                            "Semantico", 
                            nodoID.row, 
                            nodoID.col, 
                            "Ya existe la base de datos [" + idDB + "]");
                }else{
                    
                    /*crear el directorio de la base de datos*/
                    String dirBD = Archivos.bbddDir + idDB;
                    String dirTables = dirBD + "/tables/";
                    
                    Archivos.crearDirectorio(dirTables);
                    
                    /*crear la base de datos en memoria, 
                    */
                    Archivos.bbdd.crearBD(idDB, dirBD, dirTables);
                    Server.actualDB = idDB;
                    
                    Consola.write("Base de datos [" + idDB + "] creada exitosamente");
                }
                break;
                
            case "CREATE_TABLE":
                
                //Hay una BD en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    
                    
                    nodoID = create.getHijo(0);
                    Nodo lcampo = create.getHijo(1);
                    
                    String idTable = nodoID.valor;
                    
                    //No existe la tabla en la BD
                    if(!Archivos.bbdd.existeTabla(Server.actualDB, idTable)){
                        
                        //No se repite ningun campo
                        String campoRepetido = nietosRepetidosValor(create.getHijo(1), 1);
                        if( campoRepetido == null){
                            boolean pk = false;
                            String idPk = "";
                            
                            //Para cada campo evaluar los complementos
                            for(Nodo columna : create.getHijo(1).hijos){
                                Nodo tipoCol = columna.getHijo(0);
                                Nodo idColDef = columna.getHijo(1);
                                Nodo lcomp = columna.getHijo(2);
                                
                                boolean nulo = false;
                                
                                for(Nodo comp : lcomp.hijos){
                                    switch(comp.token){
                                        case "PK":
                                            if(!pk){
                                                if(!nulo){
                                                    pk = true;
                                                    idPk = idColDef.valor;
                                                }else{
                                                    response = 
                                                        Error.lenguaje(
                                                                codigo, 
                                                                "USQL", 
                                                                this.cadUsql, 
                                                                "Semantico", 
                                                                idColDef.row, 
                                                                idColDef.col, 
                                                                "La llave primaria no puede aceptar valores nulos");
                                                    return response;
                                                }
                                                
                                            }else{
                                                response = 
                                                        Error.lenguaje(
                                                                codigo, 
                                                                "USQL", 
                                                                this.cadUsql, 
                                                                "Semantico", 
                                                                idColDef.row, 
                                                                idColDef.col, 
                                                                "Multiple definicion de llave primaria en la tabla [" + idTable + "]");
                                                return response;
                                            }
                                            break;
                                            
                                        case "FK":
                                            Nodo idTableRef = comp.getHijo(0);
                                            Nodo idColRef = comp.getHijo(1);
                                            
                                            //Existe tabla referenciada por llave foranea
                                            if(Archivos.bbdd.existeTabla(Server.actualDB, idTableRef.valor)){
                                                
                                                //Exite la columna referenciada por la llave foranea
                                                if(Archivos.bbdd.existeColumna(Server.actualDB, idTableRef.valor, idColRef.valor)){
                                                    
                                                    //Tipos diferentes columna definida y columna referenciada
                                                    if(!tipoCol.valor.equals(Archivos.bbdd.getTipoColumna(Server.actualDB, idTableRef.valor, idColRef.valor))){
                                                        response = 
                                                        Error.lenguaje(
                                                                codigo, 
                                                                "USQL", 
                                                                this.cadUsql, 
                                                                "Semantico", 
                                                                idColDef.row, 
                                                                idColDef.col, 
                                                                "Tipos incompatibles entre"
                                                                + "columna definida [" + idColDef.valor + "] y "
                                                                + "columna referenciada [" + idColRef.valor + "]");
                                                        return response;
                                                    }
                                                    
                                                }else{
                                                    response = 
                                                        Error.lenguaje(
                                                                codigo, 
                                                                "USQL", 
                                                                this.cadUsql, 
                                                                "Semantico", 
                                                                idColRef.row, 
                                                                idColRef.col, 
                                                                "No existe la columna "
                                                                + "[" + idColRef.valor + "]"
                                                                + " referenciada por "
                                                                + "la llave foranea.");
                                                    return response;
                                                }
                                                
                                            }else{
                                                response = 
                                                        Error.lenguaje(
                                                                codigo, 
                                                                "USQL", 
                                                                this.cadUsql, 
                                                                "Semantico", 
                                                                idTableRef.row, 
                                                                idTableRef.col, 
                                                                "No existe la tabla "
                                                                + "[" + idTableRef.valor + "]"
                                                                + " referenciada por "
                                                                + "la llave foranea.");
                                                return response;
                                            }
                                            break;
                                            
                                        case "NULO":
                                            
                                            if(idPk.equals(idColDef.valor)){
                                                response = 
                                                        Error.lenguaje(
                                                        codigo, 
                                                        "USQL", 
                                                        this.cadUsql, 
                                                        "Semantico", 
                                                        idColDef.row, 
                                                        idColDef.col, 
                                                        "La llave primaria [" + idColDef.valor + "] "
                                                        + "no puede tener valores nulos.");
                                                return response;
                                            }else
                                                nulo = true;
                                            
                                            break;
                                            
                                        case "AUTOINC":
                                            
                                            if(!tipoCol.valor.equals("INTEGER")){
                                                response = 
                                                        Error.lenguaje(
                                                                codigo, 
                                                                "USQL", 
                                                                this.cadUsql, 
                                                                "Semantico", 
                                                                idColDef.row, 
                                                                idColDef.col, 
                                                                "Un campo autoincremental "
                                                                + "debe ser de tipo INTEGER");
                                                return response;
                                            }
                                            
                                            break;
                                    }
                                }
                                
                            }//fin evaluacion de complementos
                            
                            Archivos.bbdd.crearTabla(Server.actualDB, create);
                            Consola.writeln("Tabla [" + idTable + "] creada exitosamente.");
                            
                        }else{
                            response = Error.lenguaje(codigo, 
                                    "USQL", 
                                    this.cadUsql, 
                                    "Semantico",
                                    nodoID.row, 
                                    nodoID.col, 
                                    "Multiple declaracion de la columna [" + campoRepetido + "].");
                        }
                    }else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                this.cadUsql, 
                                "Semantico", 
                                nodoID.row, 
                                nodoID.col, 
                                "La tabla [" + idTable + "] ya existe en la base de datos [" + Server.actualDB + "]");
                    }
                    
                }else{
                    response = Error.logico(
                            codigo, 
                            "CREATE TABLE", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                
                break;
                
            case "CREATE_USER":
                if(Server.user.equals("admin")){
                    Nodo user = create.getHijo(0);
                    Nodo pass = create.getHijo(1);

                    if(!Archivos.usuarios.existsUser(user.valor)){
                        Archivos.usuarios.agregarUsuario(user.valor, pass.valor);
                        Consola.writeln("Usuario [" + user.valor + "] creado exitosamente.");
                    }else{
                        response = 
                                Error.lenguaje(
                                        codigo, 
                                        "USQL", 
                                        this.cadUsql, 
                                        "Semantico", 
                                        user.row, 
                                        user.col, 
                                        "Ya existe el usuario [" + user.valor + "]. "
                                        + "Vuelva a intentarlo con otro nombre de usuario");
                    }
                }else{
                    response = Error.logico(codigo, 
                            "CREATE USER", 
                            "Usuario [" + Server.user + "] sin permisos para crear usuarios.");
                }
                
                break;
                
            case "CREATE_OBJECT":
                
                //Hay una base de datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    
                    Nodo idObj = create.getHijo(0);
                    Nodo latr = create.getHijo(1);

                    //No existe un objeto con el mismo nombre en la base de datos
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, idObj.valor)){

                        for(Nodo atr : latr.hijos){
                            Nodo idAtr = atr.getHijo(1);

                            //El objeto contiene un atributo con el mismo nombre
                            if(Archivos.bbdd.existeAtributo(Server.actualDB, idObj.valor, idAtr.valor)){
                                response = 
                                        Error.lenguaje(
                                                codigo,
                                                "USQL",
                                                this.cadUsql,
                                                "Semantico",
                                                idAtr.row,
                                                idAtr.col, 
                                                "Multiple declaracion del atributo [" + idAtr.valor + "] "
                                                        + "en el objeto [" + idObj.valor + "] "
                                                        + "en la base de datos [" + Server.actualDB + "]");
                                return response;
                            }
                        }

                        //Si no hay errores en la declaracion de atributos
                        Archivos.bbdd.crearObjeto(Server.actualDB, create);


                    }else{
                        response = 
                            Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    this.cadUsql, 
                                    "Semantico", 
                                    idObj.row, 
                                    idObj.col, 
                                    "Ya existe el objeto [" + idObj.valor + "]"
                                    + "en la base de datos [" + Server.actualDB + "]");
                    }
                }else{
                        response = Error.logico(
                            codigo, 
                            "CREATE TABLE", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
                
            case "PROC":
            case "FUNC":
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    
                    Nodo idProc = create.getHijo(0);

                    //No existe un metodo con el mismo nombre en la base de datos
                    if(!Archivos.bbdd.existeMetodo(Server.actualDB, idProc.valor)){

                        //Analisis semantico SSL(parametros, variables locales, comprobacion de tipos)
                        String erroresSSL = analisisSemanticoSSL(create);
                        
                        if(erroresSSL == null){
                            
                        }else{
                            return erroresSSL;
                        }

                        
                        //Si no hay errores en la declaracion del metodo
                        Archivos.bbdd.crearProc(Server.actualDB, create);

                    }else{
                        response = 
                            Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    this.cadUsql, 
                                    "Semantico", 
                                    idProc.row, 
                                    idProc.col, 
                                    "Ya existe " + (create.token.equals("PROC") ? "el procedimiento" : "la funcion") 
                                    + " [" + idProc.valor + "]"
                                    + "en la base de datos [" + Server.actualDB + "]");
                    }
                }else{
                        response = Error.logico(
                            codigo, 
                            "CREATE TABLE", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
        }
        
        return response;
    }
    
    private String usar(Nodo use){
        String response = "EXITO"; 
        String idDB = use.getHijo(0).valor;
        
        if(Archivos.bbdd.existeBD(idDB)){
            if(Archivos.bbdd.tienePermisos(idDB, Server.user)){
                Server.actualDB = idDB;
                Consola.writeln("Usando Base de Datos [" + idDB + "]");
            }else{
                response = Error.logico(codigo, 
                                "PERMISOS", 
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos"
                                        + "en la base de datos [" + Server.actualDB + "]");
            }
        }else{
            response = Error.lenguaje(
                    codigo, 
                    "USQL", 
                    this.cadUsql, 
                    "Semantico", 
                    use.getHijo(0).row, 
                    use.getHijo(0).col,
                    "No existe la base de datos [" + idDB + "]");
        }
        
        return response;
    }
    
    private String alter(Nodo alter){
        String response = "EXITO";
        
        
        
        return response;
    }
    
    private String delete(Nodo delete){
        String response = "EXITO";
        String objeto = delete.getHijo(0).valor;
        String id = delete.getHijo(1).valor;
        
        
        if(objeto.equals("DDBB")){
            //if()
        }else{
            //Hay una base de datos en uso
            if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){

            }
        }
        
        
        
        
        
        return response;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    /***
     * Evalua si un nieto pertenece a mas de un hijo del nodo, basado en el valor,
     * la posicion del nieto la define index.
     * @param padre
     * @param index
     * @return Si existen repetidos, el primer nieto repetido, de lo contrario null.
     */
    private String nietosRepetidosValor(Nodo padre, int index){
        ArrayList<String> items = new ArrayList<>();
        
        for(Nodo hijo : padre.hijos){
            String nieto = hijo.getHijo(index).valor;
            
            if(items.contains(nieto))
                return nieto;
            items.add(nieto);
        }
        
        return null;
        
    }
    
    private String hijoRepetidoToken(Nodo padre){
        ArrayList<String> items = new ArrayList<>();
        
        for(Nodo hijo : padre.hijos){
            String token = hijo.token;
            
            if(items.contains(token))
                return token;
            
            items.add(token);
        }
        
        return null;
    }
    
    private String analisisSemanticoSSL(Nodo metodo){
        return "EXITO";
    }
}
