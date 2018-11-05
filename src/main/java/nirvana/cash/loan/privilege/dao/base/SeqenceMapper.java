package nirvana.cash.loan.privilege.dao.base;

import org.apache.ibatis.annotations.Param;

public interface SeqenceMapper {
	Long getSequence(@Param("seqName") String seqName);
}
