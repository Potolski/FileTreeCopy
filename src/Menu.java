import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class Menu {

	private JFrame frame;
	private JTable table;
	private File origem;
	private File destino;
	private List<File> arquivosSelecionados;
	private static Clip clip;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu window = new Menu();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Menu() {
		initialize();
	}

	public static void music() {
		try {
			URL resource = Menu.class.getResource("/main/resources/music.wav");
			clip = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream(resource);
			clip.open(ais);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void criaEstruturas(File file) {
		String estrutura = file.getAbsolutePath().substring(origem.getAbsolutePath().length(),
				file.getAbsolutePath().length() - file.getName().length());

		StringTokenizer tok = new StringTokenizer(estrutura, File.separator);
		String caminhoEstruturaIncremental = origem.getName();

		Path path = Paths.get(destino.getAbsolutePath() + File.separator + caminhoEstruturaIncremental);

		if (Files.notExists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		while (tok.hasMoreTokens()) {
			String nomePasta = tok.nextToken();
			caminhoEstruturaIncremental = caminhoEstruturaIncremental + File.separator + nomePasta;
			path = Paths.get(destino.getAbsolutePath() + File.separator + caminhoEstruturaIncremental);
			if (Files.notExists(path)) {
				try {
					Files.createDirectory(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String filepath = path + File.separator + file.getName();
		File fileDestino = new File(filepath);
		try {
			copyFileUsingStream(file, fileDestino);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public void atualizaLista(String caminho) {
		File fileDeletar = null;
		for (File file : arquivosSelecionados) {
			String caminhoFileAtual = origem.getName()
					+ file.getAbsolutePath().substring(origem.getAbsolutePath().length());
			if (caminho.equals(caminhoFileAtual)) {
				fileDeletar = file;
				break;
			}
		}
		arquivosSelecionados.remove(fileDeletar);
	}

	public void removeSelectedRows(JTable table) {
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		int[] rows = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			atualizaLista(table.getValueAt(rows[i] - i, 0).toString());
			model.removeRow(rows[i] - i);

		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		music();
		frame = new JFrame();
		frame.getContentPane().setLayout(null);
		frame.setSize(570, 400);
		frame.setLocationRelativeTo(null);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				
					System.exit(0);
			}
		});

		JLabel lblPontoInicial = new JLabel("Origem\r\n");
		lblPontoInicial.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPontoInicial.setBounds(10, 11, 80, 14);
		frame.getContentPane().add(lblPontoInicial);

		JLabelShadow lblOrigem = new JLabelShadow("Nenhum ponto selecionado");
		lblOrigem.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOrigem.setForeground(Color.BLACK);
		lblOrigem.setShadowColor(Color.lightGray);
		lblOrigem.setBounds(10, 36, 377, 14);
		frame.getContentPane().add(lblOrigem);

		JLabelShadow lblDestino = new JLabelShadow("Nenhum ponto selecionado");
		lblDestino.setForeground(SystemColor.desktop);
		lblDestino.setBackground(Color.WHITE);
		lblDestino.setShadowColor(Color.LIGHT_GRAY);
		lblDestino.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblDestino.setBounds(10, 99, 377, 14);
		frame.getContentPane().add(lblDestino);

		JButton btnSelecionarDestino = new JButton("Selecionar Destino\r\n");
		btnSelecionarDestino.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(null, "Selecione o Destino");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					destino = file;
					lblDestino.setText(destino.getAbsolutePath());
				}
			}
		});
		btnSelecionarDestino.setBounds(397, 92, 150, 28);
		frame.getContentPane().add(btnSelecionarDestino);

		JLabel lblPontoFinal = new JLabel("Destino\r\n");
		lblPontoFinal.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPontoFinal.setBounds(10, 74, 68, 14);
		frame.getContentPane().add(lblPontoFinal);

		table = new JTable();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		JScrollPane scroll = new JScrollPane(table);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		model.addColumn("Arquivos Selecionados");

		table.setBounds(20, 149, 513, 161);
		scroll.setBounds(20, 131, 513, 157);
		frame.getContentPane().add(scroll);
//		frame.getContentPane().add(table);

		JButton btnCopiar = new JButton("Copiar");
		btnCopiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (File file : arquivosSelecionados) {
					criaEstruturas(file);

				}
				JOptionPane.showMessageDialog(null, "Arquivos copiados com Sucesso!");
			}

		});
		btnCopiar.setBounds(458, 329, 89, 23);
		frame.getContentPane().add(btnCopiar);

		JButton btnDeletar = new JButton("Deletar");
		btnDeletar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<File> ListaDeletar = new ArrayList<File>();
				removeSelectedRows(table);
			}
		});
		btnDeletar.setBounds(458, 299, 89, 23);
		frame.getContentPane().add(btnDeletar);

		JButton btnSelecionarArquivos = new JButton("Selecionar arquivos");
		btnSelecionarArquivos.setEnabled(false);
		btnSelecionarArquivos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(true);
				fc.setCurrentDirectory(origem);
				int returnVal = fc.showDialog(null, "Selecione a Origem");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File[] files = fc.getSelectedFiles();
					boolean arquivoDuplicado = false;
					for (int i = 0; i < files.length; i++) {
						File file = files[i];
						if (!arquivosSelecionados.contains(file)
								&& file.getAbsolutePath().contains(origem.getAbsolutePath())) {
							arquivosSelecionados.add(file);
							model.addRow(new Object[] { origem.getName()
									+ file.getAbsolutePath().substring(origem.getAbsolutePath().length()) });
						} else if (!file.getAbsolutePath().contains(origem.getAbsolutePath())) {
							JOptionPane.showMessageDialog(null,
									"Arquivos Selecionados não estão dentro do diretório de Origem");
						} else {
							arquivoDuplicado = true;
						}
					}
					if (arquivoDuplicado) {
						JOptionPane.showMessageDialog(null, "Algum(ns) Arquivo(s) já constava(m) na lista");
					}
					table.setModel(model);
				}

			}
		});

		btnSelecionarArquivos.setBounds(30, 321, 150, 23);
		frame.getContentPane().add(btnSelecionarArquivos);

		JButton btnSelecionarOrigem = new JButton("Selecionar Origem\r\n");
		btnSelecionarOrigem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(null, "Selecione a Origem");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					origem = file;
					lblOrigem.setText(origem.getAbsolutePath());
					btnSelecionarArquivos.setEnabled(true);
					arquivosSelecionados = new ArrayList<File>();
					model.setRowCount(0);
					table.setModel(model);

				}
			}
		});
		btnSelecionarOrigem.setBounds(397, 29, 150, 28);
		frame.getContentPane().add(btnSelecionarOrigem);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBackground(new Color(244, 247, 252));
		lblNewLabel.setIcon(new ImageIcon(Menu.class.getResource("/main/resources/janela5.png")));
		lblNewLabel.setBounds(0, 0, 554, 362);
		frame.getContentPane().add(lblNewLabel);
		frame.setFocusTraversalPolicy(
				new FocusTraversalOnArray(new Component[] { frame.getContentPane(), lblPontoInicial, lblOrigem,
						btnSelecionarDestino, lblPontoFinal, lblDestino, btnSelecionarOrigem, table, btnCopiar }));
	}

	private void setBounds(int i, int j, int width, int k) {
		// TODO Auto-generated method stub

	}
}
