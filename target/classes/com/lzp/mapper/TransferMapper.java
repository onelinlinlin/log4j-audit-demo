package com.lzp.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransferMapper {

    /*执行转账记录的插入*/
    @Insert("insert into record(id,toUsername,fromUsername,account) values(0,#{toUsername},#{fromUsername},#{account})")
    public int addTransferRecord(@Param("fromUsername") String fromUsername,@Param("toUsername") String toUsername,@Param("account") int account);

}
