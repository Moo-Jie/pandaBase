package com.rich.pandabaseserver.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.model.entity.ExportRecord;
import com.rich.pandabaseserver.mapper.ExportRecordMapper;
import com.rich.pandabaseserver.service.ExportRecordService;
import org.springframework.stereotype.Service;

/**
 * 数据导出记录表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Service
public class ExportRecordServiceImpl extends ServiceImpl<ExportRecordMapper, ExportRecord>  implements ExportRecordService{

}
