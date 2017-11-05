package com.github.raghuchandrasekaran.model;

public class Message {

    private String id;

    private String messageData;

    private String emailSubject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        if (messageData != null ? !messageData.equals(message.messageData) : message.messageData != null) return false;
        return emailSubject != null ? emailSubject.equals(message.emailSubject) : message.emailSubject == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (messageData != null ? messageData.hashCode() : 0);
        result = 31 * result + (emailSubject != null ? emailSubject.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" + "id='" + id + '\'' + ", messageData='" + messageData + '\'' + ", emailSubject='" + emailSubject + '\'' + '}';
    }
}
