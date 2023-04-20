package client;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Set;

public class User implements Serializable{
	private static final long serialVersionUID = -259528965411700238L;
	
	private String id; //user name
	private InetAddress ip;
	private Set<String> sharedInfo; //Los archivos subidos


	public User(InetAddress ip) {
		this.id = null;
		this.ip = ip;
		this.sharedInfo = null;
	}

	public void addFile(String file) {sharedInfo.add(file);}
	public void removeFile(String file) {sharedInfo.remove(file);}

	public String getId() {return id;}
	public InetAddress getIp() {return ip;}
	public Set<String> getSharedInfo() {return Collections.unmodifiableSet(sharedInfo);}

	public void setIp(InetAddress ip) {this.ip = ip;}
	public void setId(String id) {this.id = id;}
	public void setSharedInfo(Set<String> sharedInfo) {this.sharedInfo = sharedInfo;}
}
