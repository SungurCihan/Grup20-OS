package Core.Utilities;

//Prosese ait durumlar�n tutuldu�u enum
public enum State {
	READY,		 //Proses i�letilmeye haz�r
	RUNNING,	 //Proses y�r�t�l�yor	
	INTERRUPTED, //Proses ask�ya al�nd�
	TERMINATED	 //Proses sonland�r�ld�
}
