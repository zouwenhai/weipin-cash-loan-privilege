package nirvana.cash.loan.privilege.service.base.impl;

import java.util.List;

import nirvana.cash.loan.privilege.dao.base.SeqenceMapper;
import nirvana.cash.loan.privilege.service.base.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public abstract class BaseService<T> implements IService<T> {

	@Autowired
	protected Mapper<T> mapper;
	@Autowired
	protected SeqenceMapper seqenceMapper;

	public Mapper<T> getMapper() {
		return mapper;
	}

	@Override
	public Long getSequence(@Param("seqName") String seqName) {
		return seqenceMapper.getSequence(seqName);
	}

	@Override
	public List<T> selectAll() {
		return mapper.selectAll();
	}

	@Override
	public T selectByKey(Object key) {
		return mapper.selectByPrimaryKey(key);
	}

	@Override
	@Transactional
	public int save(T entity) {
		return mapper.insert(entity);
	}

	@Override
	@Transactional
	public int delete(Object key) {
		return mapper.deleteByPrimaryKey(key);
	}

	@Override
	@Transactional
	public int batchDelete(List<String> list, String property, Class<T> clazz) {
		Example example = new Example(clazz);
		example.createCriteria().andIn(property, list);
		return this.mapper.deleteByExample(example);
	}

	@Override
	@Transactional
	public int updateAll(T entity) {
		return mapper.updateByPrimaryKey(entity);
	}

	@Override
	@Transactional
	public int updateNotNull(T entity) {
		return mapper.updateByPrimaryKeySelective(entity);
	}

	@Override
	public List<T> selectByExample(Object example) {
		return mapper.selectByExample(example);
	}

	@Override
	public T selectOneByExample(Object example) {
		List<T> list  = mapper.selectByExample(example);
		if(list == null || list.size() == 0){
			return null;
		}
		if(list.size() >1){
			throw new RuntimeException("查询期待结果数量等于1,但是查询出多条记录！");
		}
		return list.get(0);
	}
}
