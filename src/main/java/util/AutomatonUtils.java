package util;

import automaton.EmptyTrace;
import automaton.PossibleWorldWrap;
import automaton.TransitionLabel;
import net.sf.tweety.logics.pl.syntax.Proposition;
import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import synthesis.symbols.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * AutomatonUtils
 * <br>
 * Created by Simone Calciolari on 01/04/16.
 * @author Simone Calciolari.
 *
 * LTL-Synthesis. Perform LTL Synthesis on finite traces. Copyright (C) 2016 Simone Calciolari
 *
 * This file is part of LTL-Synthesis.
 *
 * LTL-Synthesis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LTL-Synthesis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LTL-Synthesis. If not, see <http://www.gnu.org/licenses/>.
 */
public class AutomatonUtils {



	public static Automaton translateToGameAutomaton(Automaton original, PartitionedDomain domain){
		Automaton res = new Automaton();

		//Get original states iterator
		Iterator<State> originalStates = original.states().iterator();
		//Map to translate states
		HashMap<State, State> oldToNewStates = new HashMap<>();

		//Add states to the new automaton and fill the map
		while (originalStates.hasNext()){
			State oldState = originalStates.next();
			State newState = res.addState(oldState.isInitial(), oldState.isTerminal());
			oldToNewStates.put(oldState, newState);
		}

		//ADDING TRANSITION TO NEW AUTOMATON
		//For each original state, get all transitions starting from it, translate the label,
		//and insert the new translated transition in the translated automaton

		//Get the iterator on the original states (again)
		originalStates = original.states().iterator();

		while (originalStates.hasNext()){

			State oldStart = originalStates.next();
			Set<Transition<TransitionLabel>> oldTransitions = original.delta(oldStart);

			//Iterate over all transition starting from the current (old) state.
			for (Transition<TransitionLabel> oldTransition : oldTransitions){

				//Get end state
				State oldEnd = oldTransition.end();
				//Get old label
				TransitionLabel oldLabel = oldTransition.label();

				//New label
				SynthTransitionLabel newLabel;

				if (oldLabel instanceof EmptyTrace){
					newLabel = new SynthEmptyTrace();
				} else if (oldLabel instanceof PossibleWorldWrap){
					newLabel = partitionPossibleWorld((PossibleWorldWrap) oldLabel, domain);
				} else {
					throw new RuntimeException("Unknown label type");
				}

				//Create new transition
				//Get start and end states
				State newStart = oldToNewStates.get(oldStart);
				State newEnd = oldToNewStates.get(oldEnd);

				Transition<SynthTransitionLabel> newTransition = new Transition<>(newStart, newLabel, newEnd);

				//Add it to translated automaton
				try {
					res.addTransition(newTransition);
				} catch (NoSuchStateException e){
					throw new RuntimeException(e);
				}
			}
		}

		return res;
	}

	private static PartitionedInterpretation partitionPossibleWorld(PossibleWorldWrap pw, PartitionedDomain domain){
		Interpretation environment = new Interpretation();
		Interpretation system = new Interpretation();

		for (Proposition p : pw){
			if (domain.getEnvironmentDomain().contains(p)){
				environment.add(p);
			} else if (domain.getSystemDomain().contains(p)) {
				system.add(p);
			} else {
				throw new RuntimeException("Found propositional variable not declared in domain");
			}
		}

		return new PartitionedInterpretation(environment, system);
	}
}
