import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DaemonControl{
	
	String daemon;

	public DaemonControl(String daemon){
		this.daemon=daemon;
	}
	
	public void Start(){
		this.Control("start");
	}
		
	public void Stop(){
		this.Control("stop");
	}
	
	public void Restart(){
		this.Control("restart");
	}
	
	public void Control(String control){
		
		String cmd = "/etc/init.d/dyno."+daemon+".d "+control;
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            String s;
            BufferedReader stdout = new BufferedReader (
                new InputStreamReader(p.getInputStream()));
            while ((s = stdout.readLine()) != null) {
                System.out.println(s);
  
                
            }
            p.getInputStream().close();
            p.getOutputStream().close();
            p.getErrorStream().close();
         } catch (Exception ex) {
            ex.printStackTrace();
         }	
	}
}