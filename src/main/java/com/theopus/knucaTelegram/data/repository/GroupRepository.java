package com.theopus.knucaTelegram.data.repository;

import com.theopus.knucaTelegram.data.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("select g from Group g where g.name = :name")
    Group findByName(@Param("name") String name);

    @Query(value = "select g from Group g where g.name LIKE concat('%', ?1, '%') ")
    Set<Group> findNameAlies(String name);

    @Query(value = "select g from Group g where g.name LIKE concat('%', ?1, '%') ")
    Page<Group> findNameAliesPaged(String name, Pageable pageable);




}
