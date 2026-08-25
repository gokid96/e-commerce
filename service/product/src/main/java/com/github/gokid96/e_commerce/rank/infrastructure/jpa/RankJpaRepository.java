package com.github.gokid96.e_commerce.rank.infrastructure.jpa;

import com.github.gokid96.e_commerce.rank.domain.Rank;
import com.github.gokid96.e_commerce.rank.domain.RankType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {

    List<Rank> findByRankTypeAndRankDate(RankType rankType, LocalDate rankDate);

}