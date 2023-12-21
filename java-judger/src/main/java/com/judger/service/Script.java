package com.judger.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Script{
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader("C:\\testing\\judger\\response.csv"));

        String curr;
        int currentCheck = 1;
        br.readLine();
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:\\testing\\judger\\final.csv"),true));
        while((curr = br.readLine()) != null){
        	
            String[] arr = curr.split(",");
            System.out.println("started...... " + currentCheck + " -> " + arr[0]);
            bw.write(arr[0]+ "," + arr[1]+ "," + arr[3] + ",");
            bw.flush();

            String[] gitUrlBranchProj = splitGitUrl(arr[3]);
            if (gitUrlBranchProj[0] == null) {
            	bw.write(0 + "," + gitUrlBranchProj[1]);
				bw.newLine();
            	bw.flush();
            	System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
            	continue;
            }
//			This issue is sorted
//			System.out.println(Arrays.toString(gitUrlBranchProj));
            
            String gitUrl = gitUrlBranchProj[0].replaceFirst("^https:///", "https://");
            String localPath = gitUrlBranchProj[1];
            
            System.out.println("Clonning student repo...");
            cloneStudentRepo(gitUrl, arr[0]);
            
            String folderPath = "C:\\testing\\judger\\" + arr[0];
            if(isFolderEmpty(folderPath)) {
            	bw.write(0 + "," + "Fatal : unable to clone GitHub repo");
            	bw.newLine();
            	bw.flush();
            	System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
            	continue;
            }
            
            String filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "") + "\\pom.xml";
            if (!checkPath(filePath)) {
            	bw.write(0 + "," + "Fatal : can't find pom.xml file in the directory");
            	bw.newLine();
            	bw.flush();
            	System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
            	continue;
            }
            
            System.out.println("Deleting file from student repo...");
            deleteFiles(arr[0], localPath);
            
            System.out.println("Copying file to student repo...");
            copyFiles(arr[0], localPath);
            
            System.out.println("Checking student submission...");

			//Trying to print the local path need to comment this line later
//			This issue is sorted
//			System.out.println(localPath) ;
            runTestCases(arr[0], localPath);
            
            filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "");
            if(ifCompilationError(filePath)) {
            	bw.write(0 + "," + "Compilation error. Please check if you are following all the configurations mentioned in the instruction properly");
				bw.newLine();
				bw.flush();
				System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
				continue;
            }
            
            filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "") + "\\target\\surefire-reports\\TestCases.txt";
//			This issue is sorted
//			System.out.println(filePath);
            if (!checkPath(filePath)) {
            	bw.write(0 + "," + "Fatal : Can't run the test cases!");
            	bw.newLine();
            	bw.flush();
            	System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
            	continue;
            }

            bw.newLine();
            bw.flush();
            System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
        }
        bw.close();
        br.close();
    }

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
		p = Runtime.getRuntime().exec(command);
		PrintWriter stdin = new PrintWriter(p.getOutputStream());
		//trying to go to the respective directory
		stdin.println("cd C:\\testing\\judger");
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
    
    public static boolean isFolderEmpty(String folderPath) {
    	return new File(folderPath).isDirectory() ? new File(folderPath).list().length <= 1 : true;
    }
    
    public static boolean checkPath(String filePath) {
    	return new File(filePath).exists();
    }
    
    public static void deleteFiles(String studentCode, String localPath) {
    	String[] command = {"cmd"};
		Process p;
		try{
		p = Runtime.getRuntime().exec(command);
		PrintWriter stdin = new PrintWriter(p.getOutputStream());
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
    
    public static void runTestCases(String studentCode, String localPath){
		
		String[] command = {"cmd"};
		Process p;
		try{
		p = Runtime.getRuntime().exec(command);
		PrintWriter stdin = new PrintWriter(p.getOutputStream());
		stdin.println("cd C:\\testing\\judger");
		stdin.println("cd "+ studentCode + localPath);
		stdin.println("mvn clean test > output.txt");
		stdin.println("mvn exec:java -D\"exec.mainClass\"=\"ConvertToJson\"");
		stdin.close();
		p.waitFor(5, TimeUnit.MINUTES);
		}catch(Exception e){
		    e.printStackTrace();
		}
    }
    
    public static boolean ifCompilationError(String filePath) throws Exception {
    	if(!checkPath(filePath + "/output.txt")) return false;
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
