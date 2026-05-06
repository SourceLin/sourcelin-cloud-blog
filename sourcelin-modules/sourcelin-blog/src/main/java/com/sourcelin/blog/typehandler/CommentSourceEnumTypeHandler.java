package com.sourcelin.blog.typehandler;

import com.sourcelin.blog.constant.CommentSourceEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@code b_comment.source} 与 {@link CommentSourceEnum} 互转。
 */
@MappedTypes(CommentSourceEnum.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CommentSourceEnumTypeHandler extends BaseTypeHandler<CommentSourceEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CommentSourceEnum parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public CommentSourceEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return CommentSourceEnum.fromCode(rs.getString(columnName));
    }

    @Override
    public CommentSourceEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return CommentSourceEnum.fromCode(rs.getString(columnIndex));
    }

    @Override
    public CommentSourceEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return CommentSourceEnum.fromCode(cs.getString(columnIndex));
    }
}
