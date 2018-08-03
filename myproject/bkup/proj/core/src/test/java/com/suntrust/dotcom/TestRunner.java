package com.suntrust.dotcom;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
   public static void main(String[] args) {
	   
      Result resultRecentPagesSortPositive = JUnitCore.runClasses(TestRecentPagesSortPositive.class);
      Result resultRecentPagesSortNegative = JUnitCore.runClasses(TestRecentPagesSortNegative.class);
      Result resultTitleAscendingOrderSortPositive = JUnitCore.runClasses(TestTitleAscendingOrderSortPositive.class);
      Result resultTitleAscendingOrderSortNegative = JUnitCore.runClasses(TestTitleAscendingOrderSortNegative.class);
      Result resultTitleDescendingOrderSortPositive = JUnitCore.runClasses(TestTitleDescendingOrderSortPositive.class);
      Result resultTitleDescendingOrderSortNegative = JUnitCore.runClasses(TestTitleDescendingOrderSortNegative.class);

      for (Failure failure : resultRecentPagesSortPositive.getFailures()) {
          System.out.println(failure.toString());
       }		
      System.out.println("resultRecentPagesSortPositive "+ resultRecentPagesSortPositive.wasSuccessful());
      
      
      

       for (Failure failure : resultRecentPagesSortNegative.getFailures()) {
           System.out.println(failure.toString());
        }		
       System.out.println("resultRecentPagesSortNegative "+ resultRecentPagesSortNegative.wasSuccessful());

       
       

        for (Failure failure : resultTitleAscendingOrderSortPositive.getFailures()) {
            System.out.println(failure.toString());
         }		
        System.out.println("resultTitleAscendingOrderSortPositive "+ resultTitleAscendingOrderSortPositive.wasSuccessful());

        

         for (Failure failure : resultTitleAscendingOrderSortNegative.getFailures()) {
             System.out.println(failure.toString());
          }		
         System.out.println("resultTitleAscendingOrderSortNegative "+ resultTitleAscendingOrderSortNegative.wasSuccessful());

         

          for (Failure failure : resultTitleDescendingOrderSortPositive.getFailures()) {
              System.out.println(failure.toString());
           }		
          System.out.println("resultTitleDescendingOrderSortPositive "+ resultTitleDescendingOrderSortPositive.wasSuccessful());
          
          

           for (Failure failure : resultTitleDescendingOrderSortNegative.getFailures()) {
               System.out.println(failure.toString());
            }		
           System.out.println("resultTitleDescendingOrderSortNegative "+ resultTitleDescendingOrderSortNegative.wasSuccessful());
        
   }
} 
