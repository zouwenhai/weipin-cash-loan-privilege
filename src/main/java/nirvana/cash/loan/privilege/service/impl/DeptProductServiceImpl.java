package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.dao.DeptProductMapper;
import nirvana.cash.loan.privilege.domain.DeptProduct;
import nirvana.cash.loan.privilege.fegin.FeginCashLoanApi;
import nirvana.cash.loan.privilege.fegin.NewResponseUtil;
import nirvana.cash.loan.privilege.fegin.facade.CashLoanGetAllProductsFacade;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DeptProductServiceImpl extends BaseService<DeptProduct> implements DeptProductService {
    @Autowired
    private FeginCashLoanApi feginCashLoanApi;
    @Autowired
    private DeptProductMapper deptProductMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public List<CashLoanGetAllProductsFacade> findAllProductList() {
        NewResponseUtil responseUtil = feginCashLoanApi.getAllProductList();
        if (!NewResponseUtil.SUCCESS.equals(responseUtil.getCode())) {
            return new ArrayList<>();
        }
        return JSON.parseArray(JSON.toJSONString(responseUtil.getData()), CashLoanGetAllProductsFacade.class);
    }

    @Override
    public String findProductNosByDeptId(Long deptId) {
        if (deptId == null) {
            return "";
        }
        Example example = new Example(DeptProduct.class);
        example.createCriteria().andEqualTo("deptId", deptId);
        List<DeptProduct> deptProducts = deptProductMapper.selectByExample(example);
        List<String> productNoList = deptProducts.stream().map(x -> x.getProductNo()).collect(Collectors.toList());
        return StringUtils.join(productNoList, ",");
    }

    public String findProductNosByDeptIdFromCache(Long deptId) {
        if (deptId == null) {
            return CommonContants.none_product_no;
        }
        String productNos = CommonContants.none_product_no;
        try {
            //从缓存获取关联产品编号
            String redisKey = RedisKeyContant.yofishdk_auth_productnos_prefix + deptId;
            productNos = redisService.get(redisKey, String.class);
            if (StringUtils.isNotBlank(productNos)) {
                return productNos;
            }
            //缓存未获取到，从数据库获取关联产品编号
            productNos = this.findProductNosByDeptId(deptId);
            if (StringUtils.isBlank(productNos)) {
                productNos = CommonContants.none_product_no;
            }
            redisService.put(redisKey, productNos);
        } catch (Exception ex) {
            log.error("获取运营产品队权限信息发生异常:{}", ex);
            //直接从数据库获取一次
            productNos = this.findProductNosByDeptId(deptId);
        }
        return productNos;
    }

    @Transactional
    @Override
    public void insert(Long deptId, String productNos) {
        if (StringUtils.isBlank(productNos)) {
            return;
        }
        String[] arr = productNos.split(",");
        DeptProduct dto = null;
        for (String productNo : arr) {
            dto = new DeptProduct();
/*
            dto.setId(this.getSequence(DeptProduct.SEQ));
            修改为主键自增
*/
            dto.setDeptId(deptId);
            dto.setProductNo(productNo);
            deptProductMapper.insertSelective(dto);
        }
    }

    @Transactional
    @Override
    public void delete(Long deptId) {
        if (deptId == null) {
            return;
        }
        Example example = new Example(DeptProduct.class);
        example.createCriteria().andEqualTo("deptId", deptId);
        deptProductMapper.deleteByExample(example);
    }

    @Transactional
    @Override
    public void delete(List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        Example example = new Example(DeptProduct.class);
        example.createCriteria().andIn("deptId", deptIds);
        deptProductMapper.deleteByExample(example);
    }

}
