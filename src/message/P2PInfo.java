package message;

import java.net.InetAddress;

public class P2PInfo {
	private InetAddress ip;
	private int port;
	private String fileName;

	public P2PInfo(InetAddress ip, int port, String fileName) {
		this.ip = ip;
		this.port = port;
		this.fileName = fileName;
	}


	public InetAddress getIp() {return ip;}
	public String getFileName() {return fileName;}
	public int getPort() {return port;}
	public void setIp(InetAddress ip) {this.ip = ip;}
	public void setPort(int port) {this.port = port;}
	public void setFileName(String fileName) {this.fileName = fileName;}

}
