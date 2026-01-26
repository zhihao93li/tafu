package com.tafu.bazi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tafu.bazi.dto.request.BaziCalculateRequest;
import com.tafu.bazi.dto.request.SubjectRequest;
import com.tafu.bazi.dto.response.BaziResponse;
import com.tafu.bazi.dto.response.SubjectResponse;
import com.tafu.bazi.entity.Subject;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.SubjectRepository;
import com.tafu.bazi.service.BaziService;
import com.tafu.bazi.service.SubjectService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SubjectServiceImpl
 *
 * <p>描述: 测算对象业务逻辑实现。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SubjectServiceImpl implements SubjectService {

  private final SubjectRepository subjectRepository;
  private final BaziService baziService;
  private final com.tafu.bazi.repository.FortuneReportRepository fortuneReportRepository;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public SubjectResponse create(String userId, SubjectRequest.Create request) {
    Subject subject = new Subject();
    BeanUtils.copyProperties(request, subject);
    subject.setUserId(userId);

    // 自动进行八字排盘
    Map<String, Object> baziData = calculateBazi(request);
    subject.setBaziData(baziData);

    subjectRepository.save(subject);
    return toResponse(subject);
  }

  @Override
  @Transactional
  public SubjectResponse update(String userId, String id, SubjectRequest.Update request) {
    Subject subject = getEntity(userId, id);
    BeanUtils.copyProperties(
        request,
        subject,
        "id",
        "userId",
        "createdAt",
        "baziData",
        "initialAnalysis",
        "initialAnalyzedAt");

    // 重新排盘
    Map<String, Object> baziData = calculateBazi(request);
    subject.setBaziData(baziData);

    subjectRepository.save(subject);
    return toResponse(subject);
  }

  @Override
  public void delete(String userId, String id) {
    Subject subject = getEntity(userId, id);
    subjectRepository.delete(subject);
  }

  @Override
  public SubjectResponse getDetail(String userId, String id) {
    return toResponse(getEntity(userId, id));
  }

  @Override
  public Page<SubjectResponse> getList(String userId, Pageable pageable) {
    return subjectRepository
        .findByUserIdOrderByCreatedAtDesc(userId, pageable)
        .map(this::toResponse);
  }

  @Override
  public com.tafu.bazi.entity.Subject getEntity(String userId, String id) {
    return subjectRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));
  }

  @Override
  public java.util.List<com.tafu.bazi.entity.FortuneReport> getReports(
      String userId, String subjectId) {
    // Verify ownership
    getEntity(userId, subjectId);
    return fortuneReportRepository.findByUserIdAndSubjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(
        userId, subjectId);
  }

  private Map<String, Object> calculateBazi(SubjectRequest.Create request) {
    BaziCalculateRequest calcRequest = new BaziCalculateRequest();
    // 手动映射字段（字段名不匹配，不能使用 BeanUtils.copyProperties）
    calcRequest.setYear(request.getBirthYear());
    calcRequest.setMonth(request.getBirthMonth());
    calcRequest.setDay(request.getBirthDay());
    calcRequest.setHour(request.getBirthHour());
    calcRequest.setMinute(request.getBirthMinute());
    calcRequest.setCalendarType(request.getCalendarType());
    calcRequest.setGender(request.getGender());
    calcRequest.setLeapMonth(request.isLeapMonth());
    calcRequest.setLocation(request.getLocation());

    // 调用八字计算服务（返回强类型 DTO）
    BaziResponse baziResponse = baziService.calculate(calcRequest);

    // 将 BaziResponse 转换为 Map 用于存储到数据库 JSONB 字段
    @SuppressWarnings("unchecked")
    Map<String, Object> baziData = objectMapper.convertValue(baziResponse, Map.class);
    return baziData;
  }

  private SubjectResponse toResponse(Subject subject) {
    return SubjectResponse.builder()
        .id(subject.getId())
        .name(subject.getName())
        .gender(subject.getGender())
        .calendarType(subject.getCalendarType())
        .birthYear(subject.getBirthYear())
        .birthMonth(subject.getBirthMonth())
        .birthDay(subject.getBirthDay())
        .birthHour(subject.getBirthHour())
        .birthMinute(subject.getBirthMinute())
        .isLeapMonth(subject.getIsLeapMonth())
        .location(subject.getLocation())
        .relationship(subject.getRelationship())
        .note(subject.getNote())
        .createdAt(subject.getCreatedAt())
        .baziData(subject.getBaziData()) // 返回完整的八字数据
        .build();
  }
}
