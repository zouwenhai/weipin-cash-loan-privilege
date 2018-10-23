package nirvana.cash.loan.privilege.common.dao;

import org.apache.ibatis.annotations.Param;

public interface SeqenceMapper {
	Long getSequence(@Param("seqName") String seqName);
}
