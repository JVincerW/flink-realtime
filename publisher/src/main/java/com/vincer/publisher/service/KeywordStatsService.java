package com.vincer.publisher.service;

import com.vincer.publisher.bean.KeywordStats;

import java.util.List;

/**
 * Desc: 关键词统计接口
 */
public interface KeywordStatsService {
    List<KeywordStats> getKeywordStats(int date, int limit);
}

