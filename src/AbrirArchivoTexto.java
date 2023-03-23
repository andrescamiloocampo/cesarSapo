
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class AbrirArchivoTexto extends JFrame implements ActionListener {

    public AbrirArchivoTexto() {
        //Para poder cerrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Se agrega un layout
        setLayout(new BorderLayout());

        //Se crea el editor de texto y se agrega a un scroll
        txp = new JTextPane();
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(txp);

        add(jsp, BorderLayout.CENTER);

        //Se crea un boton para abrir el archivo
        JButton btn = new JButton("Abrir");
        btn.addActionListener(this);
        //btn.setIcon( new ImageIcon( getClass().getResource( "Abrir.png" ) ) );

        add(btn, BorderLayout.NORTH);

        //Tamano de la ventana
        setSize(500, 500);

        //Esto sirve para centrar la ventana
        setLocationRelativeTo(null);

        //Hacemos visible la ventana
        setVisible(true);
    }

    //------------------------------Action Performed-------------------------------//
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        if (btn.getText().equals("Abrir")) {
            if (abrirArchivo == null) {
                abrirArchivo = new JFileChooser();
            }
            //Con esto solamente podamos abrir archivos
            abrirArchivo.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int seleccion = abrirArchivo.showOpenDialog(this);

            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File f = abrirArchivo.getSelectedFile();
                try {
                    String nombre = f.getName();
                    String path = f.getAbsolutePath();
                    String contenido = getArchivo(path);
                    //Colocamos en el titulo de la aplicacion el 
                    //nombre del archivo
                    this.setTitle(nombre);
                    //En el editor de texto colocamos su contenido
                    //txp.setText(texto);
                    txp.setText(contenido);

                } catch (Exception exp) {
                }
            }
        }
    }
    //-----------------------------------------------------------------------------//

    //-------------------------Se obtiene el contenido del Archivo----------------//
    public String getArchivo(String ruta) {
        FileReader fr = null;
        BufferedReader br = null;
        //Cadena de texto donde se guardara el contenido del archivo
        String contenido = "";
        String lineaToken[] = new String[200];
        try {
            //ruta puede ser de tipo String o tipo File
            fr = new FileReader(ruta);
            br = new BufferedReader(fr);

            String linea;
            int numLinea = 0;
            //Obtenemos el contenido del archivo linea por linea
            while ((linea = br.readLine()) != null) {
                contenido += linea + "\n";
                numLinea += 1;
                lineaToken = anaLex(linea);
                System.out.println("Linea: " + numLinea);
                for (int x = 0; x < lineaToken.length; x++) {
                    if (lineaToken[x] == null) {
                        break;
                    }
                    System.out.println("Token posicion " + x + ": " + lineaToken[x]);
                }
                FindErrors(lineaToken);
            }

        } catch (Exception e) {
            System.out.println(e);
        } //finally se utiliza para que si todo ocurre correctamente o si ocurre 
        //algun error se cierre el archivo que anteriormente abrimos
        finally {
            try {
                br.close();

            } catch (Exception ex) {
            }
        }
        return contenido;
    }
    //-----------------------------------------------------------------------------//

    public String[] anaLex(String lineas) {
        //----------------------------------------------------
        //Analisis lexico
        String token = ""; //almacena cada token
        String tokens[] = new String[200]; //vector para almacenar los tokens de cada l�nea
        int t = 0; //puntero de tokens
        char palabra[] = new char[200];
        boolean cad = false;
        palabra = lineas.toCharArray();
        //System.out.println(lineas);	
        for (int i = 0; i < palabra.length; i++) {
            //System.out.println(palabra[i]);

            if (palabra[i] == ' ') {
                //System.out.println("Espacio");
                //System.out.println("Posicion: "+ i);
            } else if (palabra[i] == '<') {
                if (palabra[i + 1] == '=') {
                    //System.out.println("Es el simbolo <=");
                    i = i + 1;
                    tokens[t] = "<=";
                    t += 1;
                } else if (palabra[i] == '>') {
                    //System.out.println("Es simbolo <>");
                    i = i + 1;
                    tokens[t] = "<>";
                    t += 1;
                } else {
                    //System.out.println("Es el simbolo <");
                    tokens[t] = "<";
                    t += 1;
                }
            }//else if
            else if(palabra[i] == '"' && i == palabra.length-1){
                tokens[t] = "" + palabra[i];
                t++;
            }
            else if (palabra[i] == '"' && palabra[i + 1] != ';' && i+1<palabra.length) {
                int j = i;
                tokens[t] = "" + palabra[i];
                t += 1;
                token = "";
                j += 1;
                while (Character.isDigit(palabra[j]) || Character.isLetter(palabra[j]) || palabra[j] == ' ') {
                    token += palabra[j];
                    j += 1;
                    if (j == palabra.length) {
                        break;
                    }
                }
                tokens[t] = token;
                token = "";
                t++;
                i = j - 1;
            } else if (palabra[i] == '"') {
                tokens[t] = "" + palabra[i];
                t++;
            } else if (palabra[i] == '=') {
                //System.out.println("Es el simbolo =");
                tokens[t] = "=";
                t += 1;

            }//else if
            else if (palabra[i] == '>') {
                if (palabra[i + 1] == '=') {
                    //System.out.println("Es el simbolo >=");
                    i = i + 1;
                    tokens[t] = ">=";
                    t += 1;
                } else {
                    //System.out.println("Es el simbolo >");
                    tokens[t] = ">";
                    t += 1;
                }
            } else if (Character.isDigit(palabra[i])) {
                // System.out.println("Es un numero");

                int j = i;
                int index = 0;
                token = "";

                while (Character.isDigit(palabra[j]) || palabra[j] == '.') {
                    index = j;
                    if (Character.isDigit(palabra[j]) && Character.isLetter(palabra[j - 1])) {
                        token += palabra[j];
                        tokens[t - 1] += token;
                        j += 1;
                    } else if (Character.isDigit(palabra[j]) && (j + 1 < palabra.length)) {
                        token = "";
                        token += palabra[j];
                        if ((Character.isLetter(palabra[j + 1]) || Character.isDigit(palabra[j + 1])) && j + 1 < palabra.length) {
                            j += 1;
                            while (Character.isLetter(palabra[j]) || Character.isDigit(palabra[j])) {
                                token += palabra[j];
                                j += 1;
                                if (j == palabra.length) {
                                    break;
                                }
                            }
                        }
                        tokens[t] = token;
                    } else if (Character.isDigit(palabra[j])) {
                        token = "";
                        token += palabra[j];
                        tokens[t] = token;
                        j += 1;
                    } else {
                        if (j == palabra.length) {
                            break;
                        } //Si llego al fin, se lee el next char
                    }

                    if (j == palabra.length) {
                        break;
                    }
//                    j += 1;
                }
                //System.out.println(token);   
                if (j != palabra.length - 1) {
                    t += 1;
                }
                i = j - 1;//Para que continue en la siguiente palabra
            } else if (Character.isLetter(palabra[i])) {
                //System.out.println("Es una letra");
                //Character.isLetter(palabra[i]);
                int j = i;
                int x = j;
                token = "";
//                if (j > 0) {
//                    if (Character.isDigit(palabra[j - 1])) {
//                        token += palabra[j - 1];
//                    }
//                }
                while (Character.isLetter(palabra[j])) {
                    token += palabra[j];
                    j += 1;
                    if (j == palabra.length) {
                        break;
                    } //Si llego al fin, se lee el next char
                }
                //System.out.println(token);

                tokens[t] = token;
                t += 1;
                i = j - 1;//Para que continue en la siguiente palabra

            } else if (palabra[i] == '\'') {
                //System.out.println("Es una cadena de texto");
                //Character.isLetter(palabra[i]);
                token = "";
                token += palabra[i];
                int j = i + 1; //se inicia despues del caracter ' para que ingrese al while
                while (palabra[j] != '\'') {
                    token += palabra[j];
                    j += 1;
                    if (j == palabra.length) {
                        break;
                    } //Si llego al fin, se lee el next char
                }
                token += palabra[j]; //Es necesario adicionar el final del caracter '
                //System.out.println(token);
                tokens[t] = token;
                t += 1;
                i = j;//Para que continue en la siguiente palabra

            } else {
                //System.out.println("Es otro caracter");
                tokens[t] = "" + palabra[i];
                t += 1;
            }
        }

        //Fin analisis lexico
        //----------------------------------------------------
        return tokens;
    }
    boolean checkFinal = false;
    public void FindErrors(String token[]) {
        String c = "\""; 
        if (token[0] != null) {

            if (token[0].equals("imprimir")) {
                
  
//                if(token.length == 5){
                if(!token[1].equals(c)){
                    System.out.println("Error:Se esperaban comillas");
                    System.out.println("se encontro: "+token[1]);
                }else if(!token[3].equals(c)){
                    System.out.println("Error:Se esperaban comillas");
                    System.out.println("se encontro: "+token[3]);
                }else if (token[1] == null) {
                    System.out.println("Error:Faltan comillas de apertura");
                } else if (token[3] == null) {
                    System.out.println("Error:Faltan comillas de cierre");
                }else if(checkFinal == true){
                    System.out.println("Error:No se puede escribir despues del programa");
                    checkFinal = false;
                }
//                }
                
//                if(token[token.length-1] == null){
//                    System.out.println("Error:Falta ';'");
//                }
                             
            }else if (token[0].equals("programa")) {
                if (token[1] == null) {
                    System.out.println("Error:El programa no tiene nombre");
                }else if(token[1]!=null){
                    char aux[] = token[1].toCharArray();
                    if(Character.isDigit(aux[0])){
                        System.out.println("Error:No se puede nombrar con numeros al principio");
                    }
                }else if(checkFinal == true){
                    System.out.println("Error:No se puede escribir despues del programa");
                    checkFinal = false;
                    }
                if (token[token.length-1] == null) {
                    System.out.println("Error:Falta ;");
                }
            } else if (token[0].contains("inicio")){
                char aux[] = token[0].toCharArray();
                if(aux.length>"inicio".length()){
                    System.out.println("Error:Se esperaba la palabra 'inicio'");
                }else if(checkFinal == true){
                    System.out.println("Error:No se puede escribir despues del programa");
                    checkFinal = false;
                    }
            }else if(token[0].equals("leer")){
                if(checkFinal == true){
                    System.out.println("Error:No se puede escribir despues del final del programa");
                    checkFinal = false;
                }else{
                if(token[1]==null){
                    System.out.println("Error: Se esperaba una variable");
                }
                if(token[2]==null){
                    System.out.println("Error: Falta ';'");
                }
                }
            }
            else if (token[0].contains("fin")){
                checkFinal = true;
            }
        }
    }

    public static void main(String[] arg) {
        try {
            //Cambiamos el Look&Feel
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (Exception e) {
        }
        new AbrirArchivoTexto();
    }

    JTextPane txp;
    JFileChooser abrirArchivo;
}
