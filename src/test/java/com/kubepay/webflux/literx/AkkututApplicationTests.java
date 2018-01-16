package com.kubepay.webflux.literx;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AkkututApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void shouldCreateMapFromTaskList() throws Exception {

		Task t1 = new Task("Write blog on Java 8 Map improvements", TaskType.BLOGGING,
				LocalDate.of(2018, Month.JANUARY, 1));
		Task t2 = new Task("Write factorial program in Java 8", TaskType.CODING, LocalDate.of(2018, Month.JANUARY, 1));
		List<Task> tasks = Arrays.asList(t1, t2);

		Map<String, Task> taskIdToTaskMap = tasks.stream()
				.collect(Collectors.toMap(Task::getId, TaskExtractor.identityOp()));

		assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t1)));
		assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
	}

	@Test
	public void shouldCreateLinkedMapFromTaskList() throws Exception {
		Task t1 = new Task("Write blog on Java 8 Map improvements", TaskType.BLOGGING,
				LocalDate.of(2018, Month.JANUARY, 1));
		Task t2 = new Task("Write factorial program in Java 8", TaskType.CODING, LocalDate.of(2018, Month.JANUARY, 1));
		List<Task> tasks = Arrays.asList(t1, t2);

		Map<String, Task> taskIdToTaskMap = tasks.stream()
				.collect(Collectors.toMap(Task::getId, TaskExtractor.identityOp(), (k1, k2) -> k1, LinkedHashMap::new));

		assertThat(taskIdToTaskMap, instanceOf(LinkedHashMap.class));
		assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t1)));
		assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldHandleTaskListWithDuplicates1() throws Exception {
		Task t1 = new Task("1", "Write blog on Java 8 Map improvements", TaskType.BLOGGING,
				LocalDate.of(2018, Month.JANUARY, 1));
		Task t2 = new Task("1", "Write factorial program in Java 8", TaskType.CODING,
				LocalDate.of(2018, Month.JANUARY, 1));
		List<Task> tasks = Arrays.asList(t1, t2);

		Map<String, Task> taskIdToTaskMap = tasks.stream()
				.collect(Collectors.toMap(Task::getId, TaskExtractor.identityOp()));

		assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t1)));
		assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
	}

	@Test
	public void shouldHandleTaskListWithDuplicates2() throws Exception {
		Task t1 = new Task("1", "Write blog on Java 8 Map improvements", TaskType.BLOGGING,
				LocalDate.of(2018, Month.JANUARY, 1));
		Task t2 = new Task("1", "Write factorial program in Java 8", TaskType.CODING,
				LocalDate.of(2018, Month.JANUARY, 1));
		List<Task> tasks = Arrays.asList(t1, t2);
		Map<String, Task> taskIdToTaskMap = tasks.stream()
				.collect(Collectors.toMap(Task::getId, TaskExtractor.identityOp(), (k1, k2) -> k2));
		assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
	}

	@Test
	public void kalamWasBornOn15October1931() throws Exception {
		final AbdulKalam kalam = new AbdulKalam();

		LocalDate dateOfBirth = kalam.dateOfBirth();
		assertThat(dateOfBirth.toString(), equalTo("1931-10-15"));
		assertThat(dateOfBirth.getMonth(), is(equalTo(Month.OCTOBER)));
		assertThat(dateOfBirth.getYear(), is(equalTo(1931)));
		assertThat(dateOfBirth.getDayOfMonth(), is(equalTo(15)));
		assertThat(dateOfBirth.getDayOfYear(), is(equalTo(288)));

		LocalTime timeOfBirth = kalam.timeOfBirth();
		assertThat(timeOfBirth.toString(), is(equalTo("01:15")));
		assertThat(timeOfBirth.getHour(), is(equalTo(1)));
		assertThat(timeOfBirth.getMinute(), is(equalTo(15)));
		assertThat(timeOfBirth.getSecond(), is(equalTo(0)));

		DayOfWeek dayOfWeek = kalam.dayOfBirthAtAge(50);
		assertThat(dayOfWeek, is(equalTo(DayOfWeek.THURSDAY)));

		long daysLived = kalam.numberOfDaysLived();
		assertThat(daysLived, is(equalTo(30601L)));
		
		Period kalamLifePeriod = kalam.kalamLifePeriod();
	    assertThat(kalamLifePeriod.getYears(), is(equalTo(83)));
	    assertThat(kalamLifePeriod.getMonths(), is(equalTo(9)));
	    assertThat(kalamLifePeriod.getDays(), is(equalTo(12)));
	    
	    final String indianDateFormat = "dd-MM-YYYY";
	    String formattedDateOfBirth = kalam.formatDateOfBirth(indianDateFormat);
	    assertThat(formattedDateOfBirth, is(equalTo("15-10-1931")));
	    
	    LocalDate kalamDateOfBirth = LocalDate.of(1931, Month.OCTOBER, 15);
	    assertThat(kalamDateOfBirth.format(DateTimeFormatter.BASIC_ISO_DATE), is(equalTo("19311015")));
	    assertThat(kalamDateOfBirth.format(DateTimeFormatter.ISO_LOCAL_DATE), is(equalTo("1931-10-15")));
	    assertThat(kalamDateOfBirth.format(DateTimeFormatter.ISO_ORDINAL_DATE), is(equalTo("1931-288")));
	    
	    final String input = "15 Oct 1931 01:15 AM";
	    LocalDateTime dateOfBirthAndTime = kalam.parseDateOfBirthAndTime(input);
	    assertThat(dateOfBirthAndTime.toString(), is(equalTo("1931-10-15T01:15")));
	    
	    LocalDate date = LocalDate.of(2015, Month.OCTOBER, 25);
	    System.out.println(date);// This will print 2015-10-25

	    LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
	    System.out.println(firstDayOfMonth); // This will print 2015-10-01

	    LocalDate firstDayOfNextMonth = date.with(TemporalAdjusters.firstDayOfNextMonth());
	    System.out.println(firstDayOfNextMonth);// This will print 2015-11-01

	    LocalDate lastFridayOfMonth = date.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));
	    System.out.println(lastFridayOfMonth); // This will print 2015-10-30
	    
	    LocalDate today = LocalDate.now();
	    TemporalAdjuster nextWorkingDayAdjuster = TemporalAdjusters.ofDateAdjuster(localDate -> {
	        DayOfWeek dayOfWeekX = localDate.getDayOfWeek();
	        if (dayOfWeekX == DayOfWeek.FRIDAY) {
	            return localDate.plusDays(3);
	        } else if (dayOfWeekX == DayOfWeek.SATURDAY) {
	            return localDate.plusDays(2);
	        }
	        return localDate.plusDays(1);
	    });
	    System.out.println(today.with(nextWorkingDayAdjuster));
	}

	@SafeVarargs
	public static <T, U> Map<T, U> createMap(SimpleEntry<T, U>... entries) {
		return Stream.of(entries).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
	}

}

