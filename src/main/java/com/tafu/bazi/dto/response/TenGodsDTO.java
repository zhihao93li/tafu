package com.tafu.bazi.dto.response;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * 十神分析 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class TenGodsDTO {
  /** 十神映射：十神名称 -> 详细信息 */
  private Map<String, TenGodInfoDTO> gods;
}
