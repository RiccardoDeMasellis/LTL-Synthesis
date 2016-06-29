package mainSynthesis;

import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import synthesis.StrategyGenerator;
import synthesis.SynthesisAutomaton;
import synthesis.symbols.PartitionedDomain;
import utils.AutomatonUtils;

/**
 * ExampleMain
 * This class has the purpose to guide the user through the basic operations needed to use this module.
 * <br>
 * Created by Simone Calciolari on 17/06/16.
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
public class ExampleMain {

    public static void main(String[] args) {

        //First of all, the specification formula must be declared.
        //Formula parsing and representation is handled by the FLLOAT library:
        //https://github.com/RiccardoDeMasellis/FLLOAT
        //For the complete grammar, please read these files:
        //https://github.com/RiccardoDeMasellis/FLLOAT/blob/master/grammars/LTLfFormulaParser.g4
        //https://github.com/RiccardoDeMasellis/FLLOAT/blob/master/grammars/PropFormulaParser.g4

		/*
		LTLf
		 */
        //String ltlfSpecification = "(G a) U (X b)";

        /*
        LDLf
         */
        String ldlfSpecification = "[a]tt && <true>b";

        //Next, you must declare the domain of the formula, specifying the partition between proposition controlled
        //by the environment and those controlled by the system.
        PropositionalSignature environment = new PropositionalSignature();
        PropositionalSignature system = new PropositionalSignature();
        environment.add(new Proposition("a"));
        system.add(new Proposition("b"));

        PartitionedDomain partitionedDomain = new PartitionedDomain(environment, system);

        //Now, the SynthesisAutomaton must be instantiated;
        //This class will handle the computations required to solve the synthesis problem,
        //And will output its solution (if it exists).
        //BEWARE: if the specification is big, this operations may require some time,
        // as all the most intensive operations are performed here
        SynthesisAutomaton sa = new SynthesisAutomaton(partitionedDomain, ldlfSpecification, true);


        /*
		LDLf
		 */
//		String ldlfSpecification = "[a]ff";
//        PropositionalSignature environment = new PropositionalSignature();
//        PropositionalSignature system = new PropositionalSignature();
//        environment.add(new LDLfLocalVar("a"));
//        system.add(new LDLfLocalVar("b"));
//
//
//        PartitionedDomain partitionedDomain = new PartitionedDomain(environment, system);


        //To know if the problem has a solution, you can call this method.
        System.out.println("Is realizable? " + sa.isRealizable());

        //If a solution exists, you can obtain it by calling this method
        //Note that it returns null if the problem cannot be solved
        StrategyGenerator stg = sa.getStrategyGenerator();

        AutomatonUtils.printAutomaton(stg.getAutomaton(), "strategyGenerator.gv");

        /*
		//Now you can start playing the game.
		//First of all, call this method to obtain the first action the system should perform.
		StrategyOutput systemAction = stg.getFirstMove();
		Interpretation environmentAction;

		//If the game is already won, the methods returns a special class that represents a SUCCESS
		if (systemAction instanceof StrategySuccessOutput){
			System.out.println("You have won the game, no point in going on");
		} else {
			//Otherwise, you will receive an interpretation over the system-controlled propositions as output
			System.out.println("Set this propositions to true: " + systemAction);

			//At this point, once you receive the environment interpretation (move) for the current turn,
			//call this method to proceed to the next turn.
			//As before, you will receive the interpretation over the system-controlled propositions to be used in the
			//next turn, or a StrategySuccessOutput if the game is won.
			environmentAction = new Interpretation();
			environmentAction.add(new Proposition("a"));

			systemAction = stg.step(environmentAction);

			if (systemAction instanceof StrategySuccessOutput){
				System.out.println("You have won the game, no point in going on");
			} else {
				System.out.println("Set this propositions to true: " + systemAction);

				//And keep going like this until you obtain a StrategySuccessOutput

				environmentAction = new Interpretation();

				systemAction = stg.step(environmentAction);

				if (systemAction instanceof StrategySuccessOutput){
					System.out.println("You have won the game, no point in going on");
				} else {
					//Keep going ...
				}
			}
		}
		*/
    }
}
