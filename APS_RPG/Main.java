import java.io.*;
import java.util.*;

/*
 Main.java - Jogo Spland (RPG)
 Versão: com comentários OO, robustez, habilidades, diálogos ramificados, loja, progressão e salvar/carregar.
 Compilar: javac Main.java
 Executar: java Main
*/

/* =========================
   Classe pública principal
   - Método Construtor: Jogo() é chamado em main
   ========================= */
public class Main {
    public static void main(String[] args) {
        Jogo jogo = new Jogo(); // Construtor: instancia Jogo
        jogo.iniciar();
    }
}

/* =========================
   Classe Jogo
   - Responsável pelo fluxo principal do jogo
   - Encapsulamento: campos privados
   - Tratamento de Exceções: try/catch em entradas e I/O
   ========================= */
class Jogo {
    private Personagem jogador; // Encapsulamento
    private final Cidade cidade; // Classe Final + atributo final
    private final Scanner scanner = new Scanner(System.in);

    // Construtor
    public Jogo() {
        cidade = new Cidade("Spland"); // Classe Final: Cidade
    }

    // Inicia o jogo
    public void iniciar() {
        System.out.println("Bem-vindo a Spland! (Digite números para escolher opções)");
        // Cria jogador (sobrecarga de construtor demonstrada em Personagem)
        jogador = new Personagem("Kevinlito, o Eco-Ladino", 100, 15);

        // Itens iniciais
        jogador.adicionarItem(new Item("Garrafa Reutilizável", "cura", 20));
        jogador.adicionarItem(new Item("Armadura Mágica", "defesa", 5));
        jogador.adicionarItem(new Item("Vassoura Sustentável", "forca", 3));
        jogador.adicionarItem(new Item("Sementes Mágicas", "buff_forca", 5));
        jogador.adicionarItem(new Item("Comida Orgânica", "cura", 30));

        menuPrincipal();
    }

