package com.pessoa;

import com.qol.Named;

public abstract class Pessoa implements Named {
    protected int id;
    protected String nome;
    protected String email;
    protected String endereco;
    protected int renda;
    protected String telefone;
    protected TipoPessoa tipoPessoa;

    public Pessoa(int idConta, String nome, String email, String endereco, int renda, String telefone, TipoPessoa tipoPessoa) {
        this.id = idConta;
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
        this.renda = renda;
        this.telefone = telefone;
        this.tipoPessoa = tipoPessoa;
    }

    // Getters
    public int getId() {
        return id;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getEndereco() {
        return endereco;
    }

    public int getRenda() {
        return renda;
    }

    public String getTelefone() {
        return telefone;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    /**
     * Método abstrato para obter o CPF ou CNPJ, dependendo do tipo de pessoa.
     * 
     * @return O CPF ou CNPJ da pessoa.
     */
    public abstract String getCertificado();

    @Override
    public String toString() {
        return "Pessoa{" +
                "idConta=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", endereco='" + endereco + '\'' +
                ", renda=" + renda +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
