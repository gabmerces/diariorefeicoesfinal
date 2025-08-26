package com.mycompany.diarioderefeicoes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException; // Importe para tratar erros de PK/Unique

public class CadastroFrame extends JFrame { // Certifique-se que o nome da classe e do arquivo são "CadastroFrame"

    // Componentes de UI (fields)
    private JTextField txtNome;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtUsuario; // Este é o nome_usuario do banco
    private JPasswordField txtSenha;

    // --- Métodos de UI para mensagens customizadas ---
    private void showCustomMessage(Component parent, String message, boolean success) {
        Color GREEN = new Color(0x406951);
        Color BLACK = Color.BLACK;
        Font fonteMsg = new Font("Segoe UI", Font.PLAIN, 18);
        Font fonteBtn = new Font("Segoe UI", Font.BOLD, 16);
        Color mainColor = success ? GREEN : BLACK;

        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 22, w = getWidth(), h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 32, 20, 32));
        panel.add(Box.createVerticalStrut(16));

        JPanel iconPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = 54, x = (getWidth() - d) / 2, y = 0;
                g2.setColor(mainColor);
                g2.fillOval(x, y, d, d);
                g2.setFont(new Font("Dialog", Font.BOLD, 38));
                g2.setColor(Color.WHITE);
                String symbol = success ? "✔" : "✖";
                FontMetrics fm = g2.getFontMetrics();
                int sx = x + (d - fm.stringWidth(symbol)) / 2;
                int sy = y + (d - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(symbol, sx, sy);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(58, 54));
        iconPanel.setMaximumSize(new Dimension(58, 54));
        panel.add(iconPanel);

        JLabel lblMsg = new JLabel(message, SwingConstants.CENTER);
        lblMsg.setFont(fonteMsg);
        lblMsg.setForeground(mainColor);
        lblMsg.setBorder(BorderFactory.createEmptyBorder(22, 10, 14, 10));
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblMsg);

        JButton btnOk = new JButton("OK");
        btnOk.setFont(fonteBtn);
        btnOk.setFocusable(false);
        btnOk.setBackground(mainColor);
        btnOk.setForeground(Color.WHITE);
        btnOk.setBorder(BorderFactory.createEmptyBorder(8, 34, 8, 34));
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(e -> dialog.dispose());
        panel.add(btnOk);
        panel.add(Box.createVerticalStrut(14));

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public CadastroFrame() {
        setTitle("Cadastro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Cores e fontes
        Color GREEN = new Color(0x406951);
        Color CARD_BORDER = new Color(0xF0F0F0);
        Font fonteTitulo = new Font("Segoe UI", Font.BOLD, 44);
        Font fonteLabel = new Font("Segoe UI", Font.PLAIN, 18);
        Font fonteInput = new Font("Segoe UI", Font.PLAIN, 17);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 17);

        getContentPane().setBackground(Color.WHITE);

        // Medidas da tela
        int telaLarg = Toolkit.getDefaultToolkit().getScreenSize().width;

        // Painel de título (título centralizado + emoji centralizado abaixo)
        JPanel painelTitulo = new JPanel(null);
        painelTitulo.setOpaque(false);
        painelTitulo.setBounds(0, 30, telaLarg, 160);

        // Título centralizado
        JLabel lblTitulo = new JLabel("Diário de Refeições", SwingConstants.CENTER);
        lblTitulo.setFont(fonteTitulo);
        lblTitulo.setForeground(GREEN);
        lblTitulo.setBounds(0, 10, telaLarg, 60);

        // Emoji centralizado abaixo do título
        JLabel emoji = new JLabel("🍽", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
        emoji.setBounds((telaLarg - 60) / 2, 75, 60, 60); // 60x60px centralizado no painel

        painelTitulo.add(lblTitulo);
        painelTitulo.add(emoji);

        // Card central com moldura arredondada (largura aumentada para caber botões)
        int cardWidth = 780, cardHeight = 390;
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            }
        };
        card.setBackground(new Color(0, 0, 0, 0));
        int cardY = 170;
        card.setBounds((telaLarg - cardWidth) / 2, cardY, cardWidth, cardHeight);

        // Campos (nome, telefone, email, usuario, senha)
        int leftLabel = 65, leftField = 250, fieldWidth = 370, rowHeight = 35, startY = 38, spacingY = 45;
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(fonteLabel);
        lblNome.setBounds(leftLabel, startY, 100, rowHeight);
        txtNome = new JTextField(); // Inicializado
        txtNome.setFont(fonteInput);
        txtNome.setBounds(leftField, startY, fieldWidth, rowHeight);
        txtNome.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtNome.setBackground(Color.WHITE);

        JLabel lblTelefone = new JLabel("Telefone:");
        lblTelefone.setFont(fonteLabel);
        lblTelefone.setBounds(leftLabel, startY + spacingY, 100, rowHeight);
        txtTelefone = new JTextField(); // Inicializado
        txtTelefone.setFont(fonteInput);
        txtTelefone.setBounds(leftField, startY + spacingY, fieldWidth, rowHeight);
        txtTelefone.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtTelefone.setBackground(Color.WHITE);

        JLabel lblEmail = new JLabel("E-mail:");
        lblEmail.setFont(fonteLabel);
        lblEmail.setBounds(leftLabel, startY + 2 * spacingY, 100, rowHeight);
        txtEmail = new JTextField(); // Inicializado
        txtEmail.setFont(fonteInput);
        txtEmail.setBounds(leftField, startY + 2 * spacingY, fieldWidth, rowHeight);
        txtEmail.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtEmail.setBackground(Color.WHITE);

        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setFont(fonteLabel);
        lblUsuario.setBounds(leftLabel, startY + 3 * spacingY, 100, rowHeight);
        txtUsuario = new JTextField(); // Inicializado
        txtUsuario.setFont(fonteInput);
        txtUsuario.setBounds(leftField, startY + 3 * spacingY, fieldWidth, rowHeight);
        txtUsuario.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtUsuario.setBackground(Color.WHITE);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(fonteLabel);
        lblSenha.setBounds(leftLabel, startY + 4 * spacingY, 100, rowHeight);
        txtSenha = new JPasswordField(); // Inicializado
        txtSenha.setFont(fonteInput);
        txtSenha.setBounds(leftField, startY + 4 * spacingY, fieldWidth, rowHeight);
        txtSenha.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtSenha.setBackground(Color.WHITE);

        card.add(lblNome);
        card.add(txtNome);
        card.add(lblTelefone);
        card.add(txtTelefone);
        card.add(lblEmail);
        card.add(txtEmail);
        card.add(lblUsuario);
        card.add(txtUsuario);
        card.add(lblSenha);
        card.add(txtSenha);

        // Painel de botões centralizado, todos na mesma linha!
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.setBounds(0, cardHeight - 70, cardWidth, 60);

        JButton btnVoltar = new JButton("Voltar");
        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnConsultar = new JButton("Consultar");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");

        JButton[] botoes = {btnVoltar, btnCadastrar, btnConsultar, btnAlterar, btnExcluir};

        for (JButton btn : botoes) {
            btn.setFont(fonteBotao);
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(120, 38));
            painelBotoes.add(btn);
        }
        card.add(painelBotoes);

        // Adicionar à tela
        add(painelTitulo);
        add(card);

        // --- Listeners dos botões ---

        btnCadastrar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();
            String usuario = txtUsuario.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();

            if (validarCampos(nome, telefone, email, usuario, senha)) {
                if (salvarUsuario(nome, telefone, email, usuario, senha)) {
                    showCustomMessage(this, "Cadastro realizado com sucesso! Faça login para continuar.", true);
                    new LoginFrame(); // Volta para a tela de Login
                    dispose();
                } else {
                    showCustomMessage(this, "Erro ao cadastrar usuário. O nome de usuário ou e-mail já pode existir.", false);
                }
            }
        });

        btnConsultar.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            if (usuario.isEmpty()) {
                showCustomMessage(this, "Informe o nome de usuário para consultar.", false);
                return;
            }

            Usuario user = consultarUsuario(usuario);
            if (user != null) {
                txtNome.setText(user.nome);
                txtTelefone.setText(user.telefone);
                txtEmail.setText(user.email);
                txtUsuario.setText(user.nomeUsuario); // Use user.nomeUsuario
                txtSenha.setText(user.senha); // ATENÇÃO: Senha não deve ser exibida!
                showCustomMessage(this, "Usuário encontrado!", true);
            } else {
                showCustomMessage(this, "Usuário não encontrado.", false);
            }
        });

        btnAlterar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim(); // Corrigido de txtTeletefone
            String email = txtEmail.getText().trim();
            String usuario = txtUsuario.getText().trim(); // Usuário a ser alterado (chave)
            String senha = new String(txtSenha.getPassword()).trim();

            if (usuario.isEmpty()) {
                showCustomMessage(this, "Informe o nome de usuário para alterar.", false);
                return;
            }

            if (validarCampos(nome, telefone, email, usuario, senha)) {
                if (alterarUsuario(nome, telefone, email, usuario, senha)) {
                    showCustomMessage(this, "Cadastro alterado com sucesso!", true);
                } else {
                    showCustomMessage(this, "Usuário não encontrado para alteração ou nenhum dado foi modificado.", false);
                }
            }
        });

        btnExcluir.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            if (usuario.isEmpty()) {
                showCustomMessage(this, "Informe o nome de usuário para excluir.", false);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o usuário " + usuario + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (excluirUsuario(usuario)) {
                    showCustomMessage(this, "Cadastro excluído com sucesso!", true);
                    // Limpar campos após a exclusão
                    txtNome.setText("");
                    txtTelefone.setText("");
                    txtEmail.setText("");
                    txtUsuario.setText("");
                    txtSenha.setText("");
                } else {
                    showCustomMessage(this, "Usuário não encontrado para exclusão.", false);
                }
            }
        });

        btnVoltar.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }

    // --- Métodos de validação e operações de banco de dados ---

    private boolean validarCampos(String nome, String telefone, String email, String usuario, String senha) {
        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || usuario.isEmpty() || senha.isEmpty()) {
            showCustomMessage(this, "Todos os campos são obrigatórios!", false);
            return false;
        }
        return true;
    }

    private boolean salvarUsuario(String nome, String telefone, String email, String usuario, String senha) {
        String sql = "INSERT INTO usuarios (nome, telefone, email, nome_usuario, senha) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar(); // Usa a classe Conexao
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.setString(3, email);
            stmt.setString(4, usuario); // Corresponde a 'nome_usuario' no BD
            stmt.setString(5, senha); // ATENÇÃO: Em produção, hash de senha aqui!

            stmt.executeUpdate();
            return true;

        } catch (SQLIntegrityConstraintViolationException ex) {
            ex.printStackTrace(); // Para depuração
            return false; // Retorna false se o nome de usuário ou e-mail já existirem
        } catch (SQLException e) {
            e.printStackTrace(); // Para depuração
            showCustomMessage(this, "Erro ao salvar no banco de dados: " + e.getMessage(), false);
            return false;
        }
    }

    private Usuario consultarUsuario(String usuario) {
        String sql = "SELECT nome, telefone, email, nome_usuario, senha FROM usuarios WHERE nome_usuario = ?";

        try (Connection conn = Conexao.conectar(); // Usa a classe Conexao
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("nome_usuario"), // Pega do banco como 'nome_usuario'
                        rs.getString("senha") // ATENÇÃO: Senha não deve ser recuperada em um app real!
                );
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Para depuração
            showCustomMessage(this, "Erro ao consultar usuário: " + e.getMessage(), false);
        }

        return null;
    }

    private boolean alterarUsuario(String nome, String telefone, String email, String usuarioAntigo, String senha) {
        // A atualização é baseada no 'nome_usuario' atual (usuarioAntigo)
        String sql = "UPDATE usuarios SET nome = ?, telefone = ?, email = ?, senha = ? WHERE nome_usuario = ?";

        try (Connection conn = Conexao.conectar(); // Usa a classe Conexao
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, telefone);
            stmt.setString(3, email);
            stmt.setString(4, senha); // ATENÇÃO: Em produção, hash de senha aqui!
            stmt.setString(5, usuarioAntigo); // WHERE clause usa o nome de usuário antigo

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // Para depuração
            showCustomMessage(this, "Erro ao alterar usuário: " + e.getMessage(), false);
            return false;
        }
    }

    private boolean excluirUsuario(String usuario) {
        String sql = "DELETE FROM usuarios WHERE nome_usuario = ?";

        try (Connection conn = Conexao.conectar(); // Usa a classe Conexao
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // Para depuração
            showCustomMessage(this, "Erro ao excluir usuário: " + e.getMessage(), false);
            return false;
        }
    }

    // Classe interna para representar um usuário
    private static class Usuario {
        String nome;
        String telefone;
        String email;
        String nomeUsuario; // Renomeado para evitar conflito e corresponder ao BD
        String senha;

        public Usuario(String nome, String telefone, String email, String nomeUsuario, String senha) {
            this.nome = nome;
            this.telefone = telefone;
            this.email = email;
            this.nomeUsuario = nomeUsuario;
            this.senha = senha;
        }
    }
}
