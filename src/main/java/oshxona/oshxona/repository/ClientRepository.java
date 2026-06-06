package oshxona.oshxona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oshxona.oshxona.model.Client;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByChatId(String chatId);

}
