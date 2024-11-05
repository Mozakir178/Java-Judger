package com.judger.service;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class GitService {
    public static String[] splitGitUrl(String githubUrl) {
        String[] parts = URLDecoder.decode(githubUrl, StandardCharsets.UTF_8).split("/");
        if(parts.length <= 1) return new String[] {null, "Fatal : can't found the submission link"};

        if(parts.length <=5 || !parts[3].equals("masai-course")) return new String[] {null, "Fatal : found personal repo link suppose to have masai GitHub folder link containig pom.xml file"};

        if(parts[5].equals("commit")) return new String[] {null, "Fatal : found commit link suppose to have masai GitHub folder link containig pom.xml file"};

        String gitUrl = String.format("https://%s/%s/%s/%s.git", parts[1], parts[2], parts[3], parts[4]);

        StringBuilder localPath = new StringBuilder();

        localPath.append("\"");
        for(int i=7; i<parts.length; i++) {
            if(parts[i].charAt(0) == '.') return new String[] {null, "Fatal : found file name start with '.' check if any of your folder name start with '.'"};
            localPath.append("\\"+parts[i]);
        }
        localPath.append("\"");

        return new String[] { gitUrl, localPath.toString()};
    }


    public static void cloneStudentRepo(String gitUrl, String studentCode) {
        String[] command = {"cmd"};
        Process p;
        try{
//            p = Runtime.getRuntime().exec(command);
//            PrintWriter stdin = new PrintWriter(p.getOutputStream());
//            //trying to go to the respective directory
//            stdin.println("cd C:\\testing\\judger");
////            stdin.println("cd src/main/resources");
//            stdin.println("mkdir "+ studentCode);
//            stdin.println("git clone "+ gitUrl + " " + studentCode + "> output.txt");
//            stdin.println("cd " + studentCode);
//            stdin.println("git restore .");
//            stdin.close();
//            p.waitFor(5, TimeUnit.MINUTES);

            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:");
            String path = resource.getFile().getAbsolutePath();
            char seprater = path.charAt(path.length()-8);
            path = path.substring(0,path.length()-14);
            path += "src"+seprater+"main"+seprater+"resources" ;
//            System.out.println(path);
            p = Runtime.getRuntime().exec(command);
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("cd "+path);
            stdin.println("mkdir "+ studentCode);
            stdin.println("git clone "+ gitUrl + " " + studentCode + "> output.txt");
            stdin.println("cd " + studentCode);
            stdin.println("git restore .");
            stdin.close();
            p.waitFor(5, TimeUnit.MINUTES);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
