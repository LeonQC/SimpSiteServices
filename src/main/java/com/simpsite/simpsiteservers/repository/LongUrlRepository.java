package com.simpsite.simpsiteservers.repository;

import com.simpsite.simpsiteservers.model.Long2ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LongUrlRepository extends JpaRepository<Long2ShortUrl,String> {
}
