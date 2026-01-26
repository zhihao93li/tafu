package com.tafu.bazi.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 十神信息 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenGodInfoDTO {
  /** 十神名称 */
  private String name;

  /** 出现次数 */
  private int count;

  /** 出现位置 */
  private List<String> positions;
}
