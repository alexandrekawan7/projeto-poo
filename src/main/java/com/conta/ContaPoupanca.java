package com.conta;
import com.pessoa.Pessoa;

import java.sql.SQLException;
import java.time.LocalDate;

public class ContaPoupanca extends Conta  {
    private final double rendimento = 0.01;

    public ContaPoupanca(int numero, String agencia, Pessoa titular, int saldo, int chequeEspecial, LocalDate dataAniversario) {
        super(numero, agencia, titular, saldo, chequeEspecial, TipoConta.Corrente, dataAniversario);
    }

    public double getRendimento() {
        return rendimento;
    }

    public double getTotalRendimento() {
        return getSaldo() - saldo;
    }

    @Override
    public int getSaldo() {
        LocalDate hoje = LocalDate.now();
        long meses = dataAniversario.until(hoje).toTotalMonths();

        if (meses < 0) {
            return saldo;
        }

        return (int) (saldo * Math.pow(1 + rendimento, meses));
    }

    @Override
    public void depositar(int valor) throws SQLException {
        ContaDAO dao = ContaDAO.getInstance();

        dao.depositar(this, valor);

        this.saldo += valor;
    }

    @Override
    public void sacar(int valor) throws SQLException {
        ContaDAO dao = ContaDAO.getInstance();

        dao.sacar(this, valor);

        this.saldo -= valor;
    }

    @Override
    public void transferir(int valor, Conta contaDestino) throws SQLException {
        ContaDAO dao = ContaDAO.getInstance();

        dao.transferir(this, contaDestino, valor);

        this.saldo -= valor;
        contaDestino.saldo += valor;
    }

    @Override
    public String toString() {
        return "ContaPoupanca{" +
                "numero=" + numero +
                ", saldo=" + saldo +
                ", titular='" + titular + '\'' +
                ", rendimento=" + rendimento +
                ", dataAniversario=" + dataAniversario +
                '}';
    }

    @Override
    public void exibirExtrato() throws SQLException {
        ContaDAO dao = ContaDAO.getInstance();

        var extrato = dao.exibirExtrato(this);

        System.out.println("Extrato da conta " + this.numero + ":");
        for (var transacao : extrato) {
            System.out.println(transacao);
        }

        int saldoCentavos = this.getSaldo();

        System.out.println("Total em rendimentos: " + this.getRendimento());
        System.out.println("Saldo atual: R$" + (int)(saldoCentavos / 100) + "," + (saldoCentavos % 100));
        System.out.println("Cheque especial: " + this.getChequeEspecial());
        System.out.println("Data de aniversário: " + this.getDataAniversario());
        System.out.println("Tipo de conta: " + "Poupança");
        System.out.println("Titular: " + this.getTitular().getNome());
        System.out.println("Agência: " + this.getAgencia());
    }
}
