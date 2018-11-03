package nirvana.cash.loan.privilege.service.impl;

import nirvana.cash.loan.privilege.common.domain.FilterId;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import nirvana.cash.loan.privilege.common.util.TreeUtils;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.dao.UserMapper;
import nirvana.cash.loan.privilege.domain.Dept;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DeptServiceImpl extends BaseService<Dept> implements DeptService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public Tree<Dept> getDeptTree() {
		List<Tree<Dept>> trees = new ArrayList<>();
		List<Dept> depts = this.findAllDepts(new Dept());
		for (Dept dept : depts) {
			Tree<Dept> tree = new Tree<>();
			tree.setId(dept.getDeptId().toString());
			tree.setParentId(dept.getParentId().toString());
			tree.setText(dept.getDeptName());
			trees.add(tree);
		}
		return TreeUtils.build(trees);
	}

	@Override
	public List<Dept> findAllDepts(Dept dept) {
		try {
			Example example = new Example(Dept.class);
			if(StringUtils.isNotBlank(dept.getDeptName())){
				example.createCriteria().andCondition("dept_name=", dept.getDeptName());
			}
			example.setOrderByClause("dept_id");
			return this.selectByExample(example);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	@Override
	public Dept findByName(String deptName) {
		Example example = new Example(Dept.class);
		example.createCriteria().andCondition("lower(dept_name) =", deptName.toLowerCase());
		List<Dept> list = this.selectByExample(example);
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	@Transactional
	public void addDept(Dept dept) {
		Long parentId = dept.getParentId();
		if (parentId == null)
			dept.setParentId(0L);
		dept.setDeptId(this.getSequence(Dept.SEQ));
		dept.setCreateTime(new Date());
		this.save(dept);
	}

	@Override
	@Transactional
	public void deleteDepts(Long deptId) {
		List<Dept> depts = this.findAllDepts(new Dept());
		if(depts!=null && depts.size()>0){
			//转换列表
			List<FilterId> allList = new ArrayList<>();
			depts.forEach(t -> {
				FilterId filterId = new FilterId(t.getDeptId(), t.getParentId(), t.getDeptName());
				allList.add(filterId);
			});
			//开始处理...
			List<FilterId> filterIdList = FilterId.filterRemoveList(allList, deptId);
			List<String> list =new ArrayList<>();
			for(FilterId item:filterIdList){
				list.add(item.getId()+"");
			}
			this.batchDelete(list, "deptId", Dept.class);
			userMapper.setDeptIdNull(deptId);
		}
	}

	@Override
	public Dept findById(Long deptId) {
		return this.selectByKey(deptId);
	}

	@Override
	@Transactional
	public void updateDept(Dept dept) {
		this.updateNotNull(dept);
	}

}