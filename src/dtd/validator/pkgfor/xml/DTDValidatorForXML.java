package dtd.validator.pkgfor.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Issa Memari
 */
public class DTDValidatorForXML {
    
    public static String readFile(String filePath) throws FileNotFoundException
    {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        StringBuilder sb = new StringBuilder();
        
        try {
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }            
        } 
        catch (IOException e) {}
        
        return sb.toString();
    }
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2)
        {
            System.out.println("Invalid input, please specify the paths for the XML file and the DTD file as arguments.");
            return;
        }
        String XMLFilePath = args[0];
        String DTDFilePath = args[1];

        File f = new File(XMLFilePath);
        if(!f.exists() || f.isDirectory())
        {
            System.out.println("XML file not found, please try again.");
            return;
        }
        
        String DTD;
        StreamReader stream;
        StreamValidator sv;
        try
        {
            DTD = readFile(DTDFilePath);
            stream = new StreamReader(XMLFilePath);
            sv = new StreamValidator(stream);
        }
        catch (FileNotFoundException e) {
            System.out.println("DTD file not found, please try again.");
            return;
        }

        int validity = sv.validate(DTD);

        switch (validity) {
            case 0:
                System.out.println("not well-formed");
                System.out.println("not valid");
                break;
            case 1:
                System.out.println("well-formed");
                System.out.println("not valid");
                break;
            case 2:
                System.out.println("well-formed");
                System.out.println("valid");
                break;
            default:
                break;
        }
    }
}