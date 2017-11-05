package com.github.raghuchandrasekaran.service.impl;

import com.github.raghuchandrasekaran.model.Link;
import com.github.raghuchandrasekaran.model.Message;
import com.github.raghuchandrasekaran.service.LinkManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LinkManagerImpl implements LinkManager {

    private Document getDocument(String html){
        return Jsoup.parse(html);
    }

    private Elements getLinks(Document doc){
        return doc.select("a[href]");
    }

    @Override
    public List<Link> getLinks(Message message) {
        List<Link> links = new ArrayList<>();
        Elements elements = getLinks(getDocument(message.getMessageData()));
        for(Element ele:elements){
            String absHref = ele.attr("abs:href");
            Link link = new Link();
            link.setSubject(message.getEmailSubject());
            link.setUrl(absHref);
            links.add(link);
        }
        return links;
    }
}
