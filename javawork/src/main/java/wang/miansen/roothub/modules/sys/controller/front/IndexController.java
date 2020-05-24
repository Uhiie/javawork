package wang.miansen.roothub.modules.sys.controller.front;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wang.miansen.roothub.common.beans.BaseEntity;
import wang.miansen.roothub.common.beans.Page;
import wang.miansen.roothub.common.beans.Result;
import wang.miansen.roothub.common.controller.BaseController;
import wang.miansen.roothub.common.util.ApiAssert;
import wang.miansen.roothub.common.util.Base64Util;
import wang.miansen.roothub.common.util.StringUtils;
import wang.miansen.roothub.config.SiteConfig;
import wang.miansen.roothub.modules.node.model.Node;
import wang.miansen.roothub.modules.node.model.NodeTab;
import wang.miansen.roothub.modules.node.service.NodeService;
import wang.miansen.roothub.modules.node.service.NodeTabService;
import wang.miansen.roothub.modules.reply.service.ReplyService;
import wang.miansen.roothub.modules.tag.dto.TagDTO2;
import wang.miansen.roothub.modules.user.model.User;
import wang.miansen.roothub.modules.user.model.UserCheck;
import wang.miansen.roothub.modules.user.service.UserCheckService;
import wang.miansen.roothub.modules.user.service.UserService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import wang.miansen.roothub.common.dto.UserExecution;
import wang.miansen.roothub.common.exception.ApiException;
import wang.miansen.roothub.modules.topic.model.Topic;
import wang.miansen.roothub.modules.tab.model.Tab;
import wang.miansen.roothub.modules.collect.service.CollectService;
import wang.miansen.roothub.modules.feedback.model.Feedback;
import wang.miansen.roothub.modules.feedback.service.FeedbackService;
import wang.miansen.roothub.modules.friendURL.model.FriendURL;
import wang.miansen.roothub.modules.friendURL.service.FriendURLService;
import wang.miansen.roothub.modules.notice.service.NoticeService;
import wang.miansen.roothub.modules.qmxGroup.model.QmxGroup;
import wang.miansen.roothub.modules.qmxGroup.service.qmxGroupService;
import wang.miansen.roothub.modules.topic.service.TopicService;
import wang.miansen.roothub.modules.topic.service.TabService;
import wang.miansen.roothub.common.util.CookieAndSessionUtil;
import wang.miansen.roothub.common.util.bcrypt.BCryptPasswordEncoder;

