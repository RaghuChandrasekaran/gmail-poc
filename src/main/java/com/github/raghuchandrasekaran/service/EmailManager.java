package com.github.raghuchandrasekaran.service;

import com.github.raghuchandrasekaran.model.Message;

import java.util.List;

public interface EmailManager {

    /**
     *
     * @param labelName
     * @return label id of the label
     */
    String getLabelId(String labelName);

    Message getMessage(String messageId);

    List<Message> getMessages(String labelId);

    boolean markMessageAsRead(String messageId);

}