    // Menu principal com opções extras (salvar/carregar)
    private void menuPrincipal() {
        int opcao = 0;
        do {
            try {
                System.out.println("\n--- Menu Principal ---");
                System.out.println("1 - Explorar a cidade");
                System.out.println("2 - Lutar contra inimigos ambientais");
                System.out.println("3 - Conversar com NPC (diálogo ramificado)");
                System.out.println("4 - Ver inventário");
                System.out.println("5 - Usar/Equipar item");
                System.out.println("6 - Enfrentar o Boss Final");
                System.out.println("7 - Ver status do herói");
                System.out.println("8 - Loja");
                System.out.println("9 - Salvar jogo");
                System.out.println("10 - Carregar jogo");
                System.out.println("11 - Sair");
                System.out.print("Escolha: ");
                String entrada = scanner.nextLine().trim();
                if (entrada.isEmpty()) throw new NumberFormatException();
                opcao = Integer.parseInt(entrada);

                switch (opcao) {
                    case 1 -> explorarCidade();
                    case 2 -> iniciarCombate();
                    case 3 -> conversarNPCRamificado();
                    case 4 -> jogador.mostrarInventario();
                    case 5 -> menuUsarEquipar();
                    case 6 -> enfrentarBoss();
                    case 7 -> jogador.mostrarStatus();
                    case 8 -> loja();
                    case 9 -> salvarJogo();
                    case 10 -> carregarJogo();
                    case 11 -> System.out.println("Saindo do jogo... até a próxima!");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            }
        } while (opcao != 11);
    }

    private void explorarCidade() {
        double r = Math.random();
        if (r < 0.4) {
            System.out.println("Você encontra uma Vassoura Sustentável!");
            jogador.adicionarItem(new Item("Vassoura Sustentável", "forca", 3));
        } else if (r < 0.7) {
            System.out.println("Você encontra uma Garrafa Reutilizável!");
            jogador.adicionarItem(new Item("Garrafa Reutilizável", "cura", 20));
        } else {
            System.out.println("Você encontra uma Armadura Mágica!");
            jogador.adicionarItem(new Item("Armadura Mágica", "defesa", 5));
        }
    }

    private void iniciarCombate() {
        Inimigo inimigo = Math.random() > 0.5 ? new MonstroPoluicao() : new EspiritoDesperdicio();
        System.out.println("Um " + inimigo.getNome() + " apareceu!");

        combateTurnoAJogador(inimigo);
    }

    // Diálogo ramificado com escolhas que afetam recompensas/progresso
    private void conversarNPCRamificado() {
        System.out.println("\nVocê encontra um ativista ambiental que oferece três opções:");
        System.out.println("1 - Ajudar a plantar árvores (ganha vida e XP)");
        System.out.println("2 - Ajudar a recolher lixo (ganha itens)");
        System.out.println("3 - Ignorar");
        System.out.print("Escolha: ");
        try {
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) throw new NumberFormatException();
            int escolha = Integer.parseInt(s);
            switch (escolha) {
                case 1 -> {
                    System.out.println("Você ajudou a plantar árvores. A cidade agradece!");
                    jogador.receberCura(10);
                    jogador.ganharXP(20);
                }
                case 2 -> {
                    System.out.println("Você recolheu lixo e encontrou itens úteis!");
                    jogador.adicionarItem(new Item("Vassoura Sustentável", "forca", 3));
                    jogador.ganharXP(10);
                }
                case 3 -> System.out.println("Você seguiu seu caminho. Talvez outra hora.");
                default -> System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida! Missão cancelada.");
        }
    }

    // Menu usar/equipar por índice (inventário numerado)
    private void menuUsarEquipar() {
        System.out.println("\n--- Usar ou Equipar Item ---");
        System.out.println("1 - Usar item consumível");
        System.out.println("2 - Equipar item");
        System.out.println("3 - Desequipar item");
        System.out.print("Escolha: ");
        try {
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) throw new NumberFormatException();
            int op = Integer.parseInt(s);
            switch (op) {
                case 1 -> {
                    jogador.mostrarInventario();
                    System.out.print("Digite o número do item consumível (ou 0 para cancelar): ");
                    String idxStr = scanner.nextLine().trim();
                    if (idxStr.isEmpty()) throw new NumberFormatException();
                    int idx = Integer.parseInt(idxStr);
                    if (idx == 0) { System.out.println("Operação cancelada."); return; }
                    jogador.usarItemConsumivelPorIndice(idx - 1);
                }
                case 2 -> {
                    jogador.mostrarInventario();
                    System.out.print("Digite o número do item para equipar (ou 0 para cancelar): ");
                    String idxStr = scanner.nextLine().trim();
                    if (idxStr.isEmpty()) throw new NumberFormatException();
                    int idx = Integer.parseInt(idxStr);
                    if (idx == 0) { System.out.println("Operação cancelada."); return; }
                    jogador.equiparItemPorIndice(idx - 1);
                }
                case 3 -> {
                    System.out.print("Desequipar (1) arma ou (2) armadura? ");
                    String tStr = scanner.nextLine().trim();
                    if (tStr.isEmpty()) throw new NumberFormatException();
                    int t = Integer.parseInt(tStr);
                    if (t == 1) jogador.desequiparArma();
                    else jogador.desequiparArmadura();
                }
                default -> System.out.println("Opção inválida!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida!");
        }
    }

    private void enfrentarBoss() {
        Inimigo boss = new SenhorDevastacao();
        System.out.println("O Boss Final apareceu: " + boss.getNome() + "!");

        combateTurnoAJogador(boss);

        if (jogador.estaVivo() && !boss.estaVivo()) {
            System.out.println("Você derrotou o Boss Final! Vitória!");
            jogador.ganharXP(100);
            FinalNarrativa.mostrarVitoria();
        } else if (!jogador.estaVivo()) {
            System.out.println("Você foi derrotado pelo Boss. Fim de jogo.");
            FinalNarrativa.mostrarDerrota();
        }
    }

    // Combate por turnos com opção de habilidades
    private void combateTurnoAJogador(Inimigo inimigo) {
        while (inimigo.estaVivo() && jogador.estaVivo()) {
            System.out.println("\n--- Turno do Jogador ---");
            System.out.println("Inimigo: " + inimigo.getNome() + " (Vida: " + inimigo.getVida() + ")");
            System.out.println("1 - Atacar");
            System.out.println("2 - Usar consumível");
            System.out.println("3 - Habilidades");
            System.out.println("4 - Equipar/Desequipar");
            System.out.println("5 - Fugir");
            System.out.print("Escolha: ");
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) { System.out.println("Entrada inválida!"); continue; }
            int op;
            try { op = Integer.parseInt(s); } catch (NumberFormatException e) { System.out.println("Entrada inválida!"); continue; }

            switch (op) {
                case 1 -> {
                    int dano = jogador.getForcaAtual();
                    System.out.println("Você ataca causando " + dano + " de dano!");
                    inimigo.receberDano(dano);
                }
                case 2 -> {
                    jogador.mostrarInventario();
                    System.out.print("Digite o número do consumível: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                        jogador.usarItemConsumivelPorIndice(idx);
                    } catch (Exception e) { System.out.println("Entrada inválida!"); }
                }
                case 3 -> {
                    jogador.mostrarHabilidades();
                    System.out.print("Escolha habilidade (número) ou 0 para cancelar: ");
                    try {
                        int h = Integer.parseInt(scanner.nextLine().trim());
                        if (h == 0) break;
                        jogador.usarHabilidade(h, inimigo);
                    } catch (Exception e) { System.out.println("Entrada inválida!"); }
                }
                case 4 -> menuUsarEquipar();
                case 5 -> {
                    if (Math.random() < 0.5) {
                        System.out.println("Você fugiu com sucesso!");
                        return;
                    } else {
                        System.out.println("Fuga falhou!");
                    }
                }
                default -> System.out.println("Opção inválida!");
            }

            if (!inimigo.estaVivo()) {
                System.out.println(inimigo.getNome() + " foi derrotado!");
                jogador.ganharXP(20);
                // drop simples
                if (Math.random() < 0.6) {
                    Item drop = new Item("Comida Orgânica", "cura", 30);
                    jogador.adicionarItem(drop);
                }
                return;
            }

            // Turno do inimigo
            System.out.println("\n--- Turno do Inimigo ---");
            inimigo.atacar(jogador);

            if (!jogador.estaVivo()) {
                System.out.println("Você foi derrotado!");
                return;
            }

            jogador.decrementarBuffs();
        }
    }

