package com.github.raghuchandrasekaran.service.impl;

import com.github.raghuchandrasekaran.model.Message;
import com.github.raghuchandrasekaran.service.EmailManager;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;

public class GMailEmailManagerImpl implements EmailManager {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Link Aggregator";
    /**
     * Default User
     */
    private static final String USER = "me";
    /**
     * Directory to store user credentials for this application.
     */
    private java.io.File dataStoreDir;
    /**
     * Global instance of the JSON factory.
     */
    private JsonFactory jsonFactory;
    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private List<String> gMailScopes;
    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private FileDataStoreFactory dataStoreFactory;
    /**
     * Global instance of the HTTP transport.
     */
    private HttpTransport httpTransport;

    private Gmail gmailService;

    public GMailEmailManagerImpl() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreDir = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-java-quickstart");
            dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
            jsonFactory = JacksonFactory.getDefaultInstance();
            gMailScopes = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
            // Build a new authorized API client service.
            gmailService = getGMailService();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = this.getClass().getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, gMailScopes).setDataStoreFactory(dataStoreFactory).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + dataStoreDir.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     */
    private Gmail getGMailService() throws IOException {
        Credential credential = this.authorize();
        return new Gmail.Builder(httpTransport, jsonFactory, credential).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * List all Messages of the user's mailbox with labelIds applied.
     *
     * @param labelIds Only return Messages with these labelIds applied.
     * @throws IOException
     */
    private List<com.google.api.services.gmail.model.Message> listMessagesWithLabels(List<String> labelIds) throws IOException {
        ListMessagesResponse response = gmailService.users().messages().list(USER).setLabelIds(labelIds).execute();
        List<com.google.api.services.gmail.model.Message> messages = new ArrayList<>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmailService.users().messages().list(USER).setLabelIds(labelIds).setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return messages;
    }

    /**
     * List all labels of the user's mailbox
     *
     * @throws IOException
     */
    private List<Label> getLabels() throws IOException {
        ListLabelsResponse response = gmailService.users().labels().list(USER).execute();
        return  response.getLabels();
    }

    /**
     * Get GMail Message of specified id
     *
     * @param messageId
     * @throws IOException
     */
    private com.google.api.services.gmail.model.Message getEmailMessage(String messageId) throws IOException {
        return gmailService.users().messages().get(USER, messageId).execute();
    }

    private Predicate<MessagePartHeader> getSubject() {
        return header -> header.getName().equalsIgnoreCase("Subject");
    }

    @Override
    public String getLabelId(String labelName) {
        try {
            return getLabels().stream().filter(label -> label.getName().equals(labelName)).findAny().map(Label::getId).orElse("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public Message getMessage(String messageId) {
        Message message = new Message();
        try {
            com.google.api.services.gmail.model.Message emailMessage = getEmailMessage(messageId);
            MessagePart part = emailMessage.getPayload();
            String messageData = StringUtils.newStringUtf8(Base64.decodeBase64(part.getBody().getData()));
            message.setMessageData(messageData);
            message.setId(emailMessage.getId());
            String subject = part.getHeaders().stream().filter(getSubject()).findAny().map(MessagePartHeader::getValue).orElse("");
            message.setEmailSubject(subject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public List<Message> getMessages(String labelId) {
        List<Message> messages = new ArrayList<>();
        try {
            List<com.google.api.services.gmail.model.Message> emailMessages = listMessagesWithLabels(Arrays.asList(labelId,"UNREAD"));
            for (com.google.api.services.gmail.model.Message msg : emailMessages) {
                messages.add(getMessage(msg.getId()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public boolean markMessageAsRead(String messageId) {
        boolean flag = false;
        try {
            ModifyMessageRequest mods = new ModifyMessageRequest()
                    .setRemoveLabelIds(Collections.singletonList("UNREAD"));
            com.google.api.services.gmail.model.Message message = gmailService.users().messages().modify(USER, messageId, mods).execute();
            flag = Optional.ofNullable(message).isPresent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  flag;
    }
}
