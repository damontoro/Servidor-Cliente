package client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileHandler implements Runnable{
	private Client cli;
	private String file, peer;
	private ServerSocket server;

	public FileHandler(Client cli, String peer, String file) throws IOException{
		this.cli = cli;
		this.peer = peer;
		this.file = file;
		server = new ServerSocket(0);
	}

	@Override
	public void run() {
		try{
			cli.sendSocketData(peer, server.getLocalPort(), file);
			Socket socket = server.accept();
			
			//Recibimos el archivo
			FileOutputStream out = new FileOutputStream("data" + File.separator + cli.getName() + File.separator + file);
			byte[] buffer = new byte[1024];
			InputStream in = socket.getInputStream();
			int count;
			while((count = in.read(buffer)) >= 0){
				out.write(buffer, 0, count);
			}
			//Notificar al usuario y al servidor que el archivo se ha descargado
			cli.onFileDownloaded(file);

			out.close();
			in.close();

		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
