package servidorfisql.interpretes;

import java.util.ArrayList;
import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;
import servidorfisql.server.manejador.Metodo;
import servidorfisql.server.manejador.Objeto;

/**
 *
 * @author jorge
 */
public class EjecucionUSQL {
    
    
    public static String ejecutar(Nodo usqlSent){
        
        String response = null;
        
        switch(usqlSent.token){
                /*   SENTENCIAS DDL   */
                case "CREATE_DB":
                    
                    String idDB = usqlSent.getHijo(0).valor;
                
                    
                    /*crear el directorio de la base de datos*/
                    String dirBD = Archivos.bbddDir + idDB;
                    String dirTables = dirBD + "/tables/";
                    
                    Archivos.crearDirectorio(dirTables);
                    
                    /*crear la base de datos en memoria*/
                    Archivos.bbdd.crearBD(idDB, dirBD, dirTables);
                    Server.actualDB = idDB;
                    
                    Consola.writeln ("Base de datos [" + idDB + "] creada exitosamente\n");
                    break;
                    
                case "CREATE_TABLE":
                    
                    String idTable = usqlSent.getHijo(0).valor;
                    
                    Archivos.bbdd.crearTabla(Server.actualDB, usqlSent);
                    Consola.writeln("Tabla [" + idTable + "] creada exitosamente.\n");
                    break;
                    
                case "CREATE_OBJECT":
                    
                    String idObj = usqlSent.getHijo(0).valor;
                    
                    Archivos.bbdd.crearObjeto(Server.actualDB, usqlSent); 
                    Objeto objeto = Archivos.bbdd.getObjeto(Server.actualDB, idObj);
                    Consola.writeln("Objeto [" + idObj + "] creado exitosamente.\n");
                    
                    EjecucionSSL.ejecutar.addObjetounico(objeto);
                    
                    break;

                case "CREATE_USER":
                    
                    String user = usqlSent.getHijo(0).valor;
                    String pass = usqlSent.getHijo(1).valor;
                    
                    Archivos.usuarios.agregarUsuario(user, pass);
                    Consola.writeln("Usuario [" + user + "] creado exitosamente.");
                    break;
                    
                case "PROC":
                case "FUNC":
                    
                    String idMet = usqlSent.getHijo(0).valor;
                    String claseMet = usqlSent.token.equals("PROC") ? "PROCEDIMIENTO" : "FUNCION";
                    
                    Archivos.bbdd.crearProc(Server.actualDB, usqlSent);
                    Consola.writeln(claseMet + " [" + idMet + "] creado exitosamente.\n");
                    break;
                    
                case "USE":
                    
                    response = usar(usqlSent);
                    break;
                    
                case "ALTER_TABLE_ADD":
                    
                    idTable = usqlSent.getHijo(0).valor;
                    Nodo lcol = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarTablaAgregar(Server.actualDB, idTable, lcol);
                    Consola.write("Se han agregado a la tabla [" + idTable + "] las columnas { ");
                    lcol.hijos.stream().forEach((col) -> {
                        Consola.append(col.getHijo(1).valor + " ");
                    });
                    Consola.append("}\n");
                    break;

                case "ALTER_TABLE_QUIT":
                    
                    idTable = usqlSent.getHijo(0).valor;
                    Nodo lid = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarTablaEliminar(Server.actualDB, idTable, lid);
                    Consola.write("Se han eliminado de la tabla [" + idTable + "] las columnas { ");
                    lid.hijos.stream().forEach((Nodo id) -> {
                        Consola.append(id.valor + " ");
                    });
                    Consola.append("}\n");
                    break;
                    
                case "ALTER_OBJECT_ADD":
                    
                    String idObject = usqlSent.getHijo(0).valor;
                    Nodo latr = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarObjetoAgregar(Server.actualDB, idObject, latr);
                            
                    Consola.write("Se han agregado al objeto [" + idObject + "] los atributos { ");
                    latr.hijos.stream().forEach((atr) -> {
                        Consola.append(atr.getHijo(0).valor + " " + atr.getHijo(1).valor + "  ");
                    });
                    Consola.append("}\n");
                    break;

                case "ALTER_OBJECT_QUIT":
                    
                    idObject = usqlSent.getHijo(0).valor;
                    lid = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarObjetoEliminar(Server.actualDB, idObject, lid);
                    Consola.write("Se han eliminado del objeto [" + idObject + "] los atributos { ");
                    lid.hijos.stream().forEach((id) -> {
                        Consola.append(id.valor + " ");
                    });
                    Consola.append("}\n");
                    break;
                    
                case "ALTER_USER":
                    
                    String idUser = usqlSent.getHijo(0).valor;
                    String passUser = usqlSent.getHijo(1).valor;
                    
                    Archivos.usuarios.modificarUsuario(idUser, passUser);
                    Consola.writeln("Se ha modificado el password del usuario [" + idUser + "] exitosamente.\n");
                    break;
                    
                    
                    
                case "DELETE":
                    
                    String usqlObject = usqlSent.getHijo(0).valor;
                    idObject =  usqlSent.getHijo(1).valor;
                    
                    switch(usqlObject){
                        case "DDBB":
                            
                            Archivos.bbdd.eliminarBD(idObject);
                            Consola.writeln("Se ha eliminado la Base de Datos [" + idObject + "]\n");
                            break;
                            
                        case "USER":
                            
                            //Eliminar todos los permisos para el usuario
                            Archivos.bbdd.denegarPermisosParaUsuario(idObject);
                            //Eliminar el usuario
                            Archivos.usuarios.eliminarUsuario(idObject);
                            Consola.writeln("Se ha eliminado el usuario [" + idObject + "]\n");
                            break;
                            
                        case "TABLE":
                            
                             //Eliminar tabla
                            Archivos.bbdd.eliminarTabla(Server.actualDB, idObject);
                            Consola.writeln("Se ha eliminado la tabla [" + idObject + " ]"
                                    + "de la base de datos [" + Server.actualDB + "]\n");
                            break;
                            
                        case "OBJECT":
                            
                             //Eliminar objeto
                            Archivos.bbdd.eliminarObjeto(Server.actualDB, idObject);
                            Consola.writeln("Se ha eliminado el objeto [" + idObject + " ]"
                                    + "de la base de datos [" + Server.actualDB + "]");
                            break;
                            
                        case "PROC":
                        case "FUNC":
                            
                            String nameAux = usqlSent.token.equals("PROC") ? "el procedimiento" : "la funcion";
                            
                             //Eliminar metodo
                            Archivos.bbdd.eliminarMetodo(Server.actualDB, idObject);
                            Consola.writeln("Se ha eliminado " + nameAux + "  [" + idObject + " ]"
                                    + "de la base de datos [" + Server.actualDB + "]\n");
                            break;
                    }
                    
                    break;
                    
                /*   SENTENCIAS DML*/
                case "INSERT":
                    response = insert(usqlSent);
                    break;
                case "SELECT":
                    response = construirPaquetePlycsSelect(select(usqlSent), false);
                    break;
                case "UPDATE":
                    update(usqlSent);
                    break;
                case "DELETE_FROM_TABLE":
                    delete(usqlSent);
                    break;
                    
                    
                /*   SENTENCIAS DCL   */
                case "GRANT":
                    
                    idUser = usqlSent.getHijo(0).valor;
                    idDB = usqlSent.getHijo(1).valor;
                    String dbObj = usqlSent.getHijo(2).valor;
                    
                    if(dbObj.equals("*")){
                        Archivos.bbdd.otorgarPermisosEnTodo(idDB, idUser);
                        Consola.writeln("Se ha otorgado permisos al usuario [" + idUser + "] "
                                + "en todos los objetos de la Base de Datos [" + idDB + "]");
                    }else{
                        Archivos.bbdd.otorgarPermisosSiExiste(idDB, dbObj, idUser);
                        Consola.writeln("Se han otorgado los permisos al usuario [" + idUser + "] "
                                + "en el elemento [" + dbObj + "] de la Base de Datos [" + idDB + "]");
                    }
                    
                    
                    break;
                    
                case "DENY":
                    
                    idUser = usqlSent.getHijo(0).valor;
                    idDB = usqlSent.getHijo(1).valor;
                    dbObj = usqlSent.getHijo(2).valor;
                    
                    if(dbObj.equals("*")){
                        Archivos.bbdd.denegarPermisosEnTodo(idDB, idUser);
                        Consola.writeln("Se ha denegado permisos al usuario [" + idUser + "] "
                                + "en todos los objetos de la Base de Datos [" + idDB + "]");
                    }else{
                        Archivos.bbdd.denegarPermisosSiExiste(idDB, dbObj, idUser);
                        Consola.writeln("Se han denegado los permisos al usuario [" + idUser + "] "
                                + "en el elemento [" + dbObj + "] de la Base de Datos [" + idDB + "]");
                    }
                    
                    break;
                    
                 case "CALL":
                    String idMetodo = usqlSent.getHijo(0).valor;
                    EjecucionSSL.ejecutar.initExecute(idMetodo);
                    
                    break;
                    
                case "PRINT":
                    Nodo exp = usqlSent.getHijo(0);
                    EjecucionSSL.ejecutar.executeCode(exp);
                    
                    break;
                    
                case "BACKUP_USQLDUMP":
                    response = backup_usqldump(usqlSent);
                    break;
                    
                case "BACKUP_COMPLETO":
                    break;
                    
                default:
            }
        
        return response;
    }
    
