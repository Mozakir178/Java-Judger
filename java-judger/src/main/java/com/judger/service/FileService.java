package com.judger.service;

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class FileService {

    public static boolean checkPath(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean isFolderEmpty(String folderPath) {
        return new File(folderPath).isDirectory() ? new File(folderPath).list().length <= 1 : true;
    }

    public static void deleteFiles(String studentCode, String localPath) {
        String[] command = {"cmd"};
        Process p;
        try{
            p = Runtime.getRuntime().exec(command);
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("cd C:\\testing\\judger");
            stdin.println("del " + studentCode + localPath + "\\src\\main\\java\\ConvertToJson.java");
            stdin.println("del " + studentCode + localPath + "\\src\\test\\java");
            stdin.println("Y");
            stdin.println("del " + studentCode + localPath + "\\pom.xml");
            stdin.println("del " + studentCode + localPath + "\\src\\main\\resources\\META-INF\\");
            stdin.println("Y");
            stdin.close();
            p.waitFor(5, TimeUnit.MINUTES);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void copyFiles(String studentCode, String localPath) {
        String[] command = {"cmd"};
        Process p;
        try{
            p = Runtime.getRuntime().exec(command);
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("cd C:\\testing\\judger");
            stdin.println("copy ConvertToJson.java "+ studentCode + localPath +"\\src\\main\\java");
            stdin.println("copy TestCases.java "+ studentCode + localPath +"\\src\\test\\java");
            stdin.println("copy pom.xml "+ studentCode + localPath);
            stdin.println("copy persistence.xml "+ studentCode + localPath +"\\src\\main\\resources\\META-INF\\");
            stdin.println("copy dbdetails.properties "+ studentCode + localPath +"\\src\\main\\resources");
            stdin.println("copy applicationContext.xml "+ studentCode + localPath +"\\src\\main\\resources");
            stdin.close();
            p.waitFor(5, TimeUnit.MINUTES);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
