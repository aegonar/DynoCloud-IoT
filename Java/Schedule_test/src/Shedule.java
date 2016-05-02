import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Shedule{

public static void main (String args[]){
	
	SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
	Date ten = null;
	Date eighteen = null;
	
	try {
		ten = parser.parse("10:00");
		eighteen = parser.parse("18:00");
	} catch (ParseException e1) {

		e1.printStackTrace();
	}

	Date userDate=null;
	
	try {
	    userDate = parser.parse("13:00");
	    if (userDate.after(ten) && userDate.before(eighteen)) {
	    	System.out.println("Day");
	    } else {
	    	System.out.println("Night");
	    }
	} catch (ParseException e) {
		e.printStackTrace();
	}
	
	
	//Date date = new Date();   // given date
	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
	calendar.setTime(userDate);   // assigns calendar to given date 
	int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
	int hour = calendar.get(Calendar.HOUR);        // gets hour in 12h format
	int min = calendar.get(Calendar.MINUTE);
	
	System.out.println("hourOfDay: " + hourOfDay);
	System.out.println("hour: " + hour);
	System.out.println("min: " + min);
	
	
	
	
	
	}
}