package com.theopus.knucaTelegram.data.entity;


import com.theopus.knucaTelegram.data.entity.enums.DayOfWeek;
import com.theopus.knucaTelegram.data.entity.enums.LessonOrder;
import com.theopus.knucaTelegram.data.entity.enums.LessonType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "lesson")
public class Lesson {

    @Id @GeneratedValue(generator = "increment")
    @GenericGenerator(name= "increment", strategy= "increment")
    @Column(name = "id", length = 6, nullable = false)
    private long id;

    @Column(name = "lesson_order")
    private LessonOrder order;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "lesson_type")
    private LessonType lessonType;

    @Column
    private DayOfWeek dayofWeek;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RoomTimePeriod> roomTimePeriod = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lesson_teacher",
            joinColumns =@JoinColumn(name = "lesson_id"),
            inverseJoinColumns =@JoinColumn(name = "teacher_id") )
    private Set<Teacher> teachers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lesson_group",
            joinColumns =@JoinColumn(name = "lesson_id"),
            inverseJoinColumns =@JoinColumn(name = "group_id") )
    private Set<Group> groups = new HashSet<>();

    @Transient
    private Group ownerGroup;

    public Lesson() {
    }

    public static Collection<Lesson> atDate(Collection<Lesson> lessons, Date date){
        Set<Lesson> result = new HashSet<>();
        lessons.forEach(lesson -> {
            if (lesson.isAtDate(date))
                result.add(lesson);
        });
        return result;
    }

    public boolean isAtDate(Date date){
        DayOfWeek thisdow = DayOfWeek.dateToDayOfWeek(date);
        for (RoomTimePeriod rtp : roomTimePeriod) {
            if (rtp.isAtDate(date) && thisdow.equals(this.dayofWeek))
                return true;
        }
        return false;
    }

    public Set<Room> getTodayRoom(){
        return getRoomByDate(new Date());
    }

    public Set<Room> getRoomByDate(Date date){
        Set<Room> result = new HashSet<>();
        for (RoomTimePeriod rtp: roomTimePeriod) {
            if (rtp.isAtDate(date))
                result.add(rtp.getRoom());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lesson lesson = (Lesson) o;

        if (order != lesson.order) return false;
        if (dayofWeek != lesson.dayofWeek) return false;
        if (!subject.equals(lesson.subject)) return false;
        if (lessonType != lesson.lessonType) return false;
        //TODO: FIGUREOUT
//        if (!roomTimePeriod.equals(lesson.roomTimePeriod)) return false;
        if (teachers != null ? !teachers.equals(lesson.teachers) : lesson. teachers != null) return false;
        if (teachers.size() != lesson.getTeachers().size()) return false;
        if (!teachers.containsAll(lesson.getTeachers())) return false;
        if (groups.size() != lesson.groups.size()) return false;
        if (!groups.containsAll(lesson.getGroups())) return false;
        return groups != null ? groups.equals(lesson.groups) : lesson.groups == null;
    }

    @Override
    public int hashCode() {
        int result = (int) id;
        result = order == null ? result : order.hashCode();
        result = dayofWeek == null ? result : dayofWeek.hashCode();
        result = subject == null ? result : 31 * result + subject.hashCode();
        result = lessonType == null ? result : 31 * result + lessonType.hashCode();
        result = groups == null ? result : 31 * result + (groups != null ? groups.hashCode() : 0);
        result = teachers == null ? result : 31 * result + (teachers != null ? teachers.hashCode() : 0);
        result = roomTimePeriod == null ? result : 31 * result + roomTimePeriod.hashCode();
        return result;
    }

    public boolean addGroup(Group add){
        if (groups.contains(add))
            groups.remove(add);
        return groups.add(add);
    }

    public DayOfWeek getDayOfWeek() {
        return dayofWeek;
    }

    public void setDayOfWeek(DayOfWeek string) {
        this.dayofWeek = string;
    }

    public boolean addTeacher(Teacher add){
        return teachers.add(add);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LessonOrder getOrder() {
        return order;
    }

    public void setOrder(LessonOrder order) {
        this.order = order;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Set<RoomTimePeriod> getRoomTimePeriod() {
        return roomTimePeriod;
    }

    public void setRoomTimePeriod(Set<RoomTimePeriod> roomTimePeriod) {
        this.roomTimePeriod = roomTimePeriod;
//        for (RoomTimePeriod rtp: this.roomTimePeriod) {
//            rtp.setLesson(this);
//        }
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public boolean removeTeacher(Teacher teacher){
        return teachers.remove(teacher);
    }

    public DayOfWeek getDayofWeek() {
        return dayofWeek;
    }

    public void setDayofWeek(DayOfWeek dayofWeek) {
        this.dayofWeek = dayofWeek;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "order=" + order +
                ", dayOfweek=" + dayofWeek +
                ", subject=" + subject +
                ", lessonType=" + lessonType +
                ", roomTimePeriod=" + roomTimePeriod +
                ", teachers=" + teachers +
                ", groups=" + groups +
                '}';
    }

    public Group getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(Group ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public void extractNotOwnerGroups(){
        groups.removeIf(group -> !group.equals(ownerGroup));
    }

    public void addAllRoomTimePeriod(Set<RoomTimePeriod> roomTimePeriod) {
        this.roomTimePeriod.addAll(roomTimePeriod);
    }
}
