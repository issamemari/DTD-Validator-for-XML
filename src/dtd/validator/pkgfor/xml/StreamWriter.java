/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtd.validator.pkgfor.xml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Issa Memari
 */
public class StreamWriter {
    
    public BufferedWriter bw;
    
    public StreamWriter(String fileName)
    {
        try
        {
            this.bw = new BufferedWriter(new FileWriter(fileName));
        }
        catch (IOException e) { System.out.println("File not found " + fileName); }
    }
    
    public StreamWriter(StreamWriter stream)
    {
        this.bw = new BufferedWriter(stream.bw);
    }
    
    public void writeLine(String s) throws IOException
    {
        bw.write(s + "\n");
    }
}