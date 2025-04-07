package com.conta;
import java.sql.SQLException;
import java.time.LocalDate;

import com.pessoa.Pessoa;


public class ContaCorrente extends Conta {
    public ContaCorrente(int numero, String agencia, Pessoa titular, int saldo, int chequeEspecial, LocalDate dataAniversario) {
        super(numero, agencia, titular, saldo, chequeEspecial, TipoConta.Corrente, dataAniversario);
    }

    @Override
    public String toString() {
        return "ContaCorrente{" +
                "numero=" + numero +
                ", saldo=" + saldo +
                ", titular='" + titular + '\'' +
                ", chequeEspecial=" + chequeEspecial  +
                '}';
    }

    @Override
    public int getSaldo() throws SQLException {
        ContaDAO dao = ContaDAO.getInstance() ;

        return dao.buscarContaPorId(this.numero).getSaldo();
    }

    @Override
    public void sacar(int valor) throws SQLException {
        ContaDAO dao = ContaDAO.getInstance();

        dao.sacar(this, valor);

        this.saldo -= valor;
    }

    @Override
    public void depositar(int valor) throws SQLException {
        ContaDAO dao = ContaDAO.getInstance();

        dao.depositar(this, valor);

        this.saldo += valor;
    }

    @Override
    public void transferir(int valor, Conta contaDestino) throws SQLException {
        ContaDAO dao = ContaDAO.getInstance();

        dao.transferir(this, contaDestino, valor);

        this.saldo -= valor;
        contaDestino.saldo += valor;
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

        System.out.println("Saldo atual: R$" + (int)(saldoCentavos / 100) + "," + (saldoCentavos % 100));
        System.out.println("Cheque especial: " + this.getChequeEspecial());
        System.out.println("Data de aniversário: " + this.getDataAniversario());
        System.out.println("Tipo de conta: " + "Corrente");
        System.out.println("Titular: " + this.getTitular().getNome());
        System.out.println("Agência: " + this.getAgencia());
    }
}
