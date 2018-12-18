package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSON;
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
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DeptProductServiceImpl extends BaseService<DeptProduct> implements DeptProductService {
    private static final String default_product_no = "0";
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
        return JSON.parseArray(JSON.toJSONString(responseUtil.getData()),CashLoanGetAllProductsFacade.class);
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
            return default_product_no;
        }
        //从缓存获取关联产品编号
        String redisKey = RedisKeyContant.yofishdk_auth_productnos_prefix + deptId;
        String productNos = redisService.get(redisKey, String.class);
        if (StringUtils.isNotBlank(productNos)) {
            return productNos;
        }
        //缓存未获取到，从数据库获取授权产品编号
        productNos = this.findProductNosByDeptId(deptId);
        if (StringUtils.isBlank(productNos)) {
            productNos = default_product_no;
        }
        redisService.putWithExpireTime(redisKey, productNos, 60*6);
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
            dto.setId(this.getSequence(DeptProduct.SEQ));
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
