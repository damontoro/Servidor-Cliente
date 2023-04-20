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
			
			//Leemos el archivo
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			File f = (File) in.readObject();
			System.out.println(f.toString());

		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
