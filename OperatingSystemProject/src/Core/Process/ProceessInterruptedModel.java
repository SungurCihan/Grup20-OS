package Core.Process;
import Core.Utilities.Priority;

//Ask�ya al�n�n proseslerin tan�mland��� s�n�f
public class ProceessInterruptedModel {
	public String ProcessId;	//PID
	public int InterruptedTime;	//Ask�ya al�nd��� saniye
	public Priority Priority;	//�ncelik de�eri
	
	
	public ProceessInterruptedModel(String processId, int interruptedTime, Priority priority) {
		ProcessId = processId;
		InterruptedTime = interruptedTime;
		Priority = priority;
	}
}
