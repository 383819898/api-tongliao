package com.shiku.mongodb.springdata;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface IBaseMongoRepository<T, ID extends Serializable> extends MongoRepository<T,ID> {




}
