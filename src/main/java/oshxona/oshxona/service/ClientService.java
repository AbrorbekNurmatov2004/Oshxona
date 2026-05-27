package oshxona.oshxona.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oshxona.oshxona.model.Client;
import oshxona.oshxona.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public Client registerByChatId(String chatId, String name) {
        return repository.findByChatId(chatId).map(existingClient -> {
            existingClient.setName(name);
            return repository.save(existingClient);
        }).orElseGet(() -> {
            Client client = new Client();
            client.setName(name);
            client.setChatId(chatId);
            return repository.save(client);
        });
    }

    public void savePhoneNumber(String chatId, String phone) {
        repository.findByChatId(chatId).ifPresent(client -> {
            client.setPhone(phone);
            repository.save(client);
        });
    }

    public Client findByChatId(String chatId) {
        return repository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException(chatId));
    }

    public void saveLocation(String chatId, double latitude, double longitude) {
        Client client = findByChatId(chatId);
        client.setLatitude(latitude);
        client.setLongitude(longitude);
        repository.save(client);
    }
}
