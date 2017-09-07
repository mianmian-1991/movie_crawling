package seassoon.court;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class RegulationTWOPageProcessor implements PageProcessor{

    private Site site = Site.me();

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return site;
    }
}
