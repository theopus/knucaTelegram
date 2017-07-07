package com.theopus.knucaTelegram.data.service.impl;

import com.theopus.knucaTelegram.data.entity.Group;
import com.theopus.knucaTelegram.data.repository.GroupRepository;
import com.theopus.knucaTelegram.data.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;


@Service
public class GroupServiceImpl implements GroupService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private GroupRepository groupRepository;

    private Set<Group> groupsCache = new HashSet<>();

    @Override
    public Set<Group> saveAll(Set<Group> groupSet) {
        Set<Group> result = new HashSet<>();
        for (Group g : groupSet) {
            if (groupsCache.contains(g))
                result.add(getGroup(g));
            else {
                Group findG = groupRepository.findByName(g.getName());
                if (findG != null){
                    result.add(findG);
                    groupsCache.add(findG);
                }
                else{
                    Group savedG = groupRepository.save(g);
                    result.add(savedG);
                    groupsCache.add(savedG);
                }

            }
        }
        return result;
    }

    @Override
    public Group getByExactName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public Set<Group> getByAlliesName(String name) {
        System.out.println(name);
        try {
            return groupRepository.findNameAlies(name);
        }catch (Exception e){
            log.error("GETALLIESBYNAME", e);
        }
        return null;

    }

    @Override
    public long getCount() {
        return groupRepository.count();
    }

    @Override
    public void flush() {
        groupsCache = null;
    }

    @Override
    public Set<Group> getAll() {
        return new HashSet<>(groupRepository.findAll());
    }

    private Group getGroup(Group group){
        for (Group g: groupsCache) {
            if (g.equals(group))
                return g;
        }
        return null;
    }
}
