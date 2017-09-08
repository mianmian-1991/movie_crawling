package seassoon.court;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class RegulationTHREEPageProcessor implements PageProcessor{

    String cookie = "ASP.NET_SessionId=fsdakgqxrl12grllmybnxosa; Hm_lvt_3f2eaec16fb0951177798309dd3127b7=1504780457,1504784925; Hm_lpvt_3f2eaec16fb0951177798309dd3127b7=1504784925";

    private Site site = Site.me().addCookie("image.lawxp.com",cookie);

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return site;
    }


    public static void main(String[] args){

        String mainpage = "https://www.lawxp.com/statute/";


    }
}
