package com.judger.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;

public class ConvertToJson {
    public static void main(String[] args) throws Exception{
        String filePath = new File("").getAbsolutePath();
        BufferedReader br = new BufferedReader(new FileReader(filePath + "/output.txt"));
//        String filePath
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:\\testing\\judger\\final.csv"),true));
        String currLine;
        JSONObject jsonObject = new JSONObject();

        while((currLine = br.readLine()) != null){

            String[] arr = Arrays.stream(currLine.split(" ")).map(str -> str.replaceAll("[\\x00]", "")).toArray(String[]::new);
            if(arr.length < 2) continue;
            if(arr[0].equals("[MARKS]")){
                jsonObject.put("marks",Double.valueOf(arr[3]));
		    bw.write(arr[3] + ",");
                continue;
            }
            if(arr[1].equals("Results:")) return;

            if(!(arr[0].equals("[INFO]") || arr[0].equals("[ERROR]"))) continue;
            if(!(arr[1].equals("Tests") && arr[2].equals("run:"))) continue;

            jsonObject.put("Testcases",Integer.valueOf(arr[3].split(",")[0]));
            jsonObject.put("Failures",Integer.valueOf(arr[5].split(",")[0]));
            jsonObject.put("Errors",Integer.valueOf(arr[7].split(",")[0]));
            jsonObject.put("Skipped",Integer.valueOf(arr[9].split(",")[0]));
            // br.readLine();
            StringBuilder result = new StringBuilder();
            arr = Arrays.stream(br.readLine().split(" ")).map(str -> str.replaceAll("[\\x00]","")).toArray(String[]::new);
            JSONArray testCasesInfo = new JSONArray();
            int i = 0;
            while(arr.length > 5 && arr[3].equals("Time") && arr[4].equals("elapsed:")){
                arr = addInfoOrErrorToJson(i,arr,testCasesInfo,br,result);
                i++;
            }

            bw.write(result.toString());
           //bw.newLine();

            jsonObject.put("testCasesInfo",testCasesInfo);

            FileWriter writer = new FileWriter("output.json");
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            br.close();
            bw.flush();
            bw.close();

            return;
        }
    }

    static String[] addInfoOrErrorToJson(int i,String[] arr,JSONArray jsonObject,BufferedReader br,StringBuilder result) throws IOException {
        JSONObject testCaseInfo = new JSONObject();
        testCaseInfo.put("testCaseName",arr[1]);
//        System.out.println(arr.length);

        if(arr.length <= 7){
            testCaseInfo.put("testCaseStatus","passed");
            jsonObject.put(testCaseInfo);
          //  if(i == 0)
             //   result.append("passed");
           // else
                result.append("passed,");
           // br.readLine();
            arr = Arrays.stream(br.readLine().split(" ")).map(str -> str.replaceAll("[\\x00]","")).toArray(String[]::new);
//            System.out.println(Arrays.toString(arr));

            return arr;
        }

        testCaseInfo.put("testCaseStatus","failed");

        StringBuilder sb = new StringBuilder();
        String curr = br.readLine();
        arr = Arrays.stream(curr.split(" ")).map(str -> str.replaceAll("[\\x00]","")).toArray(String[]::new);
        arr = Arrays.stream(arr).map(str -> str.replaceAll(",","")).toArray(String[]::new);
        while(arr.length < 2 || (!arr[0].equals("[ERROR]") && !arr[0].equals("[INFO]"))){
            sb.append(String.join(" ", arr));
            arr = Arrays.stream(br.readLine().split(" ")).map(str -> str.replaceAll("[\\x00]","")).toArray(String[]::new);
        }

        //if(i == 0)
          //  result.append(sb.toString());
        //else
            result.append(sb.toString() + ",");


        testCaseInfo.put("issue",sb.toString());
        jsonObject.put(testCaseInfo);

        return arr;

    }
}