    public static String ejecutarReporte(Nodo select){
        return construirPaquetePlycsSelect(select(select), true);
    }
    
    
    private static String usar(Nodo nodo){
        String response = null;
        String idDB;
        
        idDB = nodo.getHijo(0).valor;

        Server.actualDB = idDB;
        Consola.writeln("Usando Base de Datos [" + idDB + "]\n");
        
        //Construir tabla de simbolos
        HashMap<String, Objeto> objetos = Archivos.bbdd.getObjetos(idDB, Server.user);
        HashMap<String, Metodo> metodos = Archivos.bbdd.getMetodos(idDB, Server.user);
        
        //Limpiar tablas
        EjecucionSSL.ejecutar.tablaGlobal.clear();
        EjecucionSSL.ejecutar.tablitaObjetos.clear();
        EjecucionSSL.ejecutar.tablaLocal.clear();
        
        EjecucionSSL.ejecutar.addObjetos(objetos);
        EjecucionSSL.ejecutar.addVariables_Funciones(metodos);
        
        
        
        return response;
    }
    
    
    private static String insert(Nodo nodo){
        Nodo id = nodo.getHijo(0);
        Nodo lcol = nodo.getHijo(1);
        Nodo lexp = nodo.getHijo(2);
        
        String response = null;
        
        //Insertar normal
        if(lcol.hijos.isEmpty()){
            response = Archivos.bbdd.insertarEnTabla(Server.actualDB, id.valor, lexp);
        }else{
            response = Archivos.bbdd.insertarEnTabla(Server.actualDB, id.valor, lcol, lexp);
        }

        return response;
            
    }
    
    
    private static ArrayList<HashMap<String, String>> select(Nodo select){
        
        Nodo lcol = select.getHijo(0);
        Nodo from = select.getHijo(1);
        Nodo where = select.hijos.size() == 3 ? select.getHijo(2) : null;
        
        ArrayList<Nodo> tablas = new ArrayList<>();
        
        for(Nodo tabla : from.hijos){
            tablas.add(Archivos.bbdd.getAstRows(Server.actualDB, tabla.valor));
        }
        
        ArrayList<HashMap<String, Simbolo>> tablaTemporal = construirTablaTemp(tablas);
        ArrayList<HashMap<String, String>> tablaResultado = new ArrayList<>();
        ArrayList<String> columnasSeleccionables = null;
        
        if(!lcol.token.equals("*")){
            columnasSeleccionables = new ArrayList<>();
            
            for(Nodo col : lcol.hijos){
                String table = col.getHijo(0).valor;
                String column = col.getHijo(1).valor;
                
                columnasSeleccionables.add(table + "." + column);
            }
        }
                
        //Filtarar valores en tabla resultado
        if(where != null){
            
            Nodo cond = where.getHijo(0);
            HashMap<String, String> filaResultado;
            
            for(HashMap<String, Simbolo> filaTemporal : tablaTemporal){
                
                EjecucionSSL.ejecutar.hashtemp = filaTemporal;
                
                if(EjecucionSSL.ejecutar.Expre(cond).value.equals("1")){
                   
                    filaResultado = new HashMap<>();
                    
                    if(lcol.token.equals("*")){
                
                        for(String colName: filaTemporal.keySet()){
                            filaResultado.put(colName, filaTemporal.get(colName).value);
                        }
                        tablaResultado.add(filaResultado);
                        
                    }else{
                        for(String colName: filaTemporal.keySet()){
                            if(columnasSeleccionables.contains(colName))
                                filaResultado.put(colName, filaTemporal.get(colName).value);
                        }
                        tablaResultado.add(filaResultado);
                    }   
                }
            }
            
            if(where.hijos.size() == 2){
                Nodo order = where.getHijo(1);
                String orderCol = order.getHijo(0).valor + "." + order.getHijo(1).valor;
                String orderMode = order.getHijo(1).token;
                
                //ordenar filas
            }
            
        }else{
            
            HashMap<String, String> filaResultado;
            for(HashMap<String, Simbolo> filaTemporal : tablaTemporal){
                filaResultado = new HashMap<>();
                
                for(String colName: filaTemporal.keySet()){
                    filaResultado.put(colName, filaTemporal.get(colName).value);
                }
                tablaResultado.add(filaResultado);
            }
        }
        
        
        //Construir cadenas de respuesta con base en la tablaResultado
        
        return tablaResultado;
    }
    
