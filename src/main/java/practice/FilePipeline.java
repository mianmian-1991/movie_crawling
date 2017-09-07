package practice;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.*;
import java.util.Map;

public class FilePipeline implements Pipeline{

    String filepath = "/Users/chimney/Coding Practice/outputtext.txt";

    public FilePipeline() {
    }

    public FilePipeline(String filepath) {
        this.filepath = filepath;
    }

    public void process(ResultItems resultItems, Task task) {
        try{
            OutputStream fos = new FileOutputStream(new File(filepath),true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            for(Map.Entry<String,Object> entry:resultItems.getAll().entrySet()){
                bw.write(entry.getKey()+": "+entry.getValue()+"\n");
            }
            bw.write("----------------------------\n");
            bw.flush();
            bw.close();
            osw.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
