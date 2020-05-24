package wang.miansen.roothub.modules.reply.dao;

import java.util.List;
import java.util.Map;

import wang.miansen.roothub.modules.reply.model.Reply;
import org.apache.ibatis.annotations.Param;

import wang.miansen.roothub.modules.reply.model.ReplyAndTopicByName;

public interface ReplyDao {

	/**
	 * 查询全部评论
	 * @return
	 */
	List<Reply> selectAll();
	
	/**
	 * 分页查询所有评论
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Reply> selectAll(@Param("start") Integer start, @Param("limit") Integer limit);
	
	/**
	 * 根据评论人昵称分页查询所有评论
	 * 关联话题表
	 * @param start
	 * @param limit
	 * @return
	 */
	List<ReplyAndTopicByName> selectAllByNameAndTopic(@Param("replyAuthorName")String replyAuthorName,@Param("start") Integer start, @Param("limit") Integer limit);
	
	/**
	 * 根据评论ID查询评论
	 * @param replyId
	 * @return
	 */
	Reply selectByReplyId(@Param("replyId") Integer replyId);
	
	/**
	 * 根据话题ID查询评论
	 * @param topicId
	 * @return
	 */
	List<Reply> selectByTopicId(@Param("topicId") Integer topicId);
	
	/**
	 * 根据话题ID分页查询评论
	 * @param start
	 * @param limit
	 * @param topicId
	 * @return
	 */
	List<Reply> selectByTopicId(@Param("start") Integer start, @Param("limit") Integer limit,@Param("topicId") Integer topicId);
	List<Reply> selectByTopicIdOrderGoodNum(@Param("start") Integer start, @Param("limit") Integer limit,@Param("topicId") Integer topicId);
	/**
	 * 根据话题作者ID查询评论
	 * @param authorId
	 * @return
	 */
	List<Reply> selectByTopicAuthorId(@Param("topicAuthorId") Integer topicAuthorId);
	
	/**
	 * 根据评论人ID查询评论
	 * @param replyAuthorId
	 * @return
	 */
	List<ReplyDao> selectByReplyAuthorId(@Param("replyAuthorId") Integer replyAuthorId);
	
	/**
	 * 添加评论
	 */
	int insert(Reply reply);
	
	/**
	 * 根据评论ID删除评论
	 * @param replyId
	 * @return
	 */
	int deleteByReplyId(@Param("replyId") Integer replyId);
	
	/**
	 * 根据话题ID删除评论
	 * @param replyId
	 * @return
	 */
	int deleteByTopicId(@Param("topicId") Integer topicId);
	
	/**
	 * 根据用户名称删除评论
	 * @param replyAuthorName
	 * @return
	 */
	int deleteByReplyAuthorName(@Param("replyAuthorName") String replyAuthorName);
	
	/**
	 * 更新评论
	 * @param reply
	 * @return
	 */
	int update(Reply reply);
	
	/**
	 * 统计所有评论
	 * @return
	 */
	int countAll();
	
	/**
	 * 根据话题ID统计评论数
	 * @param topicId
	 * @return
	 */
	int countByTopicId(@Param("topicId") Integer topicId);
	
	/**
	 * 根据用户ID统计评论数
	 * @param userId
	 * @return
	 */
	int countByUserId(@Param("userId") Integer userId);
	
	/**
	 * 根据用户昵称统计评论数
	 * @param userId
	 * @return
	 */
	int countByName(@Param("name") String name);
	
	// 统计当天评论数
	int countToday();
	
	// 后台评论列表
	List<Map<String,Object>> selectAllForAdmin(@Param("author") String author,@Param("topic") String topic,
											   @Param("startDate") String startDate,@Param("endDate") String endDate,
											   @Param("start") Integer start,@Param("limit") Integer limit);
	
	// 后台评论统计
	int countAllForAdmin(@Param("author") String author,@Param("topic") String topic,
					  @Param("startDate") String startDate,@Param("endDate") String endDate);
	
//	List<Reply> selectByChoice(@Param("choice")Integer Choice,@Param("tid")Integer tid,@Param("start")Integer start,@Param("limit")Integer limit);
     
	
	/**
	 * @param: 修改的数值和replyid
	 * @return: 影响行数
	 * @author:tc.w
	 * @TODO: 修改total
	 * */
	int updateTotal(@Param("id")Integer id,@Param("modify")Integer modify);
   
	void updateLikes(@Param("one")Integer one,@Param("two")Integer two,@Param("id")Integer id);

	/**
	 * @param replyId
	 * @return
	 */
	List<Reply> selectByCid(@Param("commentId") Integer commentId);
}
