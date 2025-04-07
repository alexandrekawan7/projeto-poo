package com.ifce;
import java.math.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.Scanner;
import com.escriturario.Escriturario;
import com.escriturario.EscriturarioDAO;
import com.pessoa.PessoaDAO;
import com.pessoa.PessoaDTO;
import com.pessoa.TipoPessoa;
import com.pessoa.Pessoa;
import com.conta.Conta;
import com.conta.ContaDTO;
import com.conta.ContaDAO;
import com.conta.ContaCorrente;
import com.conta.ContaPoupanca;
import com.conta.TipoConta;
import com.produtos.Produto;
import com.produtos.ProdutoDAO;

public class Main {
    public static void main(String[] args) {

        int menu = 0;
        Scanner ler = new Scanner(System.in);
        Escriturario escriturario = EscriturarioDAO.getInstance().getEscriturario();

        PessoaDAO pessoaDAO = PessoaDAO.getInstance();

        System.out.println("Escriturário: " + escriturario.getNome());

        while (menu != 5) {
            System.out.println("##### BANK DESK #####");
            System.out.print("""
                    Digite o número ação que deseja realizar: \n
                    1.Gerenciar clientes\n
                    2.Gerenciar contas\n
                    3.Operações financeira\n
                    4.Gerenciar produtos\n
                    5.Encerrar Aplicativo\n
                    Escolha: """);
            menu = ler.nextInt();
            if (menu == 1) {
                int subMenu = 0;
                while (subMenu != 4) {
                    System.out.print("""
                            Digite o número da ação que deseja realizar: \n
                            1. Cadastrar pessoa\n
                            2. Alterar dados\n
                            3. Deletar pessoa\n
                            4. Retornar ao menu\n
                            Escolha: """);
                    subMenu = ler.nextInt();

                    if (subMenu == 1) {
                        // Cadastrar pessoa (mantém como estava)
                        PessoaDTO dto = new PessoaDTO();

                        System.out.print("""
                                Selecione tipo de pessoa deseja cadastrar:\n
                                1.Pessoa Física\n
                                2.Pessoa Jurídica\n
                                Escolha: """);
                        dto.tipoPessoa = ler.nextInt() == 1 ? TipoPessoa.Fisica : TipoPessoa.Juridica;

                        if (dto.tipoPessoa == TipoPessoa.Fisica) {
                            System.out.println("Digite o CPF do cliente:");
                            dto.certificado = ler.next();
                            ler.nextLine();
                        } else {
                            System.out.println("Digite o CNPJ do cliente:");
                            dto.certificado = ler.next();
                            ler.nextLine();
                        }

                        System.out.println("Digite o nome do cliente:");
                        dto.nome = ler.nextLine();
                        ler.nextLine();

                        System.out.println("Digite o email do cliente:");
                        dto.email = ler.nextLine();

                        System.out.println("Digite o endereço do cliente:");
                        dto.endereco = ler.nextLine();

                        System.out.println("Digite a renda do cliente:");
                        String rendaDouble = ler.next(); // Lê como double
                        ler.nextLine(); // Consome a quebra de linha
                        
                        var splitted = rendaDouble.split(",");

                        dto.renda = Integer.parseInt(splitted[0]) * 100 + Integer.parseInt(splitted[1]);

                        System.out.println("Digite o telefone do cliente:");

                        dto.telefone = ler.nextLine();

                        try {
                            PessoaDAO.getInstance().criarPessoa(dto);
                            System.out.println("Cliente cadastrado com sucesso!");
                        } catch (SQLException e) {
                            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
                        }

                    } else if (subMenu == 2) {
                        System.out.println("Digite o CPF ou CNPJ do cliente que deseja alterar:");
                        String certificado = ler.next();
                        try {
                            Pessoa pessoa = PessoaDAO.getInstance().getByCpf(certificado);
                            PessoaDTO dto = new PessoaDTO();

                            if (pessoa == null) {
                                System.out.println("Pessoa não encontrada.");
                                continue;
                            }

                            System.out.println("Digite o novo nome AQUI:");
                            dto.nome = ler.next();
                            ler.nextLine();
                            System.out.println("Digite o novo email:");
                            dto.email = ler.next();
                            ler.nextLine();
                            System.out.println("Digite o novo endereço:");
                            dto.endereco = ler.next();
                            ler.nextLine();

                            System.out.println("Digite a nova renda:");
                            String rendaDouble = ler.next(); // Lê como double
                            ler.nextLine(); // Consome a quebra de linha
                            
                            var splitted = rendaDouble.split(",");
    
                            dto.renda = Integer.parseInt(splitted[0]) * 100 + Integer.parseInt(splitted[1]);

                            System.out.println("Digite o novo telefone:");
                            dto.telefone = ler.next();
                            ler.nextLine();

                            System.out.println("Digite o novo CPF/CNPJ:");
                            String novoCertificado = ler.nextLine();
                            dto.certificado = novoCertificado;

                            boolean atualizado = PessoaDAO.getInstance().update(pessoa, dto);

                            if (atualizado) {
                                System.out.println("Pessoa atualizada com sucesso.");
                            } else {
                                System.out.println("Erro ao atualizar pessoa.");
                            }

                        } catch (SQLException e) {
                            System.out.println("Erro ao acessar o banco de dados: " + e.getMessage());
                        }
                    } else if (subMenu == 3) {
                        System.out.println("Deletar pessoa...");
                        System.out.println("Digite o CPF ou CNPJ da pessoa a ser deletada:");
                        String certificado = ler.nextLine();

                        try {
                            Pessoa pessoa = PessoaDAO.getInstance().getByCpf(certificado);
                            if (pessoa == null) {
                                System.out.println("Pessoa não encontrada.");
                                continue;
                            }

                            boolean deletado = PessoaDAO.getInstance().delete(pessoa.getId());

                            if (deletado) {
                                System.out.println("Pessoa deletada com sucesso.");
                            } else {
                                System.out.println("Erro ao deletar pessoa.");
                            }

                        } catch (SQLException e) {
                            System.out.println("Erro ao deletar pessoa: " + e.getMessage());
                        }
                    } else if (subMenu == 4) {
                        System.out.println("Retornando ao menu principal...");
                    } else {
                        System.out.println("Opção inválida.");
                    }
                }
            } else if (menu == 2) {
                int subMenu = 0;
                while (subMenu != 4) {
                    System.out.print("""
                            Digite o número da ação que deseja realizar: \n
                            1.Criar Conta\n
                            2.Conferir Extrato\n
                            3.Deletar Conta\n
                            4.Retornar ao Menu\n
                            Escolha: """);
                    subMenu = ler.nextInt();
                    ler.nextLine();

                    if (subMenu == 1) {
                        System.out.println("Digite o CPF ou CNPJ do titular da conta:");
                        String certificado = ler.nextLine();

                        try {
                            Pessoa titular = PessoaDAO.getInstance().getByCpf(certificado);
                            if (titular == null) {
                                System.out.println("Pessoa não encontrada.");
                                continue;
                            }

                            ContaDTO dto = new ContaDTO();
                            dto.titular = titular;

                            System.out.println("Digite a agência da conta:");
                            dto.agencia = ler.nextLine();
                            System.out.print("""
                                    Selecione o tipo da conta:\n
                                    1. Conta Corrente\n
                                    2. Conta Poupança\n
                                    Escolha: """);
                            int tipo = ler.nextInt();
                            dto.tipoConta = (tipo == 1) ? TipoConta.Corrente : TipoConta.Poupanca;

                            Conta contaCriada = ContaDAO.getInstance().criarConta(dto);
                            System.out.println("Conta criada com sucesso! Detalhes:");
                            System.out.println(contaCriada);

                        } catch (SQLException e) {
                            System.out.println("Erro ao buscar pessoa: " + e.getMessage());
                        }

                    } else if (subMenu == 2) {
                        System.out.println("Digite o número da conta que deseja ver extrato");
                        int numeroConta = ler.nextInt();

                        try {
                            Conta conta = ContaDAO.getInstance().buscarContaPorId(numeroConta);
                            System.out.println("Extrato da conta: ");
                                    try {
                                        conta.exibirExtrato();
                                    } catch (SQLException e) {
                                        System.out.println("Erro ao consultar extrato: " + e.getMessage());
                                    }

                        } catch (SQLException e) {
                            System.out.println("Erro ao acessar o banco de dados: " + e.getMessage());
                        }
                        
                    }
                    

                    else if (subMenu == 3) {
                        System.out.println("Digite o CPF do cliente que deseja deletar a conta:");
                        String cpf = ler.nextLine();

                        try {
                            Pessoa pessoa = PessoaDAO.getInstance().getByCpf(cpf); // método que retorna Pessoa pelo CPF
                            if (pessoa == null) {
                                System.out.println("Cliente não encontrado.");
                                return;
                            }

                            List<Conta> contas = ContaDAO.getInstance().listarContasPorCerificado(cpf);

                            if (contas.isEmpty()) {
                                System.out.println("Nenhuma conta encontrada para este cliente.");
                                return;
                            }

                            System.out.println("Contas encontradas:");
                            for (int i = 0; i < contas.size(); i++) {
                                System.out.println((i + 1) + ". " + contas.get(i));
                            }

                            System.out.print("Digite o número da conta que deseja deletar (escolha pelo índice): ");
                            int escolha = ler.nextInt();
                            ler.nextLine();

                            Conta contaSelecionada = contas.get(escolha - 1);
                            boolean deletado = ContaDAO.getInstance().deletarConta(contaSelecionada.getNumero());

                            if (deletado) {
                                System.out.println("Conta deletada com sucesso!");
                            } else {
                                System.out.println("Erro ao deletar conta.");
                            }

                        } catch (SQLException e) {
                            System.out.println("Erro ao acessar banco de dados: " + e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Índice inválido.");
                        }
                    
                    } else if (subMenu == 4) {
                        System.out.println("Retornando ao menu principal...");

                        break;
                    } else {
                        System.out.println("Opção inválida.");
                    }
                }

            } else if (menu == 3) {
                System.out.println("Digite o CPF ou CNPJ do cliente:");
                String certificado = ler.next();
                ler.nextLine();

                try {
                    List<Conta> contas = ContaDAO.getInstance().listarContasPorCerificado(certificado);

                    if (contas.isEmpty()) {
                        System.out.println("Nenhuma conta encontrada para o CPF/CNPJ informado.");
                    } else {
                        System.out.println("Contas encontradas:");
                        for (Conta conta : contas) {
                            System.out.println("Número: " + conta.getNumero() + " | Tipo: "
                                    + conta.getClass().getSimpleName() + " | Titular: " + conta.getTitular());
                        }

                        System.out.println("Digite o número da conta desejada:");
                        int numeroConta = ler.nextInt();
                        ler.nextLine();

                        Conta contaSelecionada = null;
                        for (Conta conta : contas) {
                            if (conta.getNumero() == numeroConta) {
                                contaSelecionada = conta;
                                break;
                            }
                        }

                        if (contaSelecionada == null) {
                            System.out.println("Conta não encontrada.");
                        } else {
                            int subMenu = 0;
                            while (subMenu != 5) {     
                                    System.out.print("""
                                        Digite o número da ação que deseja realizar:\n
                                        1. Conferir Extrato\n
                                        2. Sacar\n
                                        3. Depositar\n
                                        4. Transferir\n
                                        5. Retornar ao menu\n
                                        Escolha: """);
                                subMenu = ler.nextInt();
                                ler.nextLine();

                                if (subMenu == 1) {
                                    System.out.println("Extrato da conta: ");
                                    try {
                                        contaSelecionada.exibirExtrato();
                                    } catch (SQLException e) {
                                        System.out.println("Erro ao consultar extrato: " + e.getMessage());
                                    }
                                } else if (subMenu == 2) {
                                    System.out.println("Digite o valor a sacar:");
                                    String valor_sacar = ler.nextLine();
                                    var splitted = valor_sacar.split(",");
                                    int valor = Integer.parseInt(splitted[0]) * 100 + Integer.parseInt(splitted[1]);
                                    contaSelecionada.sacar(valor);


                                } else if (subMenu == 3) {
                                    System.out.println("Digite o valor a depositar:");
                                    String valor_sacar = ler.nextLine();
                                    var splitted = valor_sacar.split(",");
                                    int valor = Integer.parseInt(splitted[0]) * 100 + Integer.parseInt(splitted[1]);
                                    contaSelecionada.depositar(valor);
                                } else if (subMenu == 4) {
                                    System.out.println("Transferência...");
                                    System.out.println("Digite o valor a transferir:");
                                    String valor_sacar = ler.nextLine();
                                    var splitted = valor_sacar.split(",");
                                    int valor = Integer.parseInt(splitted[0]) * 100 + Integer.parseInt(splitted[1]);
                                    System.out.println("Digite o numero da conta");
                                    int num_conta = ler.nextInt();
                                    Conta destino = ContaDAO.getInstance().buscarContaPorId(num_conta);
                                    contaSelecionada.transferir(valor, destino);
                                    
                                }else if (subMenu == 5) {
                                    System.out.println("Retornando ao menu principal...");
                                } else {
                                    System.out.println("Opção inválida.");
                                }
                            }
                        }
               }
               
            } catch (SQLException e) {
                            System.out.println("Erro ao buscar contas: " + e.getMessage());
                }           } else if (menu == 4) {
                int subMenu = 0;
                while (subMenu != 3) {
                    System.out.print("""
                            Digite o número da ação que deseja realizar:\n
                            1. Listar Produtos\n
                            2. Vender Produto\n
                            3. Retornar ao Menu\n
                            Escolha: """);
                    subMenu = ler.nextInt();
                    ler.nextLine(); // Consumir quebra de linha

                    if (subMenu == 1) {
                        try {
                            ArrayList<Produto> produtos = ProdutoDAO.getInstance().getAllProdutos();

                            if (produtos.isEmpty()) {
                                System.out.println("Nenhum produto cadastrado.");
                                continue;
                            }

                            System.out.println("Produtos disponíveis:");
                            for (Produto p : produtos) {
                                System.out.printf("ID: %d | Nome: %s | Público-alvo: %s%n",
                                        p.getId(), p.getNome(), p.getPublicoAlvo());
                            }
                        } catch (SQLException e) {
                            System.out.println("Erro ao acessar o banco de dados: " + e.getMessage());
                        }

                    } else if (subMenu == 2) {
                        try {
                            ArrayList<Produto> produtos = ProdutoDAO.getInstance().getAllProdutos();

                            if (produtos.isEmpty()) {
                                System.out.println("Nenhum produto cadastrado.");
                                continue;
                            }

                            System.out.println("Produtos disponíveis:");
                            for (Produto p : produtos) {
                                System.out.printf("ID: %d | Nome: %s | Público-alvo: %s%n",
                                        p.getId(), p.getNome(), p.getPublicoAlvo());
                            }

                            System.out.print("Digite o ID do produto que deseja vender: ");
                            int idProduto = ler.nextInt();
                            ler.nextLine(); // Limpa quebra de linha

                            Produto produtoSelecionado = null;
                            for (Produto p : produtos) {
                                if (p.getId() == idProduto) {
                                    produtoSelecionado = p;
                                    break;
                                }
                            }

                            if (produtoSelecionado == null) {
                                System.out.println("Produto não encontrado.");
                                continue;
                            }

                            System.out.print("Digite o CPF/CNPJ do cliente: ");
                            String cpf = ler.nextLine();

                            Pessoa pessoa = PessoaDAO.getInstance().getByCpf(cpf);
                            List<Conta> contas = ContaDAO.getInstance().listarContasPorCerificado(cpf);

                            if (contas.isEmpty()) {
                                System.out.println("Nenhuma conta encontrada para este cliente.");
                                return;
                            }

                            System.out.println("Contas encontradas:");
                            for (int i = 0; i < contas.size(); i++) {
                                System.out.println((i + 1) + ". " + contas.get(i));
                            }

                            System.out.print("Digite o número da conta que deseja: ");
                            int escolha = ler.nextInt();
                            ler.nextLine();

                            Conta contaSelecionada = contas.get(escolha - 1);

                            if (pessoa == null) {
                                System.out.println("Conta do cliente não encontrada.");
                                continue;
                            }

                            ProdutoDAO.getInstance().venderProduto(produtoSelecionado, escriturario, contaSelecionada);
                            System.out.println("Produto vendido com sucesso!");

                        } catch (SQLException e) {
                            System.out.println("Erro ao vender produto: " + e.getMessage());
                        }

                    } else if (subMenu == 3) {
                        System.out.println("Retornando ao menu principal...");
                    } else {
                        System.out.println("Opção inválida.");
                    }
                }
            }
        }
    }
}
