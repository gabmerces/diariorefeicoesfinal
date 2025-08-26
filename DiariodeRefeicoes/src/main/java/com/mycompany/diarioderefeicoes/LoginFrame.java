package com.mycompany.diarioderefeicoes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection; // Importe para conexão com o banco
import java.sql.PreparedStatement; // Importe para consultas preparadas
import java.sql.ResultSet; // Importe para resultados de consulta
import java.sql.SQLException; // Importe para tratamento de erros SQL
// Não é mais necessário java.io.* (BufferedReader, FileReader) pois não usaremos arquivo de texto

public class LoginFrame extends JFrame {

    // Componentes de UI que precisam ser acessados em Listeners
    private JTextField txtUsuario;
    private JPasswordField txtSenha;

    // Caixa customizada: círculo com símbolo, borda arredondada na caixa, verde e preto
    private void showCustomMessage(Component parent, String message, boolean success) {
        Color GREEN = new Color(0x406951);
        Color BLACK = Color.BLACK;
        Font fonteMsg = new Font("Segoe UI", Font.PLAIN, 18);
        Font fonteBtn = new Font("Segoe UI", Font.BOLD, 16);

        Color mainColor = success ? GREEN : BLACK;

        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);

        // Painel arredondado com borda verde ou preta
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 22;
                int w = getWidth();
                int h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w-1, h-1, arc, arc);
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 32, 20, 32));

        // Espaço extra para o ícone não ser cortado
        panel.add(Box.createVerticalStrut(16));

        // Círculo (verde ou preto) com símbolo ✔ ou ✖ (NUNCA quadrado)
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int d = 54;
                int x = (getWidth() - d) / 2;
                int y = 0;

                // circulo verde ou preto
                g2.setColor(mainColor);
                g2.fillOval(x, y, d, d);

                // símbolo (sempre branco)
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

        // Mensagem
        JLabel lblMsg = new JLabel(message, SwingConstants.CENTER);
        lblMsg.setFont(fonteMsg);
        lblMsg.setForeground(mainColor);
        lblMsg.setBorder(BorderFactory.createEmptyBorder(22, 10, 14, 10));
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblMsg);

        // Botão OK
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

        panel.add(Box.createVerticalStrut(14)); // espaço extra abaixo

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public LoginFrame() {
        setTitle("Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza a janela
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null); // Usando layout nulo para posicionamento manual

        // Cores e fontes
        Color GREEN = new Color(0x406951);
        Color CARD_BORDER = new Color(0xF0F0F0);
        Font fonteTitulo = new Font("Segoe UI", Font.BOLD, 44);
        Font fonteLabel = new Font("Segoe UI", Font.PLAIN, 18);
        Font fonteInput = new Font("Segoe UI", Font.PLAIN, 17);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 17);
        Font fonteLink = new Font("Segoe UI", Font.PLAIN, 15);

        getContentPane().setBackground(Color.WHITE);

        // Medidas da tela
        int telaLarg = Toolkit.getDefaultToolkit().getScreenSize().width;
        int telaAlt = Toolkit.getDefaultToolkit().getScreenSize().height;

        // Título
        JLabel titulo = new JLabel("Diário de Refeições", SwingConstants.CENTER);
        titulo.setFont(fonteTitulo);
        titulo.setForeground(GREEN);

        // Emoji (garfo e faca)
        JLabel emoji = new JLabel("🍽", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 74));
        emoji.setBounds(0, 0, 140, 110);

        // Card central de login
        int cardWidth = 470, cardHeight = 320;
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
            }
        };
        card.setBackground(new Color(0,0,0,0));
        card.setBounds(0, 0, cardWidth, cardHeight);

        // Label e campo de usuário
        JLabel lblUsuario = new JLabel("Usuário");
        lblUsuario.setFont(fonteLabel);
        lblUsuario.setForeground(Color.BLACK);
        lblUsuario.setBounds(36, 26, 380, 22);

        txtUsuario = new JTextField(); // Inicializa o campo
        txtUsuario.setFont(fonteInput);
        txtUsuario.setBounds(36, 52, 398, 36);
        txtUsuario.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtUsuario.setBackground(Color.WHITE);
        txtUsuario.setMargin(new Insets(3, 8, 3, 8)); // Padding interno
        // Placeholder para o campo de usuário
        txtUsuario.setForeground(Color.GRAY);
        txtUsuario.setText("Digite o seu nome de usuário");
        txtUsuario.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsuario.getForeground().equals(Color.GRAY)) {
                    txtUsuario.setText("");
                    txtUsuario.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtUsuario.getText().isEmpty()) {
                    txtUsuario.setForeground(Color.GRAY);
                    txtUsuario.setText("Digite o seu nome de usuário");
                }
            }
        });

        // Label e campo de senha
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setFont(fonteLabel);
        lblSenha.setForeground(Color.BLACK);
        lblSenha.setBounds(36, 96, 380, 22);

        txtSenha = new JPasswordField(); // Inicializa o campo
        txtSenha.setFont(fonteInput);
        txtSenha.setBounds(36, 122, 398, 36);
        txtSenha.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        txtSenha.setBackground(Color.WHITE);
        txtSenha.setMargin(new Insets(3, 8, 3, 8)); // Padding interno
        // Placeholder para o campo de senha
        txtSenha.setEchoChar((char)0); // Mostra texto como normal quando tem placeholder
        txtSenha.setForeground(Color.GRAY);
        txtSenha.setText("Digite a sua senha");
        txtSenha.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Se o placeholder estiver presente, limpa e define o echo char
                if (new String(txtSenha.getPassword()).equals("Digite a sua senha") && txtSenha.getForeground().equals(Color.GRAY)) {
                    txtSenha.setText("");
                    txtSenha.setForeground(Color.BLACK);
                    txtSenha.setEchoChar('•'); // Altera para o caractere de senha
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                // Se o campo estiver vazio, restaura o placeholder
                if (new String(txtSenha.getPassword()).isEmpty()) {
                    txtSenha.setEchoChar((char)0); // Mostra texto como normal
                    txtSenha.setForeground(Color.GRAY);
                    txtSenha.setText("Digite a sua senha");
                }
            }
        });

        // Botão Login
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(fonteBotao);
        btnLogin.setBackground(Color.BLACK);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Ajusta padding
        btnLogin.setBounds(36, 180, 398, 38);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Link de cadastro
        JButton btnCadastro = new JButton("Cadastre-se");
        btnCadastro.setFont(fonteLink);
        btnCadastro.setForeground(Color.BLACK);
        btnCadastro.setBackground(Color.WHITE);
        btnCadastro.setBorderPainted(false);
        btnCadastro.setFocusPainted(false);
        btnCadastro.setBounds(36, 226, 398, 30);
        btnCadastro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCadastro.setHorizontalAlignment(SwingConstants.CENTER);

        // Adicionando campos e botões ao card
        card.add(lblUsuario);
        card.add(txtUsuario);
        card.add(lblSenha);
        card.add(txtSenha);
        card.add(btnLogin);
        card.add(btnCadastro);

        // Centralização dos elementos na tela maximizada
        int posY = telaAlt / 2 - (cardHeight / 2) + 60;
        int tituloY = posY - 200;
        int emojiY = tituloY + 72;

        titulo.setBounds((telaLarg - 600) / 2, tituloY, 600, 64);
        emoji.setBounds((telaLarg - 140) / 2, emojiY, 140, 110);
        card.setBounds((telaLarg - cardWidth) / 2, posY, cardWidth, cardHeight);

        // Adicionando elementos ao JFrame
        add(titulo);
        add(emoji);
        add(card);

        // Ação ao clicar no botão de login
        btnLogin.addActionListener(e -> {
            // Pega o texto dos campos, tratando o placeholder e removendo espaços em branco
            String usuarioDigitado = txtUsuario.getForeground().equals(Color.GRAY) ? "" : txtUsuario.getText().trim();
            String senhaDigitada = new String(txtSenha.getPassword()).trim();

            if (usuarioDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                showCustomMessage(this, "Por favor, preencha todos os campos.", false);
                return;
            }

            // Chama o método de validação de login no banco de dados
            UserInfo userInfo = validarLoginMySQL(usuarioDigitado, senhaDigitada);

            if (userInfo != null) {
                showCustomMessage(this, "Login bem-sucedido!", true);
                // Abre o MainFrame e passa o nome completo do usuário E o ID
                new MainFrame(userInfo.getNomeCompleto(), userInfo.getIdUsuario());
                dispose(); // Fecha a tela de login
            } else {
                showCustomMessage(this, "Usuário ou senha inválidos!", false);
            }
        });

        // Ação ao clicar no botão de cadastro
        btnCadastro.addActionListener(e -> {
            new CadastroFrame(); // Abre a tela de cadastro
            dispose(); // Fecha a tela de login
        });

        setVisible(true);
    }

    // Classe interna para encapsular o ID e o nome completo do usuário após o login
    private static class UserInfo {
        private int idUsuario;
        private String nomeCompleto;

        public UserInfo(int idUsuario, String nomeCompleto) {
            this.idUsuario = idUsuario;
            this.nomeCompleto = nomeCompleto;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public String getNomeCompleto() {
            return nomeCompleto;
        }
    }

    // Método para validar login no MySQL
    private UserInfo validarLoginMySQL(String nomeUsuario, String senha) {
        // A consulta SQL seleciona o ID, o NOME COMPLETO e a SENHA, buscando por nome_usuario
        String sql = "SELECT id, nome, senha FROM usuarios WHERE nome_usuario = ?";

        try (Connection conn = Conexao.conectar(); // Usa a classe Conexao para a conexão
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeUsuario); // Define o parâmetro para o nome de usuário
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaArmazenada = rs.getString("senha");
                // ATENÇÃO: Em um aplicativo real, use um hash de senha (ex: BCrypt) para comparação segura
                if (senha.equals(senhaArmazenada)) { // Compara a senha digitada com a senha armazenada
                    int id = rs.getInt("id"); // Obtém o ID do usuário
                    String nomeCompleto = rs.getString("nome"); // Obtém o nome completo do usuário
                    return new UserInfo(id, nomeCompleto); // Retorna um objeto UserInfo
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime o erro no console para depuração
            showCustomMessage(this, "Erro ao conectar ao banco de dados: " + e.getMessage(), false);
        }
        return null; // Retorna null se o login falhar
    }

    // Não precisa de método main aqui, o Main.java já cuida disso.
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> new LoginFrame());
    // }
}