package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.SysLog;
import nirvana.cash.loan.privilege.service.LogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl extends BaseService<SysLog> implements LogService {

	@Autowired
	private RedisService redisService;

	@Override
	public List<SysLog> findAllLogs(SysLog log) {
		try {
			Example example = new Example(SysLog.class);
			Criteria criteria = example.createCriteria();
			if (StringUtils.isNotBlank(log.getUsername())) {
				criteria.andCondition("lower(username)=", log.getUsername().toLowerCase());
			}
			if (StringUtils.isNotBlank(log.getOperation())) {
				criteria.andCondition("operation like", "%" + log.getOperation() + "%");
			}
			if (StringUtils.isNotBlank(log.getTimeField())) {
				String[] timeArr = log.getTimeField().split("~");
				criteria.andCondition("to_char(CREATE_TIME,'yyyy-mm-dd') >=", timeArr[0]);
				criteria.andCondition("to_char(CREATE_TIME,'yyyy-mm-dd') <=", timeArr[1]);
			}
			example.setOrderByClause("create_time");
			return this.selectByExample(example);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	@Override
	public void deleteLogs(String logIds) {
		List<String> list = Arrays.asList(logIds.split(","));
		this.batchDelete(list, "id", SysLog.class);
	}

	@Override
	public void addLog(String username,String url,long execTime,String params) {
		try{
			//获取"权限方法"
			String method="";
			List<Menu> permissionList=new ArrayList<>();
			String userPermissions = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX + username,String.class);
			if(StringUtils.isNotBlank(userPermissions)){
				permissionList = JSONObject.parseArray(userPermissions, Menu.class);
			}
			boolean priviligeFlag = false;
			for (Menu menu : permissionList) {
				priviligeFlag = url.contains(menu.getPerms());
				if (priviligeFlag){
					method=menu.getPerms();
					break;
				}
			}
			//插入日志
			SysLog log = new SysLog();
			log.setId(this.getSequence(SysLog.SEQ));
			log.setUsername(username);
			log.setOperation(url);
			log.setTime(execTime);//毫秒
			log.setMethod(method);
			log.setParams(params);
			log.setCreateTime(new Date());
			this.save(log);
		}catch (Exception ex){
		}
	}

}
