package nirvana.cash.loan.privilege.service.impl;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ListUtil;
import nirvana.cash.loan.privilege.dao.CacheMapper;
import nirvana.cash.loan.privilege.domain.CacheDto;
import nirvana.cash.loan.privilege.service.AuthCacheService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AuthCacheServiceImpl extends BaseService<CacheDto> implements AuthCacheService {

    @Autowired
    private CacheMapper cacheMapper;

    @Override
    public CacheDto findOne(String key) {
        Example example = new Example(CacheDto.class);
        List<CacheDto> list = cacheMapper.selectByExample(example);
        if (ListUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void insert(String key, String value, String remark) {
        CacheDto dto = new CacheDto();
        dto.setId(this.getSequence(CacheDto.SEQ));
        dto.setKey(key);
        dto.setValue(value);
        dto.setREMARK(remark);
        dto.setCreateTime(new Date());
        cacheMapper.insertSelective(dto);
    }

    @Override
    public void deleteAuthCache() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datestr = dateFormat.format(calendar.getTime());
        Date date = dateFormat.parse(datestr);
        log.info("删除前一日用户登录缓存信息:LessThan date={}",date);
        Example example = new Example(CacheDto.class);
        example.createCriteria().andLessThan("createTime",date);
        cacheMapper.deleteByExample(example);
    }
}
