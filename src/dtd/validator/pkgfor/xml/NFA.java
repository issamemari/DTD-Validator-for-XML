package dtd.validator.pkgfor.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * @author Issa Memari
 */

class InvalidRegularExpressionException extends Exception
{
    public InvalidRegularExpressionException(String s) {  }
}

public class NFA {
    NFAState startState;
    int stateCount;
    ArrayList<NFAState> states;
    
    public String fixInput(String regex)
    {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < regex.length() - 1; i++)
        {
            sb.append(regex.charAt(i));
            
            if (Character.isLetter(regex.charAt(i + 1)) && regex.charAt(i) != '(' || regex.charAt(i + 1) == '(')
                sb.append('.');
        }
        
        sb.append(regex.charAt(regex.length() - 1));
        return sb.toString();
    }
    
    public String infixToPostfix(String regex)
    {
        String res = "";
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < regex.length(); i++)
            switch (regex.charAt(i)) {
                
                case '|':
                    if (!stack.isEmpty())
                        if (stack.peek() == '.')
                            while (!stack.isEmpty() && stack.peek() != '(')
                                res += stack.pop();
                    stack.push(regex.charAt(i));
                    break;
                
                case ')':
                    while (stack.peek() != '(')
                        res += stack.pop();
                    stack.pop();
                    break;
                    
                case '.':
                case '(':
                    stack.push(regex.charAt(i));
                    break;
                    
                default:
                    res += regex.charAt(i);
                    break;
            }

        while (!stack.isEmpty())
            res += stack.pop();

        return res;
    }
    
    private NFAState getFinalState()
    {
        Stack<NFAState> stack = new Stack<>();
        stack.push(startState);

        Map<NFAState, Boolean> visited = new HashMap<>();

        while (!stack.isEmpty())
        {
            NFAState currentState = stack.pop();
            visited. put(currentState, true);
            if (currentState.isFinal)
                return currentState;

            for (int i = 0; i < 27; i++)
                if (currentState.next.get(i) != null && !currentState.next.get(i).isEmpty())
                    for (NFAState next : currentState.next.get(i))
                        if (!visited.containsKey(next) || !visited.get(next))
                            stack.push(next);
        }
        
        return null;
    }
    
    @Override
    public NFA clone()
    {
        if (this.startState == null)
            return null;

        Map<NFAState, NFAState> states = new HashMap<>();
        Queue<NFAState> queue = new LinkedList<>();
        queue.add(this.startState);

        NFAState startStateCopy = new NFAState();
        states.put(startState, startStateCopy);

        while (!queue.isEmpty())
        {
            NFAState currentState = queue.poll();
            states.get(currentState).isFinal = currentState.isFinal;

            for (int i = 0; i < 27; i++)
                if (currentState.next.get(i) != null)
                    for (NFAState next : currentState.next.get(i))
                        if (!states.containsKey(next))
                        {
                            NFAState p = new NFAState();
                            p.isFinal = next.isFinal;

                            if (states.get(currentState).next.get(i) == null)
                                states.get(currentState).next.set(i, new HashSet<>());

                            states.get(currentState).next.get(i).add(p);
                            states.put(next, p);
                            queue.add(next);
                        }
                        else
                        {
                            if (states.get(currentState).next.get(i) == null)
                                states.get(currentState).next.set(i, new HashSet<>());

                            states.get(currentState).next.get(i).add(states.get(next));
                        }
        }

        return new NFA(startStateCopy, this.stateCount);
    }

    
    public static NFA And(NFA nfa1, NFA nfa2)
    {
        NFAState startState = nfa1.startState;

        NFA result1 = nfa1.clone();
        NFA result2 = nfa2.clone();
        NFAState finalState = result1.getFinalState();
        finalState.isFinal = false;
        if (finalState.next.get(26) == null)
            finalState.next.set(26, new HashSet<>());
        finalState.next.get(26).add(result2.startState);
        result1.stateCount += result2.stateCount;

        return new NFA(result1.startState, result1.stateCount + result2.stateCount);
    }

    public static NFA Or(NFA nfa1, NFA nfa2)
    {
        NFAState startState = new NFAState();
        NFAState finalState = new NFAState();
        finalState.isFinal = true;

        NFA result1 = nfa1.clone();
        NFA result2 = nfa2.clone();

        if (startState.next.get(26) == null)
            startState.next.set(26, new HashSet<>());

        startState.next.get(26).add(result1.startState);
        startState.next.get(26).add(result2.startState);

        NFAState finalState1 = result1.getFinalState();
        finalState1.isFinal = false;
        if (finalState1.next.get(26) == null)
            finalState1.next.set(26, new HashSet<>());
        finalState1.next.get(26).add(finalState);

        NFAState finalState2 = result2.getFinalState();
        finalState2.isFinal = false;
        if (finalState2.next.get(26) == null)
            finalState2.next.set(26, new HashSet<>());
        finalState2.next.get(26).add(finalState);

        return new NFA(startState, result1.stateCount + result2.stateCount + 2);
    }

    public static NFA Star(NFA nfa)
    {
        NFA result = nfa.clone();
        NFAState finalState = result.getFinalState();
        if (finalState.next.get(26) == null)
            finalState.next.set(26, new HashSet<>());
        finalState.next.get(26).add(result.startState);

        if (result.startState.next.get(26) == null)
            result.startState.next.set(26, new HashSet<>());

        result.startState.next.get(26).add(finalState);

        return new NFA(result.startState, result.stateCount);
    }
    
    public static NFA Question(NFA nfa)
    {
        NFA result = nfa.clone();
        NFAState finalState = result.getFinalState();
        
        if (result.startState.next.get(26) == null)
            result.startState.next.set(26, new HashSet<>());

        result.startState.next.get(26).add(finalState);

        return new NFA(result.startState, result.stateCount);
    }

    public static NFA Plus(NFA nfa)
    {
        NFA result = nfa.clone();
        NFAState finalState = result.getFinalState();
        if (finalState.next.get(26) == null)
            finalState.next.set(26, new HashSet<>());

        finalState.next.get(26).add(result.startState);

        return new NFA(result.startState, result.stateCount);
    }

    public NFA(NFAState startState, int stateCount)
    {
        this.startState = startState;
        this.states = new ArrayList<>(stateCount);
        for (int i = 0; i < 27; i++)
            this.states.add(null);
        
        Stack<NFAState> stack = new Stack<>();
        stack.push(startState);

        Map<NFAState, Boolean> visited = new HashMap<>();

        int n = 0;

        while (!stack.isEmpty())
        {
            NFAState currentState = stack.pop();

            if (visited.containsKey(currentState) && visited.get(currentState))
                continue;

            visited.put(currentState, true);
            currentState.number = n;

            for (int i = 0; i < 27; i++)
                if (currentState.next.get(i) != null && !currentState.next.get(i).isEmpty())
                    for (NFAState next : currentState.next.get(i))
                        if (!visited.containsKey(next) || !visited.get(next))
                            stack.push(next);

            states.set(n, currentState);
            n++;
        }

        this.stateCount = n;
    }
    
    public NFA(String regex)
    {
        if ("_".equals(regex))
            return;
            
        String regexC = fixInput(regex);
        regex = infixToPostfix(fixInput(regex));
        Stack<NFA> stack = new Stack<>();

        for (int i = 0; i < regex.length(); i++)
        {
            switch (regex.charAt(i)) {
                case '.':
                    try
                    {
                        NFA enfa1 = stack.pop();
                        NFA enfa2 = stack.pop();
                        NFA res = And(enfa2, enfa1);
                        stack.push(res);
                    }
                    catch (Exception e) { System.out.println(e.getMessage()); }
                    break;
                case '|':
                    try
                    {
                        NFA enfa1 = stack.pop();
                        NFA enfa2 = stack.pop();
                        NFA res = Or(enfa2, enfa1);
                        stack.push(res);
                    }
                    catch (Exception e) { System.out.println(e.getMessage()); }
                    break;
                case '+':
                    try
                    {
                        NFA enfa = stack.pop();
                        NFA res = Plus(enfa);
                        stack.push(res);
                    }
                    catch (Exception e) { System.out.println(e.getMessage()); }
                    break;
                case '*':
                    try
                    {
                        NFA enfa = stack.pop();
                        NFA res = Star(enfa);
                        stack.push(res);
                    }
                    catch (Exception e) { System.out.println(e.getMessage()); }
                    break;
                case '?':
                    try
                    {
                        NFA enfa = stack.pop();
                        NFA res = Question(enfa);
                        stack.push(res);
                    }
                    catch (Exception e) { System.out.println(e.getMessage()); }
                    break;
                default:
                    NFAState startState = new NFAState();
                    NFAState finalState = new NFAState();
                    finalState.isFinal = true;
                    if (startState.next.get(regex.charAt(i) - 'a') == null)
                        startState.next.set(regex.charAt(i) - 'a', new HashSet<>());
                    startState.next.get(regex.charAt(i) - 'a').add(finalState);
                    stack.push(new NFA(startState, 2));
                    break;
            }
        }

        if (stack.size() != 1)
            System.out.println("Invalid stuff");

        NFA result = stack.pop();

        NFA cl = result.clone();
        this.startState = cl.startState;
        this.stateCount = cl.stateCount;
        this.states = cl.states;
    }
    
    private HashSet<NFAState> getEpsilonClosure(NFAState state)
    {
        HashSet<NFAState> result = new HashSet<>();
        Stack<NFAState> stack = new Stack<>();
        Map<NFAState, Boolean> visited = new HashMap<>();
        stack.push(state);

        while (!stack.isEmpty())
        {
            NFAState currentState = stack.pop();
            result.add(currentState);
            visited.put(currentState, true);

            if (currentState.next.get(26) != null)
                for (NFAState next : currentState.next.get(26))
                    if (!visited.containsKey(next) || !visited.get(next))
                        stack.push(next);
        }

        return result;
    }
    
    private HashSet<NFAState> getEpsilonClosure(HashSet<NFAState> stateSet)
    {
        HashSet<NFAState> result = new HashSet<>();
        for (NFAState state : stateSet)
            result.addAll(getEpsilonClosure(state));
        
        return result;
    }
    
    private HashSet<NFAState> getClosure(NFAState state, Character c)
    {
        HashSet<NFAState> result = new HashSet<>();
        if (state.next.get(c - 'a') != null)
            for (NFAState s : state.next.get(c - 'a'))
                result.add(s);

        return result;
    }
    
    private HashSet<NFAState> getClosure(HashSet<NFAState> stateSet, Character c)
    {
        HashSet<NFAState> result = new HashSet<>();
        for (NFAState state : stateSet)
            result.addAll(getClosure(state, c));

        return result;
    }
    
    private int NFAStatesToInt(HashSet<NFAState> states)
    {
        int result = 0;
        for (NFAState state : states)
            result |= 1 << state.number;

        return result;
    }
    
    public DFA ToDFA()
    {
        HashSet<Character> Alphabet = new HashSet<>();
        Stack<NFAState> stack1 = new Stack<>();
        stack1.push(startState);

        HashMap<NFAState, Boolean> visited = new HashMap<>();

        while (!stack1.isEmpty())
        {
            NFAState currentState = stack1.pop();
            visited.put(currentState, true);

            for (int i = 0; i < 27; i++)
                if (currentState.next.get(i) != null && !currentState.next.get(i).isEmpty())
                {
                    if (i != 26)
                        Alphabet.add((char)(i + 'a'));
                    for (NFAState next : currentState.next.get(i))
                        if (!visited.containsKey(next) || !visited.get(next))
                            stack1.push(next);
                }
        }
        
        HashSet<NFAState> startSet = new HashSet<>();
        startSet.add(this.startState);

        HashMap<Integer, DFAState> conversion = new HashMap<>();

        Stack<Integer> stack = new Stack<>();
        stack.push(NFAStatesToInt(startSet));

        while (!stack.isEmpty())
        {
            int currentSet = stack.pop();
            HashSet<NFAState> hs = new HashSet<>();
            for (int i = 0; i < this.stateCount; i++)
                if ((currentSet & (1 << i)) != 0)
                    hs.add(states.get(i));

            DFAState dfaState = new DFAState();
            HashSet<NFAState> epsilonClosure = getEpsilonClosure(hs);
            for (NFAState state : epsilonClosure)
                if (state.isFinal)
                {
                    dfaState.isFinal = true;
                    break;
                }
            int eps = NFAStatesToInt(epsilonClosure);

            if (!conversion.containsKey(eps))
                conversion.put(eps, dfaState);
            else
                dfaState = conversion.get(eps);

            for (Character c : Alphabet)
            {
                HashSet<NFAState> transition = getClosure(epsilonClosure, c);
                transition = getEpsilonClosure(transition);
                if (!transition.isEmpty())
                {
                    if (!conversion.containsKey(NFAStatesToInt(transition)))
                    {
                        DFAState newdfa = new DFAState();
                        for (NFAState state : transition)
                            if (state.isFinal)
                            {
                                newdfa.isFinal = true;
                                break;
                            }
                        conversion.put(NFAStatesToInt(transition), newdfa);
                        dfaState.next.set(c - 'a', newdfa);
                        stack.push(NFAStatesToInt(transition));
                    }
                    else
                    {
                        dfaState.next.set(c - 'a', conversion.get(NFAStatesToInt(transition)));
                    }
                }
            }
        }

        return new DFA(conversion.get(NFAStatesToInt(getEpsilonClosure(startSet))));
    }
    
    public Boolean match(String word)
    {
        HashSet<NFAState> startSet = getEpsilonClosure(startState);

        for (int i = 0; i < word.length(); i++)
        {
            startSet = getClosure(startSet, word.charAt(i));
            startSet = getEpsilonClosure(startSet);
        }

        for (NFAState s : startSet)
            if (s.isFinal) return true;

        return false;
    }
}