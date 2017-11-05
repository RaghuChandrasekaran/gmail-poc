package com.github.raghuchandrasekaran.model;

public class Link {

    private String url;

    private String subject;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Link{" + "url='" + url + '\'' + ", subject='" + subject + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (!url.equals(link.url)) return false;
        return subject.equals(link.subject);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + subject.hashCode();
        return result;
    }
}
