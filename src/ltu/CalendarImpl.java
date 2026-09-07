package ltu;

import java.util.Calendar;
import java.util.Date;

public class CalendarImpl implements ICalendar {

	private final Date currentDate;

	/**
	 * Uses the actual current date and time.
	 */
	public CalendarImpl() {
		this.currentDate = Calendar.getInstance().getTime();
	}

	/**
	 * Uses a specific date.
	 *
	 * @param year  e.g. 2026
	 * @param month 1-12
	 * @param day   1-31
	 */
	public CalendarImpl(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.currentDate = cal.getTime();
	}

	@Override
	public Date getDate() {
		return currentDate;
	}
}