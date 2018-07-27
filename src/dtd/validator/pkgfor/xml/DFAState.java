/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtd.validator.pkgfor.xml;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Issa Memari
 */
class DFAState
{
    List<DFAState> next;
    boolean isFinal;
    int number;

    public DFAState()
    {
        isFinal = false;
        next = new ArrayList<>(27);
        for (int i = 0; i < 27; i++)
            next.add(null);
        number = 0;
    }
}