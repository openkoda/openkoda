package timelog.repository;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;
import timelog.model.Assignment;
import timelog.model.Timelog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static timelog.TimelogApp.TIMELOG;

@Repository
@SearchableRepositoryMetadata(
    entityClass = Timelog.class,
    entityKey = TIMELOG
)
public interface TimelogRepository extends SecureRepository<Timelog> {

    record Day(List<Timelog> logs, Integer sum, LocalDate date, Boolean isWeekend) {};
    record Week(List<Day> days, Integer sum, LocalDate date) {};
    record Month(List<Week> weeks, Map<Assignment, Integer> cwAssigmentSums, Integer sum, Integer creativeWorkSum, LocalDate date) {};

    default Month summarize(List<Timelog> timelogs, int monthOffset) {
        LocalDate dayStart = LocalDate.now().minusMonths(monthOffset).withDayOfMonth(1);
        Stream<LocalDate> stream = dayStart.datesUntil(dayStart.plusMonths(1));
        Map<Integer, Map<Integer, List<Timelog>>> weeksDaysSummary = new LinkedHashMap<>();
        stream.forEach(ld -> {
            Map<Integer, List<Timelog>> days = weeksDaysSummary.get(ld.get(WeekFields.ISO.weekOfYear()));
            if (days == null) {
                days = new LinkedHashMap<>();
                weeksDaysSummary.put(ld.get(WeekFields.ISO.weekOfYear()), days);
            }
            days.putIfAbsent(ld.getDayOfWeek().getValue(), new ArrayList<>());
        });

        for (Timelog t : timelogs) {
            Map<Integer, List<Timelog>> w = weeksDaysSummary.get(t.getStartedOn().get(WeekFields.ISO.weekOfYear()));
            if (w == null) { continue; }
            List<Timelog> tl = w.get(t.getStartedOn().getDayOfWeek().getValue());
            if (tl == null) { continue; }

            tl.add(t);
        }

        Map<Integer, Map<Integer, List<Timelog>>> value = weeksDaysSummary;
        Map<Assignment, Integer> cwAssigmentSums = new HashMap<>();
        List<Week> weeks = new ArrayList<>();
        int monthSum = 0;
        int creativeWorkSum = 0;
        int weekCount = 0;
        int dayCount = 0;
        for (Map.Entry<Integer, Map<Integer, List<Timelog>>> e2 : value.entrySet()) {
            Integer k2 = e2.getKey();
            Map<Integer, List<Timelog>> v2 = e2.getValue();
            List<Day> days = new ArrayList<>();
            int weekSum = 0;
            for (Map.Entry<Integer, List<Timelog>> e3 : v2.entrySet()) {
                Integer k3 = e3.getKey();
                List<Timelog> v3 = e3.getValue();
                int daySum = 0;
                for (Timelog t: v3) {
                    daySum += t.getDuration();
                    if(t.getAssignment() != null && t.getAssignment().isCreativeWork()) {
                        creativeWorkSum += t.getDuration();
                        if(cwAssigmentSums.containsKey(t.getAssignment())) {
                            cwAssigmentSums.put(t.getAssignment(), cwAssigmentSums.get(t.getAssignment()) + t.getDuration());
                        } else {
                            cwAssigmentSums.put(t.getAssignment(), t.getDuration());
                        }
                    }
                    System.out.println(t.getStartedOn() + " " + dayStart.plusDays(dayCount));
                }
                LocalDate dayDate = dayStart.plusDays(dayCount++);
                Day d = new Day(v3, daySum, dayDate, isWeekend(dayDate));
                days.add(d);
                weekSum += daySum;
            }
            weeks.add(new Week(days, weekSum, dayStart.plusDays(dayCount-1 )));
            monthSum += weekSum;
        }
        return new Month(weeks, cwAssigmentSums, monthSum, creativeWorkSum, dayStart);
    }

    default String convertToAssignmentsDescriptionString(Month month){

        return month.cwAssigmentSums().entrySet().stream()
                .map(entry -> entry.getKey().getDescription() + ": " + convertToHoursString(entry.getValue()))
                .collect(Collectors.joining(", \n"));
    }

    default Integer getMonthFromSummary(Month month){
        return month.date.getMonthValue();
    }

    default Integer getYearFromSummary(Month month){
        return month.date.getYear();
    }

    private boolean isWeekend(LocalDate localDate) {
        return (localDate.get(ChronoField.DAY_OF_WEEK) == 6)
                || (localDate.get(ChronoField.DAY_OF_WEEK) == 7);
    }

    String patternString = "(\\d+(\\.\\d+)?)[\\s]*([mhd])"; //eg "1h", "3.5h", "25m", "1.2d"
    Pattern pattern = java.util.regex.Pattern.compile(patternString);

    default Integer convertToSeconds(Object timeString) {
        return convertToSecondsStatic(timeString);
    }

    static Integer convertToSecondsStatic(Object timeString) {
        Map<Character, Integer> unitSeconds = Map.of('m', 60, 'h', 3600, 'd', 8 * 3600);
        Matcher matcher = pattern.matcher(timeString.toString().trim());
        if (!matcher.matches()) {
            return 0;
        }
        String value = matcher.group(1);
        Character unit = matcher.group(3).charAt(0);

        return (int)(Double.parseDouble(value) * unitSeconds.get(unit));
    }

    default String convertToHoursString(Object seconds) {
        return convertToHoursStringStatic(seconds);
    }
    static String convertToHoursStringStatic(Object seconds) {
        if (seconds == null) {
            return "";
        }
        NumberFormat format = DecimalFormat.getInstance();
        format.setMaximumFractionDigits(2);
        return format.format(((Integer)seconds).doubleValue() / 3600) + " h";
    }

}

