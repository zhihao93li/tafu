package com.tafu.bazi.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 大运信息 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class YunInfoDTO {
  /** 起运年龄 */
  private int startAge;

  /** 是否顺行 */
  private boolean forward;

  /** 十步大运列表 */
  private List<DaYunDTO> daYunList;
}
