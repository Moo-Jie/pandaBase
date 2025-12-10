package com.rich.pandabaseserver.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rich.pandabaseserver.model.entity.CustomerService;
import com.rich.pandabaseserver.mapper.CustomerServiceMapper;
import com.rich.pandabaseserver.service.CustomerServiceService;
import org.springframework.stereotype.Service;

/**
 * 客服会话表 服务层实现。
 *
 * @author @author DuRuiChi
 */
@Service
public class CustomerServiceServiceImpl extends ServiceImpl<CustomerServiceMapper, CustomerService>  implements CustomerServiceService{

}
