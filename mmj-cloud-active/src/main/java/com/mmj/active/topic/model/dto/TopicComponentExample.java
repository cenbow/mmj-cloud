package com.mmj.active.topic.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TopicComponentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TopicComponentExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Integer value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Integer value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Integer value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Integer value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Integer value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Integer> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Integer> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Integer value1, Integer value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTopicIdIsNull() {
            addCriterion("topic_id is null");
            return (Criteria) this;
        }

        public Criteria andTopicIdIsNotNull() {
            addCriterion("topic_id is not null");
            return (Criteria) this;
        }

        public Criteria andTopicIdEqualTo(Integer value) {
            addCriterion("topic_id =", value, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdNotEqualTo(Integer value) {
            addCriterion("topic_id <>", value, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdGreaterThan(Integer value) {
            addCriterion("topic_id >", value, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("topic_id >=", value, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdLessThan(Integer value) {
            addCriterion("topic_id <", value, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdLessThanOrEqualTo(Integer value) {
            addCriterion("topic_id <=", value, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdIn(List<Integer> values) {
            addCriterion("topic_id in", values, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdNotIn(List<Integer> values) {
            addCriterion("topic_id not in", values, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdBetween(Integer value1, Integer value2) {
            addCriterion("topic_id between", value1, value2, "topicId");
            return (Criteria) this;
        }

        public Criteria andTopicIdNotBetween(Integer value1, Integer value2) {
            addCriterion("topic_id not between", value1, value2, "topicId");
            return (Criteria) this;
        }

        public Criteria andJumpTypeIsNull() {
            addCriterion("jump_type is null");
            return (Criteria) this;
        }

        public Criteria andJumpTypeIsNotNull() {
            addCriterion("jump_type is not null");
            return (Criteria) this;
        }

        public Criteria andJumpTypeEqualTo(String value) {
            addCriterion("jump_type =", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeNotEqualTo(String value) {
            addCriterion("jump_type <>", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeGreaterThan(String value) {
            addCriterion("jump_type >", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeGreaterThanOrEqualTo(String value) {
            addCriterion("jump_type >=", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeLessThan(String value) {
            addCriterion("jump_type <", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeLessThanOrEqualTo(String value) {
            addCriterion("jump_type <=", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeLike(String value) {
            addCriterion("jump_type like", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeNotLike(String value) {
            addCriterion("jump_type not like", value, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeIn(List<String> values) {
            addCriterion("jump_type in", values, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeNotIn(List<String> values) {
            addCriterion("jump_type not in", values, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeBetween(String value1, String value2) {
            addCriterion("jump_type between", value1, value2, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpTypeNotBetween(String value1, String value2) {
            addCriterion("jump_type not between", value1, value2, "jumpType");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1IsNull() {
            addCriterion("jump_url1 is null");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1IsNotNull() {
            addCriterion("jump_url1 is not null");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1EqualTo(String value) {
            addCriterion("jump_url1 =", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1NotEqualTo(String value) {
            addCriterion("jump_url1 <>", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1GreaterThan(String value) {
            addCriterion("jump_url1 >", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1GreaterThanOrEqualTo(String value) {
            addCriterion("jump_url1 >=", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1LessThan(String value) {
            addCriterion("jump_url1 <", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1LessThanOrEqualTo(String value) {
            addCriterion("jump_url1 <=", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1Like(String value) {
            addCriterion("jump_url1 like", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1NotLike(String value) {
            addCriterion("jump_url1 not like", value, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1In(List<String> values) {
            addCriterion("jump_url1 in", values, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1NotIn(List<String> values) {
            addCriterion("jump_url1 not in", values, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1Between(String value1, String value2) {
            addCriterion("jump_url1 between", value1, value2, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl1NotBetween(String value1, String value2) {
            addCriterion("jump_url1 not between", value1, value2, "jumpUrl1");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2IsNull() {
            addCriterion("jump_url2 is null");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2IsNotNull() {
            addCriterion("jump_url2 is not null");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2EqualTo(String value) {
            addCriterion("jump_url2 =", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2NotEqualTo(String value) {
            addCriterion("jump_url2 <>", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2GreaterThan(String value) {
            addCriterion("jump_url2 >", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2GreaterThanOrEqualTo(String value) {
            addCriterion("jump_url2 >=", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2LessThan(String value) {
            addCriterion("jump_url2 <", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2LessThanOrEqualTo(String value) {
            addCriterion("jump_url2 <=", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2Like(String value) {
            addCriterion("jump_url2 like", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2NotLike(String value) {
            addCriterion("jump_url2 not like", value, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2In(List<String> values) {
            addCriterion("jump_url2 in", values, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2NotIn(List<String> values) {
            addCriterion("jump_url2 not in", values, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2Between(String value1, String value2) {
            addCriterion("jump_url2 between", value1, value2, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl2NotBetween(String value1, String value2) {
            addCriterion("jump_url2 not between", value1, value2, "jumpUrl2");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3IsNull() {
            addCriterion("jump_url3 is null");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3IsNotNull() {
            addCriterion("jump_url3 is not null");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3EqualTo(String value) {
            addCriterion("jump_url3 =", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3NotEqualTo(String value) {
            addCriterion("jump_url3 <>", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3GreaterThan(String value) {
            addCriterion("jump_url3 >", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3GreaterThanOrEqualTo(String value) {
            addCriterion("jump_url3 >=", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3LessThan(String value) {
            addCriterion("jump_url3 <", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3LessThanOrEqualTo(String value) {
            addCriterion("jump_url3 <=", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3Like(String value) {
            addCriterion("jump_url3 like", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3NotLike(String value) {
            addCriterion("jump_url3 not like", value, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3In(List<String> values) {
            addCriterion("jump_url3 in", values, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3NotIn(List<String> values) {
            addCriterion("jump_url3 not in", values, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3Between(String value1, String value2) {
            addCriterion("jump_url3 between", value1, value2, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andJumpUrl3NotBetween(String value1, String value2) {
            addCriterion("jump_url3 not between", value1, value2, "jumpUrl3");
            return (Criteria) this;
        }

        public Criteria andSortNumIsNull() {
            addCriterion("sort_num is null");
            return (Criteria) this;
        }

        public Criteria andSortNumIsNotNull() {
            addCriterion("sort_num is not null");
            return (Criteria) this;
        }

        public Criteria andSortNumEqualTo(Integer value) {
            addCriterion("sort_num =", value, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumNotEqualTo(Integer value) {
            addCriterion("sort_num <>", value, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumGreaterThan(Integer value) {
            addCriterion("sort_num >", value, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("sort_num >=", value, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumLessThan(Integer value) {
            addCriterion("sort_num <", value, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumLessThanOrEqualTo(Integer value) {
            addCriterion("sort_num <=", value, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumIn(List<Integer> values) {
            addCriterion("sort_num in", values, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumNotIn(List<Integer> values) {
            addCriterion("sort_num not in", values, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumBetween(Integer value1, Integer value2) {
            addCriterion("sort_num between", value1, value2, "sortNum");
            return (Criteria) this;
        }

        public Criteria andSortNumNotBetween(Integer value1, Integer value2) {
            addCriterion("sort_num not between", value1, value2, "sortNum");
            return (Criteria) this;
        }

        public Criteria andUserTypeIsNull() {
            addCriterion("user_type is null");
            return (Criteria) this;
        }

        public Criteria andUserTypeIsNotNull() {
            addCriterion("user_type is not null");
            return (Criteria) this;
        }

        public Criteria andUserTypeEqualTo(String value) {
            addCriterion("user_type =", value, "userType");
            return (Criteria) this;
        }

        public Criteria andCondition(String condition) {
            addCriterion(condition);
            return (Criteria) this;
        }

        public Criteria andUserTypeNotEqualTo(String value) {
            addCriterion("user_type <>", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeGreaterThan(String value) {
            addCriterion("user_type >", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeGreaterThanOrEqualTo(String value) {
            addCriterion("user_type >=", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeLessThan(String value) {
            addCriterion("user_type <", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeLessThanOrEqualTo(String value) {
            addCriterion("user_type <=", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeLike(String value) {
            addCriterion("user_type like", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeNotLike(String value) {
            addCriterion("user_type not like", value, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeIn(List<String> values) {
            addCriterion("user_type in", values, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeNotIn(List<String> values) {
            addCriterion("user_type not in", values, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeBetween(String value1, String value2) {
            addCriterion("user_type between", value1, value2, "userType");
            return (Criteria) this;
        }

        public Criteria andUserTypeNotBetween(String value1, String value2) {
            addCriterion("user_type not between", value1, value2, "userType");
            return (Criteria) this;
        }

        public Criteria andImage1IsNull() {
            addCriterion("image1 is null");
            return (Criteria) this;
        }

        public Criteria andImage1IsNotNull() {
            addCriterion("image1 is not null");
            return (Criteria) this;
        }

        public Criteria andImage1EqualTo(String value) {
            addCriterion("image1 =", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1NotEqualTo(String value) {
            addCriterion("image1 <>", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1GreaterThan(String value) {
            addCriterion("image1 >", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1GreaterThanOrEqualTo(String value) {
            addCriterion("image1 >=", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1LessThan(String value) {
            addCriterion("image1 <", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1LessThanOrEqualTo(String value) {
            addCriterion("image1 <=", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1Like(String value) {
            addCriterion("image1 like", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1NotLike(String value) {
            addCriterion("image1 not like", value, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1In(List<String> values) {
            addCriterion("image1 in", values, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1NotIn(List<String> values) {
            addCriterion("image1 not in", values, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1Between(String value1, String value2) {
            addCriterion("image1 between", value1, value2, "image1");
            return (Criteria) this;
        }

        public Criteria andImage1NotBetween(String value1, String value2) {
            addCriterion("image1 not between", value1, value2, "image1");
            return (Criteria) this;
        }

        public Criteria andImage2IsNull() {
            addCriterion("image2 is null");
            return (Criteria) this;
        }

        public Criteria andImage2IsNotNull() {
            addCriterion("image2 is not null");
            return (Criteria) this;
        }

        public Criteria andImage2EqualTo(String value) {
            addCriterion("image2 =", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2NotEqualTo(String value) {
            addCriterion("image2 <>", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2GreaterThan(String value) {
            addCriterion("image2 >", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2GreaterThanOrEqualTo(String value) {
            addCriterion("image2 >=", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2LessThan(String value) {
            addCriterion("image2 <", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2LessThanOrEqualTo(String value) {
            addCriterion("image2 <=", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2Like(String value) {
            addCriterion("image2 like", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2NotLike(String value) {
            addCriterion("image2 not like", value, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2In(List<String> values) {
            addCriterion("image2 in", values, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2NotIn(List<String> values) {
            addCriterion("image2 not in", values, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2Between(String value1, String value2) {
            addCriterion("image2 between", value1, value2, "image2");
            return (Criteria) this;
        }

        public Criteria andImage2NotBetween(String value1, String value2) {
            addCriterion("image2 not between", value1, value2, "image2");
            return (Criteria) this;
        }

        public Criteria andImage3IsNull() {
            addCriterion("image3 is null");
            return (Criteria) this;
        }

        public Criteria andImage3IsNotNull() {
            addCriterion("image3 is not null");
            return (Criteria) this;
        }

        public Criteria andImage3EqualTo(String value) {
            addCriterion("image3 =", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3NotEqualTo(String value) {
            addCriterion("image3 <>", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3GreaterThan(String value) {
            addCriterion("image3 >", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3GreaterThanOrEqualTo(String value) {
            addCriterion("image3 >=", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3LessThan(String value) {
            addCriterion("image3 <", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3LessThanOrEqualTo(String value) {
            addCriterion("image3 <=", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3Like(String value) {
            addCriterion("image3 like", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3NotLike(String value) {
            addCriterion("image3 not like", value, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3In(List<String> values) {
            addCriterion("image3 in", values, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3NotIn(List<String> values) {
            addCriterion("image3 not in", values, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3Between(String value1, String value2) {
            addCriterion("image3 between", value1, value2, "image3");
            return (Criteria) this;
        }

        public Criteria andImage3NotBetween(String value1, String value2) {
            addCriterion("image3 not between", value1, value2, "image3");
            return (Criteria) this;
        }

        public Criteria andCouponNameIsNull() {
            addCriterion("coupon_name is null");
            return (Criteria) this;
        }

        public Criteria andCouponNameIsNotNull() {
            addCriterion("coupon_name is not null");
            return (Criteria) this;
        }

        public Criteria andCouponNameEqualTo(String value) {
            addCriterion("coupon_name =", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotEqualTo(String value) {
            addCriterion("coupon_name <>", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameGreaterThan(String value) {
            addCriterion("coupon_name >", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameGreaterThanOrEqualTo(String value) {
            addCriterion("coupon_name >=", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameLessThan(String value) {
            addCriterion("coupon_name <", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameLessThanOrEqualTo(String value) {
            addCriterion("coupon_name <=", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameLike(String value) {
            addCriterion("coupon_name like", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotLike(String value) {
            addCriterion("coupon_name not like", value, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameIn(List<String> values) {
            addCriterion("coupon_name in", values, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotIn(List<String> values) {
            addCriterion("coupon_name not in", values, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameBetween(String value1, String value2) {
            addCriterion("coupon_name between", value1, value2, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponNameNotBetween(String value1, String value2) {
            addCriterion("coupon_name not between", value1, value2, "couponName");
            return (Criteria) this;
        }

        public Criteria andCouponId1IsNull() {
            addCriterion("coupon_id1 is null");
            return (Criteria) this;
        }

        public Criteria andCouponId1IsNotNull() {
            addCriterion("coupon_id1 is not null");
            return (Criteria) this;
        }

        public Criteria andCouponId1EqualTo(Integer value) {
            addCriterion("coupon_id1 =", value, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1NotEqualTo(Integer value) {
            addCriterion("coupon_id1 <>", value, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1GreaterThan(Integer value) {
            addCriterion("coupon_id1 >", value, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1GreaterThanOrEqualTo(Integer value) {
            addCriterion("coupon_id1 >=", value, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1LessThan(Integer value) {
            addCriterion("coupon_id1 <", value, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1LessThanOrEqualTo(Integer value) {
            addCriterion("coupon_id1 <=", value, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1In(List<Integer> values) {
            addCriterion("coupon_id1 in", values, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1NotIn(List<Integer> values) {
            addCriterion("coupon_id1 not in", values, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1Between(Integer value1, Integer value2) {
            addCriterion("coupon_id1 between", value1, value2, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId1NotBetween(Integer value1, Integer value2) {
            addCriterion("coupon_id1 not between", value1, value2, "couponId1");
            return (Criteria) this;
        }

        public Criteria andCouponId2IsNull() {
            addCriterion("coupon_id2 is null");
            return (Criteria) this;
        }

        public Criteria andCouponId2IsNotNull() {
            addCriterion("coupon_id2 is not null");
            return (Criteria) this;
        }

        public Criteria andCouponId2EqualTo(Integer value) {
            addCriterion("coupon_id2 =", value, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2NotEqualTo(Integer value) {
            addCriterion("coupon_id2 <>", value, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2GreaterThan(Integer value) {
            addCriterion("coupon_id2 >", value, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2GreaterThanOrEqualTo(Integer value) {
            addCriterion("coupon_id2 >=", value, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2LessThan(Integer value) {
            addCriterion("coupon_id2 <", value, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2LessThanOrEqualTo(Integer value) {
            addCriterion("coupon_id2 <=", value, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2In(List<Integer> values) {
            addCriterion("coupon_id2 in", values, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2NotIn(List<Integer> values) {
            addCriterion("coupon_id2 not in", values, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2Between(Integer value1, Integer value2) {
            addCriterion("coupon_id2 between", value1, value2, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId2NotBetween(Integer value1, Integer value2) {
            addCriterion("coupon_id2 not between", value1, value2, "couponId2");
            return (Criteria) this;
        }

        public Criteria andCouponId3IsNull() {
            addCriterion("coupon_id3 is null");
            return (Criteria) this;
        }

        public Criteria andCouponId3IsNotNull() {
            addCriterion("coupon_id3 is not null");
            return (Criteria) this;
        }

        public Criteria andCouponId3EqualTo(Integer value) {
            addCriterion("coupon_id3 =", value, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3NotEqualTo(Integer value) {
            addCriterion("coupon_id3 <>", value, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3GreaterThan(Integer value) {
            addCriterion("coupon_id3 >", value, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3GreaterThanOrEqualTo(Integer value) {
            addCriterion("coupon_id3 >=", value, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3LessThan(Integer value) {
            addCriterion("coupon_id3 <", value, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3LessThanOrEqualTo(Integer value) {
            addCriterion("coupon_id3 <=", value, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3In(List<Integer> values) {
            addCriterion("coupon_id3 in", values, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3NotIn(List<Integer> values) {
            addCriterion("coupon_id3 not in", values, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3Between(Integer value1, Integer value2) {
            addCriterion("coupon_id3 between", value1, value2, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCouponId3NotBetween(Integer value1, Integer value2) {
            addCriterion("coupon_id3 not between", value1, value2, "couponId3");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}