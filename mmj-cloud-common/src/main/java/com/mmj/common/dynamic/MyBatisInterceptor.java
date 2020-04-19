/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.mmj.common.dynamic;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.baomidou.mybatisplus.MybatisDefaultParameterHandler;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.plugins.SqlParserHandler;
import com.baomidou.mybatisplus.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.plugins.parser.ISqlParser;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.SecurityUserUtil;
import com.xiaoleilu.hutool.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author lyf
 * @Description 重写分页插件，同时兼容分表插件
 * @date 2019年5月28日 下午3:25:02
 */

@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MyBatisInterceptor extends SqlParserHandler implements Interceptor {

    private static final String ORDER = "order";
	private static final String ORDER_GROUP = "order_group";
	private static final String GM = "GM-";
	private static final String MFS = "MFS";
	private static final String _9 = "_9";
	private static final String _8 = "_8";
	private static final String _7 = "_7";
	private static final String _6 = "_6";
	private static final String _5 = "_5";
	private static final String _4 = "_4";
	private static final String _3 = "_3";
	private static final String _2 = "_2";
	private static final String _1 = "_1";
	private static final String _0 = "_0";
	private static final String T = "t_";
	private static final String USER = "user";
	private static final String T_USER_RECOMMEND_FILE = "t_user_recommend_file";
	private static final String T_USER_RECOMMEND = "t_user_recommend";
	private ISqlParser sqlParser;
    private boolean overflowCurrent = false;
    private String dialectType;
    private String dialectClazz;
    private boolean localPage = true;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        /** this Pointcut sharding table action */
        String originalSql = modifySql(boundSql);
        metaObject.setValue("delegate.boundSql.sql", originalSql);

        this.sqlParser(metaObject);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }
        RowBounds rowBounds = (RowBounds) metaObject.getValue("delegate.rowBounds");
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            if (localPage) {
                rowBounds = PageHelper.getPagination();
                if (rowBounds == null) {
                    return invocation.proceed();
                }
            } else {
                return invocation.proceed();
            }
        }

        Connection connection = (Connection) invocation.getArgs()[0];
        DBType dbType = StringUtils.isNotEmpty(dialectType) ? DBType.getDBType(dialectType) : JdbcUtils.getDbType(connection.getMetaData().getURL());
        if (rowBounds instanceof Pagination) {
            Pagination page = (Pagination) rowBounds;
            boolean orderBy = true;
            if (page.isSearchCount()) {
                SqlInfo sqlInfo = SqlUtils.getOptimizeCountSql(page.isOptimizeCountSql(), sqlParser, originalSql);
                orderBy = sqlInfo.isOrderBy();
                this.queryTotal(overflowCurrent, sqlInfo.getSql(), mappedStatement, boundSql, page, connection);
                if (page.getTotal() <= 0) {
                    return invocation.proceed();
                }
            }
            String buildSql = SqlUtils.concatOrderBy(originalSql, page, orderBy);
            originalSql = DialectFactory.buildPaginationSql(page, buildSql, dbType, dialectClazz);
        } else {
            originalSql = DialectFactory.buildPaginationSql(rowBounds, originalSql, dbType, dialectClazz);
        }

        metaObject.setValue("delegate.boundSql.sql", originalSql);
        metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        PageHelper.remove();
        return invocation.proceed();
    }

    protected void queryTotal(boolean overflowCurrent, String sql, MappedStatement mappedStatement, BoundSql boundSql, Pagination page, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            DefaultParameterHandler parameterHandler = new MybatisDefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            int total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getInt(1);
                }
            }
            page.setTotal(total);
            int pages = page.getPages();
            if (overflowCurrent && (page.getCurrent() > pages)) {
                page.setCurrent(1);
            }
        } catch (Exception e) {
            log.error("Error: Method queryTotal execution error !", e);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
        String dialectType = prop.getProperty("dialectType");
        String dialectClazz = prop.getProperty("dialectClazz");
        String localPage = prop.getProperty("localPage");

        if (StringUtils.isNotEmpty(dialectType)) {
            this.dialectType = dialectType;
        }
        if (StringUtils.isNotEmpty(dialectClazz)) {
            this.dialectClazz = dialectClazz;
        }
        if (StringUtils.isNotEmpty(localPage)) {
            this.localPage = Boolean.valueOf(localPage);
        }
    }

    public MyBatisInterceptor setDialectType(String dialectType) {
        this.dialectType = dialectType;
        return this;
    }

    public MyBatisInterceptor setDialectClazz(String dialectClazz) {
        this.dialectClazz = dialectClazz;
        return this;
    }

    public MyBatisInterceptor setOverflowCurrent(boolean overflowCurrent) {
        this.overflowCurrent = overflowCurrent;
        return this;
    }

    public MyBatisInterceptor setSqlParser(ISqlParser sqlParser) {
        this.sqlParser = sqlParser;
        return this;
    }

    public MyBatisInterceptor setLocalPage(boolean localPage) {
        this.localPage = localPage;
        return this;
    }

    private String modifySql(BoundSql boundSql) {
        String targetSql = boundSql.getSql().trim().toLowerCase();
        List<String> tableNames = getTableName(targetSql);
        for (String tableName : tableNames) {
            if (tableName.contains(T_USER_RECOMMEND) || tableName.contains(T_USER_RECOMMEND_FILE)) {
                continue;
            }
            if (StrUtil.isNotEmpty(tableName) && tableName.indexOf(USER) > -1 && tableName.startsWith(T)) {
                if (tableName.endsWith(_0) || tableName.endsWith(_1) || tableName.endsWith(_2)
                        || tableName.endsWith(_3) || tableName.endsWith(_4) || tableName.endsWith(_5)
                        || tableName.endsWith(_6) || tableName.endsWith(_7) || tableName.endsWith(_8)
                        || tableName.endsWith(_9)) {
                    continue;
                }
                Object obj = BaseContextHandler.get(SecurityConstants.SHARDING_KEY);
                if (obj != null) {
                    String shardingUserIdKey = obj.toString();
                    String prefix = CommonConstant.Symbol.UNDERLINE + Long.valueOf(shardingUserIdKey) % 10;
                    log.info("-->分表{}，从ThreadLocal中获取到userId:{}", tableName, shardingUserIdKey);
                    targetSql = targetSql.replace(tableName, tableName + prefix);
                } else {
                    JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
                    if (jwtUser != null) {
                        Long user_id = jwtUser.getUserId();
                        String prefix = CommonConstant.Symbol.UNDERLINE + user_id % 10;
                        log.info("-->分表{}，从令牌中获取到userId:{}", tableName, user_id);
                        targetSql = targetSql.replace(tableName, tableName + prefix);
                    }
                }
            }
            if (StrUtil.isNotEmpty(tableName) && tableName.indexOf("after") > -1 && tableName.startsWith(T)) {
                Object obj = BaseContextHandler.get(SecurityConstants.SHARDING_KEY);
                if (obj != null) {
                    String shardingUserIdKey = obj.toString();
                    String prefix = CommonConstant.Symbol.UNDERLINE + Long.valueOf(shardingUserIdKey) % 10;
                    targetSql = targetSql.replace(tableName, tableName + prefix);
                } else {
                    JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
                    if (jwtUser != null) {
                        Long user_id = jwtUser.getUserId();
                        String prefix = CommonConstant.Symbol.UNDERLINE + user_id % 10;
                        targetSql = targetSql.replace(tableName, tableName + prefix);
                    }
                }
            }

            if (StrUtil.isNotEmpty(tableName) && tableName.indexOf(ORDER_GROUP) > -1 && tableName.startsWith(T)) {
                Object obj = BaseContextHandler.get(SecurityConstants.SHARDING_GROUP_KEY);
                log.info("order_group表shardingGroupKey:{}", obj);
                if (obj != null && obj.toString().startsWith(GM)) {
                    String shardingGoupNoKey = obj.toString();
                    shardingGoupNoKey = shardingGoupNoKey.substring(shardingGoupNoKey.length() - 3);
                    String prefix = CommonConstant.Symbol.UNDERLINE + Long.valueOf(shardingGoupNoKey);
                    targetSql = targetSql.replace(tableName, tableName + prefix);
                } else if (obj != null && obj.toString().startsWith(MFS)) {
                    String prefix = _0;
                    targetSql = targetSql.replace(tableName, tableName + prefix);
                } else {
                    String prefix = _0;
                    targetSql = targetSql.replace(tableName, tableName + prefix);
                }
            } else if (StrUtil.isNotEmpty(tableName) && tableName.indexOf(ORDER) > -1 && tableName.startsWith(T)) {
                Object obj = BaseContextHandler.get(SecurityConstants.SHARDING_KEY);
                if (obj != null) {
                    String shardingUserIdKey = obj.toString();
                    String prefix = CommonConstant.Symbol.UNDERLINE + Long.valueOf(shardingUserIdKey) % 100;
                    targetSql = targetSql.replace(tableName, tableName + prefix);
                } else {
                    JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
                    if (jwtUser != null) {
                        Long user_id = jwtUser.getUserId();
                        String prefix = CommonConstant.Symbol.UNDERLINE + user_id % 100;
                        targetSql = targetSql.replace(tableName, tableName + prefix);
                    }
                }
            }
        }
        return targetSql;
    }

    private List<String> getTableName(String sql) {
        String dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        List<String> tables = new ArrayList<>();
        for (int i = 0; i < stmtList.size(); i++) {
            SQLStatement stmt = stmtList.get(i);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);
            Map<TableStat.Name, TableStat> tableOpt = visitor.getTables();
            for (TableStat.Name key : tableOpt.keySet()) {
                tables.add(key.toString());
            }
        }
        return tables;
    }
}
