package Core.Utilities;

//Prosese ait durumlarýn tutulduðu enum
public enum State {
	READY,		 //Proses iþletilmeye hazýr
	RUNNING,	 //Proses yürütülüyor	
	INTERRUPTED, //Proses askýya alýndý
	TERMINATED	 //Proses sonlandýrýldý
}
