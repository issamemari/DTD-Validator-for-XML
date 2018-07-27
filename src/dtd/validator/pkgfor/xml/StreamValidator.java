/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtd.validator.pkgfor.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Issa Memari
 */
public class StreamValidator {
    
    private StreamReader stream;
    
    public StreamValidator(StreamReader stream)
    {
        this.stream = new StreamReader(stream);
    }
    
    // returns an integer
    // if returned value is 0 -> not valid and not well formed
    // if returned value is 1 -> not valid and well formed
    // if returned value is 2 -> valid and well formed
    public int validate(String DTD)
    {
        Boolean valid = true;
        Boolean wellFormed = true;
        
        Map<Character, String> DTDStuff = new HashMap<>();
        DTD = DTD.trim();
        
        if ("".equals(DTD))
            valid = false;
        
        String DTDLines[] = DTD.split("\\r?\\n\\s*");
        
        if (valid)
            for (String DTDLine : DTDLines) {
                String[] stuff = DTDLine.trim().split("\\s+");
                DTDStuff.put(stuff[0].charAt(0), stuff[1]);
            }
        
        Map<Character, DFA> DTDRules = new HashMap<>();
        for (Character c : DTDStuff.keySet())
            if (!"_".equals(DTDStuff.get(c)))
                DTDRules.put(c, new DFA(DTDStuff.get(c)));
            else
                DTDRules.put(c, null);
        
        String line;
        try {
            
            Stack<DFAState> states = new Stack<>();
            Stack<Character> names = new Stack<>();
            
            String firstLine;
            while(((firstLine = stream.nextLine()) != null) && "".equals(firstLine.trim())) { }

            if (firstLine == null)
                return 0;
                    
            if (DTDRules.get(firstLine.charAt(firstLine.length() - 1)) != null)
                states.push(DTDRules.get(firstLine.charAt(firstLine.length() - 1)).startState);
            
            names.push(firstLine.charAt(firstLine.length() - 1));
            
            int i = 0;
            while((line = stream.nextLine()) != null)
            {
                i++;
                if ("".equals(line.trim()))
                    continue;
                
                if (names.empty())
                {
                    wellFormed = false;
                    valid = false;
                    break;
                }

                if (states.empty())
                    valid = false;
                
                line = line.trim();
                Character elementName = line.charAt(line.length() - 1);
                if (line.substring(0, 1).equals("0"))
                {
                    DFAState fatherState;
                    if (!states.empty())
                    {
                        fatherState = states.pop();
                        if (fatherState != null)
                        {
                            DFAState nextState = fatherState.next.get(elementName - 'a');
                            if (nextState == null)
                                valid = false;
                            else
                            {
                                fatherState = nextState;
                                states.push(fatherState);
                            }
                            states.push(DTDRules.get(elementName) != null ? DTDRules.get(elementName).startState : null);
                        }
                        else
                            valid = false;
                    }
                    else
                        valid = false;
                    
                    names.push(elementName);
                }
                else if (line.substring(0, 1).equals("1"))
                {
                    if (!states.empty())
                        if (states.peek() != null)
                            if (!states.peek().isFinal)
                                valid = false;

                    if (!states.empty())
                        states.pop();
                    else
                        valid = false;

                    if (!Objects.equals(names.peek(), elementName))
                    {
                        wellFormed = false;
                        valid = false;
                        break;
                    }
                    
                    names.pop();    
                }
                else
                {
                    wellFormed = false;
                    valid = false;
                    break;
                }
            }
            
            if (!names.empty())
                wellFormed = false;
                
        } catch (IOException ex) {
            Logger.getLogger(StreamValidator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (wellFormed && valid)
            return 2;
        else if (wellFormed && !valid)
            return 1;
        else
            return 0;
    }
}