import java.io.*;
import java.net.*;

public class Cliente extends Thread {
	private static boolean done = false;
	private Socket conexao;

	public Cliente(Socket s) {
		conexao = s;
	}

	public static void main(String[] args) throws IOException {
		Socket conexao = new Socket("localhost", 2001);
		PrintStream saida = new PrintStream(conexao.getOutputStream());
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Entre com o seu nome: ");
		String meuNome = teclado.readLine();
		saida.println(meuNome);
		Thread t = new Cliente(conexao);
		t.start();
		String linha;
		while (true) {
			if (done) {
				break;
			}

			System.out.println("> ");
			linha = teclado.readLine();
			saida.println(linha);
		}
	}

	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			String linha;
		while (true) {
			linha = entrada.readLine();
			if (linha.trim().equals("")) {
				System.out.println("Conexao encerrada!!!");
				break;
			}
			System.out.println();
			System.out.println(linha);
			System.out.print("...> ");
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		done = true;
	}
}