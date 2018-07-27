/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtd.validator.pkgfor.xml;

/**
 *
 * @author Issa Memari
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StreamReader {
    
    public BufferedReader br;
    
    public StreamReader(String fileName)
    {
        try
        {
            this.br = new BufferedReader(new FileReader(fileName));
        }
        catch (IOException e) { System.out.println("File not found " + fileName); }
    }
    
    public StreamReader(StreamReader stream)
    {
        this.br = new BufferedReader(stream.br);
    }
    
    public String nextLine() throws IOException
    {
        return br.readLine();
    }
}