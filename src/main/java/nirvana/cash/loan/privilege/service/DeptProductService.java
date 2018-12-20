package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.domain.DeptProduct;
import nirvana.cash.loan.privilege.fegin.facade.CashLoanGetAllProductsFacade;
import nirvana.cash.loan.privilege.service.base.IService;

import java.util.List;

public interface DeptProductService extends IService<DeptProduct> {

    /**
     * 查询全部产品列表
     *
     * @return
     */
    List<CashLoanGetAllProductsFacade> findAllProductList();

    /**
     * 查询部门关联的产品编号，多个使用逗号分隔
     *
     * @param deptId
     * @return
     */
    String findProductNosByDeptId(Long deptId);

    /**
     * 从缓存查询部门关联的产品编号，多个使用逗号分隔
     * 若部门未配置产品编号，则返回产品编号"0"
     *
     * @param deptId
     * @return
     */
    String findProductNosByDeptIdFromCache(Long deptId);

    /**
     * 从缓存查询部门关联的产品编号，多个使用逗号分隔
     * 配置的全部产品
     * @return
     */
    String findAllProductNosByDeptIdFromCache();

    /**
     * 添加部门产品关联信息
     *
     * @param deptId
     * @param productNos
     */
    void insert(Long deptId, String productNos);

    /**
     * 删除部门产品关联信息
     *
     * @param deptId
     */
    void delete(Long deptId);

    /**
     * 删除部门产品关联信息
     *
     * @param deptIds
     */
    void delete(List<String> deptIds);
}