    private static String construirPaquetePlycsSelect(ArrayList<HashMap<String, String>> select, boolean reporte){
        String response = "";
        
        if(reporte){
            
            
            //Encabezado
            response += "    <tr>\n";
            
            HashMap<String, String> encabezados = select.get(0);
            for(String colName : encabezados.keySet()){
                response += "        <th>" + colName + "</th>\n";
            }
            
            response += "    </tr>\n";
            
            //Contenido
            for(HashMap<String, String> fila : select){
                response += "    <tr>\n";
                
                for(String colName : fila.keySet()){
                    response += "        <td>" + fila.get(colName) + "</td>\n";
                }
                
                response += "    </tr>\n";
            }
        }else{
            for(HashMap<String, String> fila : select){
                response += "        [\n";
                
                for(String colName : fila.keySet()){
                    response += "            \"" + colName + "\" => \"" + fila.get(colName) + "\",\n";
                }
                
                response += "        ],\n";
            }
        }
        
        
        return response;
    }
    
    
    
    private static String update(Nodo update){
        String response = null;
        
        Nodo idTable = update.getHijo(0);
        Nodo lcol = update.getHijo(1);
        Nodo lexp = update.getHijo(2);
        Nodo cond = update.hijos.size() == 4 ? update.getHijo(3) : null;
        Nodo rows = new Nodo("RowsFile");
        Nodo row;
        
        ArrayList<Nodo> tablas = new ArrayList<>();
        tablas.add(Archivos.bbdd.getAstRows(Server.actualDB, idTable.valor));
        ArrayList<HashMap<String, Simbolo>> tablaTemporal = construirTablaTemp(tablas);
        
        HashMap<String, String> columnas = new HashMap<>();
        for(int i = 0; i < lcol.hijos.size(); i++){
            Nodo col = lcol.getHijo(i);
            String val = EjecucionSSL.ejecutar.Expre(lexp.getHijo(i)).value;
            columnas.put(idTable.valor + "." + col.valor, val);
        }
        
        if(cond != null){
            
            for(HashMap<String, Simbolo> filaTemporal : tablaTemporal){
                
                EjecucionSSL.ejecutar.hashtemp = filaTemporal;
                if(EjecucionSSL.ejecutar.Expre(cond).value.equals("1")){
                    for(String colName: filaTemporal.keySet()){
                        if(columnas.containsKey(colName)){
                            filaTemporal.get(colName).value = columnas.get(colName);
                        }
                    }
                }
            }
            
        }else{
            for(HashMap<String, Simbolo> filaTemporal : tablaTemporal){
                
                for(String colName: filaTemporal.keySet()){
                    if(columnas.containsKey(colName)){
                        filaTemporal.get(colName).value = columnas.get(colName);
                    }
                }

            }
        }
        

        for(HashMap<String, Simbolo> filaTemporal : tablaTemporal){
            row = new Nodo("row");
            for(String colName : filaTemporal.keySet()){
                Nodo cn = new Nodo(colName);
                cn.agregarHijo(new Nodo("CAD", filaTemporal.get(colName).value));
                row.agregarHijo(cn);
            }
            rows.agregarHijo(row);
        }
        
        Archivos.bbdd.setAstRows(Server.actualDB, idTable.valor, rows);
        
        return response;
    }
    
