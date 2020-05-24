package wang.miansen.roothub.modules.reply.controller.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import wang.miansen.roothub.common.beans.BaseEntity;
import wang.miansen.roothub.common.beans.Page;
import wang.miansen.roothub.common.beans.Result;
import wang.miansen.roothub.common.controller.BaseController;
import wang.miansen.roothub.common.dto.ReplyExecution;
import wang.miansen.roothub.common.util.ApiAssert;
import wang.miansen.roothub.modules.notice.model.Notice;
import wang.miansen.roothub.modules.reply.model.Reply;
import wang.miansen.roothub.modules.user.model.User;
import org.slf4j.Logger;
import org.springframework.ui.Model;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import wang.miansen.roothub.modules.topic.model.Topic;
import wang.miansen.roothub.modules.notice.service.NoticeService;
import wang.miansen.roothub.modules.reply.service.ReplyService;
import wang.miansen.roothub.modules.topic.service.TopicService;

@Controller
public class ReplyController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ReplyService replyService;

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private TopicService topicService;

	@Autowired
	private BaseEntity baseEntity;

	/**
	 * 评论接口
	 *
	 * @param request
	 * @param topicId：话题ID
	 * @param content：评论内容
	 * @param type：类型      0：富文本 1：Markdown
	 * @return
	 */
	@RequestMapping(value = "/reply/save", method = RequestMethod.POST)
	@ResponseBody
	private Result<ReplyExecution> save(HttpServletRequest request, @RequestParam("topicId") Integer topicId,
			@RequestParam("content") String content, @RequestParam("type") String type,
			@RequestParam(value = "rank", defaultValue = "-1") Integer rank,
			@RequestParam(value = "cid", defaultValue = "0") Integer cid) {
		User user = null;
		try {
			user = getUser(request);
		} catch (Exception e) {	
		}
		ApiAssert.notNull(user, "用户未登陆");
		Reply reply = new Reply();
		Reply answer = new Reply();
		reply.setTopicId(topicId);// 话题id

		// 如果是 Markdown 格式的正文，则先渲染后再保存进数据库
		if ("1".equals(type)) {
			content = baseEntity.formatContent(content);
		}
		if (rank != 2)
			reply.setRank(rank + 1);
		else
			reply.setRank(2);
		reply.setTotal(0);
		if (cid != 0) {
			replyService.updateTotal(cid, 1);
			answer = replyService.findById(cid);
		}
		reply.setCommentId(cid);
		reply.setReplyContent(content);// 回复内容
		reply.setCreateDate(new Date());// 回复时间
		reply.setUpdateDate(new Date());// 更新时间
		reply.setReplyAuthorId(user.getUserId());// 当前回复用户ID
		reply.setReplyAuthorName(user.getUserName());// 当前回复用户昵称
		reply.setIsDelete(false);// 是否删除 0:默认 1:删除
		reply.setIsRead(false);// 是否已读 0:默认 1:未读
		reply.setIsShow(false);// 是否可见 0:默认 1:不可见
		reply.setReplyGoodCount(0);// 点赞
		reply.setReplyBadCount(0);// 踩数
		reply.setReplyType(null);
		reply.setReplyReadCount(0);
		reply.setAvatar(user.getAvatar());
		reply.setStatusCd("1000");// 回复状态 1000:有效 1100:无效 1200:未生效
		ReplyExecution save = replyService.save(reply);// 添加回复
		Topic findByTopicId = topicService.findByTopicId(topicId);
		findByTopicId.setReplyCount(findByTopicId.getReplyCount() + 1);// 回复量+1
		findByTopicId.setLastReplyAuthor(user.getUserName());// 最后回复人昵称
		findByTopicId.setLastReplyTime(new Date());// 最后回复时间
		topicService.updateTopic(findByTopicId);// 更新话题
		// 回复者与话题作者不是同一个人的时候发送通知
		if (!user.getUserName().equals(findByTopicId.getAuthor()) && reply.getRank() == 0) {
			Notice notice = new Notice();
			notice.setNoticeTitle("replyNotice");// 通知标题
			notice.setIsRead(false);// 是否已读：0:默认 1:已读
			notice.setNoticeAuthorId(user.getUserId());// 发起通知用户ID
			notice.setNoticeAuthorName(user.getUserName());// 发起通知用户昵称
			notice.setTargetAuthorName(findByTopicId.getAuthor());// 要通知用户的昵称
//            notice.setTargetAuthorId();//要通知用户的id
			notice.setCreateDate(new Date());// 创建时间
			notice.setUpdateDate(new Date());// 更新时间
			notice.setNoticeAction("reply");// 通知动作
			notice.setTopicId(findByTopicId.getTopicId());// 话题ID
			notice.setNoticeContent(content);// 通知内容
			notice.setStatusCd("1000");// 通知状态 1000:有效 1100:无效 1200:未生效
			noticeService.save(notice);// 添加通知
		} else if (!user.getUserName().equals(answer.getReplyAuthorName())) {
			Notice notice = new Notice();
			notice.setNoticeTitle("replyNotice");// 通知标题
			notice.setIsRead(false);// 是否已读：0:默认 1:已读
			notice.setNoticeAuthorId(user.getUserId());// 发起通知用户ID
			notice.setNoticeAuthorName(user.getUserName());// 发起通知用户昵称
			notice.setTargetAuthorName(findByTopicId.getAuthor());// 要通知用户的昵称
//            notice.setTargetAuthorId();//要通知用户的id
			notice.setCreateDate(new Date());// 创建时间
			notice.setUpdateDate(new Date());// 更新时间
			notice.setNoticeAction("reply");// 通知动作
			notice.setTopicId(findByTopicId.getTopicId());// 话题ID
			notice.setNoticeContent(content);// 通知内容
			notice.setStatusCd("1000");// 通知状态 1000:有效 1100:无效 1200:未生效
			noticeService.save(notice);// 添加通知
		}

		return new Result<ReplyExecution>(true, save);
	}

	@RequestMapping(value = "/reply/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<String> deleteReply(@RequestParam("replyId") Integer replyId) {
		try {
			List<Reply> list = new ArrayList<Reply>();
			Reply reply = replyService.findById(replyId);
			list.add(reply);
			for (int i = 0; i < list.size(); i++) {
				reply = list.get(i);
				list.addAll(replyService.findByCid(reply.getReplyId()));
				replyService.deleteByReplyId(reply.getReplyId());
			}
			return new Result<>(true, "删除成功");
		} catch (Exception e) {
			return new Result<>(false, "删除失败");
		}
	}
}
