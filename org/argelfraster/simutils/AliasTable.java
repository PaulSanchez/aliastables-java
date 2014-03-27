/*
 * AliasTable.java
 *
 * Copyright (C) 2014	Paul J. Sanchez <pjs(at)alum(dot)mit(dot)edu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.argelfraster.simutils;

import java.lang.IllegalArgumentException;
import java.text.DecimalFormat;
import java.util.Stack;
import java.util.Random;

/**
 * Generates random variates from a discrete distribution with
 * finite support in constant time per value generated.
 * 
 * @author pjs
 *
 * @param <V>
 *   The generated value's data type.
 */
public class AliasTable<V> {
   private static Random r = new Random();
   private static DecimalFormat df2 = new DecimalFormat(" 0.00;-0.00");

   private V[] primary;
   private V[] alias;
   private double[] primaryP;
   private double[] primaryPgivenCol;

   private static boolean notCloseEnough(double target, double value) {
      return Math.abs(target - value) > 1E-10;
   }

   /**
    * Constructs the AliasTable given the set of values
    * and corresponding probabilities.  Construction is O(k) for
    * a distribution with k outcomes.
    * 
    * @param value
    *   An array of the set of outcome values for the distribution.
    *    
    * @param pOfValue
    *   An array of corresponding probabilities for each outcome.
    *   
    * @throws IllegalArgumentException
    *   The values and probability arrays must be of the same length,
    *   the probabilities must all be positive, and they must sum to one.
    */
   public AliasTable(V[] value, double[] pOfValue) {
      super();      
      if (value.length != pOfValue.length) {
         throw new IllegalArgumentException(
               "Args to AliasTable must be vectors of the same length.");
      }
      double total = 0.0;
      for (double d : pOfValue) {
         if (d < 0) {
            throw new
               IllegalArgumentException("p_values must all be positive.");
         }
         total += d;
      }
      if (notCloseEnough(1.0, total)) {
         throw new IllegalArgumentException("p_values must sum to 1.0");
      }
      
      // Done with the safety checks, now let's do the work...
      
      // Cloning the values prevents people from changing outcomes
      // after the fact.
      primary = value.clone();
      alias = value.clone();
      primaryP = pOfValue.clone();
      primaryPgivenCol = new double[primary.length];
      for (int i = 0; i < primaryPgivenCol.length; ++i) {
         primaryPgivenCol[i] = 1.0;
      }
      double equiProb = 1.0 / primary.length;
      
      Stack<Integer> deficitSet = new Stack<Integer>();
      Stack<Integer> surplusSet = new Stack<Integer>();
      
      // initial allocation of values to deficit/surplus sets
      for (int i = 0; i < primary.length; ++i) {
         if (notCloseEnough(equiProb, primaryP[i])) {
            if (primaryP[i] < equiProb) {
               deficitSet.add(i);
            } else {
               surplusSet.add(i);
            }
         }
      }
      
      /*
       * Pull a deficit element from what remains.  Grab as much
       * probability as you need from a surplus element.  Re-allocate
       * the surplus element based on the amount of probability taken
       * from it to the deficit, surplus, or completed set.
       * 
       * Lather, rinse, repeat.
       */
      while (!deficitSet.isEmpty()) {
         int deficitColumn = deficitSet.pop();
         int surplusColumn = surplusSet.pop();
         primaryPgivenCol[deficitColumn] = primaryP[deficitColumn] / equiProb;
         alias[deficitColumn] = primary[surplusColumn];
         primaryP[surplusColumn] -= equiProb - primaryP[deficitColumn];
         if (notCloseEnough(equiProb, primaryP[surplusColumn])) {
            if (primaryP[surplusColumn] < equiProb) {
               deficitSet.add(surplusColumn);
            } else {
               surplusSet.add(surplusColumn);
            }
         }
      }
      
   }

   /**
    * Generate a value from the input distribution.  The alias table
    * does this in O(1) time, regardless of the number of elements in
    * the distribution.
    * 
    * @return
    *   A value from the specified distribution.
    */
   public V generate() {
      int column = (int) (primary.length * r.nextDouble());
      return r.nextDouble() <= primaryPgivenCol[column] ?
                  primary[column] : alias[column];
   }
   
   public void printAliasTable() {
      System.out.flush();
      System.err.println("Primary\t\tprimaryPgivenCol\tAlias");
      for(int i = 0; i < primary.length; ++i) {
         System.err.println(primary[i] + "\t\t\t"
            + df2.format(primaryPgivenCol[i]) + "\t\t" + alias[i]);
      }
      System.err.println();
      System.err.flush();
   }

}
