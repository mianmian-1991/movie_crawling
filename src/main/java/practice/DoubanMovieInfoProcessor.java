package practice;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.selector.Selectable;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 爬取电影列表 v0.1
 * 更多影片信息可以去电影页面补充
 */
public class DoubanMovieInfoProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(1).setDomain("www.douban.com")
            .setSleepTime(10000).setRetrySleepTime(10000);

    private String regex_page = "https://movie\\.douban\\.com/people/BBmachtalles/collect\\?start=\\d+.+";

    private String DB_URL = "jdbc:mysql://localhost:3306/chimney?characterEncoding=utf8&useSSL=false";
    private String USER = "root";
    private String PASS = "123456";
    private String tableName = "movie_collect";

//    private String outputPath = "/Users/chimney/Coding Practice/movieresulttest.txt";

    private BufferedWriter bw;



    public DoubanMovieInfoProcessor(){
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("movie.txt"),true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ConnectionDB connectionDB = new ConnectionDB(DB_URL, USER, PASS);//用于写入数据库

    public void process(Page page) {




            }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String mainUrl = "https://movie.douban.com/people/BBmachtalles/collect";
        String mainUrl2 = "https://movie.douban.com/people/BBmachtalles/collect?start=0&amp;sort=time&amp;rating=all&amp;filter=all&amp;mode=grid";

        String movieListPath = "/Users/chimney/Coding Practice/movielist";

        List<String> movieList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((new FileInputStream(new File(movieListPath)))));
            String url;
            while ((url = br.readLine())!=null){
                movieList.add(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        String[] movies = movieList.toArray(new String[movieList.size()]);


        BasicConfigurator.configure();
        Spider.create(new DoubanMovieInfoProcessor()).addUrl(movies)
//                .addPipeline(new FilePipeline("/Users/chimney/Coding Practice/movieresulttest.txt"))
                .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                .thread(3).run();
    }

    public static String removeHTMLLabel(String text) {
        if (text == null) return null;
        StringBuilder textBuilder = new StringBuilder(text);
        removeLabel(textBuilder);
        return textBuilder.toString().trim();
    }

    private static void removeLabel(StringBuilder contextDetail) {
        Pattern p = Pattern.compile("<.+?>");
        Matcher m = p.matcher(contextDetail);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            contextDetail.delete(start, end);
            m = p.matcher(contextDetail);
        }
    }

}
