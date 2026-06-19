package com.github.gokid96.e_commerce.rank.infrastructure.jpa;

import com.github.gokid96.e_commerce.rank.domain.Rank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {
}