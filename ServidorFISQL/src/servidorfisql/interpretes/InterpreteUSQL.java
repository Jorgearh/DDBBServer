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
                /*   SENTENCIAS DDL   */
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
                case "ALTER_USER":
                    response += alter(sent);
                    break;
                    
                case "DELETE":
                    response += delete(sent);
                    break;
                    
                    
                /*   SENTENCIAS DML*/
                case "INSERT":
                    break;
                case "SELECT":
                    break;
                case "UPDATE":
                    break;
                case "DELETE_FROM_TABLE":
                    break;
                    
                    
                /*   SENTENCIAS DCL   */
                case "GRANT":
                    response += grant(sent);
                    break;
                case "DENY":
                    response += deny(sent);
                    break;
                    
                default:
                    response += Error.logico(codigo, "logico", "Sentencia " + idSent + "invalida");
            }
        }
        
        
        return response;
    }
    
    /*    SENTENCIAS DDL    */
    
    private String create(Nodo create){
        String response = "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + this.codigo + ",\n" +
                            "	\"sentencia\": \"" + create.token + "\"\n" +
                            "]";
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
                    
                    Consola.writeln ("Base de datos [" + idDB + "] creada exitosamente\n");
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
                        String analisis = analisisDeclaracionColumnas(create);
                        
                        if(analisis == null){
                            Archivos.bbdd.crearTabla(Server.actualDB, create);
                            Consola.writeln("Tabla [" + idTable + "] creada exitosamente.\n");
                        }else{
                            return analisis;
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

                    //No existe un objeto con el mismo nombre en la base de datos
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, idObj.valor)){

                        String analisis = analisisDeclaracionAtributos(create);
                        if(analisis == null){
                            //Si no hay errores en la declaracion de atributos
                            Archivos.bbdd.crearObjeto(Server.actualDB, create); 
                            Consola.writeln("Objeto [" + idObj.valor + "] creado exitosamente.\n");
                        }else{
                            return analisis;
                        }

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
                            "CREATE OBJECT", 
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
                        
                        if(erroresSSL != null){
                            return erroresSSL;
                        }

                        
                        //Si no hay errores en la declaracion del metodo
                        Archivos.bbdd.crearProc(Server.actualDB, create);
                        Consola.writeln((create.token.equals("PROC") ? "PROCEDIMIENTO" : "FUNCION") + " [" + idProc + "] creado exitosamente.\n");

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
                            "CREATE" + (create.token.equals("PROC") ? " PROCEDURE" : " FUNCTION"), 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
            
        }
        
        return response;
    }
    
    private String usar(Nodo use){
        String response = "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + this.codigo + ",\n" +
                            "	\"sentencia\": \"" + use.token + "\"\n" +
                            "]";
        
        String idDB = use.getHijo(0).valor;
        
        if(Archivos.bbdd.existeBD(idDB)){
            if(Archivos.bbdd.tienePermisos(idDB, Server.user)){
                Server.actualDB = idDB;
                Consola.writeln("Usando Base de Datos [" + idDB + "]\n");
            }else{
                response = Error.logico(codigo, 
                                "PERMISOS USE", 
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
        String response = "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + this.codigo + ",\n" +
                            "	\"sentencia\": \"" + alter.token + "\"\n" +
                            "]";
        Nodo idTable;
        Nodo idObject;
        Nodo lid;
        
        switch(alter.valor){
            case "ALTER_USER":
                Nodo idUser = alter.getHijo(0);
                Nodo passUser = alter.getHijo(1);

                //Existe el usuario a modificar
                if(Archivos.usuarios.existsUser(idUser.valor)){
                    //Un usuario modifica sus credenciales
                    if(Server.user.equals("admin") || Server.user.equals(idUser.valor)){
                        
                        //Modificar password
                        Archivos.usuarios.modificarUsuario(idUser.valor, passUser.valor);
                        Consola.writeln("Se ha modificado el password del usuario [" + idUser.valor + "]\n");

                    }else{                                  //Un usuario no admin 
                        response = Error.logico(
                                codigo, 
                                cadUsql, 
                                "Solo el usuario admin puede modificar credenciales ajenas.");
                    }
                }else{
                    response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idUser.row, 
                                            idUser.col,
                                            "No existe el usuario [" + idUser.valor + "]");
                }
                
                break;
            case "ALTER_TABLE_QUIT":
                idTable = alter.getHijo(0);
                lid = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe la Tabla
                    if(!Archivos.bbdd.existeTabla(Server.actualDB, idTable.valor)){
                        //El usuario actual tiene permisos en la tabla
                        if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idTable.valor, Server.user)){
                            
                            for(Nodo col : lid.hijos){
                                if(!Archivos.bbdd.existeColumna(Server.actualDB, idTable.valor, col.valor)){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                this.cadUsql, 
                                                "Semantico", 
                                                col.row, 
                                                col.col,
                                                "No existe [" + col.valor + "]");
                                    return response;
                                }
                            }

                            //Eliminar las columnas especificadas
                            Archivos.bbdd.modificarTablaEliminar(Server.actualDB, idTable.valor, lid);
                            Consola.write("Se han eliminado de la tabla [" + idTable.valor + "] las columnas { ");
                            lid.hijos.stream().forEach((id) -> {
                                Consola.append(id.valor + " ");
                            });
                            Consola.append("}\n");
                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER TABLE QUIT", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en la tabla [" + idTable.valor + "]");
                        }
                        
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idTable.row, 
                                            idTable.col,
                                            "No existe la tabla [" + idTable.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER TABLE QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                
                break;
            case "ALTER_OBJECT_QUIT":
                idObject = alter.getHijo(0);
                lid = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe el Objeto
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, idObject.valor)){
                        //El usuario actual tiene permisos en el objeto
                        if(Archivos.bbdd.tienePermisosObjeto(Server.actualDB, idObject.valor, Server.user)){
                            for(Nodo atr : lid.hijos){
                                if(!Archivos.bbdd.existeAtributo(Server.actualDB, idObject.valor, atr.valor)){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                this.cadUsql, 
                                                "Semantico", 
                                                atr.row, 
                                                atr.col,
                                                "No existe el atributo [" + atr.valor + "]");
                                    return response;
                                }
                            }

                            //Eliminar los atributos especificadas
                            Archivos.bbdd.modificarObjetoEliminar(Server.actualDB, idObject.valor, lid);
                            Consola.write("Se han eliminado del objeto [" + idObject.valor + "] los atributos { ");
                            lid.hijos.stream().forEach((id) -> {
                                Consola.append(id.valor + " ");
                            });
                            Consola.append("}\n");

                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER OBJECT QUIT", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en el objeto [" + idObject.valor + "]");
                        }
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idObject.row, 
                                            idObject.col,
                                            "No existe el objeto [" + idObject.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER OBJECT QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
            case "ALTER_TABLE_ADD":
                idTable = alter.getHijo(0);
                Nodo lcol = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe la Tabla
                    if(!Archivos.bbdd.existeTabla(Server.actualDB, idTable.valor)){
                        //El usuario actual tiene permisos en la tabla
                        if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idTable.valor, Server.user)){
                            
                            //Analisis semantico de las nuevas columnas
                            String analisisColumnas = analisisDeclaracionColumnas(alter);
                            if(analisisColumnas != null)
                                return analisisColumnas;
                            
                            //Verificar que las nuevas columnas no existan en la tabla
                            for(Nodo col : lcol.hijos){
                                Nodo idCol = col.getHijo(1);
                                
                                if(Archivos.bbdd.existeColumna(Server.actualDB, idTable.valor, idCol.valor)){
                                    response = Error.lenguaje(codigo, 
                                                                "USQL", 
                                                                this.cadUsql, 
                                                                "Semantico",
                                                                idCol.row, 
                                                                idCol.col, 
                                                                "Multiple declaracion de la columna [" + idCol.valor + "]."); 
                                    return response;
                                }
                            }
                            
                            //Verificar existencia de llaves primarias multiples
                            for(Nodo col : lcol.hijos){
                                Nodo idCol = col.getHijo(1);
                                Nodo lcomp = col.getHijo(2);
                                
                                for(Nodo comp : lcomp.hijos){
                                    if(comp.token.equals("PK")){
                                        String pk = Archivos.bbdd.obtenerPk(Server.actualDB, idTable.valor);
                                        
                                        if(pk != null){
                                            response = 
                                                Error.lenguaje(
                                                        codigo, 
                                                        "USQL", 
                                                        this.cadUsql, 
                                                        "Semantico", 
                                                        idCol.row, 
                                                        idCol.col, 
                                                        "Multiple definicion de llave primaria en la tabla [" + idTable.valor + "]");
                                            return response; 
                                        }
                                    }
                                }
                            }
                            
                            //Agregar las columnas especificadas
                            Archivos.bbdd.modificarTablaAgregar(Server.actualDB, idTable.valor, lcol);
                            Consola.write("Se han agregado a la tabla [" + idTable.valor + "] las columnas { ");
                            lcol.hijos.stream().forEach((col) -> {
                                Consola.append(col.getHijo(1).valor + " ");
                            });
                            Consola.append("}\n");
                            
                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER TABLE ADD", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en la tabla [" + idTable.valor + "]");
                        }
                        
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idTable.row, 
                                            idTable.col,
                                            "No existe la tabla [" + idTable.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER TABLE QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
            case "ALTER_OBJECT_ADD":
                idObject = alter.getHijo(0);
                Nodo latr = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe el Objeto
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, idObject.valor)){
                        //El usuario actual tiene permisos en el objeto
                        if(Archivos.bbdd.tienePermisosObjeto(Server.actualDB, idObject.valor, Server.user)){
                            
                            //Declaracion multiple de atributos agregados
                            String analisis = analisisDeclaracionAtributos(alter);
                            if(analisis != null)
                                return analisis;
                            
                            for(Nodo atr : latr.hijos){
                                Nodo tipoAtr = atr.getHijo(0);
                                Nodo idAtr = atr.getHijo(1);
                                
                                //Ya existe el atributo
                                if(Archivos.bbdd.existeAtributo(Server.actualDB, idObject.valor, idAtr.valor)){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                this.cadUsql, 
                                                "Semantico", 
                                                atr.row, 
                                                atr.col,
                                                "Multiple declaracion el atributo [" + idAtr.valor + "]");
                                    return response;
                                }
                                
                                //El atributo es de tipo objeto
                                if(tipoAtr.token.equals("ID")){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                this.cadUsql, 
                                                "Semantico", 
                                                atr.row, 
                                                atr.col,
                                                "El atributo [" + idAtr.valor + "] no puede ser de tipo objeto");
                                    return response;
                                }
                            }

                            //Eliminar los atributos especificadas
                            Archivos.bbdd.modificarObjetoAgregar(Server.actualDB, idObject.valor, latr);
                            
                            Consola.write("Se han agregado al objeto [" + idObject.valor + "] los atributos { ");
                            latr.hijos.stream().forEach((atr) -> {
                                Consola.append(atr.getHijo(0).valor + " " + atr.getHijo(1).valor + "  ");
                            });
                            Consola.append("}\n");

                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER OBJECT QUIT", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en el objeto [" + idObject.valor + "]");
                        }
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idObject.row, 
                                            idObject.col,
                                            "No existe el objeto [" + idObject.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER OBJECT QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
        }
        
        
        return response;
    }
    
    private String delete(Nodo delete){
        String response = "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + this.codigo + ",\n" +
                            "	\"sentencia\": \"" + delete.token + "\"\n" +
                            "]";
        Nodo usqlObject = delete.getHijo(0);
        Nodo idObject = delete.getHijo(1);
        
        
        if(usqlObject.valor.equals("DDBB")){
            if(idObject.valor.equals(Server.actualDB)){
                response = Error.logico(
                        codigo, 
                        "DELETE DB", 
                        "No puede eleiminarse la base de datos [" + idObject.valor + "] "
                                + "porque se encuentra en uso");
            }else{
                //Existe la Base de Datos a eliminar
                if(Archivos.bbdd.existeBD(idObject.valor)){
                    //No tiene permisos en la base de datos
                    if(!Archivos.bbdd.tienePermisos(idObject.valor, Server.user)){
                        response = Error.logico(codigo, 
                                    "PERMISOS DELETE DB", 
                                    "El usuario [" + Server.user + "] "
                                            + "no tiene permisos"
                                            + "en la base de datos [" + Server.actualDB + "]");
                    }else{
                        Archivos.bbdd.eliminarBD(idObject.valor);
                        Consola.writeln("Se ha eliminado la Base de Datos [" + idObject.valor + "]\n");
                    }
                }else{
                    response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idObject.row, 
                                            idObject.col,
                                            "No existe la Base de Datos [" + idObject.valor + "]");
                }
            }
        }else if(usqlObject.valor.equals("USER")){
            
            if(Server.user.equals("admin")){
                if(!idObject.valor.equals("admin")){
                    //Existe el usuario a eliminar
                    if(Archivos.usuarios.existsUser(idObject.valor)){
                        //Eliminar todos los permisos para el usuario
                        Archivos.bbdd.denegarPermisosParaUsuario(idObject.valor);
                        //Eliminar el usuario
                        Archivos.usuarios.eliminarUsuario(idObject.valor);
                        Consola.writeln("Se ha eliminado el usuario [" + idObject.valor + "]\n");
                    }else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                idObject.row, 
                                idObject.col, 
                                "No existe el usuario [" + idObject.valor + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "DELTE USER ADMIN", 
                            "No se puede eliminar el usuario admin");
                }
            }else{
                response = Error.logico(
                        codigo, 
                        "DELETE USER NOT ADMIN", 
                        "Solo el usuario admin puede eliminar usuarios.");
            }
            
        }else{
            //Hay una base de datos en uso
            if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                //El usuario actual tiene permisos en la Base de Datos actual
                if(Archivos.bbdd.tienePermisos(Server.actualDB, Server.user)){
                    switch(usqlObject.valor){
                        case "TABLE":
                            //Existe la tabla a eliminar
                            if(Archivos.bbdd.existeTabla(Server.actualDB, idObject.valor)){
                                
                                //El usuario actual tiene permisos en la tabla
                                if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idObject.valor, Server.user)){
                                    //Eliminar tabla
                                    Archivos.bbdd.eliminarTabla(Server.actualDB, idObject.valor);
                                    Consola.writeln("Se ha eliminado la tabla [" + idObject.valor + " ]"
                                            + "de la base de datos [" + Server.actualDB + "]\n");
                                }else{
                                    response = Error.logico(codigo, 
                                                        "PERMISOS DELETE TABLE", 
                                                        "El usuario [" + Server.user + "] "
                                                                + "no tiene permisos"
                                                                + "en la tabla [" + idObject.valor + "]");
                                }
                            }else{
                                response = Error.lenguaje(
                                                    codigo, 
                                                    "USQL", 
                                                    this.cadUsql, 
                                                    "Semantico", 
                                                    idObject.row,
                                                    idObject.col,
                                                    "No existe la tabla [" + idObject.valor + "]"
                                                    + "en la Base de Datos [" + Server.actualDB + "]");
                            }
                            break;
                        case "OBJECT":
                            //Existe el objeto a eliminar
                            if(Archivos.bbdd.existeObjeto(Server.actualDB, idObject.valor)){
                                
                                //El usuario actual tiene permisos en el objeto
                                if(Archivos.bbdd.tienePermisosObjeto(Server.actualDB, idObject.valor, Server.user)){
                                    //Eliminar objeto
                                    Archivos.bbdd.eliminarObjeto(Server.actualDB, idObject.valor);
                                    Consola.writeln("Se ha eliminado el objeto [" + idObject.valor + " ]"
                                            + "de la base de datos [" + Server.actualDB + "]");
                                }else{
                                    response = Error.logico(codigo, 
                                                        "PERMISOS DELETE OBJECT", 
                                                        "El usuario [" + Server.user + "] "
                                                                + "no tiene permisos"
                                                                + "en el objeto [" + idObject.valor + "]");
                                }
                            }else{
                                response = Error.lenguaje(
                                                    codigo, 
                                                    "USQL", 
                                                    this.cadUsql, 
                                                    "Semantico", 
                                                    idObject.row,
                                                    idObject.col,
                                                    "No existe el objeto [" + idObject.valor + "]"
                                                    + "en la Base de Datos [" + Server.actualDB + "]");
                            }
                            break;
                        case "PROC":
                        case "FUNC":
                             //Existe el metodo a eliminar
                            String nameAux = usqlObject.token.equals("PROC") ? "el procedimiento" : "la funcion";
                            
                            if(Archivos.bbdd.existeMetodo(Server.actualDB, idObject.valor)){
                                
                                //El usuario actual tiene permisos en el metodo
                                if(Archivos.bbdd.tienePermisosMetodo(Server.actualDB, idObject.valor, Server.user)){
                                    //Eliminar metodo
                                    Archivos.bbdd.eliminarMetodo(Server.actualDB, idObject.valor);
                                    Consola.writeln("Se ha eliminado " + nameAux + "  [" + idObject.valor + " ]"
                                            + "de la base de datos [" + Server.actualDB + "]\n");
                                }else{
                                    response = Error.logico(codigo, 
                                                        "PERMISOS DELETE OBJECT", 
                                                        "El usuario [" + Server.user + "] "
                                                                + "no tiene permisos"
                                                                + "en " + nameAux + " [" + idObject.valor + "]");
                                }
                            }else{
                                response = Error.lenguaje(
                                                    codigo, 
                                                    "USQL", 
                                                    this.cadUsql, 
                                                    "Semantico", 
                                                    idObject.row,
                                                    idObject.col,
                                                    "No existe " + nameAux + " [" + idObject.valor + "]"
                                                    + "en la Base de Datos [" + Server.actualDB + "]");
                            }
                            break;
                    }
                }else{
                    response = Error.logico(codigo, 
                                "PERMISOS DELETE", 
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos"
                                        + "en la base de datos [" + Server.actualDB + "]");
                }
            }else{
                response = Error.logico(
                            codigo, 
                            "CREATE TABLE", 
                            "No se ha seleccionado una base de datos para usar.");
            }
        }
        
        
        
        
        
        return response;
    }
    
    
    
    /*    SENTENCIAS DML    */
    
    private String insert(Nodo insert){
        String response = "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + this.codigo + ",\n" +
                            "	\"sentencia\": \"" + insert.token + "\"\n" +
                            "]";
        
        
        
        return response;
    }

    


    /*    SENTENCIAS DCL    */

    private String grant(Nodo grant){
        String response = "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + this.codigo + ",\n" +
                            "	\"sentencia\": \"" + grant.token + "\"\n" +
                            "]";
        
        //Solo el usuario admin puede otorgar permisos
        if(Server.user.equals("admin")){
            
            Nodo idUser = grant.getHijo(0);
            Nodo idDB = grant.getHijo(1);
            Nodo dbObj = grant.getHijo(2);
            
            //Existe el usuario
            if(Archivos.usuarios.existsUser(idUser.valor)){
                
                //Existe la Base de Datos
                if(Archivos.bbdd.existeBD(idDB.valor)){
                    if(dbObj.token.equals("*")){
                        Archivos.bbdd.otorgarPermisosEnTodo(idDB.valor, idUser.valor);
                        Consola.writeln("Se ha otorgado permisos al usuario [" + idUser.valor + "] "
                                + "en todos los objetos de la Base de Datos [" + idDB.valor + "]");
                    }else{
                        
                        //No se ha encontrado el objeto
                        if(!Archivos.bbdd.otorgarPermisosSiExiste(idDB.valor, dbObj.valor, idUser.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    this.cadUsql,
                                    "Semantico",
                                    dbObj.row,
                                    dbObj.col,
                                    "No existe el elemento [" + dbObj.valor + "] "
                                            + "en la Base de Datos [" + idDB.valor + "]");
                        }else{
                            Consola.writeln("Se han otorgado los permisos al usuario [" + idUser.valor + "] "
                                + "en el elemento [" + dbObj.valor + "] de la Base de Datos [" + idDB.valor + "]");
                        }
                    }
                }else{
                 response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idDB.row, 
                                            idDB.col,
                                            "No existe la Base de Datos [" + idDB.valor + "]");   
                }
            }else{
                response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idUser.row, 
                                            idUser.col,
                                            "No existe el usuario[" + idUser.valor + "]");
            }
            
            
        }else{
            response = Error.logico(codigo, 
                            "OTORGAR PERMISOS", 
                            "Solo el usuario [admin] puede otorgar permisos");
        }
        
        return response;
    } 
    
    private String deny(Nodo deny){
        String response = "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + this.codigo + ",\n" +
                            "	\"sentencia\": \"" + deny.token + "\"\n" +
                            "]";
        
        //Solo el usuario admin puede otorgar permisos
        if(Server.user.equals("admin")){
            
            Nodo idUser = deny.getHijo(0);
            Nodo idDB = deny.getHijo(1);
            Nodo dbObj = deny.getHijo(2);
            
            //Existe el usuario
            if(Archivos.usuarios.existsUser(idUser.valor)){
                
                //Existe la Base de Datos
                if(Archivos.bbdd.existeBD(idDB.valor)){
                    if(dbObj.token.equals("*")){
                        Archivos.bbdd.denegarPermisosEnTodo(idDB.valor, idUser.valor);
                        Consola.writeln("Se ha denegado permisos al usuario [" + idUser.valor + "] "
                                + "en todos los objetos de la Base de Datos [" + idDB.valor + "]");
                    }else{
                        
                        //No se ha encontrado el objeto
                        if(!Archivos.bbdd.denegarPermisosSiExiste(idDB.valor, dbObj.valor, idUser.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    this.cadUsql,
                                    "Semantico",
                                    dbObj.row,
                                    dbObj.col,
                                    "No existe el elemento [" + dbObj.valor + "] "
                                            + "en la Base de Datos [" + idDB.valor + "]");
                        }else{
                            Consola.writeln("Se han denegado los permisos al usuario [" + idUser.valor + "] "
                                + "en el elemento [" + dbObj.valor + "] de la Base de Datos [" + idDB.valor + "]");
                        }
                    }
                }else{
                 response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idDB.row, 
                                            idDB.col,
                                            "No existe la Base de Datos [" + idDB.valor + "]");   
                }
            }else{
                response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            this.cadUsql, 
                                            "Semantico", 
                                            idUser.row, 
                                            idUser.col,
                                            "No existe el usuario[" + idUser.valor + "]");
            }
            
            
        }else{
            response = Error.logico(codigo, 
                            "DENEGAR PERMISOS", 
                            "Solo el usuario [admin] puede denegar permisos");
        }
        
        return response;
    }
    
    
    

    /*    SENTENCIAS SSL*/
    
    
    
    
    
    
    
    
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
    
    
    private String analisisDeclaracionColumnas(Nodo nodo){
        String response = null;
        String idTable = nodo.getHijo(0).valor;
        Nodo lcampo = nodo.getHijo(1);

        ArrayList<String> items = new ArrayList<>();
        int index = 1;
        
        for(Nodo hijo : lcampo.hijos){
            Nodo nieto = hijo.getHijo(index);
            
            if(items.contains(nieto.valor)){
                
                response = Error.lenguaje(codigo, 
                                    "USQL", 
                                    this.cadUsql, 
                                    "Semantico",
                                    nieto.row, 
                                    nieto.col, 
                                    "Multiple declaracion de la columna [" + nieto + "]."); 
                
                return response;
            }
            items.add(nieto.valor);
        }
        
        
        boolean pk = false;
        String idPk = "";
        
        //Para cada campo evaluar los complementos
        for(Nodo columna : lcampo.hijos){
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
        
        return response;
    }
    
    private String analisisDeclaracionAtributos(Nodo nodo){
        String response = null;
        Nodo latr = nodo.getHijo(1);

        ArrayList<String> items = new ArrayList<>();
        int index = 1;
        
        for(Nodo atr : latr.hijos){
            Nodo idAtr = atr.getHijo(index);
            
            if(items.contains(idAtr.valor)){
                
                response = Error.lenguaje(codigo, 
                                    "USQL", 
                                    this.cadUsql, 
                                    "Semantico",
                                    idAtr.row, 
                                    idAtr.col, 
                                    "Multiple declaracion del atributo [" + idAtr + "]."); 
                
                return response;
            }
            items.add(idAtr.valor);
        }
        
        return response;
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
        return null;
    }
}
