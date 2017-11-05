package com.github.raghuchandrasekaran.app;

import com.github.raghuchandrasekaran.model.Link;
import com.github.raghuchandrasekaran.model.Message;
import com.github.raghuchandrasekaran.service.EmailManager;
import com.github.raghuchandrasekaran.service.LinkManager;
import com.github.raghuchandrasekaran.service.impl.GMailEmailManagerImpl;
import com.github.raghuchandrasekaran.service.impl.LinkManagerImpl;

import java.util.List;

public class Application {

    private static final String LABEL_NAME = "NewsLetter";

    public static void main(String[] args) {
        EmailManager emailMgr = new GMailEmailManagerImpl();
        LinkManager linkMgr = new LinkManagerImpl();
        String labelId = emailMgr.getLabelId(LABEL_NAME);
        List<Message> messages = emailMgr.getMessages(labelId);
        for(Message msg:messages){
            List<Link> links=linkMgr.getLinks(msg);
            System.out.println(links);
            emailMgr.markMessageAsRead(msg.getId());
        }
    }
}
