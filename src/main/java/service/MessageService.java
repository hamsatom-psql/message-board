package service;

import model.Message;
import repository.IMessageRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

class MessageService implements IMessageService {
    private final IMessageRepository repository;

    public MessageService(IMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createMessage(String content, UUID parentId) {
        String author = ""; // TODO FIXME Get author from security
        Message message = new Message().setContent(content).setParentId(parentId);
        repository.saveMessage(message);
    }

    @Override
    public void modifyMessageContent(UUID id, String content) {
        String author = ""; // TODO FIXME Get author from security
        Optional<Message> savedMessage = repository.selectOneMessage(id);
        if (!savedMessage.isPresent()) {
            throw new NoSuchElementException("No message id " + id);
        }
        Message modifiedMessage = savedMessage.get();
        if (!modifiedMessage.getAuthor().equals(author)) {
            throw new SecurityException("User '" + author + "' can't delete message id " + id);
        }
        modifiedMessage.setContent(content);
        repository.saveMessage(modifiedMessage);
    }

    @Override
    public void deleteMessage(UUID id) {
        String author = ""; // TODO FIXME Get author from security
        Optional<Message> savedMessage = repository.selectOneMessage(id);
        if (!savedMessage.isPresent()) {
            throw new NoSuchElementException("No message id " + id);
        }
        if (!savedMessage.get().getAuthor().equals(author)) {
            throw new SecurityException("User '" + author + "' can't delete message id " + id);
        }
        repository.deleteMessage(id);
    }

    @Override
    public List<Message> getAllChildMessages(UUID parentId) {
        return repository.selectChildMessages(parentId);
    }
}