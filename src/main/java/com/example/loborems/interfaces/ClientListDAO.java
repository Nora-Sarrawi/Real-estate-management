package com.example.loborems.interfaces;

import com.example.loborems.models.Client;
import java.util.List;

public interface ClientListDAO {

    void addClient(Client client) throws Exception;

    void updateClient(Client client) throws Exception;

    void deleteClient(int clientId) throws Exception;

    Client getClientById(int clientId) throws Exception;

    List<Client> getAllClients() throws Exception;

    List<Client> searchClientsByName(String name) throws Exception;
}
