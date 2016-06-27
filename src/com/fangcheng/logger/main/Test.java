package com.fangcheng.logger.main;
import java.util.UUID;
   public class Test {
      public static void main(String[] args) {
        UUID uuid = UUID.randomUUID();
        for(int i=0;i<10;i++)
        System.out.println (uuid.toString());
      }
   }