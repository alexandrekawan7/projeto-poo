package com.pessoa;

public class PessoaJuridica extends Pessoa {
    private String cnpj;

    public PessoaJuridica(int idConta, String nome, String email, String endereco, int renda, String telefone, String cnpj) {
        super(idConta, nome, email, endereco, renda, telefone, TipoPessoa.Juridica);
        this.cnpj = cnpj;
    }

    @Override
    public String getCertificado() {
        return cnpj;
    }

    @Override
    public String toString() {
        return "PessoaJuridica{" +
                super.toString() +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