    // Loja simples: comprar e vender
    private void loja() {
        System.out.println("\n--- Loja de Spland ---");
        List<Item> estoque = new ArrayList<>();
        estoque.add(new Item("Comida Orgânica", "cura", 30)); // preço implícito
        estoque.add(new Item("Vassoura Sustentável", "forca", 3));
        estoque.add(new Item("Armadura Mágica", "defesa", 5));

        System.out.println("1 - Comprar");
        System.out.println("2 - Vender (pegará o último item do inventário como exemplo)");
        System.out.print("Escolha: ");
        try {
            int op = Integer.parseInt(scanner.nextLine().trim());
            if (op == 1) {
                System.out.println("Itens disponíveis:");
                for (int i = 0; i < estoque.size(); i++) {
                    System.out.println((i + 1) + " - " + estoque.get(i).getNome() + " : " + estoque.get(i).getEfeito());
                }
                System.out.print("Digite o número do item para comprar (ou 0 para cancelar): ");
                int idx = Integer.parseInt(scanner.nextLine().trim());
                if (idx == 0) { System.out.println("Compra cancelada."); return; }
                if (idx < 1 || idx > estoque.size()) { System.out.println("Índice inválido."); return; }
                Item comprado = estoque.get(idx - 1);
                jogador.adicionarItem(comprado);
                System.out.println("Compra realizada: " + comprado.getNome());
            } else if (op == 2) {
                if (jogador.getInventario().isEmpty()) { System.out.println("Nada para vender."); return; }
                Item vendido = jogador.getInventario().remove(jogador.getInventario().size() - 1);
                System.out.println("Você vendeu: " + vendido.getNome() + " (recebeu recompensa simbólica)");
                jogador.ganharXP(5);
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.out.println("Entrada inválida na loja.");
        }
    }

    // Salvar jogo (simples arquivo de texto)
    private void salvarJogo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("savegame.txt"))) {
            pw.println(jogador.serialize());
            System.out.println("Jogo salvo em savegame.txt");
        } catch (IOException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }

    // Carregar jogo (simples)
    private void carregarJogo() {
        File f = new File("savegame.txt");
        if (!f.exists()) {
            System.out.println("Arquivo de save não encontrado.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha = br.readLine();
            if (linha != null && !linha.isEmpty()) {
                Personagem p = Personagem.deserialize(linha);
                if (p != null) {
                    this.jogador = p;
                    System.out.println("Jogo carregado com sucesso.");
                } else {
                    System.out.println("Arquivo de save inválido.");
                }
            } else {
                System.out.println("Arquivo de save vazio.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar: " + e.getMessage());
        }
    }
}

/* =========================
   Classe Cidade
   - Classe Final (não pode ser estendida)
   - Encapsulamento: atributo privado final
   ========================= */
final class Cidade {
    private final String nome; // atributo final

    public Cidade(String nome) { this.nome = nome; } // Construtor

    public String getNome() { return nome; } // getter
}

/* =========================
   Classe Personagem
   - Encapsulamento, Construtor, Sobrecarga, Polimorfismo (usa Inimigo), Progressão, Habilidades
   ========================= */
class Personagem {
    private String nome; // Encapsulamento
    private int vida;
    private int vidaMax;
    private int forca;
    private int nivel;
    private int xp;
    private List<Item> inventario = new ArrayList<>();

    // Equipamentos persistentes
    private Item armaEquipada = null;
    private Item armaduraEquipada = null;

    // Buffs temporários
    private List<Buff> buffs = new ArrayList<>();

    // Habilidades (exemplo simples)
    private List<Habilidade> habilidades = new ArrayList<>();

    // Construtor principal
    public Personagem(String nome, int vidaMax, int forca) {
        this.nome = nome;
        this.vidaMax = vidaMax;
        this.vida = vidaMax;
        this.forca = forca;
        this.nivel = 1;
        this.xp = 0;
        // Habilidades iniciais
        habilidades.add(new Habilidade("Golpe Rápido", 0, 1, (j, inimigo) -> {
            int dano = j.getForcaAtual();
            System.out.println(j.getNome() + " usa Golpe Rápido causando " + dano + " de dano!");
            inimigo.receberDano(dano);
        }));
        habilidades.add(new Habilidade("Investida Forte", 0, 2, (j, inimigo) -> {
            int dano = j.getForcaAtual() + 5;
            System.out.println(j.getNome() + " usa Investida Forte causando " + dano + " de dano!");
            inimigo.receberDano(dano);
        }));
    }

    // Sobrecarga de construtor
    public Personagem(String nome) { this(nome, 80, 10); }

    // Getters
    public String getNome() { return nome; }
    public int getVidaAtual() { return vida; }
    public int getVidaMax() { return vidaMax; }
    public List<Item> getInventario() { return inventario; }

    // Adiciona item
    public void adicionarItem(Item item) {
        if (item == null) return;
        inventario.add(item);
        System.out.println("Item adicionado: " + item.getNome());
    }

    // Recupera vida
    public void receberCura(int cura) {
        vida = Math.min(vidaMax, vida + cura);
        System.out.println(nome + " recuperou " + cura + " de vida. Vida atual: " + vida);
    }

    // Recebe dano considerando armadura equipada
    public void receberDano(int dano) {
        int reducao = (armaduraEquipada != null && "defesa".equals(armaduraEquipada.getTipo())) ? armaduraEquipada.getValor() : 0;
        int danoFinal = Math.max(0, dano - reducao);
        vida = Math.max(0, vida - danoFinal);
        System.out.println(nome + " recebeu " + danoFinal + " de dano (redução " + reducao + "). Vida restante: " + vida);
    }

    public boolean estaVivo() { return vida > 0; }

    // Cálculo de força atual (considera arma e buffs)
    public int getForcaAtual() {
        int bonusArma = (armaEquipada != null && "forca".equals(armaEquipada.getTipo())) ? armaEquipada.getValor() : 0;
        int bonusBuff = getBuffValue("forca");
        return forca + bonusArma + bonusBuff + (nivel - 1); // leve progressão por nível
    }

    // Ataque padrão (polimorfismo: usado contra Inimigo)
    public void atacar(Inimigo inimigo) {
        int dano = getForcaAtual();
        System.out.println(nome + " ataca causando " + dano + " de dano!");
        inimigo.receberDano(dano);
    }

    // Mostrar inventário numerado
    public void mostrarInventario() {
        System.out.println("\n--- Inventário ---");
        if (inventario.isEmpty()) {
            System.out.println("Inventário vazio.");
        } else {
            for (int i = 0; i < inventario.size(); i++) {
                Item item = inventario.get(i);
                System.out.println((i + 1) + " - " + item.getNome() + " : " + item.getEfeito());
            }
        }
        System.out.println("Arma equipada: " + (armaEquipada != null ? armaEquipada.getNome() : "Nenhuma"));
        System.out.println("Armadura equipada: " + (armaduraEquipada != null ? armaduraEquipada.getNome() : "Nenhuma"));
    }

    // Usar consumível por índice (robusto)
    public void usarItemConsumivelPorIndice(int indice) {
        if (indice < 0 || indice >= inventario.size()) {
            System.out.println("Índice inválido!");
            return;
        }
        Item item = inventario.get(indice);
        if (item == null) { System.out.println("Item inválido."); return; }
        String tipo = item.getTipo().toLowerCase();
        switch (tipo) {
            case "cura":
                receberCura(item.getValor());
                inventario.remove(indice);
                System.out.println(item.getNome() + " usado com sucesso!");
                return;
            case "buff_forca":
                buffs.add(new Buff("forca", item.getValor(), 3));
                inventario.remove(indice);
                System.out.println(item.getNome() + " usado: + " + item.getValor() + " de força por 3 turnos!");
                return;
            default:
                System.out.println("Item não é consumível. Tente equipar.");
                return;
        }
    }

    // Equipar por índice (robusto)
    public void equiparItemPorIndice(int indice) {
        if (indice < 0 || indice >= inventario.size()) {
            System.out.println("Índice inválido!");
            return;
        }
        Item item = inventario.get(indice);
        if (item == null) { System.out.println("Item inválido."); return; }
        String tipo = item.getTipo().toLowerCase();
        if ("forca".equals(tipo)) {
            armaEquipada = item;
            inventario.remove(indice);
            System.out.println("Você equipou a arma: " + item.getNome());
        } else if ("defesa".equals(tipo)) {
            armaduraEquipada = item;
            inventario.remove(indice);
            System.out.println("Você equipou a armadura: " + item.getNome());
        } else {
            System.out.println("Este item não pode ser equipado.");
        }
    }

    // Métodos por nome (compatibilidade)
    public void usarItemConsumivel(String nomeItem) {
        if (nomeItem == null) { System.out.println("Nome inválido."); return; }
        String nomeNormalizado = nomeItem.trim();
        Iterator<Item> it = inventario.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getNome().equalsIgnoreCase(nomeNormalizado)) {
                switch (item.getTipo()) {
                    case "cura":
                        receberCura(item.getValor());
                        it.remove();
                        return;
                    case "buff_forca":
                        buffs.add(new Buff("forca", item.getValor(), 3));
                        System.out.println("Você ganhou + " + item.getValor() + " de força por 3 turnos!");
                        it.remove();
                        return;
                    default:
                        System.out.println("Item não é consumível. Tente equipar.");
                        return;
                }
            }
        }
        System.out.println("Item não encontrado!");
    }

    public void equiparItem(String nomeItem) {
        if (nomeItem == null) { System.out.println("Nome inválido."); return; }
        String nomeNormalizado = nomeItem.trim();
        Iterator<Item> it = inventario.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getNome().equalsIgnoreCase(nomeNormalizado)) {
                if ("forca".equals(item.getTipo())) {
                    armaEquipada = item;
                    it.remove();
                    System.out.println("Você equipou a arma: " + item.getNome());
                    return;
                } else if ("defesa".equals(item.getTipo())) {
                    armaduraEquipada = item;
                    it.remove();
                    System.out.println("Você equipou a armadura: " + item.getNome());
                    return;
                } else {
                    System.out.println("Este item não pode ser equipado.");
                    return;
                }
            }
        }
        System.out.println("Item não encontrado no inventário!");
    }

    public void desequiparArma() {
        if (armaEquipada != null) {
            adicionarItem(armaEquipada);
            System.out.println("Desequipou: " + armaEquipada.getNome());
            armaEquipada = null;
        } else {
            System.out.println("Nenhuma arma equipada.");
        }
    }

    public void desequiparArmadura() {
        if (armaduraEquipada != null) {
            adicionarItem(armaduraEquipada);
            System.out.println("Desequipou: " + armaduraEquipada.getNome());
            armaduraEquipada = null;
        } else {
            System.out.println("Nenhuma armadura equipada.");
        }
    }

    // Buffs
    private int getBuffValue(String tipo) {
        int soma = 0;
        for (Buff b : buffs) {
            if (b.getTipo().equals(tipo)) soma += b.getValor();
        }
        return soma;
    }

    public void decrementarBuffs() {
        Iterator<Buff> it = buffs.iterator();
        while (it.hasNext()) {
            Buff b = it.next();
            b.decrementarTurno();
            if (b.getTurnosRestantes() <= 0) {
                System.out.println("O efeito de " + b.getTipo() + " de +" + b.getValor() + " expirou.");
                it.remove();
            }
        }
    }

    // Habilidades
    public void mostrarHabilidades() {
        System.out.println("\n--- Habilidades ---");
        for (int i = 0; i < habilidades.size(); i++) {
            Habilidade h = habilidades.get(i);
            System.out.println((i + 1) + " - " + h.getNome() + " (Cooldown: " + h.getCooldown() + ")");
        }
    }

    public void usarHabilidade(int indice, Inimigo inimigo) {
        if (indice < 1 || indice > habilidades.size()) {
            System.out.println("Habilidade inválida.");
            return;
        }
        Habilidade h = habilidades.get(indice - 1);
        if (h.isDisponivel()) {
            h.usar(this, inimigo);
            h.resetCooldown();
        } else {
            System.out.println("Habilidade em cooldown. Aguarde " + h.getCooldown() + " turnos.");
        }
    }

    // Progressão: XP e nível simples
    public void ganharXP(int quantidade) {
        xp += quantidade;
        System.out.println("Você ganhou " + quantidade + " XP.");
        while (xp >= nivel * 50) {
            xp -= nivel * 50;
            nivel++;
            vidaMax += 10;
            vida = vidaMax;
            forca += 2;
            System.out.println("Parabéns! Você subiu para o nível " + nivel + "!");
        }
    }

    public void mostrarStatus() {
        System.out.println("\n--- Status de " + nome + " ---");
        System.out.println("Vida: " + vida + " / " + vidaMax);
        System.out.println("Força base: " + forca);
        System.out.println("Nível: " + nivel + " | XP: " + xp + "/" + (nivel * 50));
        System.out.println("Bônus de arma: " + (armaEquipada != null ? armaEquipada.getValor() : 0));
        System.out.println("Bônus de buffs (força): " + getBuffValue("forca"));
        System.out.println("Armadura (redução): " + (armaduraEquipada != null ? armaduraEquipada.getValor() : 0));
    }

    // Serialização simples para salvar (classe por classe não exigida aqui)
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(nome).append("|").append(vida).append("|").append(vidaMax).append("|").append(forca)
          .append("|").append(nivel).append("|").append(xp).append("|");
        // inventario: nome;tipo;valor, separated by #
        for (int i = 0; i < inventario.size(); i++) {
            Item it = inventario.get(i);
            sb.append(it.getNome().replace("|","/")).append(";").append(it.getTipo()).append(";").append(it.getValor());
            if (i < inventario.size() - 1) sb.append("#");
        }
        // arma/armadura equipadas (nome only)
        sb.append("|").append(armaEquipada != null ? armaEquipada.getNome().replace("|","/") : "");
        sb.append("|").append(armaduraEquipada != null ? armaduraEquipada.getNome().replace("|","/") : "");
        return sb.toString();
    }

    // Desserialização simples
    public static Personagem deserialize(String linha) {
        try {
            String[] parts = linha.split("\\|", -1);
            if (parts.length < 9) return null;
            Personagem p = new Personagem(parts[0], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            p.vida = Integer.parseInt(parts[1]);
            p.nivel = Integer.parseInt(parts[4]);
            p.xp = Integer.parseInt(parts[5]);
            String inv = parts[6];
            if (!inv.isEmpty()) {
                String[] itens = inv.split("#");
                for (String it : itens) {
                    String[] campos = it.split(";");
                    if (campos.length >= 3) {
                        p.adicionarItem(new Item(campos[0], campos[1], Integer.parseInt(campos[2])));
                    }
                }
            }
            String armaNome = parts[7];
            String armaduraNome = parts[8];
            // equipar por nome se existir no inventario
            if (!armaNome.isEmpty()) {
                for (Iterator<Item> it = p.inventario.iterator(); it.hasNext();) {
                    Item item = it.next();
                    if (item.getNome().equalsIgnoreCase(armaNome)) {
                        p.armaEquipada = item;
                        it.remove();
                        break;
                    }
                }
            }
            if (!armaduraNome.isEmpty()) {
                for (Iterator<Item> it = p.inventario.iterator(); it.hasNext();) {
                    Item item = it.next();
                    if (item.getNome().equalsIgnoreCase(armaduraNome)) {
                        p.armaduraEquipada = item;
                        it.remove();
                        break;
                    }
                }
            }
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}

/* =========================
   Habilidade (Functional interface + implementação)
   - Demonstra Interface e Polimorfismo de ação
   ========================= */
interface AcaoHabilidade {
    void executar(Personagem jogador, Inimigo inimigo);
}

class Habilidade {
    private final String nome;
    private final int custo; // placeholder
    private int cooldown;
    private int cooldownAtual;
    private final AcaoHabilidade acao;

    public Habilidade(String nome, int custo, int cooldown, AcaoHabilidade acao) {
        this.nome = nome;
        this.custo = custo;
        this.cooldown = cooldown;
        this.cooldownAtual = 0;
        this.acao = acao;
    }

    public String getNome() { return nome; }
    public int getCooldown() { return cooldownAtual; }
    public boolean isDisponivel() { return cooldownAtual == 0; }

    public void usar(Personagem jogador, Inimigo inimigo) {
        if (isDisponivel()) {
            acao.executar(jogador, inimigo);
            cooldownAtual = cooldown;
        } else {
            System.out.println("Habilidade em cooldown.");
        }
    }

    public void resetCooldown() { /* cooldown já setado em usar */ }

    public void tickCooldown() {
        if (cooldownAtual > 0) cooldownAtual--;
    }
}

/* =========================
   Buff
   ========================= */
class Buff {
    private final String tipo;
    private final int valor;
    private int turnosRestantes;

    public Buff(String tipo, int valor, int turnos) {
        this.tipo = tipo;
        this.valor = valor;
        this.turnosRestantes = turnos;
    }

    public String getTipo() { return tipo; }
    public int getValor() { return valor; }
    public int getTurnosRestantes() { return turnosRestantes; }
    public void decrementarTurno() { turnosRestantes--; }
}

/* =========================
   Inimigo (Classe Abstrata) - demonstra Classe Abstrata e Polimorfismo
   ========================= */
abstract class Inimigo {
    protected String nome;
    protected int vida;
    protected int defesa;

    public Inimigo(String nome, int vida, int defesa) { // Construtor
        this.nome = nome;
        this.vida = vida;
        this.defesa = defesa;
    }

    // Método abstrato: sobrescrito nas subclasses (Sobrescrita)
    public abstract void atacar(Personagem jogador);

    public void receberDano(int dano) {
        vida = Math.max(0, vida - dano);
        System.out.println(nome + " recebeu " + dano + " de dano. Vida restante: " + vida);
    }

    public boolean estaVivo() { return vida > 0; }
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public int getDefesa() { return defesa; }
}

/* =========================
   Subclasses de Inimigo (Herança, Sobrescrita)
   ========================= */
class MonstroPoluicao extends Inimigo {
    public MonstroPoluicao() { super("Monstro da Poluição", 80, 5); } // Herança
    @Override
    public void atacar(Personagem jogador) { // Sobrescrita
        int dano = 12;
        System.out.println(nome + " lança fumaça tóxica!");
        jogador.receberDano(dano);
    }
}

class EspiritoDesperdicio extends Inimigo {
    public EspiritoDesperdicio() { super("Espírito do Desperdício", 60, 3); }
    @Override
    public void atacar(Personagem jogador) {
        int dano = (int)(Math.random() * 15);
        System.out.println(nome + " joga lixo ao redor!");
        jogador.receberDano(dano);
    }
}

class SenhorDevastacao extends Inimigo {
    public SenhorDevastacao() { super("Senhor Devastação - Poluição do Ar", 150, 10); }
    @Override
    public void atacar(Personagem jogador) {
        int escolha = (int)(Math.random() * 2);
        if (escolha == 0) {
            int dano = 20;
            System.out.println(nome + " libera uma nuvem tóxica sufocante!");
            jogador.receberDano(dano);
        } else {
            int dano = 25;
            System.out.println(nome + " cobre Spland com fumaça negra!");
            jogador.receberDano(dano);
        }
    }
}

/* =========================
   Interface Equipavel e Classe Item
   - Interface: demonstra contrato; Item implementa Equipavel
   - Atributo Estático: totalItemsCriados
   - Sobrecarga de Construtor
   ========================= */
interface Equipavel {
    String getNome();
    String getTipo();
    int getValor();
}

class Item implements Equipavel {
    private final String nome; // Encapsulamento
    private final String tipo; // cura, forca, defesa, buff_forca
    private final int valor;
    private static int totalItemsCriados = 0; // Atributo Estático

    public Item(String nome, String tipo, int valor) {
        this.nome = nome;
        this.tipo = tipo;
        this.valor = valor;
        totalItemsCriados++;
    }

    public Item(String nome, String tipo) { this(nome, tipo, 1); } // Sobrecarga

    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public int getValor() { return valor; }
    public static int getTotalItemsCriados() { return totalItemsCriados; }

    public String getEfeito() {
        switch (tipo) {
            case "cura": return "Recupera " + valor + " de vida";
            case "forca": return "Aumenta ataque em +" + valor + " (equipável)";
            case "defesa": return "Reduz dano recebido em -" + valor + " (equipável)";
            case "buff_forca": return "Buff temporário: +" + valor + " de força por 3 turnos";
            default: return "Efeito desconhecido";
        }
    }
}

/* =========================
   FinalNarrativa
   ========================= */
class FinalNarrativa {
    public static void mostrarVitoria() {
        System.out.println("\n--- FINAL: Vitória ---");
        System.out.println("Kevinlito restaura Spland: árvores voltam a crescer, o ar clareia.");
        System.out.println("A cidade celebra sua ação. Missão cumprida!");
    }

    public static void mostrarDerrota() {
        System.out.println("\n--- FINAL: Derrota ---");
        System.out.println("Sem proteção, Spland afunda em poluição. Há sempre uma nova chance.");
    }
}
