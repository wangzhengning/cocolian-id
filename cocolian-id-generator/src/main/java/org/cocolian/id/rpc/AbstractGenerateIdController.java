package org.cocolian.id.rpc;

import org.cocolian.id.IdRpcService;
import org.cocolian.rpc.NotFoundException;
import org.cocolian.rpc.SystemException;
import org.cocolian.rpc.UserException;

/**
 * @description:
 * @author: zn.wang , Created in 14:08 2018/6/24.
 */
public abstract class AbstractGenerateIdController {

    public abstract IdRpcService.GenerateIdResponse process(IdRpcService.GenerateIdRequest request)
            throws NotFoundException, SystemException, UserException;
}
