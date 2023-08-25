package com.simpsite.simpsiteservers.repository;

import com.simpsite.simpsiteservers.model.UrlData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlData,String> {
    Optional<UrlData> findByShortUrl(String shortUrl);

    Optional<UrlData> findByLongUrl(String longUrl);

    List<UrlData> findByUserId(int user);
}
