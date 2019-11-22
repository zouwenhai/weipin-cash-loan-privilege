package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.common.enums.RoleEnum;
import nirvana.cash.loan.privilege.fegin.facade.*;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import nirvana.cash.loan.privilege.common.util.MD5Utils;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.fegin.FeginCollectionApi;
import nirvana.cash.loan.privilege.fegin.FeginRiskApi;
import nirvana.cash.loan.privilege.fegin.NewResponseUtil;
import nirvana.cash.loan.privilege.service.LogoutUserService;
import nirvana.cash.loan.privilege.dao.RoleMapper;
import nirvana.cash.loan.privilege.dao.UserMapper;
import nirvana.cash.loan.privilege.dao.UserRoleMapper;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.UserRole;
import nirvana.cash.loan.privilege.domain.UserWithRole;
import nirvana.cash.loan.privilege.service.UserRoleService;
import nirvana.cash.loan.privilege.service.UserService;
import nirvana.cash.loan.privilege.common.exception.BizException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class UserServiceImpl extends BaseService<User> implements UserService {
    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private FeginCollectionApi feginCollectionApi;
    @Autowired
    private FeginRiskApi feginRiskApi;
    @Autowired
    private LogoutUserService logoutUserService;

    @Override
    public User findByName(String userName) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("lower(username)=", userName.toLowerCase());
        return this.selectOneByExample(example);
    }

    @Override
    public List<User> findUserWithDept(User user) {
        if (StringUtils.isNotBlank(user.getUsername())) {
            user.setUsername(user.getUsername().toLowerCase());
        }
        return this.userMapper.findUserWithDept(user);
    }

    @Override
    @Transactional
    public ResResult addUser(User user, List<Long> roles, User loginUser) {
        if (StringUtils.isBlank(user.getDeptId())) {
            user.setViewRange(0);
        } else {
            user.setViewRange(1);
        }

        /*  user.setUserId(this.getSequence(User.SEQ))
         * 数据库住建设为自增
         * */
        ;
        user.setCrateTime(new Date());
        user.setTheme(User.DEFAULT_THEME);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
        user.setIsDelete(0);
        user.setIsSeperate(0);
        user.setIsSeat(0);
        user.setExtNumber("0");
        this.save(user);
        setUserRoles(user, roles);
        //子系统用户同步
        List<Long> roleIds = roles;
        List<String> roleCodeList = roleMapper.findRoleCodeListByRoleIds(roleIds);
        //催收用户
        List<String> collRoleCodeList = filterRoleCodeList(roleCodeList, "coll");
        if (collRoleCodeList != null && collRoleCodeList.size() > 0) {
            if (collRoleCodeList.size() > 1) {
                throw BizException.newInstance("添加催收用户失败:一个催收登录帐号只能拥有一个催收角色");
            }
            UserAddApiFacade facade = new UserAddApiFacade();
            facade.setUserName(user.getName());
            facade.setLoginName(user.getUsername());
            facade.setMobile(user.getMobile());
            facade.setRoleCodeList(collRoleCodeList);
            facade.setCreateUser(loginUser.getUsername());
            facade.setUpdateUser(loginUser.getUsername());
            try {
                NewResponseUtil apiRes = feginCollectionApi.addUser(facade);
                logger.info("添加催收用户失败|响应数据:{}", JSON.toJSONString(apiRes));
            } catch (Exception ex) {
                logger.error("添加催收用户失败|程序异常:{}", ex);
            }
        }
        //风控
        List<String> riskRoleCodeList = filterRoleCodeList(roleCodeList, "risk");
        if (riskRoleCodeList != null && riskRoleCodeList.size() > 0) {
            if (riskRoleCodeList.size() > 1) {
                throw BizException.newInstance("添加风控用户失败:一个风控登录帐号只能拥有一个风控角色");
            }
            RiskUserAddApiFacade facade = new RiskUserAddApiFacade();
            facade.setUserName(user.getName());
            facade.setLoginName(user.getUsername());
            facade.setMobile(user.getMobile());
            facade.setRoleType(riskRoleCodeList.get(0));
            facade.setUserStatus("1");
            try {
                NewResponseUtil apiRes = feginRiskApi.addOrderUser(facade);
                logger.info("添加风控用户失败|响应数据:{}", JSON.toJSONString(apiRes));
            } catch (Exception ex) {
                logger.error("添加风控用户失败|程序异常:{}", ex);
            }
        }
        return ResResult.success();
    }

    private void setUserRoles(User user, List<Long> roles) {
        for (Long roleId : roles) {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(roleId);
            this.userRoleMapper.insert(ur);
        }
    }

    @Override
    @Transactional
    public void updateUser(User user, List<Long> roles, Long loginUserId, String username) {
        if (StringUtils.isBlank(user.getDeptId())) {
            user.setViewRange(0);
        } else {
            user.setViewRange(1);
        }
        List<String> oldRoleCodeList = userRoleService.findRoleCodeListByUserId(user.getUserId().intValue());
        List<String> newRoleCodeList = roleMapper.findRoleCodeListByRoleIds(roles);
        User oldUser = this.userMapper.selectByPrimaryKey(user.getUserId());

        user.setCrateTime(oldUser.getCrateTime());
        user.setPassword(oldUser.getPassword());
        user.setUsername(oldUser.getUsername());
        user.setModifyTime(new Date());
        user.setIsDelete(0);
        user.setIsSeperate(oldUser.getIsSeperate());
        user.setIsSeat(oldUser.getIsSeat());
        user.setExtNumber(oldUser.getExtNumber());
        this.updateAll(user);
        Example example = new Example(UserRole.class);
        example.createCriteria().andCondition("user_id=", user.getUserId());
        this.userRoleMapper.deleteByExample(example);
        setUserRoles(user, roles);

        if (user.getUserId().longValue() != loginUserId) {
            logoutUserService.logoutUser(user.getUserId());
        }

        //子系统用户同步
        //催收用户
        List<String> oldCollRoleCodeList = filterRoleCodeList(oldRoleCodeList, "coll");
        List<String> newCollRoleCodeList = filterRoleCodeList(newRoleCodeList, "coll");
        if (oldCollRoleCodeList.size() > 0 || newCollRoleCodeList.size() > 0) {
            if (newCollRoleCodeList.size() > 1) {
                throw BizException.newInstance("修改催收用户失败:一个催收登录帐号只能拥有一个催收角色");
            }
            UserUpdateApiFacade facade = new UserUpdateApiFacade();
            facade.setUserName(user.getName());
            facade.setLoginName(user.getUsername());
            facade.setMobile(user.getMobile());
            facade.setUpdateUser(username);
            if (newCollRoleCodeList.size() == 0) {
                facade.setStatus(2);//删除
                facade.setRoleCodeList(oldCollRoleCodeList);
            } else {
                facade.setStatus(1);//修改
                facade.setRoleCodeList(newCollRoleCodeList);
            }
            try {
                NewResponseUtil apiRes = feginCollectionApi.updateUser(facade);
                logger.info("修改催收用户成功|响应数据:{}", JSON.toJSONString(apiRes));
            } catch (Exception ex) {
                logger.error("修改催收用户失败|程序异常:{}", ex);
            }
        }

        //风控
        List<String> oldRiskRoleCodes = filterRoleCodeList(oldRoleCodeList, "risk");
        List<String> newRiskRoleCodes = filterRoleCodeList(newRoleCodeList, "risk");
        RiskUserUpdateApiFacade facade = new RiskUserUpdateApiFacade();
        facade.setMobile(user.getMobile());
        facade.setLoginName(user.getUsername());
        facade.setUserName(user.getName());
        //撤销用户风控角色
        if (!CollectionUtils.isEmpty(oldRiskRoleCodes) && CollectionUtils.isEmpty(newRiskRoleCodes)) {
            //删除风控用户
            logger.info("删除风控用户:{}", user.getUsername());
            facade.setRoleType(oldRiskRoleCodes.get(0));
            facade.setUserStatus("0");
        }
        //设置用户风控角色，但角色数量大于1，不允许
        if (!CollectionUtils.isEmpty(newRiskRoleCodes) && newRiskRoleCodes.size() > 1) {
            throw BizException.newInstance("修改风控用户失败:一个风控登录帐号只能拥有一个风控角色");
        }
        //设置用户风控角色，且风控角色数量等于1
        if (!CollectionUtils.isEmpty(newRiskRoleCodes)) {
            //更新风控用户角色
            String newRoleCode = newRiskRoleCodes.get(0);
            logger.info("更新风控用户：{} 的角色为：{}", user.getUsername(), newRoleCode);
            facade.setRoleType(newRoleCode);
            facade.setUserStatus("1");
            try {
                logger.info("修改风控用户:" + JSONObject.toJSONString(facade));
                NewResponseUtil result = feginRiskApi.updateOrderUser(facade);
                if (!NewResponseUtil.SUCCESS.equals(result.getCode())) {
                    throw BizException.newInstance(result.getDesc());
                }
            } catch (Exception e) {
                logger.error("修改风控用户失败:{}", e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userMapper.selectByPrimaryKey(Long.valueOf(userId));
        List<String> roleCodeList = userRoleService.findRoleCodeListByUserId(userId);
        user.setIsDelete(1);
        this.updateNotNull(user);
        this.userRoleService.deleteUserRolesByUserId(userId.toString());

        logoutUserService.logoutUser(user.getUserId());

        //子系统用户同步
        //催收用户
        List<String> collRoleCodeList = filterRoleCodeList(roleCodeList, "coll");
        if (collRoleCodeList != null && collRoleCodeList.size() > 0) {
            UserUpdateApiFacade facade = new UserUpdateApiFacade();
            facade.setUserName(user.getName());
            facade.setLoginName(user.getUsername());
            facade.setMobile(user.getMobile());
            facade.setRoleCodeList(collRoleCodeList);
            facade.setStatus(2);
            NewResponseUtil apiRes = feginCollectionApi.updateUser(facade);
            logger.info("删除催收用户失败|响应数据:{}", JSON.toJSONString(apiRes));
        }
        //风控
        List<String> riskRoleCodeList = filterRoleCodeList(roleCodeList, "risk");
        if (riskRoleCodeList != null && riskRoleCodeList.size() > 0) {
            RiskUserUpdateApiFacade facade = new RiskUserUpdateApiFacade();
            facade.setUserName(user.getName());
            facade.setLoginName(user.getUsername());
            facade.setMobile(user.getMobile());
            facade.setRoleType(riskRoleCodeList.get(0));
            facade.setUserStatus("0");//删除
            NewResponseUtil apiRes = feginRiskApi.updateOrderUser(facade);
            logger.info("删除风控用户失败|响应数据:{}", JSON.toJSONString(apiRes));
        }
    }

    @Override
    @Transactional
    public void updateLoginTime(String userName) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("lower(username)=", userName.toLowerCase());
        User user = new User();
        user.setLastLoginTime(new Date());
        this.userMapper.updateByExampleSelective(user, example);
    }

    @Override
    @Transactional
    public void updatePassword(String password, Long userId) {
        User user = this.userMapper.selectByPrimaryKey(userId);
        String newPassword = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);

        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setPassword(newPassword);
        this.userMapper.updateByPrimaryKeySelective(newUser);
    }

    @Override
    public UserWithRole findById(Long userId) {
        List<UserWithRole> list = this.userMapper.findUserWithRole(userId);
        List<Long> roleList = new ArrayList<>();
        for (UserWithRole uwr : list) {
            roleList.add(uwr.getRoleId());
        }
        if (list.size() == 0) {
            return null;
        }
        UserWithRole userWithRole = list.get(0);
        userWithRole.setRoleIds(roleList);
        return userWithRole;
    }

    @Override
    public String findUserRoldIds(Integer userId) {
        return this.userMapper.findUserRoldIds(userId);
    }

    @Override
    public String findUserRoldCodes(String roleIds) {
        List<Long> roleIdList = new ArrayList<>();
        Arrays.asList(roleIds.split(",")).forEach(t -> {
            roleIdList.add(Long.valueOf(t));
        });
        List<String> roleCodeList = roleMapper.findRoleCodeListByRoleIds(roleIdList);
        return String.join(",", roleCodeList);
    }

    public List<String> filterRoleCodeList(List<String> roleCodeList, String syncService) {
        List<String> collRoleCodeList = new ArrayList<>();
        if (roleCodeList != null) {
            for (String roleCode : roleCodeList) {
                RoleEnum roleEnum = RoleEnum.getPaymentStatusEnumByValue(roleCode);
                String service = roleEnum == null ? null : roleEnum.getService();
                if (service != null && service.equals(syncService)) {
                    collRoleCodeList.add(roleCode);
                }
            }
        }
        return collRoleCodeList;
    }

    @Override
    public List<User> findByIds(Set<Long> userIdSet) {
        Example example = new Example(User.class);
        example.createCriteria().andIn("userId", userIdSet);
        return userMapper.selectByExample(example);
    }

    @Override
    public List<User> findAllLikeDeptId(Long deptId) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo("isDelete", 0);
        if (deptId != null) {
            criteria.andLike("deptId", "%" + deptId + "%");
        }
        return userMapper.selectByExample(example);
    }

    @Override
    public List<User> findUserById(List<Long> userIdList, Integer isSeperate) {
        return userMapper.getUserById(userIdList, isSeperate);


    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public int isDivideOrder(IsDivideOrderFacade isDivideOrderFacade) {
        return userMapper.updateDivideOrder(isDivideOrderFacade);
    }

    @Override
    public List<User> getAuditUser(Integer isSeperate) {

        return userMapper.getAuditUser(isSeperate);
    }

    @Override
    public void isOpenSeat(IsOpenSeatFacade isOpenSeatFacade) {
        userMapper.updateSeat(isOpenSeatFacade);
    }

    @Override
    public void addExtNumber(ExtNumberFacade extNumberFacade) {
        userMapper.updateExtNumber(extNumberFacade);
    }

}
