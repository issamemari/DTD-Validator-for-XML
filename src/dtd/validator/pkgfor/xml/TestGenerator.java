/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtd.validator.pkgfor.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Issa Memari
 */
public class TestGenerator {
    
    private String DTD = "";
    public TestGenerator(String DTD)
    {
        this.DTD = DTD.trim();
    }
    
    public void generate(String fileName)
    {
        Map<Character, String> DTDStuff = new HashMap<>();
        String DTDLines[] = DTD.split("\\r?\\n\\s*");
        
        Character rootElement = DTDLines[0].trim().split("\\s+")[0].charAt(0);
        
        for (String DTDLine : DTDLines) {
            DTDLine = DTDLine.trim();
            String[] stuff = DTDLine.split("\\s+");
            DTDStuff.put(stuff[0].charAt(0), stuff[1]);
        }
        
        Map<Character, Xeger> DTDRules = new HashMap<>();
        DTDStuff.keySet().forEach((c) -> {
            if (!"_".equals(DTDStuff.get(c)))
                DTDRules.put(c, new Xeger(DTDStuff.get(c)));
            else
                DTDRules.put(c, null);
        });
        
        StreamWriter sw = new StreamWriter(fileName);
        Stack<String> strings = new Stack<>();
        Stack<Integer> positions = new Stack<>();
        Stack<Character> elements = new Stack<>();
        
        String rootString = DTDRules.get(rootElement).generate();
        
        try {
            sw.writeLine("0 " + rootElement);
        } catch (IOException ex) {
            Logger.getLogger(TestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        strings.push(rootString);
        elements.push(rootElement);
        positions.push(0);
        
        try
        {
            while(!strings.empty())
            {
                String currentString = strings.peek();
                Integer currentPosition = positions.peek();
                Character currentElement = elements.peek();
                
                if (currentPosition == currentString.length())
                {
                    sw.writeLine("1 " + currentElement);
                    strings.pop();
                    elements.pop();
                    positions.pop();
                }
                else
                {
                    if (!"_".equals(currentString))
                    {
                        sw.writeLine("0 " + currentString.charAt(currentPosition));
                        positions.push(positions.pop() + 1);
                        if (DTDRules.get(currentString.charAt(currentPosition)) != null)
                        {
                            String newString = DTDRules.get(currentString.charAt(currentPosition)).generate();
                            strings.push(newString);
                            positions.push(0);
                            elements.push(currentString.charAt(currentPosition));
                        }
                        else
                        {
                            strings.push("_");
                            positions.push(0);
                            elements.push(currentString.charAt(currentPosition));
                        }
                    }
                    else
                    {
                        positions.push(positions.pop() + 1);
                    }
                }
            }
        }
        catch (IOException e) {  }
        try {
            sw.bw.close();
        } catch (IOException ex) {
            Logger.getLogger(TestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}