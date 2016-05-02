

public class Daemon {
	
	public static void main (String[] args) {
		
		DaemonControl daemon = new DaemonControl("telemetry");
		
		//daemon.Start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Stop");
		daemon.Stop();
		System.out.println("Stoped");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Restart");
		daemon.Restart();
		System.out.println("Restarted");
		
		
	}
}
