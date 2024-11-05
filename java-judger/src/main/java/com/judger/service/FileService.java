package com.judger.service;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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
//            p = Runtime.getRuntime().exec(command);
//            PrintWriter stdin = new PrintWriter(p.getOutputStream());
//            stdin.println("cd C:\\testing\\judger");
////            stdin.println("cd src/main/resources");
//            stdin.println("del " + studentCode + localPath + "\\src\\main\\java\\ConvertToJson.java");
//            stdin.println("del " + studentCode + localPath + "\\src\\test\\java");
//            stdin.println("Y");
//            stdin.println("del " + studentCode + localPath + "\\pom.xml");
//            stdin.println("del " + studentCode + localPath + "\\src\\main\\resources\\META-INF\\");
//            stdin.println("Y");
//            stdin.close();
//            p.waitFor(5, TimeUnit.MINUTES);


            p = Runtime.getRuntime().exec(command);
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:");
            String path = resource.getFile().getAbsolutePath();
            char seprater = path.charAt(path.length()-8);
            path = path.substring(0,path.length()-14);
            path += "src"+seprater+"main"+seprater+"resources" ;
            stdin.println("cd "+path);
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



//      Need to change above code with following code, but following code is not working right now
//    public static void deleteFiles(String studentCode, String localPath) {
//        String pathToDelete = "C:"+File.separator+"testing" + File.separator + "judger" + File.separator;
//        localPath = localPath.replace("\"" , "");
//        localPath = localPath+File.separator ;
////        System.out.println(pathToDelete);
//
//        try {
//            // Delete individual files
//            Files.delete(Paths.get(pathToDelete + studentCode + localPath + "src" + File.separator + "main" + File.separator + "java" + File.separator + "ConvertToJson.java"));
//            Files.delete(Paths.get(pathToDelete + studentCode + localPath + "pom.xml"));
//
//            // Recursively delete directories
//            Files.walk(Paths.get(pathToDelete + studentCode + localPath + "src" + File.separator + "test" + File.separator + "java"))
//                    .sorted(Comparator.reverseOrder()) // Delete files before directories
//                    .map(Path::toFile)
//                    .forEach(File::delete);
//            Files.walk(Paths.get(pathToDelete + studentCode + localPath + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "META-INF"))
//                    .sorted(Comparator.reverseOrder())
//                    .map(Path::toFile)
//                    .forEach(File::delete);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void copyFiles(String studentCode, String localPath) {
        String[] command = {"cmd"};
        Process p;
        try{
//            p = Runtime.getRuntime().exec(command);
//            PrintWriter stdin = new PrintWriter(p.getOutputStream());
//            stdin.println("cd C:\\testing\\judger");
////            stdin.println("cd src/main/resources");
//            stdin.println("copy ConvertToJson.java "+ studentCode + localPath +"\\src\\main\\java");
//            stdin.println("copy TestCases.java "+ studentCode + localPath +"\\src\\test\\java");
//            stdin.println("copy pom.xml "+ studentCode + localPath);
//            stdin.println("copy persistence.xml "+ studentCode + localPath +"\\src\\main\\resources\\META-INF\\");
//            stdin.println("copy dbdetails.properties "+ studentCode + localPath +"\\src\\main\\resources");
//            stdin.println("copy applicationContext.xml "+ studentCode + localPath +"\\src\\main\\resources");
//            stdin.close();
//            p.waitFor(5, TimeUnit.MINUTES);



            p = Runtime.getRuntime().exec(command);
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:");
            String path = resource.getFile().getAbsolutePath();
            char seprater = path.charAt(path.length()-8);
            path = path.substring(0,path.length()-14);
            path += "src"+seprater+"main"+seprater+"resources" ;
            stdin.println("cd "+path);
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


    public static String uploadFile(MultipartFile file) throws RuntimeException{
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }

            // Get the root directory of the project
            String rootDirectory = System.getProperty("user.dir");

            // Specify the path where you want to save the file in the "resources" folder
            Path destination = Paths.get(rootDirectory, "src", "main", "resources", file.getOriginalFilename());

            // Save the file
        try {
            Files.write(destination, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "File uploaded successfully!";

    }

}
