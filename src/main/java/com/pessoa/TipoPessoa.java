package com.pessoa;

public enum TipoPessoa {
    Fisica {
        @Override
        public String toString() {
            return "Pessoa Fisica";
        }
    },
    Juridica {
        @Override
        public String toString() {
            return "Pessoa Juridica";
        }
    }
}
