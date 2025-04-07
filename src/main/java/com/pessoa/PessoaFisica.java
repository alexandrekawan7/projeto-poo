package com.pessoa;

public class PessoaFisica extends Pessoa {
    private String cpf;

    public PessoaFisica(int idConta, String nome, String email, String endereco, int renda, String telefone, String cpf) {
        super(idConta, nome, email, endereco, renda, telefone, TipoPessoa.Fisica);
        this.cpf = cpf;
    }

    @Override
    public String getCertificado() {
        return cpf;
    }

    @Override
    public String toString() {
        return "PessoaFisica{" +
                super.toString() +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
