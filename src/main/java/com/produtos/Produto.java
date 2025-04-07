package com.produtos;

import com.qol.Named;

public class Produto implements Named {
    private int id;
    private String nome;
    private String descricao;
    private String publicoAlvo;

    public Produto(int id, String nome, String descricao, String publicoAlvo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.publicoAlvo = publicoAlvo;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPublicoAlvo() {
        return publicoAlvo;
    }
}