    private static String delete(Nodo delete){
        String response = null;
        
        Nodo idTable = delete.getHijo(0);
        Nodo cond = delete.hijos.size() == 2 ? delete.getHijo(1) : null;
        Nodo rows = new Nodo("RowsFile");
        Nodo row;
        
        ArrayList<Nodo> tablas = new ArrayList<>();
        tablas.add(Archivos.bbdd.getAstRows(Server.actualDB, idTable.valor));
        ArrayList<HashMap<String, Simbolo>> tablaTemporal = construirTablaTemp(tablas);
        
        if(cond != null){
            
            ArrayList<Integer> indices = new ArrayList<>();
            
            for(int i = 0; i < tablaTemporal.size(); i++){
                
                HashMap<String, Simbolo> filaTemporal = tablaTemporal.get(i);  
                EjecucionSSL.ejecutar.hashtemp = filaTemporal;
                
                if(EjecucionSSL.ejecutar.Expre(cond).value.equals("1")){
                    indices.add(i);
                }
            }
            
            for(int i : indices)
                tablaTemporal.remove(i);
        }else{
            tablaTemporal.clear();
        }
        
        for(HashMap<String, Simbolo> filaTemporal : tablaTemporal){
            row = new Nodo("row");
            for(String colName : filaTemporal.keySet()){
                Nodo cn = new Nodo(colName);
                cn.agregarHijo(new Nodo("CAD", filaTemporal.get(colName).value));
                row.agregarHijo(cn);
            }
            rows.agregarHijo(row);
        }
        
        Archivos.bbdd.setAstRows(Server.actualDB, idTable.valor, rows);
        
        return response;
    }
    
