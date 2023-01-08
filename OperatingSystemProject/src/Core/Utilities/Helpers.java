package Core.Utilities;

import Core.Process.Process;
import Core.Queue.Queue;

//Proje genelinde kullan�lacak olan genel yard�mc� fonksiyonlar�n yaz�ld��� static s�n�f
public final class Helpers {
	
	//Proses id 4 basamakl� hale getiriliyor.
	public static String CreatePid(int pid) 
	{
		String temp = String.valueOf(pid);
		
		int length =4-temp.length();
		for(int i =0;i<length;i++) {
			temp="0"+temp;
			
		}
		return temp;		
	}
	
	//Girilen integer degeri i�in ilgili Priority de�erini d�nd�r�yor.
	public static Priority GetPriorityEnum(int number) 
	{
		switch(number) 
		{
		case 0:
			return Priority.P0;
		case 1:
			return Priority.P1;
		case 2:
			return Priority.P2;
		case 3:
			return Priority.P3;
		default:
			return null;
		}
	}
	
	//Kuyruk i�erisindeki prosesleri var�� zaman�na g�re s�ralayan fonksiyon.
	public static void bubbleSortWithArrivalTime(Queue queue) 
	{  
	      int size = queue.getSize();  
	      Process temp = null;  
	      for(int i=0; i < size; i++)
	      {  
		      for(int j=1; j < (size - i); j++)
		      {  
			      if(queue.getProcess(j-1).ArrivalTime > queue.getProcess(j).ArrivalTime)
			      {  
			      //iki eleman�n yerleri de�i�tiriliyor.  
			      temp = queue.getProcess(j-1);  
			      queue.change(j-1, queue.getProcess(j));
			      queue.change(j, temp);
			      }  	                          
		      }  
	      }  
	  
	  }
}
