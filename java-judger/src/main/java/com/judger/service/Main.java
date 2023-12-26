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

public class Main{
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

            String[] gitUrlBranchProj = GitService.splitGitUrl(arr[3]);
            if (gitUrlBranchProj[0] == null) {
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

            String folderPath = "C:\\testing\\judger\\" + arr[0];
            if(FileService.isFolderEmpty(folderPath)) {
                bw.write(0 + "," + "Fatal : unable to clone GitHub repo");
                bw.newLine();
                bw.flush();
                System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
                continue;
            }

            String filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "") + "\\pom.xml";
            if (!FileService.checkPath(filePath)) {
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

            filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "");
            if(TestingService.ifCompilationError(filePath)) {
                bw.write(0 + "," + "Compilation error. Please check if you are following all the configurations mentioned in the instruction properly");
                bw.newLine();
                bw.flush();
                System.out.println("ended...... " + currentCheck++ + " -> " + arr[0]);
                continue;
            }

            filePath = "C:\\testing\\judger\\" + arr[0] + localPath.replace("\"", "") + "\\target\\surefire-reports\\TestCases.txt";
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