@Controller
public class IndexController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private UserCheckService userCheckService;
	@Autowired
	private TopicService topicService;
	@Autowired
	private NodeTabService nodeTabService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private ReplyService replyService;
	@Autowired
	private CollectService collectDaoService;
	@Autowired
	private RedisTemplate<String, List<String>> redisTemplate;
	@Autowired
	private TabService tabService;
	@Autowired
	private SiteConfig siteConfig;
	@Autowired
	private BaseEntity baseEntity;
	@Autowired
	private NodeService nodeService;
	@Autowired
	private qmxGroupService groupService;
	@Autowired
	private FeedbackService feedbackService;
	// @Autowired
	@Autowired
	private FriendURLService friendURLService;

	/**
	 * 首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	private String index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "p", defaultValue = "1") Integer p,
			@RequestParam(value = "tab", defaultValue = "all") String tab) {
		String cookie=null;
		try {
			cookie=CookieAndSessionUtil.getCookie(request, "user");
		}catch(Exception e) {}
		if(cookie!=null) {
			User user=userService.selectByThirdToken(Base64Util.decode(cookie));
			CookieAndSessionUtil.setSession(request, "user", user);
		}
		Page<Topic> page = topicService.pageAllByTab(p, 25, tab);
		List<Tab> tabList = tabService.selectAll();
		List<Node> nodeList = nodeService.findAllByTab(tab, 0, 5);
		// 热门话题榜
		List<Topic> findHot = topicService.findHot(0, 10);
		// 今日等待回复的话题
		List<Topic> findTodayNoReply = topicService.findTodayNoReply(0, 10);
		// 最热标签
		Page<TagDTO2> tag = topicService.findByTagDTO(1, 10);
		List<Node> nodeList2 = nodeService.findAll(0, 10);
		// 注册会员的数量
		int countUserAll = userService.countUserAll();
		// 所有话题的数量
		int countAllTopic = topicService.countAllTopic(null);
		// 所有评论的数量
		int countAllReply = replyService.countAll();
		// url信息

		List<FriendURL> friendURL = new ArrayList<FriendURL>();
		try {
			friendURL = friendURLService.selectAllTop10();
		} catch (Exception e) {
			friendURL = new ArrayList<FriendURL>();
		}
		if (!friendURL.isEmpty()) {
			request.setAttribute("friendURL", friendURL);
		}
		request.setAttribute("page", page);
		request.setAttribute("findHot", findHot);
		request.setAttribute("findTodayNoReply", findTodayNoReply);
		request.setAttribute("tabList", tabList);
		request.setAttribute("nodeList", nodeList);
		request.setAttribute("nodeList2", nodeList2);
		request.setAttribute("tab", tab);
		request.setAttribute("tag", tag);
		request.setAttribute("countUserAll", countUserAll);
		request.setAttribute("countAllTopic", countAllTopic);
		request.setAttribute("countAllReply", countAllReply);
		return "/default/front/index";
	}

	/**
	 * 注册页面
	 */
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	private String register(HttpServletRequest request) {
		List<QmxGroup> groupList = groupService.findAll();
		request.setAttribute("groupList", groupList);
		return "/default/front/register";
	}

	/**
	 * 注册接口
	 * 
	 * @param username
	 * @param password
	 * @param email
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	private Result<UserExecution> register(@RequestParam("username") String username, @RequestParam("sex") String sex,
			@RequestParam("password") String password, @RequestParam("email") String email,
			@RequestParam("group") Integer groupid, HttpSession session, @RequestParam("sex") String userSex,
			@RequestParam("checkcode") String cko, HttpServletRequest request) {
		ApiAssert.notEmpty(username, "请输入用户名");
		ApiAssert.notEmpty(password, "请输入密码");
		ApiAssert.notEmpty(email, "请输入邮箱");

		String checkCode = (String) request.getSession().getAttribute("simpleCaptcha");

		// 获得验证码对象
		/* Object cko = session.getAttribute("simpleCaptcha"); */
		if (cko == null || cko == "") {
			return new Result<UserExecution>(false, "请输入验证码！");
		}
		String captcha = cko.toString();// 文本框输入的验证码
		// 判断验证码输入是否正确
		Date now = new Date();
		Long codeTime = Long.valueOf(session.getAttribute("codeTime") + "");
		if (StringUtils.isEmpty(checkCode) || captcha == null || !(checkCode.equalsIgnoreCase(captcha))) {
			return new Result<UserExecution>(false, "验证码错误!");
		} else if ((now.getTime() - codeTime) / 1000 / 60 > 1) {
			return new Result<UserExecution>(false, "验证码已失效，请重新获取！");
		} else {
			User user = userService.findByName(username);
			ApiAssert.isNull(user, "用户已存在");
			user = userService.findByEmail(email);
			ApiAssert.isNull(user, "邮箱已存在");
			UserExecution save = userService.createUser(username, password, email, sex);
			userCheckService.createUserCheck(save.getUser().getUserId(), groupid);
			// CookieAndSessionUtil.setSession(request, "user", save.getUser());
			return new Result<UserExecution>(true, save);
		}

	}

	/**
	 * 登录页面
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	private String login(HttpServletRequest request) {
		return "/default/front/login";
	}

	/**
	 * 登录接口
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	private Result<User> login(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("checkbox") boolean checkbox, HttpServletRequest request, HttpServletResponse response) {
		User user = new User();
		try {
			try {
				user = userService.findByName(username);
			} catch (Exception e) {
			}
			if (user == null) {
				try {
					user = userService.findByEmail(username);
				} catch (Exception e) {
					ApiAssert.notNull(user, "用户名不正确");
				}
			}
			ApiAssert.notNull(user, "用户名不正确");
			boolean pass = true;
			UserCheck userCheck = userCheckService.findById(user.getUserId());
			if (userCheck.getStatus() == 0) {
				pass = false;
			}
			ApiAssert.isTrue(pass, "用户未审核或被封禁");
			ApiAssert.isTrue(new BCryptPasswordEncoder().matches(password, user.getPassword()), "密码不正确");
		} catch (ApiException e) {
			return new Result<User>(false, e.getMessage());
		}
		// 设置cookie
		if (checkbox) {
			CookieAndSessionUtil.setCookie(siteConfig.getCookieConfig().getName(),
					Base64Util.encode(user.getThirdAccessToken()), siteConfig.getCookieConfig().getMaxAge(),
					siteConfig.getCookieConfig().getPath(), siteConfig.getCookieConfig().isHttpOnly(), response);
		}
		// 设置session
		CookieAndSessionUtil.setSession(request, "user", user);
		return new Result<User>(true, user);
	}

	/**
	 * 退出
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	private String logout(HttpServletRequest request, HttpServletResponse response) {
		// stringRedisTemplate.delete("user");
		CookieAndSessionUtil.removeSession(request, "user");
		CookieAndSessionUtil.removeCookie(response,
		siteConfig.getCookieConfig().getName(),
		siteConfig.getCookieConfig().getPath(),
		siteConfig.getCookieConfig().isHttpOnly());
		return "redirect:/";
	}
	/**
	 * 标签页
	 * 
	 * @param request
	 * @param p
	 * @return
	 */
	@RequestMapping(value = "/tags", method = RequestMethod.GET)
	private String tag(HttpServletRequest request, @RequestParam(value = "p", defaultValue = "1") Integer p) {
		// Page<Tag> tag = topicService.findByTag(p, 50);
		Page<TagDTO2> tag = topicService.findByTagDTO(p, 50);
		request.setAttribute("tag", tag);
		return "/default/front/tag/list";
	}

	@RequestMapping(value = "/session", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, String> session(HttpServletRequest request) {
		User user = getUser(request);
		HashedMap map = new HashedMap();
		if (user != null) {
			map.put("success", true);
			map.put("user", user.getUserName());
			return map;
		} else {
			map.put("success", false);
			map.put("user", "");
			return map;
		}
	}

	/**
	 * 搜索
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	private String search(HttpServletRequest request, @RequestParam("s") String search,
			@RequestParam(value = "p", defaultValue = "1") Integer p) {
		if (search == null || search.equals("")) {
			return "search";
		}
		Page<Topic> pageLike = topicService.pageLike(p, 50, search);
		// BaseEntity baseEntity = new BaseEntity();
		// request.setAttribute("baseEntity", baseEntity);
		request.setAttribute("pageLike", pageLike);
		request.setAttribute("search", search);
		return "/default/front/search/view";
	}

	/**
	 * Top100积分榜
	 * 
	 * @return
	 */
	@RequestMapping(value = "/top100")
	private String top100() {
		return "/default/front/integral/list";
	}

	/**
	 * 关于
	 * 
	 * @return
	 */
	@RequestMapping(value = "/about")
	private String about() {
		return "/default/front/common/about";
	}

	/**
	 * faq
	 * 
	 * @return
	 */
	@RequestMapping(value = "/faq")
	private String faq() {
		return "/default/front/common/faq";
	}

	/**
	 * useragreement
	 * 
	 * @return
	 */
	@RequestMapping(value = "/useragreement")
	private String useragreement() {
		return "/default/front/common/useagreement";
	}

	/**
	 * developer
	 * 
	 * @return
	 */
	@RequestMapping(value = "/developer")
	private String developer() {
		return "/default/front/common/developer";
	}

	/**
	 * contactinfo
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contactinfo")
	private String contactinfo() {
		return "/default/front/common/contactinfo";
	}

	/**
	 * 反馈建议
	 * 
	 * @return
	 */
	@RequestMapping(value = "/feedback")
	private String feedback() {
		return "/default/front/common/feedback";
	}

	@RequestMapping(value = "/feedback/add", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	private Result<String> feedbackAdd(@RequestParam("info") String info, HttpServletRequest request) {
		try {
			Feedback feedback = new Feedback();
			User user = CookieAndSessionUtil.getSession(request, "user");
			feedback.setContent(info);
			feedback.setUid(user.getUserId());
			feedback.setUserName(user.getUserName());
			feedbackService.insert(feedback);
			return new Result<>(true, "反馈成功!我们会尽快解决!谢谢您的反馈!");
		} catch (Exception e) {
			return new Result<>(false, "反馈失败...");
		}
	}

	/**
	 * 这是测试代码，与项目无关 excel
	 * 
	 * @return
	 */
	@RequestMapping(value = "/excel")
	private String excel(HttpServletRequest request) {
		List<Topic> row1 = topicService.findAll();// 全部话题
		List<Tab> row2 = tabService.selectAll();// 父板块
		List<NodeTab> row3 = nodeTabService.findAll();// 子版块
		request.setAttribute("row1", row1);
		request.setAttribute("row2", row2);
		request.setAttribute("row3", row3);
		return "/default/front/common/excel";
	}

	/**
	 * 这是测试代码，与项目无关
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel/download")
	private void excel02(HttpServletResponse response) throws Exception {
		List<Topic> row1 = topicService.findAll();
		// List<RootTopic> row2 = rootTopicService.findHot(1, 50);
		List<Tab> row2 = tabService.selectAll();
		List<NodeTab> row3 = nodeTabService.findAll();
		List<Topic> rows1 = CollUtil.newArrayList(row1);
		List<Tab> rows2 = CollUtil.newArrayList(row2);
		List<NodeTab> rows3 = CollUtil.newArrayList(row3);
		// List<List<? extends Object>> rows3 = CollUtil.newArrayList(row1,row2,row3);
		ExcelWriter writer = ExcelUtil.getWriter("d:/writeTest04.xlsx", "话题");
		writer.addHeaderAlias("topicId", "话题标识");
		writer.addHeaderAlias("ptab", "父板块标识");
		writer.addHeaderAlias("tab", "子版块标识");
		writer.addHeaderAlias("title", "话题标题");
		writer.addHeaderAlias("tag", "话题内容标签");
		writer.addHeaderAlias("content", "话题内容");
		writer.addHeaderAlias("createDate", "创建时间");
		writer.addHeaderAlias("updateDate", "更新时间");
		writer.addHeaderAlias("lastReplyTime", "最后回复话题时间");
		writer.addHeaderAlias("lastReplyAuthor", "最后回复话题的用户");
		writer.addHeaderAlias("viewCount", "浏览量");
		writer.addHeaderAlias("author", "话题作者");
		writer.addHeaderAlias("top", "1置顶 0默认");
		writer.addHeaderAlias("good", "1精华 0默认");
		writer.addHeaderAlias("showStatus", "1显示 0不显示");
		writer.addHeaderAlias("replyCount", "回复数量");
		writer.addHeaderAlias("isDelete", "1删除 0默认");
		writer.addHeaderAlias("tagIsCount", "话题内容标签是否被统计过 1是 0否默认");
		writer.addHeaderAlias("postGoodCount", "点赞");
		writer.addHeaderAlias("postBadCount", "踩数");
		writer.addHeaderAlias("statusCd", "话题状态 1000:有效 1100:无效 1200:未生效");
		writer.addHeaderAlias("nodeSlug", "所属节点");
		writer.addHeaderAlias("nodeTitle", "节点名称");
		writer.addHeaderAlias("remark", "备注");
		writer.addHeaderAlias("avatar", "话题作者头像");
		writer.write(rows1);
		writer.setSheet("父板块");
		writer.addHeaderAlias("id", "父板块标识");
		writer.addHeaderAlias("tabName", "父板块名称");
		writer.addHeaderAlias("tabDesc", "父板块描述");
		writer.addHeaderAlias("isDelete", "是否删除 0：否 1：是");
		writer.addHeaderAlias("createDate", "创建时间");
		writer.addHeaderAlias("tabOrder", "排列顺序");
		writer.write(rows2);
		writer.setSheet("子板块");
		writer.addHeaderAlias("sectionId", "子板块标识");
		writer.addHeaderAlias("sectionName", "子板块名称");
		writer.addHeaderAlias("sectionTab", "子板块标签");
		writer.addHeaderAlias("sectionDesc", "子板块描述");
		writer.addHeaderAlias("sectionTopicNum", "板块帖子数目");
		writer.addHeaderAlias("showStatus", "是否显示，0:不显示 1:显示");
		writer.addHeaderAlias("displayIndex", "子板块排序");
		writer.addHeaderAlias("defaultShow", "默认显示板块 0:默认 1:显示");
		writer.addHeaderAlias("pid", "模块父节点");
		writer.addHeaderAlias("createDate", "创建时间");
		writer.addHeaderAlias("updateDate", "更新时间");
		writer.addHeaderAlias("statusCd", "板块状态 1000:有效 1100:无效 1200:未生效");
		writer.write(rows3);
		// response为HttpServletResponse对象
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		// test.xlsx是弹出下载对话框的文件名，不能为中文，中文请自行编码
		response.setHeader("Content-Disposition", "attachment;filename=test02.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out);
		// 关闭writer，释放内存
		// 关闭writer，释放内存
		writer.close();
	}
}