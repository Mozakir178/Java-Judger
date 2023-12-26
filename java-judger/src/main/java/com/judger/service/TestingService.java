package com.judger.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TestingService {

    public static void runTestCases(String studentCode, String localPath){

        String[] command = {"cmd"};
        Process p;
        try{
            p = Runtime.getRuntime().exec(command);
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("cd C:\\testing\\judger");
//            stdin.println("cd src/main/resources");
            stdin.println("cd "+ studentCode + localPath);
            stdin.println("mvn clean test > output.txt");
//            stdin.println("mvn clean test surefire-report:report -Dsurefire.useFile=false");
            stdin.println("mvn exec:java -D\"exec.mainClass\"=\"ConvertToJson\"");
            stdin.close();
            p.waitFor(5, TimeUnit.MINUTES);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static boolean ifCompilationError(String filePath) throws Exception {
        if(!FileService.checkPath(filePath + "/output.txt")) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/output.txt"))) {
            String currLine;

            while ((currLine = br.readLine()) != null) {

                String[] arr = Arrays.stream(currLine.split(" ")).map(str -> str.replaceAll("[\\x00]", "")).toArray(String[]::new);
                if (arr.length < 2) continue;
                if (arr[1].equals("Results:")) return false;
                if (!(arr[0].equals("[INFO]") || arr[0].equals("[ERROR]"))) continue;
                if (arr[0].equals("[ERROR]") && arr[1].equals("COMPILATION") && arr[2].equals("ERROR")) return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
