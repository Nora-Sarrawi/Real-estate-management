package com.example.loborems.models;

import javax.persistence.*;

@Entity
@Table(name = "interactions")
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "interaction_type")
    private String interaction_type;
    @Column(name = "interaction_date")
    private String date;
    @Column(name = "interaction_details")
    private String details;
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    private Client client_id;

    public Interaction() {}

    public Interaction(String interaction_type, String date, String details, Client client_id) {
        this.interaction_type = interaction_type;
        this.date = date;
        this.details = details;
        this.client_id = client_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInteraction_type() {
        return interaction_type;
    }

    public void setInteraction_type(String description) {
        this.interaction_type = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Client getClient_id() {
        return client_id;
    }

    public void setClient_id(Client client_id) {
        this.client_id = client_id;
    }

    public Client getClient() {
        return client_id;
    }


}
