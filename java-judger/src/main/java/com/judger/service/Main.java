package com.judger.service;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

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

public class Main{
    public static void main(String[] args) throws Exception{

        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("classpath:");
        String path = resource.getFile().getAbsolutePath();
        char seprater = path.charAt(path.length()-8);
        path = path.substring(0,path.length()-14);
        path += "src"+seprater+"main"+seprater+"resources" ;
        BufferedReader br = new BufferedReader(new FileReader(path+seprater+"response.csv"));

        String curr;
        int currentCheck = 1;
        br.readLine();
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path+seprater+"final.csv"),true));
        while((curr = br.readLine()) != null){

            String[] arr = curr.split(",");
            System.out.println(Arrays.toString(arr));
            System.out.println("started...... " + currentCheck + " -> " + arr[0]);
            bw.write(arr[0]+ "," + arr[1]+ "," + arr[3] + ",");
            bw.flush();

            String[] gitUrlBranchProj = GitService.splitGitUrl(arr[3]);
            System.out.println(Arrays.toString(gitUrlBranchProj));
            if (gitUrlBranchProj[0] == null) {
                System.out.println("Git URL is wrong");
                bw.write(0 + "," + gitUrlBranchProj[1]);
                bw.newLine();
                bw.flush();
                System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
                continue;
            }
//			This issue is sorted
//			System.out.println(Arrays.toString(gitUrlBranchProj))
//          getting git urls
            String gitUrl = gitUrlBranchProj[0].replaceFirst("^https:///", "https://");
            String localPath = gitUrlBranchProj[1];

            System.out.println("Clonning student repo...");
            GitService.cloneStudentRepo(gitUrl, arr[0]);

//            String folderPath = "C:\\testing\\judger\\" + arr[0];
            String folderPath = path+seprater+arr[0] ;
            if(FileService.isFolderEmpty(folderPath)) {

                bw.write(0 + "," + "Fatal : unable to clone GitHub repo");
                bw.newLine();
                bw.flush();
                System.out.println("Folder is empty");
                System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
                continue;
            }

//            String filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "") + "\\pom.xml";
            String filePath = path+seprater + arr[0] + localPath.replace("\"", "") + "\\pom.xml";
            if (!FileService.checkPath(filePath)) {
                System.out.println("Pom file missing");
                bw.write(0 + "," + "Fatal : can't find pom.xml file in the directory");
                bw.newLine();
                bw.flush();
                System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
                continue;
            }

            System.out.println("Deleting file from student repo...");
            FileService.deleteFiles(arr[0], localPath);

            System.out.println("Copying file to student repo...");
            FileService.copyFiles(arr[0], localPath);

            System.out.println("Checking student submission...");

            //Trying to print the local path need to comment this line later
//			This issue is sorted
//			System.out.println(localPath) ;
            TestingService.runTestCases(arr[0], localPath);

//            filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "");
            filePath =path+seprater+ arr[0] + localPath.replace("\"", "");
            if(TestingService.ifCompilationError(filePath)) {
                System.out.println("CompilationError");
                bw.write(0 + "," + "Compilation error. Please check if you are following all the configurations mentioned in the instruction properly");
                bw.newLine();
                bw.flush();
                System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
                continue;
            }

//            filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "") + "\\target\\surefire-reports\\TestCases.txt";
            filePath = path+seprater + arr[0] + localPath.replace("\"", "") + "\\target\\surefire-reports\\TestCases.txt";

//			This issue is sorted
//			System.out.println(filePath);
            if (!FileService.checkPath(filePath)) {
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

}
