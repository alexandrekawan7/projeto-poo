package com.conta;
import java.sql.SQLException;
import java.time.LocalDate;

import com.pessoa.Pessoa;

public abstract class Conta implements Extrato {
    protected String agencia;
    protected int numero;
    protected int saldo;
    protected int chequeEspecial;
    protected Pessoa titular;
    protected TipoConta tipoConta;
    protected LocalDate dataAniversario;

    public Conta(int numero, String agencia, Pessoa titular, int saldo, int chequeEspecial, TipoConta tipoConta, LocalDate dataAniversario) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldo;
        this.chequeEspecial = chequeEspecial;
        this.agencia = agencia;
        this.tipoConta = tipoConta;
        this.dataAniversario = dataAniversario;
    }
    
    public int getNumero() {
        return numero;
    }

    public Pessoa getTitular() {
        return titular;
    }

    public String getAgencia(){
        return agencia;
    }

    public int getChequeEspecial(){
        return chequeEspecial;
    }

    public TipoConta geTipoConta() {
        return tipoConta;
    }

    public LocalDate getDataAniversario() {
        return dataAniversario;
    }


    /**
     * Método abstrato que deve ser implementado pelas subclasses para obter o saldo.
     * 
     * @return saldo atual da conta
     */
    public abstract int getSaldo() throws SQLException;

    /**
     * Método abstrato que deve ser implementado pelas subclasses para depositar dinheiro.
     * @param valor valor a ser depositado
     */
    public abstract void depositar(int valor) throws SQLException;

    /**
     * Método abstrato que deve ser implementado pelas subclasses para sacar dinheiro.
     * @param valor valor a ser sacado
     */
    public abstract void sacar(int valor) throws SQLException;

    /**
     * Método abstrato que deve ser implementado pelas subclasses para transferir dinheiro.
     * @param valor valor a ser transferido
     * @param contaDestino conta de destino
     */
    public abstract void transferir(int valor, Conta contaDestino) throws SQLException;

    @Override
    public String toString() {
        return "Conta{" +
                "numero=" + numero +
                ", saldo=" + saldo +
                ", titular='" + titular + '\'' +
                '}';
    }
}
