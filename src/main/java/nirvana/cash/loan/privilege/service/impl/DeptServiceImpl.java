package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.exception.BizException;
import nirvana.cash.loan.privilege.common.util.TreeUtils;
import nirvana.cash.loan.privilege.dao.DeptMapper;
import nirvana.cash.loan.privilege.dao.UserMapper;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.AuthDeptProductInfoVo;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.LogoutUserService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DeptServiceImpl extends BaseService<Dept> implements DeptService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DeptProductService deptProductService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private LogoutUserService logoutUserService;
    @Autowired
    private DeptMapper deptMapper;

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
        Example example = new Example(Dept.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo("isDelete", 0);
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            criteria.andEqualTo("deptName", dept.getDeptName());
        }
        example.setOrderByClause("dept_id");
        return this.selectByExample(example);
    }

    @Override
    public Dept findByName(String deptName) {
        Example example = new Example(Dept.class);
        example.createCriteria()
                .andEqualTo("isDelete", 0)
                .andEqualTo("deptName", deptName);
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
        if (parentId == null) {
            dept.setParentId(0L);
        }
        if (StringUtils.isBlank(dept.getProductNos())) {
            dept.setViewRange(0);
        } else {
            dept.setViewRange(1);
        }
/*
        dept.setDeptId(this.getSequence(Dept.SEQ));
        设置为主键自增
*/
        dept.setCreateTime(new Date());
        dept.setIsDelete(0);
        this.save(dept);

        //添加部门产品关联信息
        if (dept.getViewRange() == 1) {
            Long deptId = dept.getDeptId();
            String productNos = dept.getProductNos();
            deptProductService.insert(deptId, productNos);
        }
    }

    @Override
    public Dept findById(Long deptId) {
        return this.selectByKey(deptId);
    }

    @Override
    @Transactional
    public void updateDept(Dept dept, User loginUser) {
        Long parentId = dept.getParentId();
        Long deptId = dept.getDeptId();
        if (parentId == null) {
            dept.setParentId(0L);
        }
        if (StringUtils.isBlank(dept.getProductNos())) {
            dept.setViewRange(0);
        } else {
            dept.setViewRange(1);
        }
        this.updateNotNull(dept);
        //重新添加部门产品关联信息
        deptProductService.delete(deptId);
        if (dept.getViewRange() == 1) {
            String productNos = dept.getProductNos();
            deptProductService.insert(deptId, productNos);
        }
        //关联登录用户强制退出
        Example example = new Example(User.class);
        example.createCriteria().andLike("deptId", "%" + deptId + "%");
        List<User> userList = userMapper.selectByExample(example);
        List<Long> userIds = userList.stream()
                .filter(t -> StringUtils.isNotBlank(t.getDeptId()))
                .map(t -> t.getUserId())
                .filter(t -> !t.equals(loginUser.getUserId())).collect(Collectors.toList());
        logoutUserService.batchLogoutUser(userIds);

        //删除关联产品缓存
        String redisKey = RedisKeyContant.yofishdk_auth_productnos_prefix + deptId;
        redisService.delete(redisKey);
        //删除缓存的部门信息
        String rediskey = RedisKeyContant.yofishdk_auth_deptname_prefix + dept.getDeptId();
        redisService.delete(rediskey);
    }

    @Override
    public AuthDeptProductInfoVo findAuthDeptProductInfoFromCache(Long userId, Long deptId) {
        log.info("获取运营团队权限信息请求参数:userId={},deptId={}", userId, deptId);
        //从缓存获取部门信息
        Dept dept = null;
        try {
            String rediskey = RedisKeyContant.yofishdk_auth_deptname_prefix + deptId;
            String deptStr = redisService.get(rediskey, String.class);
            if (StringUtils.isNotBlank(deptStr)) {
                dept = JSONObject.parseObject(deptStr, Dept.class);
            } else {
                dept = this.findById(deptId);
                if (dept == null) {
                    log.error("部门信息不存在,请检查用户所属部门配置！(用户ID:{},部门ID:{})", userId, deptId);
                    BizException.newInstance("部门信息不存在,请检查用户所属部门配置！");
                }
                redisService.put(rediskey, JSON.toJSONString(dept));
            }
        } catch (Exception ex) {
            log.error("获取运营团队权限信息发生异常:{}", ex);
            //直接从数据库获取一次
            dept = this.findById(deptId);
        }

        //获取关联产品编号
        String productNos = CommonContants.all_product_no;
        if (dept.getViewRange() == 1) {
            productNos = deptProductService.findProductNosByDeptIdFromCache(deptId);
        }
        AuthDeptProductInfoVo vo = new AuthDeptProductInfoVo();
        vo.setDeptId(deptId);
        vo.setProductNos(productNos);
        return vo;
    }

}
