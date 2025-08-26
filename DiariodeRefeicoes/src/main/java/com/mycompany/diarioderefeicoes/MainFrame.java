package com.mycompany.diarioderefeicoes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private JPanel painelMenu;
    private boolean menuAberto = false;
    private JPanel painelConteudo;
    private JLabel lblCaloriasTotais;
    private int totalCalorias = 0;

    private JPanel painelRefeicoes;
    private final ArrayList<Refeicao> refeicoes = new ArrayList<>();
    private final ArrayList<Refeicao> historicoRefeicoes = new ArrayList<>();

    private boolean mostrandoHistorico = false;

    private JLabel tituloHistorico;
    private JButton btnRegistrar;

    private int larguraMenu = 210;
    private int idUsuarioLogado; // Campo para armazenar o ID do usuário logado

    // Construtor para aceitar nome e ID do usuário logado
    public MainFrame(String nomeUsuario, int idUsuario) {
        this.idUsuarioLogado = idUsuario;

        setTitle("Diário de Refeições");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Fontes
        Font fonteRegular = new Font("Segoe UI", Font.PLAIN, 16);
        Font fonteSemibold = new Font("Segoe UI Semibold", Font.BOLD, 22);
        Font fonteTitulo = new Font("Segoe UI Semibold", Font.BOLD, 26);
        Font fonteLight = new Font("Segoe UI Light", Font.PLAIN, 16);

        // CABEÇALHO
        JPanel painelCabecalho = new JPanel(new BorderLayout());
        painelCabecalho.setBackground(Color.WHITE);
        painelCabecalho.setBounds(0, 0, 1200, 60);
        painelCabecalho.setPreferredSize(new Dimension(1200, 60));

        JButton btnMenu = new JButton("≡");
        btnMenu.setFont(fonteSemibold.deriveFont(26f));
        btnMenu.setBackground(Color.BLACK);
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setBorderPainted(false);
        btnMenu.setFocusPainted(false);
        btnMenu.setPreferredSize(new Dimension(50, 40));
        painelCabecalho.add(btnMenu, BorderLayout.WEST);

        JLabel saudacao = new JLabel("Olá, " + nomeUsuario + "!");
        saudacao.setFont(fonteSemibold);
        saudacao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        painelCabecalho.add(saudacao, BorderLayout.CENTER);

        add(painelCabecalho);

        // MENU LATERAL
        painelMenu = new JPanel();
        painelMenu.setLayout(new BoxLayout(painelMenu, BoxLayout.Y_AXIS));
        painelMenu.setBackground(new Color(230, 230, 230));
        painelMenu.setBounds(0, painelCabecalho.getHeight(), larguraMenu, 700);
        painelMenu.setVisible(false);

        String[] opcoes = {"Diário de Refeições", "Histórico de Refeições", "Sair"};
        for (String opcao : opcoes) {
            JButton btn = criarBotaoMenu(opcao, opcao.equals("Diário de Refeições"), fonteRegular, fonteSemibold);
            btn.setMaximumSize(new Dimension(180, 35));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelMenu.add(btn);
            painelMenu.add(Box.createVerticalStrut(10));

            if (opcao.equals("Sair")) {
                btn.setForeground(new Color(180, 0, 0));
                btn.addActionListener(e -> voltarTelaLogin());
            } else if (opcao.equals("Histórico de Refeições")) {
                btn.addActionListener(e -> {
                    mostrandoHistorico = true;
                    loadRefeicoesFromDatabase();
                    setBotaoMenuSelecionado(btn, fonteRegular, fonteSemibold);
                    fecharMenu();
                });
            } else if (opcao.equals("Diário de Refeições")) {
                btn.addActionListener(e -> {
                    mostrandoHistorico = false;
                    loadRefeicoesFromDatabase();
                    setBotaoMenuSelecionado(btn, fonteRegular, fonteSemibold);
                    fecharMenu();
                });
            }
        }
        add(painelMenu);

        // PAINEL DE CONTEÚDO PRINCIPAL
        painelConteudo = new JPanel(null);
        painelConteudo.setBackground(Color.WHITE);
        painelConteudo.setBounds(0, painelCabecalho.getHeight(), 1200, 700);
        add(painelConteudo);

        // Título do histórico
        tituloHistorico = new JLabel("Histórico de Refeições");
        tituloHistorico.setFont(fonteTitulo);
        tituloHistorico.setForeground(new Color(40, 40, 40));
        tituloHistorico.setBounds(30, 20, 500, 38);
        tituloHistorico.setVisible(false);
        painelConteudo.add(tituloHistorico);

        // Label Total Calorias
        lblCaloriasTotais = new JLabel("Total de Calorias: 0");
        lblCaloriasTotais.setFont(fonteSemibold.deriveFont(20f));
        lblCaloriasTotais.setBounds(30, 40, 400, 30);
        painelConteudo.add(lblCaloriasTotais);

        // Painel de cards de refeições
        painelRefeicoes = new JPanel();
        painelRefeicoes.setLayout(new BoxLayout(painelRefeicoes, BoxLayout.Y_AXIS));
        painelRefeicoes.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(painelRefeicoes);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        painelConteudo.add(scroll);

        // Botão Registrar Refeição
        btnRegistrar = criarBotaoPreto("Registrar refeição +", fonteSemibold);
        painelConteudo.add(btnRegistrar);

        btnMenu.addActionListener(e -> {
            if (menuAberto) {
                fecharMenu();
            } else {
                abrirMenu();
            }
        });

        painelConteudo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (menuAberto) fecharMenu();
            }
        });

        btnRegistrar.addActionListener(ev -> abrirDialogRefeicao(null, fonteRegular, fonteSemibold, fonteLight));

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> reposicionarComponentes());
            }
        });

        loadRefeicoesFromDatabase();

        SwingUtilities.invokeLater(() -> {
            atualizarLista();
            reposicionarComponentes();
        });

        setVisible(true);
    }

    private void abrirMenu() {
        menuAberto = true;
        painelMenu.setVisible(true);
        reposicionarComponentes();
    }

    private void fecharMenu() {
        menuAberto = false;
        painelMenu.setVisible(false);
        reposicionarComponentes();
    }

    private void reposicionarComponentes() {
        int larguraJanela = getWidth();
        int alturaJanela = getHeight();
        int alturaCabecalho = 60;

        getContentPane().getComponent(0).setBounds(0, 0, larguraJanela, alturaCabecalho);
        painelMenu.setBounds(0, alturaCabecalho, menuAberto ? larguraMenu : 0, alturaJanela - alturaCabecalho);
        painelConteudo.setBounds(menuAberto ? larguraMenu : 0, alturaCabecalho,
                larguraJanela - (menuAberto ? larguraMenu : 0), alturaJanela - alturaCabecalho);

        int margemTopo = 85;
        if (mostrandoHistorico) {
            margemTopo = 60 + tituloHistorico.getHeight();
        }
        int margemDireita = 70;
        int margemEsquerda = 30;

        int larguraConteudo = painelConteudo.getWidth();
        int alturaConteudo = painelConteudo.getHeight();

        int larguraScroll = Math.max(600, larguraConteudo - margemEsquerda - margemDireita);
        int xScroll = margemEsquerda;
        int alturaScroll = alturaConteudo - margemTopo - 160;
        if (alturaScroll < 100) alturaScroll = 100;

        Component[] comps = painelConteudo.getComponents();
        for (Component c : comps) {
            if (c instanceof JScrollPane) {
                c.setBounds(xScroll, margemTopo, larguraScroll, alturaScroll);
            }
        }

        int btnWidth = 260, btnHeight = 48;
        int xBtn = larguraConteudo - btnWidth - 40;
        int yBtn = alturaConteudo - btnHeight - 80;
        if (yBtn < 10) yBtn = 10;
        btnRegistrar.setBounds(xBtn, yBtn, btnWidth, btnHeight);
        painelConteudo.setComponentZOrder(btnRegistrar, 0);

        painelMenu.repaint();
        painelConteudo.repaint();
        painelConteudo.revalidate();
    }

    private void setBotaoMenuSelecionado(JButton btnSelecionado, Font fonteNormal, Font fonteBold) {
        for (Component c : painelMenu.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setFont(btn == btnSelecionado ? fonteBold.deriveFont(16f) : fonteNormal);
            }
        }
    }

    private void abrirDialogRefeicao(Refeicao refEdicao, Font fonteRegular, Font fonteSemibold, Font fonteLight) {
        boolean isEdicao = refEdicao != null;
        Color GREEN = new Color(0x406951);

        JDialog dialog = new JDialog(this, isEdicao ? "Editar Refeição" : "Registrar Refeição", true);
        dialog.setUndecorated(true);
        dialog.setSize(470, 520);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        Color CARD_BORDER = new Color(0xF0F0F0);
        Font fonteTituloCaixa = new Font("Segoe UI", Font.BOLD, 25);
        Font fonteLabel = new Font("Segoe UI", Font.PLAIN, 18);
        Font fonteInput = new Font("Segoe UI", Font.PLAIN, 17);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 17);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 18;
                int w = getWidth();
                int h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w-1, h-1, arc, arc);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
            }
        };
        card.setBackground(new Color(0,0,0,0));
        card.setBounds(0, 0, 470, 500);

        JLabel lblTitulo = new JLabel(isEdicao ? "Editar Refeição" : "Registrar Refeição", SwingConstants.CENTER);
        lblTitulo.setFont(fonteTituloCaixa);
        lblTitulo.setForeground(GREEN);
        lblTitulo.setBounds(0, 18, 470, 36);
        card.add(lblTitulo);

        final JTextField txtNome = new JTextField();
        final JTextField txtCalorias = new JTextField();
        final JTextArea txtDesc = new JTextArea();
        final JScrollPane scrollDesc = new JScrollPane(txtDesc);
        final JButton btnSalvar = new JButton(isEdicao ? "Salvar alterações" : "Registrar refeição");
        final JButton btnFechar = new JButton("Fechar");

        JLabel lblNome = new JLabel("Refeição");
        lblNome.setFont(fonteLabel);
        lblNome.setForeground(Color.BLACK);
        lblNome.setBounds(36, 68, 380, 22);
        txtNome.setFont(fonteInput);
        txtNome.setBounds(36, 94, 398, 36);
        txtNome.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtNome.setBackground(Color.WHITE);
        txtNome.setForeground(Color.BLACK);
        txtNome.setMargin(new Insets(3, 8, 3, 8));
        if(isEdicao) txtNome.setText(refEdicao.nome);

        JLabel lblCalorias = new JLabel("Calorias");
        lblCalorias.setFont(fonteLabel);
        lblCalorias.setForeground(Color.BLACK);
        lblCalorias.setBounds(36, 142, 380, 22);
        txtCalorias.setFont(fonteInput);
        txtCalorias.setBounds(36, 168, 398, 36);
        txtCalorias.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtCalorias.setBackground(Color.WHITE);
        txtCalorias.setForeground(Color.BLACK);
        txtCalorias.setMargin(new Insets(3, 8, 3, 8));
        if(isEdicao) txtCalorias.setText(String.valueOf(refEdicao.calorias));

        JLabel lblDesc = new JLabel("Descrição");
        lblDesc.setFont(fonteLabel);
        lblDesc.setForeground(Color.BLACK);
        lblDesc.setBounds(36, 216, 380, 22);
        txtDesc.setFont(fonteInput);
        txtDesc.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtDesc.setBackground(Color.WHITE);
        txtDesc.setForeground(Color.BLACK);
        txtDesc.setMargin(new Insets(3, 8, 3, 8));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setRows(2);
        txtDesc.setColumns(1);
        if(isEdicao) txtDesc.setText(refEdicao.descricao);

        scrollDesc.setBounds(36, 242, 398, 90);
        scrollDesc.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scrollDesc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDesc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Adapta altura do JTextArea conforme texto digitado (até 8 linhas)
        txtDesc.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateHeight() {
                try {
                    int lines = txtDesc.getLineCount();
                    int maxLines = 8;
                    int visibleLines = Math.max(2, Math.min(maxLines, lines));
                    int h = txtDesc.getFontMetrics(txtDesc.getFont()).getHeight() * visibleLines + 16;
                    scrollDesc.setBounds(36, 242, 398, h);
                    btnSalvar.setBounds(36, 242 + h + 24, 398, 38);
                    btnFechar.setBounds(36, 242 + h + 24 + 38 + 12, 398, 38);
                    card.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateHeight(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateHeight(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateHeight(); }
        });
        SwingUtilities.invokeLater(() -> {
            try {
                int lines = txtDesc.getLineCount();
                int maxLines = 8;
                int visibleLines = Math.max(2, Math.min(maxLines, lines));
                int h = txtDesc.getFontMetrics(txtDesc.getFont()).getHeight() * visibleLines + 16;
                scrollDesc.setBounds(36, 242, 398, h);
                btnSalvar.setBounds(36, 242 + h + 24, 398, 38);
                btnFechar.setBounds(36, 242 + h + 24 + 38 + 12, 398, 38);
                card.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnSalvar.setFont(fonteBotao);
        btnSalvar.setBackground(Color.BLACK);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        btnSalvar.setBounds(36, 366, 398, 38);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnFechar.setFont(fonteBotao);
        btnFechar.setBackground(Color.WHITE);
        btnFechar.setForeground(GREEN);
        btnFechar.setFocusPainted(false);
        btnFechar.setBounds(36, 416, 398, 38);
        btnFechar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFechar.setBorder(BorderFactory.createLineBorder(GREEN, 2, true));
        btnFechar.setContentAreaFilled(true);
        btnFechar.setOpaque(true);
        btnFechar.addActionListener(ev -> dialog.dispose());

        card.add(lblNome);
        card.add(txtNome);
        card.add(lblCalorias);
        card.add(txtCalorias);
        card.add(lblDesc);
        card.add(scrollDesc);
        card.add(lblTitulo);
        card.add(btnSalvar);
        card.add(btnFechar);

        JPanel panelContent = new JPanel(null);
        panelContent.setBackground(new Color(0,0,0,0));
        panelContent.add(card);
        panelContent.setSize(470, 520);

        dialog.setContentPane(panelContent);

        btnSalvar.addActionListener(ev -> {
            try {
                String nome = txtNome.getText().trim();
                int calorias = Integer.parseInt(txtCalorias.getText().trim());
                String descricao = txtDesc.getText().trim();
                String data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                if (nome.isEmpty() || descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!");
                    return;
                }

                if(isEdicao) {
                    try (Connection conn = Conexao.conectar();
                         PreparedStatement stmt = conn.prepareStatement(
                                 "UPDATE registro_refeicao SET refeicao = ?, calorias = ?, qtde_total = ?, descricao = ? WHERE id = ? AND id_cliente = ?")) {

                        stmt.setString(1, nome);
                        stmt.setInt(2, calorias);
                        stmt.setInt(3, calorias);
                        stmt.setString(4, descricao);
                        stmt.setInt(5, refEdicao.getId());
                        stmt.setInt(6, this.idUsuarioLogado);

                        stmt.executeUpdate();

                        int caloriasAntigas = refEdicao.calorias;
                        refEdicao.nome = nome;
                        refEdicao.calorias = calorias;
                        refEdicao.descricao = descricao;
                        totalCalorias = totalCalorias - caloriasAntigas + calorias;

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "Erro ao atualizar no banco de dados: " + ex.getMessage());
                        return;
                    }
                } else {
                    try (Connection conn = Conexao.conectar();
                         PreparedStatement stmt = conn.prepareStatement(
                                 "INSERT INTO registro_refeicao (id_cliente, refeicao, calorias, qtde_total, data, descricao) VALUES (?, ?, ?, ?, ?, ?)",
                                 Statement.RETURN_GENERATED_KEYS)) {

                        stmt.setInt(1, this.idUsuarioLogado);
                        stmt.setString(2, nome);
                        stmt.setInt(3, calorias);
                        stmt.setInt(4, calorias);
                        stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
                        stmt.setString(6, descricao);

                        stmt.executeUpdate();

                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                int novoId = rs.getInt(1);
                                Refeicao nova = new Refeicao(novoId, nome, calorias, descricao, data);
                                refeicoes.add(nova);
                                historicoRefeicoes.add(nova);
                                totalCalorias += calorias;
                            }
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "Erro ao salvar no banco de dados: " + ex.getMessage());
                        return;
                    }
                }

                atualizarLista();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Informe um valor numérico válido para calorias.");
            }
        });

        dialog.setVisible(true);
    }

    private void atualizarLista() {
        painelRefeicoes.removeAll();
        ArrayList<Refeicao> lista = mostrandoHistorico ? historicoRefeicoes : refeicoes;

        int total = 0;
        for (Refeicao ref : lista) {
            if (!mostrandoHistorico) total += ref.calorias;

            JPanel card = criarCardRefeicao(ref);
            painelRefeicoes.add(card);
            painelRefeicoes.add(Box.createVerticalStrut(22));
        }

        if (!mostrandoHistorico) {
            lblCaloriasTotais.setText("Total de Calorias: " + total);
            lblCaloriasTotais.setVisible(true);
            tituloHistorico.setVisible(false);
        } else {
            lblCaloriasTotais.setVisible(false);
            tituloHistorico.setVisible(true);
        }

        painelRefeicoes.revalidate();
        painelRefeicoes.repaint();
        reposicionarComponentes();
    }

    // Carrega as refeições do banco de dados para o usuário logado
    private void loadRefeicoesFromDatabase() {
        refeicoes.clear();
        historicoRefeicoes.clear();
        totalCalorias = 0;

        String sql = "SELECT id, refeicao, calorias, descricao, data FROM registro_refeicao WHERE id_cliente = ? ORDER BY data DESC, id DESC";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuarioLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("refeicao");
                    int calorias = rs.getInt("calorias");
                    String descricao = rs.getString("descricao");
                    String data = rs.getString("data");
                    Refeicao refeicao = new Refeicao(id, nome, calorias, descricao, data);
                    refeicoes.add(refeicao);
                    historicoRefeicoes.add(refeicao);
                    totalCalorias += calorias;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar refeições do banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JMenuItem criarMenuItemVerdeBorda(String texto) {
        JMenuItem item = new JMenuItem(texto);
        Color GREEN = new Color(0x406951);
        item.setFont(new Font("Segoe UI", Font.BOLD, 15));
        item.setBackground(Color.WHITE);
        item.setForeground(GREEN);
        item.setOpaque(true);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN, 2, true),
                BorderFactory.createEmptyBorder(7, 18, 7, 18)
        ));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return item;
    }

    private JPanel criarCardRefeicao(Refeicao ref) {
        Font fonteCard = new Font("Segoe UI Semibold", Font.BOLD, 20);
        Font fonteInfo = new Font("Segoe UI", Font.PLAIN, 16);
        Font fonteCal = new Font("Segoe UI Semibold", Font.BOLD, 18);
        Font fonteData = new Font("Segoe UI", Font.PLAIN, 15);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(180,180,180), 1, true));

        JLabel lblNome = new JLabel(ref.nome);
        lblNome.setFont(fonteCard);
        lblNome.setForeground(new Color(35,35,35));
        lblNome.setBounds(24, 13, 520, 28);
        card.add(lblNome);

        JLabel lblCal = new JLabel(ref.calorias + " Kcal");
        lblCal.setFont(fonteCal);
        lblCal.setForeground(new Color(30, 30, 30));
        lblCal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCal.setBounds(820, 13, 140, 24);
        card.add(lblCal);

        JLabel lblData = new JLabel(ref.data);
        lblData.setFont(fonteData.deriveFont(Font.PLAIN, 15f));
        lblData.setForeground(new Color(120,120,120));
        lblData.setHorizontalAlignment(SwingConstants.RIGHT);
        lblData.setBounds(820, 44, 140, 18);
        card.add(lblData);

        JButton menuBolinhas = new JButton("⋯");
        menuBolinhas.setFont(new Font("Dialog", Font.BOLD, 21));
        menuBolinhas.setFocusPainted(false);
        menuBolinhas.setContentAreaFilled(false);
        menuBolinhas.setBorderPainted(false);
        menuBolinhas.setBounds(960, 13, 45, 28);
        menuBolinhas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu popup = new JPopupMenu();
        JMenuItem editar = criarMenuItemVerdeBorda("Editar");
        JMenuItem excluir = criarMenuItemVerdeBorda("Excluir");
        popup.add(editar);
        popup.add(excluir);

        editar.addActionListener(e -> abrirDialogRefeicao(ref, fonteInfo, fonteCard, fonteInfo));
        excluir.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(card, "Tem certeza que deseja excluir esta refeição?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = Conexao.conectar();
                     PreparedStatement stmt = conn.prepareStatement(
                             "DELETE FROM registro_refeicao WHERE id = ? AND id_cliente = ?")) {

                    stmt.setInt(1, ref.getId());
                    stmt.setInt(2, this.idUsuarioLogado);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        refeicoes.remove(ref);
                        historicoRefeicoes.remove(ref);
                        totalCalorias -= ref.calorias;
                        atualizarLista();
                        JOptionPane.showMessageDialog(card, "Refeição excluída com sucesso.");
                    } else {
                        JOptionPane.showMessageDialog(card, "Refeição não encontrada ou você não tem permissão para excluí-la.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(card, "Erro ao excluir do banco de dados: " + ex.getMessage());
                }
            }
        });

        menuBolinhas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popup.show(menuBolinhas, e.getX(), e.getY());
            }
        });
        card.add(menuBolinhas);

        JTextArea lblDesc = new JTextArea(ref.descricao);
        lblDesc.setFont(fonteInfo.deriveFont(Font.PLAIN, 16f));
        lblDesc.setLineWrap(true);
        lblDesc.setWrapStyleWord(true);
        lblDesc.setEditable(false);
        lblDesc.setOpaque(false);
        lblDesc.setForeground(new Color(60,60,60));
        lblDesc.setSize(770, Short.MAX_VALUE);
        int descHeight = lblDesc.getPreferredSize().height;
        lblDesc.setBounds(24, 43, 770, descHeight);
        card.add(lblDesc);

        int alturaCard = Math.max(90, 43 + descHeight + 15);
        card.setPreferredSize(new Dimension(1060, alturaCard));
        card.setMaximumSize(new Dimension(1060, alturaCard));
        card.setMinimumSize(new Dimension(1060, alturaCard));
        card.setSize(1060, alturaCard);

        return card;
    }

    private void voltarTelaLogin() {
        this.setVisible(false);
        new LoginFrame();
        dispose();
    }

    private JButton criarBotaoMenu(String texto, boolean bold, Font fonteNormal, Font fonteBold) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(230,230,230));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 18, 5, 5));
        btn.setFont(bold ? fonteBold.deriveFont(16f) : fonteNormal);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton criarBotaoPreto(String texto, Font fonte) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btn.setFont(fonte.deriveFont(17f));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // CLASSE  REFEICAO
    static class Refeicao {
        private int id;
        String nome;
        int calorias;
        String descricao;
        String data;

        public Refeicao(String nome, int calorias, String descricao, String data) {
            this.nome = nome;
            this.calorias = calorias;
            this.descricao = descricao;
            this.data = data;
        }

        public Refeicao(int id, String nome, int calorias, String descricao, String data) {
            this.id = id;
            this.nome = nome;
            this.calorias = calorias;
            this.descricao = descricao;
            this.data = data;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getNome() { return nome; }
        public int getCalorias() { return calorias; }
        public String getDescricao() { return descricao; }
        public String getData() { return data; }
    }
}