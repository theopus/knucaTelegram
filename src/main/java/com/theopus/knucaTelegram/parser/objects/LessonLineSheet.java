package com.theopus.knucaTelegram.parser.objects;

import com.theopus.knucaTelegram.data.entity.*;
import com.theopus.knucaTelegram.data.entity.enums.LessonOrder;
import com.theopus.knucaTelegram.data.entity.enums.LessonType;
import com.theopus.knucaTelegram.parser.ParserUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LessonLineSheet {

    private String line;
    private DayLessonListSheet parent;
    private LessonOrder lessonOrder;
    private Lesson lesson;


    public LessonLineSheet(String line, DayLessonListSheet parent, LessonOrder lessonOrder) {
        this.line = line;
        this.parent = parent;
        this.lessonOrder = lessonOrder;
        this.lesson = new Lesson();
        this.lesson.setOwnerGroup(parent.getParent().getGroup());
        this.lesson.addGroup(parent.getParent().getGroup());
        this.lesson.setOrder(lessonOrder);
        this.lesson.setDayOfWeek(parent.getDayOfWeek());

    }

    public Lesson parse(){
        String[] lessonLine = line.split(";");
        StringBuilder rightSB = new StringBuilder();
        for (int i = 1; i < lessonLine.length; i++) {
            rightSB.append(lessonLine[i]).append(" ");
        }

        String leftSide = lessonLine[0];
        String rightSide = rightSB.toString();

        this.lesson.setSubject(parseSubject(leftSide));
        this.lesson.setLessonType(parseLessonType(leftSide));
        this.lesson.getTeachers().addAll(parseTeachers(rightSide));
        this.lesson.getGroups().addAll(parseGroups(rightSide));
        this.lesson.setRoomTimePeriod(parserRTP(rightSide));
        return this.lesson;
    }

    private final static Pattern ROOM_PATTERN = Pattern.compile("ауд\\.([^\\s\\]]+)");
    private final static Pattern EXACT_DAY_PATTERN = Pattern.compile("(^|([^доз]\\s))(\\d?\\d\\.\\d\\d)");
    private final static Pattern FROM_DAY_PATTERN = Pattern.compile("з\\s(\\d?\\d\\.\\d\\d)");
    private final static Pattern TO_DAY_PATTERN = Pattern.compile("до\\s(\\d?\\d\\.\\d\\d)");

    private Set<RoomTimePeriod> parserRTP(String rightSide){
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(rightSide);
        List<String> rtpStr = new ArrayList<>();
        while(m.find())
            rtpStr.add(m.group(1));
        Set<RoomTimePeriod> result = new LinkedHashSet<>();
        Collections.reverse(rtpStr);
        Room tmpRoom = null;
        RoomTimePeriod tmpRTP = null;

        for (int i = 0; i < 2; i++) {
            for (String s : rtpStr) {
                RoomTimePeriod loopRTP = new RoomTimePeriod();
                Room room = findRoom(s);
                if (room == null) {
                    if (tmpRoom != null)
                        room = tmpRoom;
                    else
                        room = new Room("NoRoom");
                }
                loopRTP.setRoom(room);
                tmpRoom = room;

                loopRTP.addAllLessonDate(findSingleDates(s));
                loopRTP.addAllLessonDate(findFromToDates(s));

                if (loopRTP.getLessonDate().isEmpty()){
                    if (tmpRTP != null){
                        loopRTP.setLessonDate(tmpRTP.getLessonDate());
                    }
                }

                tmpRTP = loopRTP;
                result.add(loopRTP);
            }
            Collections.reverse(rtpStr);
        }

        boolean hasValidDate = false;

        for (RoomTimePeriod roomTimePeriod : result) {
            if (!roomTimePeriod.getLessonDate().isEmpty()) {
                hasValidDate = true;
                break;
            }
        }

        if (hasValidDate){
            result.removeIf(roomTimePeriod -> roomTimePeriod.getLessonDate().isEmpty());
        }
        else {
            result.forEach(roomTimePeriod -> {
                roomTimePeriod.addLessonDate(new LessonDate(
                        ParserUtils.minDateOffser(parent.getParent().getMinDate(), parent.getDayOfWeek()),
                        ParserUtils.maxDateOffser(parent.getParent().getMaxDate(),parent.getDayOfWeek())));
            });
    }


        return result;
    }


    private Room findRoom(String bracket){
        Matcher match = ROOM_PATTERN.matcher(bracket);
        if (match.find()){
            return new Room(match.group(0));
        }
        else
            return null;
    }

    public Set<LessonDate> findFromToDates(String bracket){
        Set<LessonDate> result = new HashSet<>();
        Set<String> fromLessonDates = new HashSet<>();
        Set<String> toLessonDates = new HashSet<>();
        Matcher mOne = FROM_DAY_PATTERN.matcher(bracket);
        Matcher mTwo = TO_DAY_PATTERN.matcher(bracket);
        while (mOne.find()){
            if (mTwo.find(mOne.end())){
                String from =  mOne.group(1);
                fromLessonDates.add(from);
                String to = mTwo.group(1);
                toLessonDates.add(to);
                LessonDate lessonDate = new LessonDate(
                        ParserUtils.stringToDate(from, parent.getParent().getTitledDate()),
                        ParserUtils.stringToDate(to, parent.getParent().getTitledDate()));
                result.add(lessonDate);
            }
        }
        mOne.reset();
        while (mOne.find()) {
            String from = mOne.group(1);
            if (!fromLessonDates.contains(from)) {
                LessonDate lessonDate = new LessonDate(
                        ParserUtils.stringToDate(from, parent.getParent().getTitledDate()),
                        ParserUtils.maxDateOffser(parent.getParent().getMaxDate(), parent.getDayOfWeek()));
                result.add(lessonDate);
            }
        }
        mTwo.reset();
        while (mTwo.find()){
            String to = mTwo.group(1);
            if (!toLessonDates.contains(to)){
                LessonDate lessonDate = new LessonDate(
                        ParserUtils.minDateOffser(parent.getParent().getMinDate(), parent.getDayOfWeek()),
                        ParserUtils.stringToDate(to, parent.getParent().getTitledDate()));
                result.add(lessonDate);
            }
        }
        return result;
    }

    private Set<LessonDate> findSingleDates(String bracket){
        Matcher match = EXACT_DAY_PATTERN.matcher(bracket);
        Set<LessonDate> result = new HashSet<>();
        while (match.find()) {
            result.add(new LessonDate(ParserUtils.stringToDate(match.group(3),parent.getParent().getTitledDate())));
        }
        return result;
    }

    private Set<Group> parseGroups(String rightSide){
        Set<Group> result = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b[А-яІіЇїЄє]{1,6}-(\\S){1,6}\\b");
        Matcher matcher = pattern.matcher(rightSide);
        while (matcher.find()){
            result.add(new Group(rightSide.substring(matcher.start(),matcher.end())));
        }
        return result;
    }

    private Set<Teacher> parseTeachers(String rightSide){
        Set<Teacher> result = new HashSet<>();
        Pattern teacherPattern = Pattern.compile("\\b((([^.,\\s\\d\\p{Punct}]{2,5}.)?[^.,\\s\\d\\p{Punct}]{2,4}\\.)|[^.,\\d\\s]{3,}\\.)\\s[^.,\\s\\d]+(\\s[^.,\\d\\s]\\.)?([^.,\\d\\s]\\.?)?");
        Matcher matcher = teacherPattern.matcher(rightSide);
        while (matcher.find()){
            result.add(new Teacher(rightSide.substring(matcher.start(),matcher.end())));
        }
        return result;
    }

    private Subject parseSubject(String leftSide){
        StringBuilder sb = new StringBuilder("");
        String[] line = leftSide.split(" ");
        for (int i = 0; i < line.length-1 ; i++) {
            sb.append(line[i]).append(i == line.length-1 ? "" : " ");
        }
        return new Subject(sb.toString().trim());
    }

    private LessonType parseLessonType(String leftSide){
        String[] line = leftSide.split(" ");
        leftSide = line[line.length - 1];
        leftSide = leftSide.toLowerCase();

        Pattern lect = Pattern.compile("((.+)?)лек((.+)?)");
        Pattern prakt = Pattern.compile("((.+)?)пра((.+)?)");
        Pattern lab = Pattern.compile("((.+)?)лаб((.+)?)");

        Matcher m = lect.matcher(leftSide);
        if (m.matches())
            return LessonType.LECTION;

        m = prakt.matcher(leftSide);
        if (m.matches())
            return LessonType.PRACT;

        m = lab.matcher(leftSide);
        if (m.matches())
            return LessonType.LAB;


        return LessonType.ELSE;
    }

    public String getLine() {
        return line;
    }

    public LessonOrder getLessonOrder() {
        return lessonOrder;
    }

}
