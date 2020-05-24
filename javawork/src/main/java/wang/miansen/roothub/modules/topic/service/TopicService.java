package wang.miansen.roothub.modules.topic.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import wang.miansen.roothub.common.beans.Page;
import wang.miansen.roothub.common.dto.TopicExecution;
import wang.miansen.roothub.modules.topic.model.Topic;
import wang.miansen.roothub.modules.user.model.User;
import wang.miansen.roothub.modules.tag.dto.TagDTO2;
import wang.miansen.roothub.modules.tag.model.Tag;

public interface TopicService {

	/**
	 * 根据节点和sectionName查询话题
	 * @param pageNumber
	 * @param pageSize
	 * @param tab
	 * @param ptab
	 * @return
	 */
	Page<Topic> pageByNodeAndNodeTab(Integer pageNumber, Integer pageSize, String nodeTab, String nodeTitle);
	
	/**
	 * 模糊查询话题列表
	 * @param pageNumber
	 * @param pageSize
	 * @param tab
	 * @return
	 */
	Page<Topic> pageLike(Integer pageNumber, Integer pageSize, String like);
	
	/**
	 * 根据板块查询所有话题
	 * @param pageNumber
	 * @param pageSize
	 * @param tab
	 * @return
	 */
	Page<Topic> pageAllByTab(Integer pageNumber, Integer pageSize,String tab);
	
	/**
	 * 根据节点查询所有话题
	 * @param pageNumber
	 * @param pageSize
	 * @param nodeCode
	 * @return
	 */
	Page<Topic> pageAllByNode(Integer pageNumber, Integer pageSize,String nodeCode);
	
	/**
	 * 根据板块和昵称分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param ptab:板块
	 * @param author:作者昵称
	 * @return
	 */
	Page<Topic> pageAllByPtabAndAuthor(Integer pageNumber, Integer pageSize,String ptab,String author);
	
	/**
	 * 根据节点查询最新话题
	 * @param pageNumber
	 * @param pageSize
	 * @param tab
	 * @return
	 */
	Page<Topic> pageAllNewest(Integer pageNumber, Integer pageSize,String nodeCode);
	
	/**
	 * 根据节点查询查询精华话题
	 */
	Page<Topic> pageGood(Integer pageNumber, Integer pageSize,String nodeCode);
	
	/**
	 * 根据节点查询无人回复的话题
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	Page<Topic> pageNoReply(Integer pageNumber, Integer pageSize,String nodeCode);

	/**
	 * 根据ID查询话题
	 * @param id
	 * @return
	 */
	Topic findByTopicId(Integer topicId);
	
	/**
	 * 查询当前作者的其他话题
	 * @param currentTopicId
	 * @param author
	 * @param limit
	 * @return
	 */
	List<Topic> findOtherTopicByAuthor(Integer currentTopicId, String author, Integer limit);
	
	/**
	 * 侧边栏-今日热门话题
	 * @param currentTopicId
	 * @param author
	 * @param limit
	 * @return
	 */
	List<Topic> findHot(Integer start,Integer limit);
	
	/**
	 * 侧边栏-今日等待回复的话题
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Topic> findTodayNoReply(Integer start,Integer limit);
	
	/**
	 * 首页-最热话题
	 * @param start
	 * @param limit
	 * @param tab 子版块
	 * @return
	 */
	Page<Topic> findIndexHot(Integer start,Integer limit,String tab);
	
	/**
	 * 根据作者昵称分页查询所有话题
	 * @param pageNumber
	 * @param pageSize
	 * @param author
	 * @return
	 */
	Page<Topic> pageByAuthor(Integer pageNumber, Integer pageSize, String author);
	
	/**
	 * 查询所有话题
	 * @return
	 */
	List<Topic> findAll();

	/**
	 * 根据ID删除话题
	 * @param id
	 */
	void deleteByTopicId(Integer topicId);
	
	/**
	 * 根据作者删除话题
	 * @param author
	 */
	void deleteByAuthor(String author);
	
	/**
	 * 根据ID置顶话题
	 * @param id
	 */
	void topByTopicId(Integer topicId);
	
	/**
	 * 根据ID加精话题
	 * @param id
	 */
	void goodByTopicId(Integer topicId);
	
	/**
	 * 发布话题
	 * @param topic
	 */
	TopicExecution saveTopic(Topic topic);
	
	TopicExecution createTopic(String title, String content, String tab, String nodeCode,String nodeTitle,String tag,User user);
	
	/**
	 * 更新话题
	 * @param topic
	 */
	void updateTopic(Topic topic);
	
	/**
     * 更新主题的头像
     * @param user
     * @return
     */
	void updateTopicAvatar(User user);
    
	/**
	 * 更新节点名称
	 * @param oldNodeTitle:老的节点名称
	 * @param newNodeTitle:新的节点名称
	 */
	void updateNodeTitile(String oldNodeTitle, String newNodeTitle);
	
	/**
	 * 收藏的话题列表
	 * @param pageNumber
	 * @param pageSize
	 * @param uid
	 * @return
	 */
	Page<Topic> findCollectsById(Integer pageNumber, Integer pageSize, Integer uid);
	
	/**
	 * 查询用户发布主题的数量
	 * @param userId
	 * @return
	 */
	int countByUserName(String userName);

	/**
	 * 分组统计所有标签
	 * @return
	 */
	//Integer countTag();
	
	/**
	 * 分页查询所有标签
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	Page<Tag> findByTag(Integer pageNumber, Integer pageSize);
	Page<TagDTO2> findByTagDTO(Integer pageNumber, Integer pageSize);
	
	/**
	 * 根据标签查询话题
	 * @param tag
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	Page<Topic> pageByTag(String tag,Integer pageNumber, Integer pageSize);
	
	/**
     * 根据板块统计所有话题
     * @return
     */
    int countAllTopic(String tab);
    
    /**
     * 根据节点统计所有话题
     * @param node
     * @return
     */
    int countTopicByNode(String nodeTitle);
    
    /**
     * 作者的其他话题
     * @param userName
     * @param topicId
     * @return
     */
    List<Topic> findOther(String userName,Integer topicId);
    
    /**
     * 统计当天话题数
     * @return
     */
    int countToday();
    
    Page<Topic> pageForAdmin(String author,String startDate,String endDate,Integer pageNumber, Integer pageSize);
    
    int countAllForAdmin(String author,String startDate,String endDate);
    
    Topic findById(Integer id);
    
    int updateUpAndDown(@Param("id")Integer id,@Param("good")Integer good,@Param("bad")Integer bad);
    
    int addGood(@Param("id")Integer id);
    
    int addBad(@Param("id")Integer id);
    
    int minusGood(@Param("id")Integer id);
    
    int minusBad(@Param("id")Integer id);
}

