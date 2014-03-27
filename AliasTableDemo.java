/*
 * AliasTableDemo.java - a small program to demo the use of class AliasTable
 *
 * Copyright (C) 2014   Paul J. Sanchez <pjs(at)alum(dot)mit(dot)edu>
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

import org.argelfraster.simutils.AliasTable;

public class AliasTableDemo {   
   private static void demoPrint(int n, AliasTable<?> t) {
      for(int i = 0; i < n;) {
         System.out.print(t.generate() +
               ((++i % 20 == 0) || (i == n) ? "\n" : ", "));
      }
      System.out.println();
   }

   public static void main(String[] args) {
      Integer[] x = {1, 2, 3, 4, 42};
      String[] s = {"a", "b", "c", "d", "e"};
      double[] probs = {0.07, 0.5, 0.03, 0.05, 0.35};
      AliasTable<Integer> at1 = new AliasTable<Integer>(x, probs);
      at1.printAliasTable();
      demoPrint(600, at1);
      AliasTable<String> at2 = new AliasTable<String>(s, probs);
      at2.printAliasTable();
      demoPrint(600, at2);
   }

}
