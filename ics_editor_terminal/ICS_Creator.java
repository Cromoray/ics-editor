import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import Exception.InsufficientArgumentException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ICS_Creator {
    private static Path inpuPath;
    private static Path outpuPath;

    private static BufferedReader reader;
    private static BufferedWriter writer;

    private static int numRigheTot = 0;
    public static int numRigheConvertite = 0;

    private static int[] fieldIndex = new int[8];
    /*
     * * index 0 = data inizio evento
     * * index 1 = ora inizio evento
     * * index 2 = data fine evento
     * * index 3 = ora fine evento
     * * index 4 = title
     * * index 5 = descrizione / note
     * * index 6 = luogo
     * * index 7 = avviso
     */
    public static void main(String[] args) {
        
        try {
            initComponent(args); //SOSTITUIRE CON args
        } catch (InsufficientArgumentException iae) {
            //iae.printStackTrace();
            System.out.println(InsufficientArgumentException.getErrorMessage());
            System.exit(-1);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(-1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(-1);
        }

        try {
            setupIndex();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            Converter.convert(reader, writer, fieldIndex);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("\nConvertite " + numRigheConvertite + " di " + numRigheTot);
        }
        
        
    }

    private static void initComponent(String[] args) throws InsufficientArgumentException, FileNotFoundException, IOException {
        File iFile;
        if (args.length>=2) {
            inpuPath = FileSystems.getDefault().getPath(args[0]);
            outpuPath = FileSystems.getDefault().getPath(args[1]);
        } else throw new InsufficientArgumentException("Usage: ICS_Creator <inputFile.csv> <outputFile.ics>");


        iFile = new File(inpuPath.toString());

        if (!iFile.exists()) throw new FileNotFoundException("Input file not found");

        reader = new BufferedReader(new FileReader(inpuPath.toString()));
        writer = new BufferedWriter(new FileWriter(outpuPath.toString()));
    }

    private static void setupIndex() throws Exception{
        String line = reader.readLine();
        String fields[] = line.split(";");

        String[] menu = { "- data inizio evento", "- ora inizio evento", "- data fine evento", "- ora fine evento", "- titolo evento", "- Descrizione evento", "- luogo evento", "- notifica evento"};

        System.out.println("\n\n");
        System.out.println("Definizione campi dati.\n");
        System.out.println("Indicare in ordine i seguenti campi a cosa corrispondono nei dati utilizzando l'indice di posizione (0 = null):");
        System.out.println("Dati prima riga:");
        for (int i = 0; i < fields.length; i++) {
            System.out.println((i+1) + " - " + fields[i]);
        }
        
        System.out.println("\n");
        for (int i = 0; i < fieldIndex.length; i++) {
            System.out.print(menu[i] + ": ");
            fieldIndex[i] = TerminalInput.readInt() - 1;
            System.out.print("\n");
        }

        for (int i = 0; reader.readLine() != null; i++) {
            numRigheTot = i+1;
        }

        //resetto il reader per permettere il readLine()
        reader.close();
        reader = new BufferedReader(new FileReader(inpuPath.toString()));
    }


    // ------------------------------------------
    // INNER CLASS
    // ------------------------------------------
    private class TerminalInput {
        
        public static int readInt() throws IOException{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            //checking if user insert an integer number
            int integerInput;
            try { 
                integerInput = Integer.parseInt(input); 
            } catch(NumberFormatException e) { 
                return -1; 
            } catch(NullPointerException e) {
                return -1;
            }
            
            return integerInput;
        }
    
        //inserire gli altri lettori se necessari
    }
}