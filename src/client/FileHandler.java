package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileHandler implements Runnable{

	private Client cli;
	private String file, peer;
	private ServerSocket server;

	public FileHandler(Client cli, String file, String peer) throws IOException{
		this.cli = cli;
		this.file = file;
		this.peer = peer;
		server = new ServerSocket(0);
	}

	@Override
	public void run() {
		try{
			cli.sendSocketData(peer, server.getLocalPort(), file);
			Socket socket = server.accept();
			
			//Leemos el archivo
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			File f = (File) in.readObject();

			//Lo guardamos en la carpeta de descargas
			FileOutputStream out = new FileOutputStream("data" + File.separator + 
														cli.getName() + File.separator + 
														f.getName());

			//Actualizamos mi lista local de archivos y la del server

		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
