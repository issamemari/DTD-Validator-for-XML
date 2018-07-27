package dtd.validator.pkgfor.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Issa Memari
 */
public class NFAState {
    
    List<HashSet<NFAState>> next;
    boolean isFinal;
    int number;

    public NFAState()
    {
        isFinal = false;
        next = new ArrayList<>(27);
        for (int i = 0; i < 27; i++)
            next.add(null);
        
        number = 0;
    }
    
}
