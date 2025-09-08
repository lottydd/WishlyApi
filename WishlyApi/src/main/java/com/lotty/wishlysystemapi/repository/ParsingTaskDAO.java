package com.lotty.wishlysystemapi.repository;

import com.lotty.wishlysystemapi.model.ParsingTask;
import org.springframework.stereotype.Repository;

@Repository
public class ParsingTaskDAO extends BaseDAO<ParsingTask, Integer> {

    public ParsingTaskDAO() {
        super(ParsingTask.class);
    }

}