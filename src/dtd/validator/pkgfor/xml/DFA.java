/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtd.validator.pkgfor.xml;

import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author ragheed
 */
class DFA
{
    DFAState startState;
    int stateCount;

    public DFA(DFAState startState)
    {
        this.startState = startState;

        Stack<DFAState> stack = new Stack<>();
        stack.push(startState);

        HashMap<DFAState, Boolean> visited = new HashMap<>();

        int n = 0;

        while (!stack.isEmpty())
        {
            DFAState currentState = stack.pop();
            visited.put(currentState, true);
            currentState.number = n;

            for (int i = 0; i < 26; i++)
                if (currentState.next.get(i) != null)
                    if (!visited.containsKey(currentState.next.get(i)) || !visited.get(currentState.next.get(i)))
                        stack.push(currentState.next.get(i));

            n++;
        }

        this.stateCount = n;
    }

    public DFA(String regex)
    {
        NFA thing = new NFA(regex);
        DFA dfa = new NFA((thing).startState, thing.stateCount).ToDFA();
        this.stateCount = dfa.stateCount;
        this.startState = dfa.startState;
    }

    public Boolean match(String word)
    {
        DFAState currentState = startState;
        for (int i = 0; i < word.length(); i++)
        {
            if (currentState.next.get(word.charAt(i) - 'a') != null)
                currentState = currentState.next.get(word.charAt(i) - 'a');
            else
                return false;
        }

        return currentState.isFinal;
    }
}