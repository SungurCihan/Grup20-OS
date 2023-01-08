package Core.Queue;

import java.util.ArrayList;

import Core.Process.ProceessInterruptedModel;
import Core.Process.Process;
import Core.Utilities.Helpers;
import Core.Utilities.Priority;

//Uyglaman�n hayat d�ng�s� boyunca kullan�lan kuyruk ve dizilerin tutuldu�u static s�n�f
public final class QueuePool {	
	//Proseslerin tutuldu�u kuyuruklar
	private static Queue RealTimeQueue;	
	private static Queue UserBasedQueue1;
	private static Queue UserBasedQueue2;
	private static Queue UserBasedQueue3;
	
	//O anda y�r�t�len prosesin tutuldu�u de�i�ken
	public static Process RunningProcess=null; 
	
	//Ask�ya al�nm�� proseslerin tutuldu�u dizi
	private static ArrayList<ProceessInterruptedModel> InterruptedProcesses;

	//Getter ve Setterlar
	public static ArrayList<ProceessInterruptedModel> getInterruptedProcesses() {
		return InterruptedProcesses;
	}
	public static void setInterruptedProcesses(ArrayList<ProceessInterruptedModel> interruptedProcesses) {
		InterruptedProcesses = interruptedProcesses;
	}
	public static Queue getRealTimeQueue() {
		return RealTimeQueue;
	}
	public static void setRealTimeQueue(Queue realTimeQueue) {
		RealTimeQueue = realTimeQueue;
	}
	public static Queue getUserBasedQueue1() {
		return UserBasedQueue1;
	}
	public static void setUserBasedQueue1(Queue userBasedQueue1) {
		UserBasedQueue1 = userBasedQueue1;
	}
	public static Queue getUserBasedQueue2() {
		return UserBasedQueue2;
	}
	public static void setUserBasedQueue2(Queue userBasedQueue2) {
		UserBasedQueue2 = userBasedQueue2;
	}
	public static Queue getUserBasedQueue3() {
		return UserBasedQueue3;
	}
	public static void setUserBasedQueue3(Queue userBasedQueue3) {
		UserBasedQueue3 = userBasedQueue3;
	}
	
	//Parametre olarak al�n�n �ncelik de�erine g�re ilgili kuyruktan veri silen fonksiyon.
	public static void remove(Priority priority) {
		switch(priority) 
		{
		case P0:
			RealTimeQueue.remove();
			break;
		case P1:
			UserBasedQueue1.remove();
			break;
		case P2:
			UserBasedQueue2.remove();
			break;
		case P3:
			UserBasedQueue3.remove();
			break;	
		}
	}
	
	//Parametre olarak al�n�n �ncelik de�erine g�re ilgili kuyru�a 
	//yine parametre olarak ald��� "Proses" verisini ekleyen fonksiyon.
	public static void add(Core.Process.Process process) {
		switch(process.Priority) 
		{
		case P0:
			RealTimeQueue.add(process);
			break;
		case P1:
			UserBasedQueue1.add(process);
			break;
		case P2:
			UserBasedQueue2.add(process);
			break;
		case P3:
			UserBasedQueue3.add(process);
			break;	
		}
	}
	
	//Kuyruklar�n initialize edildi�i fonksiyon.
	public static void InitializeQueue() {
		RealTimeQueue = new Queue();
		UserBasedQueue1 = new Queue();
		UserBasedQueue2 = new Queue();
		UserBasedQueue3 = new Queue();
		InterruptedProcesses = new ArrayList<>();
	}
	
	//Kuyuklar�n i�ersindeki prosesler var�� zamanlar�na g�re s�raland��� fonksiyon
	public static void SortAllQueues() {
		Helpers.bubbleSortWithArrivalTime(RealTimeQueue);
		Helpers.bubbleSortWithArrivalTime(UserBasedQueue1);
		Helpers.bubbleSortWithArrivalTime(UserBasedQueue2);
		Helpers.bubbleSortWithArrivalTime(UserBasedQueue3);

	}
	
	//Parametre olarak ald��� �ncelik de�erine g�re ilgili kuyru�un ilk eleman�n d�nd�ren fonksiyon
	public static Process GetFirstItem(Priority priority) {
		switch(priority) {
		case P0:
			return RealTimeQueue.getProcess(RealTimeQueue.first);
		case P1:
			return UserBasedQueue1.getProcess(UserBasedQueue1.first);
		case P2:
			return UserBasedQueue2.getProcess(UserBasedQueue2.first);
		case P3:
			return UserBasedQueue3.getProcess(UserBasedQueue3.first);
			default:
				return null;
		}
	}
	
	//Kuyruklar�n hepsinin bo�ald���, dolay�s�yla 
	//g�revlendiricinin vazifesinin noktaland���n� tespit eden fonkiyon
	public static Boolean CheckIfSchedulerEnded() {
		if(RealTimeQueue.getSize()==0&&
		   UserBasedQueue1.getSize()==0&&
		   UserBasedQueue2.getSize()==0&&
		   UserBasedQueue3.getSize()==0)
			return true;
		return false;
	}
}