class AbdulKalam {
	public LocalDate dateOfBirth() {
		return LocalDate.of(1931, Month.OCTOBER, 15);
	}

	public LocalTime timeOfBirth() {
		return LocalTime.of(1, 15);
	}

	public LocalDateTime dateOfBirthAndTime() {
		return LocalDateTime.of(dateOfBirth(), timeOfBirth());
	}

	public DayOfWeek dayOfBirthAtAge(final int age) {
		return dateOfBirth().plusYears(age).getDayOfWeek();
	}

	public List<DayOfWeek> allBirthDateDayOfWeeks(int limit) {
		return Stream.iterate(dateOfBirth(), db -> db.plusYears(1)).map(LocalDate::getDayOfWeek).limit(limit)
				.collect(Collectors.toList());
	}

	public Duration kalamLifeDuration() {
		LocalDateTime deathDateAndTime = LocalDateTime.of(LocalDate.of(2015, Month.JULY, 27), LocalTime.of(19, 0));
		return Duration.between(dateOfBirthAndTime(), deathDateAndTime);
	}

	public long numberOfDaysLived() {
		return kalamLifeDuration().toDays();
	}
	
	public Period kalamLifePeriod() {
	    LocalDate deathDate = LocalDate.of(2015, Month.JULY, 27);
	    return Period.between(dateOfBirth(), deathDate);
	}
	
	public String formatDateOfBirth(final String pattern) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	    return dateOfBirth().format(formatter);
	}
	
	public LocalDateTime parseDateOfBirthAndTime(String input) {
	    return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"));
	}
}

interface TaskExtractor<R> extends Function<Task, R> {

	static TaskExtractor<Task> identityOp() {
		return t -> t;
	}
}
