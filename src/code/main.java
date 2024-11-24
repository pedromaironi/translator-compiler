/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import static code.Tokens.Numero;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.JFileChooser;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 *
 * @author Charly Ponce
 */
public class main extends javax.swing.JFrame {

    /**
     * Creates new form FrmPrincipal
     */
    
    private String erroresSemanticos = "";
    private String traducido = "";
    private String intermedio = "";
    private String ambitoActual = ""; 
    HashMap<String, String> variablesDeclaradas = new HashMap<>(); 
    
    public main() {
        initComponents();
        this.setLocationRelativeTo(null);
    }
    
    
    public void RunCprogram(){
        // C code stored in a Java string variable
        String cCode = traducido;

        // Create a temporary file to store the C code
        File tempFile;
        try {
            tempFile = File.createTempFile("temp", ".c");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(cCode);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Compile the temporary C file
        String outputPath = tempFile.getAbsolutePath().replace(".c", ".exe");
        String compileCommand = "gcc " + tempFile.getAbsolutePath() + " -o " + outputPath;
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCommand);
            compileProcess.waitFor();

            // Check if compilation was successful
            if (compileProcess.exitValue() != 0) {
                System.err.println("Compilation failed");
                return;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return;
        }

        // Execute the compiled C program by explicitly opening CMD
        try {
            String executeCommand = "cmd /c start cmd.exe /K \"" + outputPath + "\"";
            Process executeProcess = Runtime.getRuntime().exec(executeCommand);

            // Optionally, read output or error streams if needed
            BufferedReader reader = new BufferedReader(new InputStreamReader(executeProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Get the exit code
            int exitCode = executeProcess.waitFor();
            System.out.println("C program exited with code: " + exitCode);

            // Clean up temporary files
            tempFile.delete();
            new File(outputPath).delete();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    
    }
    
    
    public void CodigoIntermedio() throws IOException{
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        String cierreFuncion = "";
        
        intermedio = "";
        
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                txtCodigoIntermedio.setText(intermedio);
                return;
            }
            
            switch (token) {
                case Linea:
                    intermedio += "\n";
                    break;
                case Comillas:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Cadena:
                    intermedio += "String ";
                    break;
                case T_dato:
                    switch(lexer.lexeme){
                        case "entero":
                            intermedio += "variableInt ";
                            break;
                        case "caracter":      
                            intermedio += "variableChar ";
                            break;
                        case "largo":      
                            intermedio += "variableLong ";
                            break;
                        case "decimal":      
                            intermedio += "variableFloat ";
                            break;
                        case "doble":      
                            intermedio += "variableDouble ";
                            break;    
                    }
                    break;
                case If:
                    cierreFuncion = "If";
                    intermedio += "inicioIf ";
                    break;
                case Else:
                    intermedio += "inicioElse ";
                    cierreFuncion = "Else";
                    break;
                case Do:
                    intermedio += "inicioDo ";
                    cierreFuncion = "Do";
                    break;
                case While:
                    intermedio += "inicioWhile ";;
                    cierreFuncion = "While";
                    break;
                case For:
                    cierreFuncion = "For";
                    intermedio += "inicioFor ";;
                    break;
                case Igual:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Suma:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Resta:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Multiplicacion:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Division:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Op_logico:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Op_incremento:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Op_relacional:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Op_atribucion:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Op_booleano:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Parentesis_a:
                    intermedio += lexer.lexeme;
                    break;
                case Parentesis_c:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Llave_a:
                    //intermedio += lexer.lexeme + " ";
                    break;
                case Llave_c:
                    intermedio += "cierre" + cierreFuncion;
                    cierreFuncion = "Main";
                    break;
                case Corchete_a:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Corchete_c:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Main:
                    intermedio = "";
                    intermedio += "inicioMain ";
                    cierreFuncion = "Main";
                    break;
                case P_coma:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Identificador:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Numero:
                    intermedio += lexer.lexeme + " ";
                    break;
                case NumeroDecimal:
                    intermedio += lexer.lexeme + " ";
                    break;
                case Print:
                    intermedio += "Printf ";
                    break;
                case String_lit:
                    intermedio += lexer.lexeme + " ";
                    break;
                default:
                    break;
            }
        }
        
    }
    
    
    
    
    public void Traductor2() throws IOException{
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        
        int tamano;
        traducido = "#include <stdio.h>\n\n";
        
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                txtCodigoTraducido.setText(traducido);
                return;
            }
            switch (token) {
                case Linea:
                    traducido += "\n";
                    break;
                case Comillas:
                    traducido += lexer.lexeme + " ";
                    break;
                case Cadena:
                    traducido += "String ";
                    break;
                case T_dato:
                    switch(lexer.lexeme){
                        case "entero":
                            traducido += "int ";
                            break;
                        case "caracter":      
                            traducido += "char ";
                            break;
                        case "largo":      
                            traducido += "long ";
                            break;
                        case "decimal":      
                            traducido += "float ";
                            break;
                        case "doble":      
                            traducido += "double ";
                            break;    
                    }
                    break;
                case If:
                    traducido += "if ";
                    break;
                case Else:
                    traducido += "else ";
                    break;
                case Do:
                    traducido += "do ";
                    break;
                case While:
                    traducido += "while ";;
                    break;
                case For:
                    traducido += "for ";;
                    break;
                case Igual:
                    traducido += lexer.lexeme + " ";
                    break;
                case Suma:
                    traducido += lexer.lexeme + " ";
                    break;
                case Resta:
                    traducido += lexer.lexeme + " ";
                    break;
                case Multiplicacion:
                    traducido += lexer.lexeme + " ";
                    break;
                case Division:
                    traducido += lexer.lexeme + " ";
                    break;
                case Mod:
                    traducido += lexer.lexeme + " ";
                    break;
                case Op_logico:
                    traducido += lexer.lexeme + " ";
                    break;
                case Op_incremento:
                    traducido += lexer.lexeme + " ";
                    break;
                case Op_relacional:
                    traducido += lexer.lexeme + " ";
                    break;
                case Op_atribucion:
                    traducido += lexer.lexeme + " ";
                    break;
                case Op_booleano:
                    traducido += lexer.lexeme + " ";
                    break;
                case Parentesis_a:
                    traducido += lexer.lexeme;
                    break;
                case Parentesis_c:
                    traducido += lexer.lexeme + " ";
                    break;
                case Llave_a:
                    traducido += lexer.lexeme + " ";
                    break;
                case Llave_c:
                    traducido += lexer.lexeme + " ";
                    break;
                case Corchete_a:
                    traducido += lexer.lexeme + " ";
                    break;
                case Corchete_c:
                    traducido += lexer.lexeme + " ";
                    break;
                case Main:
                    traducido += "main ";
                    break;
                case P_coma:
                    traducido += lexer.lexeme + " ";
                    break;
                case Identificador:
                    tamano = traducido.length();
                    if (tamano >= 5 && 
                        Character.toLowerCase(traducido.charAt(tamano - 7)) == 'p' &&
                        Character.toLowerCase(traducido.charAt(tamano - 6)) == 'r' &&
                        Character.toLowerCase(traducido.charAt(tamano - 5)) == 'i' &&
                        Character.toLowerCase(traducido.charAt(tamano - 4)) == 'n' &&
                        Character.toLowerCase(traducido.charAt(tamano - 3)) == 't' &&
                        Character.toLowerCase(traducido.charAt(tamano - 2)) == 'f') {
                      // Penultimate character is part of "printf" (case-insensitive)
                      traducido += "\"%" + "d " + "\", ";
                      traducido += lexer.lexeme + " ";
                    } else {
                      traducido += lexer.lexeme + " ";
                    }
                    break;
                case Print:
                    traducido += "printf";
                    break;
                case Numero:
                    traducido += lexer.lexeme + " ";
                    break;
                case NumeroDecimal:
                    traducido += lexer.lexeme + " ";
                    break;
                case String_lit:
                    tamano = traducido.length();
                    
                    if (tamano >= 5 && 
                        Character.toLowerCase(traducido.charAt(tamano - 7)) == 'p' &&
                        Character.toLowerCase(traducido.charAt(tamano - 6)) == 'r' &&
                        Character.toLowerCase(traducido.charAt(tamano - 5)) == 'i' &&
                        Character.toLowerCase(traducido.charAt(tamano - 4)) == 'n' &&
                        Character.toLowerCase(traducido.charAt(tamano - 3)) == 't' &&
                        Character.toLowerCase(traducido.charAt(tamano - 2)) == 'f') {
                      // Penultimate character is part of "printf" (case-insensitive)
                      traducido += lexer.lexeme + " ";
                      Tokens siguienteToken = lexer.yylex();
                      if (siguienteToken != null && siguienteToken == Tokens.Identificador){
                        traducido += ", " + lexer.lexeme;
                      }else{
                          traducido += lexer.lexeme + " ";
                      }
                      
                    } else {
                      traducido += lexer.lexeme + " ";
                    }
                    break;
                default:
                    break;
            }
        }
        
    }
    
    
        
    
    public void Traductor() throws IOException{
        String codigofinal = "";
        StringBuilder codigoTraducido = new StringBuilder();
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        int linea = 1;
        String ambitoActual = "otro";

        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                break;
            }

            switch (token) {
                case Linea:
                    codigoTraducido.append("\n");
                    linea++;
                    break;
                case T_dato:
                    String tipoDato = lexer.lexeme;
                    Tokens nextToken = lexer.yylex();
                    if (nextToken == Tokens.Identificador) {
                        String identificador = lexer.lexeme;
                        // Agregar la declaración de variable en el código traducido
                        codigoTraducido.append(tipoDato).append(" ").append(identificador);
                        // Verificar si hay una asignación de valor a la variable
                        Tokens tokenAsignacion = lexer.yylex();
                        if (tokenAsignacion == Tokens.Igual) {
                            codigoTraducido.append(" = ");
                            // Agregar el valor asignado a la variable en el código traducido
                            Tokens tipoValor = lexer.yylex();
                            if (tipoValor == Tokens.Numero || tipoValor == Tokens.NumeroDecimal) {
                                codigoTraducido.append(lexer.lexeme);
                            } else if (tipoValor == Tokens.Cadena) {
                                codigoTraducido.append("\"").append(lexer.lexeme).append("\"");
                            } else if (tipoValor == Tokens.Identificador) {
                                codigoTraducido.append(lexer.lexeme);
                            }
                        }
                        codigoTraducido.append(";\n");
                    }
                    break;
                case Identificador:
                    // Agregar el identificador al código traducido
                    codigoTraducido.append(lexer.lexeme);
                    // Verificar si hay una asignación de valor a la variable
                    Tokens tokenAsignacion = lexer.yylex();
                    if (tokenAsignacion == Tokens.Igual) {
                        codigoTraducido.append(" = ");
                        // Agregar el valor asignado a la variable en el código traducido
                        Tokens tipoValor = lexer.yylex();
                        if (tipoValor == Tokens.Numero || tipoValor == Tokens.NumeroDecimal) {
                            codigoTraducido.append(lexer.lexeme);
                        } else if (tipoValor == Tokens.Cadena) {
                            codigoTraducido.append("\"").append(lexer.lexeme).append("\"");
                        } else if (tipoValor == Tokens.Identificador) {
                            codigoTraducido.append(lexer.lexeme);
                        }
                    }
                    codigoTraducido.append(";\n");
                    break;
                case Llave_a:
                    // Agregar llave de apertura al código traducido
                    codigoTraducido.append("{\n");
                    ambitoActual = "main";
                    break;
                case Llave_c:
                    // Agregar llave de cierre al código traducido
                    codigoTraducido.append("}\n");
                    ambitoActual = "otro";
                    break;
                case Main:
                    // Si se encuentra la función main, traducir a la estructura de la función main en Java
                    codigoTraducido.append("public static void main(String[] args) {\n");
                    ambitoActual = "main";
                    break;
                default:
                    break;
            }
        }
        
        codigofinal = codigoTraducido.toString();
        txtCodigoTraducido.setText(codigofinal);

    }
    
    
    
    
    public void analizarSemantica() throws IOException {
        // Inicialización de variables
        ambitoActual = "";
        erroresSemanticos = "";
        variablesDeclaradas.clear();
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        boolean seEncontraronErrores = false; // Variable para controlar si se encontraron errores
        int linea = 1; // Contador de línea
        String ambitoActual = "otro"; // Ámbito actual (main o otro)

        // Análisis léxico y semántico
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                break;
            }

            switch (token) {
                case Linea:
                    linea++;
                    break;
                case Cadena:
                    Tokens nexttToken = lexer.yylex();
                    if (nexttToken == Tokens.Identificador) {
                        String identificadorr = lexer.lexeme;
                        variablesDeclaradas.put(identificadorr, "Cadena");
                    }
                    break;
                case T_dato:
                    String tipoDato = lexer.lexeme;
                    Tokens nextToken = lexer.yylex();
                    if (nextToken == Tokens.Identificador) {
                        String identificador = lexer.lexeme;
                        // Verificar si la variable se declaro dentro de main o no
                        if (ambitoActual.equals("main")) {
                            //validar si la variable se ha declarado mas de una vez
                            if (variablesDeclaradas.containsKey(identificador)) {
                            erroresSemanticos += " Error: La variable '" + identificador + "' ya ha sido declarada en el mismo bloque de codigo (línea " + linea + ")\n\n";
                            seEncontraronErrores = true;
                        } else {
                            variablesDeclaradas.put(identificador, tipoDato);
                            // Verificar si hay un valor asignado a la variable y que este sea el corecto
                            Tokens tokenAsignacion = lexer.yylex();
                            if (tokenAsignacion == Tokens.Igual) {
                                Tokens tipoValor = lexer.yylex();
                                if ((tipoDato.equals("entero") && tipoValor == Tokens.NumeroDecimal) ||
                                    (tipoDato.equals("decimal") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal) || 
                                        (tipoDato.equals("doble") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal)){
                                    erroresSemanticos += " Error: Tipo de dato esta incorrecto para la variable '" + identificador + "' (línea " + linea + ")\n\n";
                                    seEncontraronErrores = true;
                                }
                            }
                        }
                        } else {
                            erroresSemanticos += " Error: Variable '" + identificador + "' declarada fuera de 'main ' INICIO (línea " + linea + ")\n\n";
                            seEncontraronErrores = true;
                        }
                    }
                    break;
                case Identificador:
                    String identificador = lexer.lexeme;
                    // Verificar si la variable no ha sido declarada
                    if (!variablesDeclaradas.containsKey(identificador)) {
                        erroresSemanticos += " Error: Variable no declarada '" + identificador + "' (línea " + linea + ")\n";
                        seEncontraronErrores = true;
                    } else {
                        Tokens tokenAsignacion = lexer.yylex();
                        // Verificar si hay un valor asignado a la variable y que este sea el corecto
                        if (tokenAsignacion == Tokens.Igual) {
                            String tipoDatoVariable = variablesDeclaradas.get(identificador);
                            Tokens tipoValor = lexer.yylex();
                            if ((tipoDatoVariable.equals("entero") && tipoValor == Tokens.NumeroDecimal) ||
                                (tipoDatoVariable.equals("decimal") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal) ||
                                (tipoDatoVariable.equals("doble") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal)) {
                                erroresSemanticos += " Error: Tipo de dato incorrecto para la variable '" + identificador + "' (línea " + linea + ")\n";
                                seEncontraronErrores = true;
                            }
                        }
                    }
                    break;
                case Llave_a:
                    ambitoActual = "main";
                    break;
                case Llave_c:
                    ambitoActual = "otro";
                    break;
                default:
                    break;
            }
        }

        // Verificación final de errores y mensajes
         if (!seEncontraronErrores) {
            erroresSemanticos += "Semántica correcta. \n";
        }

         // Mostrar los errores semánticos
        txtAnalizarSem.setText(erroresSemanticos);
    }

    
    private void analizarSintactico(){
        // TODO add your handling code here:
        String ST = txtResultado.getText();
        Sintax s = new Sintax(new code.LexerCup(new StringReader(ST)));
        
        try {
            s.parse();
            txtAnalizarSin.setText("Analisis realizado correctamente");
            txtAnalizarSin.setForeground(new Color(25, 111, 61));
        } catch (Exception ex) {
            Symbol sym = s.getS();
            txtAnalizarSin.setText("Error de sintaxis. Linea: " + (sym.right + 1) + " Columna: " + (sym.left + 1) + ", Texto: \"" + sym.value + "\"");
            txtAnalizarSin.setForeground(Color.red);
        }
    }
    
    private void analizarLexico() throws IOException{
        int cont = 1;
        
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        String resultado = "Linea " + cont + "\t\tToken\n";
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                txtAnalizarLex.setText(resultado);
                return;
            }
            switch (token) {
                case Linea:
                    cont++;
                    resultado += "Linea " + cont + "\n";
                    break;
                case Comillas:
                    resultado += "  Comillas\t\t" + lexer.lexeme + "\n";
                    break;
                case Cadena:
                    resultado += "  Tipo de dato\t" + lexer.lexeme + "\n";
                    break;
                case T_dato:
                    resultado += "  Tipo de dato\t\t" + lexer.lexeme + "\n";
                    break;
                case If:
                    resultado += "  Reservada if\t" + lexer.lexeme + "\n";
                    break;
                case Else:
                    resultado += "  Reservada else\t" + lexer.lexeme + "\n";
                    break;
                case Do:
                    resultado += "  Reservada do\t" + lexer.lexeme + "\n";
                    break;
                case While:
                    resultado += "  Reservada while\t" + lexer.lexeme + "\n";
                    break;
                case For:
                    resultado += "  Reservada while\t" + lexer.lexeme + "\n";
                    break;
                case Igual:
                    resultado += "  Operador igual\t" + lexer.lexeme + "\n";
                    break;
                case Suma:
                    resultado += "  Operador suma\t" + lexer.lexeme + "\n";
                    break;
                case Resta:
                    resultado += "  Operador resta\t" + lexer.lexeme + "\n";
                    break;
                case Multiplicacion:
                    resultado += "  Operador multiplicacion\t" + lexer.lexeme + "\n";
                    break;
                case Division:
                    resultado += "  Operador division\t" + lexer.lexeme + "\n";
                    break;
                case Mod:
                    resultado += "  Operador mod\t\t" + lexer.lexeme + "\n";
                    break;
                case Op_logico:
                    resultado += "  Operador logico\t" + lexer.lexeme + "\n";
                    break;
                case Op_incremento:
                    resultado += "  Operador incremento\t" + lexer.lexeme + "\n";
                    break;
                case Op_relacional:
                    resultado += "  Operador relacional\t" + lexer.lexeme + "\n";
                    break;
                case Op_atribucion:
                    resultado += "  Operador atribucion\t" + lexer.lexeme + "\n";
                    break;
                case Op_booleano:
                    resultado += "  Operador booleano\t" + lexer.lexeme + "\n";
                    break;
                case Parentesis_a:
                    resultado += "  Parentesis de apertura\t" + lexer.lexeme + "\n";
                    break;
                case Parentesis_c:
                    resultado += "  Parentesis de cierre\t" + lexer.lexeme + "\n";
                    break;
                case Llave_a:
                    resultado += "  Llave de apertura\t" + lexer.lexeme + "\n";
                    break;
                case Llave_c:
                    resultado += "  Llave de cierre\t\t" + lexer.lexeme + "\n";
                    break;
                case Corchete_a:
                    resultado += "  Corchete de apertura\t" + lexer.lexeme + "\n";
                    break;
                case Corchete_c:
                    resultado += "  Corchete de cierre\t" + lexer.lexeme + "\n";
                    break;
                case Main:
                    resultado += "  Reservada main\t" + lexer.lexeme + "\n";
                    break;
                case P_coma:
                    resultado += "  Punto y coma\t\t" + lexer.lexeme + "\n";
                    break;
                case Identificador:
                    resultado += "  Identificador\t\t" + lexer.lexeme + "\n";
                    break;
                case Print:
                    resultado += "  Reservada Printf\t" + lexer.lexeme + "\n";
                    break;
                case Numero:
                    resultado += "  Numero\t\t" + lexer.lexeme + "\n";
                    break;
                case NumeroDecimal:
                    resultado += "  NumeroDecimal\t" + lexer.lexeme + "\n";
                    break;
                case String_lit:
                    resultado += "  String literal\t\t" + lexer.lexeme + "\n";
                    break;
                case ERROR:
                    resultado += "  Simbolo no definido\n";
                    break;
                default:
                    resultado += "   " + lexer.lexeme + " \n";
                    break;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAnalizarSin = new javax.swing.JTextArea();
        btnAnalizarSin = new javax.swing.JButton();
        btnLimpiarSin = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        btnArchivo = new javax.swing.JButton();
        btnLimpiarEditor = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAnalizarSem = new javax.swing.JTextArea();
        btnAnalizarSem = new javax.swing.JButton();
        btnLimpiarSem = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtCodigoTraducido = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtCodigoIntermedio = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        btnAnalisis = new javax.swing.JButton();
        btnLimpiarGeneral = new javax.swing.JButton();
        btnEjecutar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAnalizarLex = new javax.swing.JTextArea();
        btnAnalizarLex = new javax.swing.JButton();
        btnLimpiarLex = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Análisis Sintáctico", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N

        txtAnalizarSin.setEditable(false);
        txtAnalizarSin.setColumns(20);
        txtAnalizarSin.setRows(5);
        jScrollPane3.setViewportView(txtAnalizarSin);

        btnAnalizarSin.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAnalizarSin.setText("Analizar");
        btnAnalizarSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarSinActionPerformed(evt);
            }
        });

        btnLimpiarSin.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLimpiarSin.setText("Limpiar");
        btnLimpiarSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarSinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAnalizarSin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLimpiarSin)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAnalizarSin)
                    .addComponent(btnLimpiarSin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Editor", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N

        txtResultado.setColumns(20);
        txtResultado.setRows(5);
        jScrollPane1.setViewportView(txtResultado);

        btnArchivo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnArchivo.setText("Abrir archivo");
        btnArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchivoActionPerformed(evt);
            }
        });

        btnLimpiarEditor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLimpiarEditor.setText("Limpiar");
        btnLimpiarEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarEditorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnLimpiarEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnArchivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLimpiarEditor)
                        .addGap(271, 271, 271))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Análisis Semántico", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N

        txtAnalizarSem.setEditable(false);
        txtAnalizarSem.setColumns(20);
        txtAnalizarSem.setRows(5);
        jScrollPane4.setViewportView(txtAnalizarSem);

        btnAnalizarSem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAnalizarSem.setText("Analizar");
        btnAnalizarSem.setName(""); // NOI18N
        btnAnalizarSem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarSemActionPerformed(evt);
            }
        });

        btnLimpiarSem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLimpiarSem.setText("Limpiar");
        btnLimpiarSem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarSemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnAnalizarSem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLimpiarSem)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAnalizarSem)
                    .addComponent(btnLimpiarSem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Codigo Traducido", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(711, 121));

        txtCodigoTraducido.setColumns(20);
        txtCodigoTraducido.setRows(5);
        jScrollPane5.setViewportView(txtCodigoTraducido);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Código Intermedio", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N

        txtCodigoIntermedio.setEditable(false);
        txtCodigoIntermedio.setColumns(20);
        txtCodigoIntermedio.setRows(5);
        jScrollPane6.setViewportView(txtCodigoIntermedio);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnAnalisis.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAnalisis.setText("Analizar");
        btnAnalisis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalisisActionPerformed(evt);
            }
        });

        btnLimpiarGeneral.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLimpiarGeneral.setText("Limpiar");
        btnLimpiarGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarGeneralActionPerformed(evt);
            }
        });

        btnEjecutar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnEjecutar.setText("Ejecutar");
        btnEjecutar.setEnabled(false);
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAnalisis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEjecutar, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(btnLimpiarGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(btnAnalisis)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLimpiarGeneral)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEjecutar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Análisis Léxico / Tabla de Símbolos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N

        txtAnalizarLex.setEditable(false);
        txtAnalizarLex.setColumns(20);
        txtAnalizarLex.setRows(5);
        jScrollPane2.setViewportView(txtAnalizarLex);

        btnAnalizarLex.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAnalizarLex.setText("Analizar");
        btnAnalizarLex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarLexActionPerformed(evt);
            }
        });

        btnLimpiarLex.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLimpiarLex.setText("Limpiar");
        btnLimpiarLex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarLexActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnAnalizarLex)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLimpiarLex)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiarLex)
                    .addComponent(btnAnalizarLex))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(24, 24, 24))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel4.getAccessibleContext().setAccessibleName("Analisis General");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivoActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File archivo = new File(chooser.getSelectedFile().getAbsolutePath());
        
        try {
            String ST = new String(Files.readAllBytes(archivo.toPath()));
            txtResultado.setText(ST);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnArchivoActionPerformed

    private void btnLimpiarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarLexActionPerformed
        // TODO add your handling code here:
        txtAnalizarLex.setText(null);
    }//GEN-LAST:event_btnLimpiarLexActionPerformed

    private void btnLimpiarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarSinActionPerformed
        // TODO add your handling code here:
        txtAnalizarSin.setText(null);
    }//GEN-LAST:event_btnLimpiarSinActionPerformed

    private void btnAnalizarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarLexActionPerformed
        try {
            analizarLexico();
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalizarLexActionPerformed

    private void btnAnalizarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarSinActionPerformed
        analizarSintactico();
    }//GEN-LAST:event_btnAnalizarSinActionPerformed

    private void btnLimpiarEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarEditorActionPerformed
        // TODO add your handling code here:
        txtResultado.setText(null);
    }//GEN-LAST:event_btnLimpiarEditorActionPerformed

    private void btnAnalisisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalisisActionPerformed
        // TODO add your handling code here:
        
        String palabra = "Error";
        
        try {
            analizarLexico();
            analizarSintactico();
            analizarSemantica();
            
            
            //Traductor();
            String sintactico = txtAnalizarSin.getText().toString().trim();
            String semantico = txtAnalizarSem.getText().toString().trim();
            if (sintactico.toLowerCase().contains(palabra.toLowerCase()) || semantico.toLowerCase().contains(palabra.toLowerCase())){
                txtCodigoTraducido.setText("Hay errores en el codigo, verificar los errores ya que no es posible traducir el codigo con errores.");
                txtCodigoIntermedio.setText("Hay errores en el codigo.\n No es posible generar el codigo intermedio con errores.");
            }else{
                Traductor2();
                CodigoIntermedio();
                btnEjecutar.setEnabled(true);
            }
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalisisActionPerformed

    private void btnLimpiarGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarGeneralActionPerformed
        // TODO add your handling code here:
        txtResultado.setText(null);
        txtAnalizarLex.setText(null);
        txtAnalizarSin.setText(null);
        txtAnalizarSem.setText(null);
        txtCodigoTraducido.setText(null);
        txtCodigoIntermedio.setText(null);
    }//GEN-LAST:event_btnLimpiarGeneralActionPerformed

    private void btnAnalizarSemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarSemActionPerformed
        try {
            // TODO add your handling code here:
            analizarSemantica();
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalizarSemActionPerformed

    private void btnLimpiarSemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarSemActionPerformed
        // TODO add your handling code here:
        txtAnalizarSem.setText(null);
    }//GEN-LAST:event_btnLimpiarSemActionPerformed

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        // TODO add your handling code here:
        if (!"".equals(traducido)){
            RunCprogram();
        } else{
            JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "Ingresar codigo poara poder ejecutarlo");
        }
        
    }//GEN-LAST:event_btnEjecutarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalisis;
    private javax.swing.JButton btnAnalizarLex;
    private javax.swing.JButton btnAnalizarSem;
    private javax.swing.JButton btnAnalizarSin;
    private javax.swing.JButton btnArchivo;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnLimpiarEditor;
    private javax.swing.JButton btnLimpiarGeneral;
    private javax.swing.JButton btnLimpiarLex;
    private javax.swing.JButton btnLimpiarSem;
    private javax.swing.JButton btnLimpiarSin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea txtAnalizarLex;
    private javax.swing.JTextArea txtAnalizarSem;
    private javax.swing.JTextArea txtAnalizarSin;
    private javax.swing.JTextArea txtCodigoIntermedio;
    private javax.swing.JTextArea txtCodigoTraducido;
    private javax.swing.JTextArea txtResultado;
    // End of variables declaration//GEN-END:variables
}
