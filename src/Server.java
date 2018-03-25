import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

public class Server extends Thread {
	private static Map clientes;
	private static List nomeClientesConectados = new ArrayList();
	private Socket conexao;
	private static String meuNome;
	private static InetAddress ip_cliente;
	private static String host_name_cliente;
	private static Integer porta;
	private static FileWriter imp;
	static Integer numLinha = 0;
	private static ArrayList<String> listaLog = new ArrayList<String>();

	public Server(Socket s) {
		conexao = s;
	}
	public boolean verificaNomeRepetido(String nome) {
		   for (int i = 0; i < nomeClientesConectados.size(); i++) {
	            if (nomeClientesConectados.get(i).equals(nome))
	                return true;
	        }
		   nomeClientesConectados.add(nome);
	        return false;
	}
	// Metodos para adicionar as ações do cliente no arquivo de LOG
	public static void adicionaListaLog(String msg) throws IOException {
		listaLog.add(msg);
		System.out.println("mensagem:"+listaLog+"\nindex :");
		gravarLog(listaLog);
	}

	public static void gravarLog(ArrayList<String> listamsg) throws IOException {
		File f = new File("texto.txt");
		imp = new FileWriter(f,true);
		imp.write(numLinha + " - " + listamsg.get(numLinha) + "\n");
		numLinha++;
		fechaArquivoLog();
	}

	public static void fechaArquivoLog() throws IOException {
		imp.close();
	}
	// Metodos para adicionar as ações do cliente no arquivo de LOG

	public static void main(String[] args) throws IOException {
		clientes = new HashMap();
		ServerSocket s = new ServerSocket(2001);
		while (true) {
			System.out.print("Esperando conectar...");
			Socket conexao = s.accept();
			System.out.println(" Conectou!");
			Thread t = new Server(conexao);
			ip_cliente = conexao.getInetAddress();
			host_name_cliente = ip_cliente.getHostName();
			porta = conexao.getPort();
			adicionaListaLog("<nome da máquina>" + host_name_cliente + "<ip da máquina>" + ip_cliente + "<porta>#"
					+ porta + "<mensagem> - CONECTOU");
			t.start();
		}
	}

	public void run() {
		BufferedReader entrada = null;
		String linha ;
		
		try {
			PrintStream saida ;
			entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			
			saida = new PrintStream(conexao.getOutputStream());
			meuNome = entrada.readLine();
			if(verificaNomeRepetido(this.meuNome)) {
				saida.println("Ja existe um cliente com este Nome, use Outro!");
				 return;
			}
			if(this.meuNome == null) {
				return;
			}
			clientes.put(this.meuNome, saida);
			
			
			linha = entrada.readLine();
			String[] msg=linha.split("-");
			while ((linha != null) && (!linha.trim().equals(""))) {
				saida.println("Conectados:" + nomeClientesConectados.toString());
				adicionaListaLog(this.meuNome + " - " + "<nome da máquina>" + host_name_cliente + "<ip da máquina>"
						+ ip_cliente + "<porta>#" + porta + "<mensagem> = " + linha);
				send(saida, " disse: ", linha,msg);
				linha = entrada.readLine();
				msg=entrada.readLine().split("-");
			}

			send(saida," saiu ", " do Chat!",msg);
			adicionaListaLog(this.meuNome + " - " + "saiu do chat" + linha);
			clientes.remove(Server.meuNome);
			nomeClientesConectados.remove(this.meuNome);
			saida.println("");
			conexao.close();
			fechaArquivoLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	public void send(PrintStream saida, String acao, String linha,String[] msg) throws IOException {
		out:
			for (Iterator iterator = clientes.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry cliente = (Entry) iterator.next();
				PrintStream chat = (PrintStream) cliente.getValue();
				
				if(chat != saida) {
					if(msg.length == 1) {
						saida.println("Conectados:" + nomeClientesConectados.toString());
						adicionaListaLog(this.meuNome + " - " + "<nome da máquina>" + host_name_cliente + "<ip da máquina>"
								+ ip_cliente + "<porta>#" + porta + "<mensagem> = " + linha);
						chat.println(this.meuNome + acao + linha);
					}
					else {
						if (msg[1].equalsIgnoreCase((String) cliente.getKey())) {
							saida.println("Conectados:" + nomeClientesConectados.toString());
							adicionaListaLog(this.meuNome + " - " + "<nome da máquina>" + host_name_cliente + "<ip da máquina>"
									+ ip_cliente + "<porta>#" + porta + "<mensagem> = " + msg[0]);
	                        chat.println(this.meuNome + acao + msg[0]);
	                        break out;
	                    }
					}
				}
			}
		

	}


}