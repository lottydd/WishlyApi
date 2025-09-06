package com.lotty.wishlysystemapi.repository;

import com.lotty.wishlysystemapi.model.ParsingTask;
import com.lotty.wishlysystemapi.model.User;

import java.util.List;
import java.util.Optional;

public class PasringTaskDAO extends BaseDAO<ParsingTask, Integer> {
    protected PasringTaskDAO(Class<ParsingTask> entityClass) {
        super(ParsingTask.class);
    }

//    public List<Optional<ParsingTask>> findByUserId(String username) {
//
//    }

}