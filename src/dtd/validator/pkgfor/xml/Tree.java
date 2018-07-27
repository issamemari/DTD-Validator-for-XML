package dtd.validator.pkgfor.xml;

import java.util.ArrayList;

/**
 *
 * @author Issa Memari
 */
public class Tree {
    
    ArrayList<Tree> children;
    Character name;
    
    public Tree(Character name)
    {
        this.name = name;
        this.children = new ArrayList<>();
    }
}
