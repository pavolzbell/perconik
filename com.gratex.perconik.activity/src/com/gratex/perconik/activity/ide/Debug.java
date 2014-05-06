package com.gratex.perconik.activity.ide;

import static com.gratex.perconik.activity.ide.Internals.console;
import static com.gratex.perconik.activity.ide.Internals.debug;
import static com.gratex.perconik.activity.ide.Internals.options;
import static com.gratex.perconik.activity.ide.Internals.optionsSequence;
import static java.lang.String.valueOf;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.gratex.perconik.activity.TimeSupplier;

final class Debug
{
	static final TimeSupplier timeSupplier = new TimeSupplier()
	{
		public final XMLGregorianCalendar from(final long time, boolean utc)
		{
			GregorianCalendar calendar = utc ? new GregorianCalendar(TimeZone.getTimeZone("UTC")) : new GregorianCalendar();
			
			calendar.setTimeInMillis(time);
			calendar.set(Calendar.YEAR, 2000);
			
			return Internals.datatypeFactory.newXMLGregorianCalendar(calendar);
		}
	};

	static
	{
		if (debug)
		{
			MapJoiner entries = Joiner.on("; ").withKeyValueSeparator(": ");
			
			console.print("UACA environment variable: %s", valueOf(optionsSequence));
			console.print("UACA extracted flags: %s", options.isEmpty() ? "none" : entries.join(options));
		}
	}
	
	private Debug()
	{
		throw new AssertionError();
	}
}
