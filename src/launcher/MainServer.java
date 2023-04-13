package launcher;

public class MainServer {
	
	public static void main(String[] args) {
		while(true){
			try{
				Thread.sleep(100);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			System.out.println("Hello Server");
		}
	}
}
