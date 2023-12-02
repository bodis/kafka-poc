package com.nitrowise.kafkapoc.repository;

import com.nitrowise.kafkapoc.entity.UserEntity;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
