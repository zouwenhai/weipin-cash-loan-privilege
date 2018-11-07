package nirvana.cash.loan.privilege.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongdong
 * @date 2018/11/6
 */
@Data
public class MessageDto implements Serializable {

    private String content;
    private Integer msgModule;
    private String uuid;

}
