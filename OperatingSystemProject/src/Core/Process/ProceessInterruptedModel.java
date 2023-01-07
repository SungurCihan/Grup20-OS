package Core.Process;
import Core.Utilities.Priority;

//Askýya alýnýn proseslerin tanýmlandýðý sýnýf
public class ProceessInterruptedModel {
	public String ProcessId;	//PID
	public int InterruptedTime;	//Askýya alýndýðý saniye
	public Priority Priority;	//Öncelik deðeri
	
	
	public ProceessInterruptedModel(String processId, int interruptedTime, Priority priority) {
		ProcessId = processId;
		InterruptedTime = interruptedTime;
		Priority = priority;
	}
}
