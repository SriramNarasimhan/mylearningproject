package com.suntrust.dotcom.utils;

import java.util.HashMap;

public class ProgressDetails {
   // class variables
   private String taskId;
   private int total=0;
   private int totalProcessed=0;
 
   // field setters
   public void setTaskId(String taskId) {
      this.taskId = taskId;
   }
   public void setTotal(int total) {
      this.total = total;
   }
   public void setTotalProcessed(int totalProcessed) {
      this.totalProcessed = totalProcessed;
   }
 
   public String toString(){
      return "{total:"+this.total+",totalProcessed:"+this.totalProcessed+"}";
   }
 
   public static HashMap<String, ProgressDetails> taskProgressHash = new HashMap<String, ProgressDetails>();
 
}