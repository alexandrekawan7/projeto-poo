package com.pessoa;

public class PessoaDTO {
    public String nome;
    public String email;
    public String endereco;
    public int renda;
    public String telefone;
    public String certificado;
    public TipoPessoa tipoPessoa;

    public PessoaDTO() { }

    public PessoaDTO(String nome, String email, String endereco, int renda, String telefone, TipoPessoa tipoPessoa, String certificado) {
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
        this.renda = renda;
        this.telefone = telefone;
        this.certificado = certificado;
        this.tipoPessoa = tipoPessoa;
    }
}
