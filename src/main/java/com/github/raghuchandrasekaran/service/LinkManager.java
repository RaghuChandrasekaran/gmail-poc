package com.github.raghuchandrasekaran.service;

import com.github.raghuchandrasekaran.model.Link;
import com.github.raghuchandrasekaran.model.Message;

import java.util.List;

public interface LinkManager {

    List<Link> getLinks(Message message);
}
