package com.theopus.knucaTelegram.data.repository;

import com.theopus.knucaTelegram.data.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_group lg " +
            "ON les.id = lg.lesson_id " +
            "INNER JOIN group_p gro ON lg.group_id = gro.id " +
            "WHERE (gro.name=?2 AND les.dayofWeek = ?1)", nativeQuery = true)
    List<Lesson> findDayGroupName(int day, String name);

    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_group lg " +
            "ON les.id = lg.lesson_id " +
            "INNER JOIN group_p gro ON lg.group_id = gro.id " +
            "WHERE (gro.id=?2 AND les.dayofWeek = ?1)", nativeQuery = true)
    List<Lesson> findDayGroupID(int day, long id);

    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_teacher lt " +
            "ON les.id = lt.lesson_id " +
            "INNER JOIN teacher t ON lt.teacher_id = t.id " +
            "WHERE (t.name=?2 AND les.dayofWeek = ?1)", nativeQuery = true)
    List<Lesson> findDayTeacherName(int day, String name);

    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_teacher lt " +
            "ON les.id = lt.lesson_id " +
            "INNER JOIN teacher t ON lt.teacher_id = t.id " +
            "WHERE (t.id=?2 AND les.dayofWeek = ?1)", nativeQuery = true)
    List<Lesson> findDayTeacherID(int day, long id);


    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_teacher lt " +
            "ON les.id = lt.lesson_id " +
            "INNER JOIN teacher t ON lt.teacher_id = t.id " +
            "WHERE (t.name=?1)", nativeQuery = true)
    List<Lesson> getAllByTeacherName(String teacherName);

    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_teacher lt " +
            "ON les.id = lt.lesson_id " +
            "INNER JOIN teacher t ON lt.teacher_id = t.id " +
            "WHERE (t.id=?1)", nativeQuery = true)
    List<Lesson> getAllByTeacherId(long id);

    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_group lg " +
            "ON les.id = lg.lesson_id " +
            "INNER JOIN group_p gro ON lg.group_id = gro.id " +
            "WHERE (gro.name=?1)", nativeQuery = true)
    List<Lesson> getAllByGroupName(String groupName);

    @Query(value = "select les.* " +
            "FROM lesson les " +
            "INNER JOIN lesson_group lg " +
            "ON les.id = lg.lesson_id " +
            "INNER JOIN group_p gro ON lg.group_id = gro.id " +
            "WHERE (gro.id=?1)", nativeQuery = true)
    List<Lesson> getAllByGroupId(long groupId);


}
