package Core.Utilities;

//Proje de kullan�lan sabit de�i�kenlerin tutuldu�u static s�n�f
public final class Constants {
	//Konsola renlki ��kt� almak i�in tan�mlanan de�i�kenler
	public static final String COLOR_SPACE_PREFIX="\u001B[38;5;";
	public static final String COLOR_SPACE_SUFFIX="\u001b[0m";
	
	//Prosesin durum de�i�imlerinde ekrana bas�lacak olan ifadeler.
	public static final String MESSAGE_INTERRUPTED="proses askiya alindi.";
	public static final String MESSAGE_RUNNING="proses yurutuldu. ";
	public static final String MESSAGE_READY="proses hazir durumuna gecti. ";
	public static final String MESSAGE_TERMINATED="proses sonlandirildi. ";
	public static final String MESSAGE_TIMEOUT="proses zaman asimina ugradigi icin sonlandirildi. ";
}
