package com.escriturario;

import com.qol.Named;

public class Escriturario implements Named {
    private int idEscriturario;
    private String nome;
    private String cpf;

    // Construtor vazio
    public Escriturario() {}

    // Construtor completo
    public Escriturario(int idEscriturario, String nome, String cpf) {
        this.idEscriturario = idEscriturario;
        this.nome = nome;
        this.cpf = cpf;
    }

    // Getters e Setters
    public int getId() {
        return idEscriturario;
    }

    public void setIdEscriturario(int idEscriturario) {
        this.idEscriturario = idEscriturario;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "Escriturario{" +
                "idEscriturario=" + idEscriturario +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
