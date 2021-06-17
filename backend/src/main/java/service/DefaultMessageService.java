package service;

import model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import repository.IMessageRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultMessageService implements IMessageService {
    private final IMessageRepository repository;

    @Autowired
    public DefaultMessageService(IMessageRepository repository) {
        this.repository = repository;
        repository.saveMessage(new Message().setAuthor("user").setContent("content").setParentId(UUID.randomUUID())); // TODO FIXME remove
        repository.saveMessage(new Message().setAuthor("user2").setContent("content2"));
        UUID parentId = UUID.randomUUID();
        repository.saveMessage(new Message().setAuthor("user3").setContent("content3").setId(parentId));
        repository.saveMessage(new Message().setAuthor("user3").setContent("content4").setParentId(parentId));
    }

    private String getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .orElseThrow(() -> new BadCredentialsException("Not logged in"));
    }

    @Override
    public UUID createMessage(String content, UUID parentId) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty message content");
        }
        Message message = new Message().setContent(content).setParentId(parentId).setAuthor(getCurrentUser());
        return repository.saveMessage(message);
    }

    @Override
    public void modifyMessageContent(UUID id, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty new modified message content in " + id);
        }
        Optional<Message> savedMessage = repository.selectOneMessage(id);
        if (!savedMessage.isPresent()) {
            throw new NoSuchElementException("No message id " + id);
        }
        String currentUser = getCurrentUser();
        Message modifiedMessage = savedMessage.get();
        if (!modifiedMessage.getAuthor().equals(currentUser)) {
            throw new SecurityException("User '" + currentUser + "' can't modify message " + id);
        }
        modifiedMessage.setContent(content);
        repository.updateMessage(modifiedMessage);
    }

    @Override
    public void deleteMessage(UUID id) {
        Optional<Message> savedMessage = repository.selectOneMessage(id);
        if (!savedMessage.isPresent()) {
            throw new NoSuchElementException("No message id " + id);
        }
        String currentUser = getCurrentUser();
        if (!savedMessage.get().getAuthor().equals(currentUser)) {
            throw new SecurityException("User '" + currentUser + "' can't delete message " + id);
        }
        repository.deleteMessage(id);
    }

    @Override
    public List<Message> getAllChildMessages(UUID parentId) {
        return repository.selectChildMessages(parentId);
    }
}
