package com.zjuse.bankingsystem.mapper.loan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjuse.bankingsystem.entity.loan.Officer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OfficerMapper extends BaseMapper<Officer> {
    @Select("SELECT * FROM officer WHERE username = #{username}")
    Officer findByUsername(String username);

    @Delete("DELETE FROM officer WHERE officer_id = #{id}")
    int deleteOfficer(int id);

    @Update("UPDATE officer SET password = #{newpassword} WHERE officer_id = #{officer_id} AND password = #{currentpassword}")
    int updatepassword(int officer_id,  String currentpassword,  String newpassword);
}


