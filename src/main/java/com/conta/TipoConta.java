package com.conta;

public enum TipoConta {
    Corrente {
        @Override
        public String toString() {
            return "C";
        }
    },
    Poupanca {
        @Override
        public String toString() {
            return "P";
        }
    }
}
