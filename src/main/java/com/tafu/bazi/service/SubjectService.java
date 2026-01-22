package com.tafu.bazi.service;

import com.tafu.bazi.dto.request.SubjectRequest;
import com.tafu.bazi.dto.response.SubjectResponse;
import com.tafu.bazi.entity.FortuneReport;
import com.tafu.bazi.entity.Subject;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * SubjectService Interface
 *
 * <p>描述: 测算对象业务逻辑接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface SubjectService {

  SubjectResponse create(String userId, SubjectRequest.Create request);

  SubjectResponse update(String userId, String id, SubjectRequest.Update request);

  void delete(String userId, String id);

  SubjectResponse getDetail(String userId, String id);

  List<FortuneReport> getReports(String userId, String id);

  Page<SubjectResponse> getList(String userId, Pageable pageable);

  // Internal use
  Subject getEntity(String userId, String id);
}
