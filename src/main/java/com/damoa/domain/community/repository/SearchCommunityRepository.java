package com.damoa.domain.community.repository;

import com.damoa.domain.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchCommunityRepository {

    Page<Community> findAllSearch(String type, String keyword, Pageable pageable);
}
