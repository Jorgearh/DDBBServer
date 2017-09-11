package servidorfisql.interpretes;

/**
 *
 * @author jorge
 */
public class Error {
    
    
    public static String logico(int codigo, String clase, String descripcion){
        String error;
        
        error = "[\n" +
                "	\"paquete\": \"error\",\n" +
                "	\"validar\": " + codigo + ",\n" +
                "	\"tipo\": \"" + clase + "\",\n" +
                "	\"descripcion\": \"" + descripcion + "\"\n" +
                "	\n" +
                "]\n";
        
        return error;
    }
    
    
    public static String lenguaje(int codigo, String lenguaje, String cadena, String clase, int row, int col, String descripcion){
        String error;
        
        error = "[\n" +
                "	\"paquete\": \"error\",\n" +
                "	\"validar\": " + codigo + ",\n" +
                "	\"tipo\": \"lenguaje\",\n" +
                "	\"detalles\": [\n" +
                "		\"Lenguaje\": \"" + lenguaje + "\",\n" +
                "		\"Instruccion\": \"" + cadena + "\",\n" +
                "		\"Tipo\": \"" + clase + "\",\n" +
                "		\"Fila\": " + row + ",\n" +
                "		\"Columna\": " + col + ",\n" +
                "		\"Descripcion\": \"" + descripcion + "\"\n" +
                "	]\n" +
                "]\n";
        
        return error;
    }
    
}