    private static ArrayList<HashMap<String, Simbolo>> temp =  new ArrayList<>();
    
    private static ArrayList<HashMap<String, Simbolo>> construirTablaTemp(ArrayList<Nodo> tablas){
        temp.clear();
        
        metodo(tablas, new HashMap<>());
        
        return temp;
    }
    
    private static void metodo(ArrayList<Nodo> tablas, HashMap<String, Simbolo> fila){
        
        for(int i =0; i < tablas.size(); i++){
            Nodo actual = tablas.get(i);
            ArrayList<Nodo> copia = copiarArrayList(tablas);///<<<<<
            copia.remove(i);
            String nombre = actual.valor;//<<<
            
            for(Nodo row : actual.hijos){
                
                HashMap<String, Simbolo> copiaFila = copiarHash(fila);
                
                for(Nodo col : row.hijos){
                    Simbolo s = new Simbolo();
                    s.value = col.getHijo(0).valor;
                    s.value = Archivos.bbdd.getTipoColumna(Server.actualDB, nombre, col.valor);
                    copiaFila.put(nombre + "." + col.valor, s);
                }
                
                if(copia.size() > 0){
                    metodo(copia, copiaFila);
                }else{
                    temp.add(copiaFila);
                }
            }
        }
    }
    
    
    private static HashMap<String, Simbolo> copiarHash(HashMap<String, Simbolo> hash){
        HashMap<String, Simbolo> nueva = new HashMap<>();
        
        for(String clave : hash.keySet()){
            Simbolo s = hash.get(clave);
            Simbolo nuevo = new Simbolo();
            
            nuevo.value = s.value;
            nuevo.tipe = s.tipe;
            
            nueva.put(clave, nuevo);
        }
        
        return nueva;
    }
    
    
    private static ArrayList<Nodo> copiarArrayList(ArrayList<Nodo> nodos){
        ArrayList<Nodo> nueva = new ArrayList<>();
        
        for(Nodo nodo : nodos){
            nueva.add(clonar(nodo));
        }
        
        return nueva;
    }
    
    private static Nodo clonar(Nodo nodo){
        Nodo nuevo = new Nodo();
        
        nuevo.token = nodo.token;
        nuevo.valor = nodo.valor;
        
        for(Nodo hijo : nodo.hijos){
            nuevo.agregarHijo(clonar(hijo));
        }
        
        return nuevo;
    }

    private static String backup_usqldump(Nodo nodo) {

        Nodo idDB = nodo.getHijo(0);
        Nodo idFile = nodo.getHijo(1);
        String fileName = Archivos.bbddDir + idDB.valor + "/" + idFile.valor + ".udmp";
        String backup = Archivos.bbdd.getBackUpUsqlDump(idDB.valor);
        
        Archivos.escribirArchivo(fileName, backup);
        
        
        
        return backup;
    }
    
    
}
