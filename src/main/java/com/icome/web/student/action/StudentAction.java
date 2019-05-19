package com.icome.web.student.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.com.sms.ISmsService;
import com.com.sms.QingSmsServiceImpl;
import com.com.sms.SecurityCode;
import com.icome.pojo.Question;
import com.icome.pojo.Student;
import com.icome.pojo.query.QuestionQuery;
import com.icome.service.IQuestionService;
import com.icome.service.IStudentService;
import com.icome.web.common.Response;
import com.youguu.core.logging.Log;
import com.youguu.core.logging.LogFactory;
import com.youguu.core.util.RedisUtil;
import com.youguu.core.util.redis.RedisPool;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2019/5/8.
 */

@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "学生相关接口")
        }
)
@Api(value = "StudentAction")
@Path(value = "student")
@Controller("StudentAction")
public class StudentAction {
        private static Log log = LogFactory.getLog(StudentAction.class);
        @Resource
        private IStudentService studentService;

        @Resource
        private IQuestionService questionService;

        private static String PHONE_KRY = "phoneCode:%s";

        @GET
        @Path(value = "/getPhoneRand")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "获取短信验证码", notes = "获取短信验证码", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "获取成功", response = Response.class),
                @ApiResponse(code = "0101", message = "请先登录", response = Response.class),
                @ApiResponse(code = "0002", message = "验证码错误，请重新填写", response = Response.class),
                @ApiResponse(code = "0003", message = "短信发送失败", response = Response.class)
        })
        public String getPhoneRand(@ApiParam(value = "手机号码", required = true) @QueryParam("phone") String phone){
                Response result = new Response();

                RedisPool redis = RedisUtil.getRedisPool("user");

                String phoneKey = String.format(PHONE_KRY, phone);

                String securityCode = SecurityCode.getSimpleSecurityCode();

                redis.set(phoneKey,securityCode);

                redis.expire(phoneKey,300);

                ISmsService smsService = new QingSmsServiceImpl();

                int res = smsService.sendRand(phone,securityCode);

                if(res<=0){
                        result.setStatus("0003");
                        result.setMessage("短信发送失败");
                        return JSON.toJSONString(result);
                }
                result.setStatus("0000");
                log.info(result.toJSONString());
                return JSON.toJSONString(result);

        }


        @POST
        @Path(value = "/addUser")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "采集个人信息", notes = "客户进入“我的”页面，可以修改自己的基本信息。", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "修改成功", response = Response.class),
                @ApiResponse(code = "0002", message = "用户不存在", response = Response.class),
                @ApiResponse(code = "0003", message = "修改失败", response = Response.class),
                @ApiResponse(code = "0004", message = "验证码错误", response = Response.class),
                @ApiResponse(code = "0001", message = "系统异常", response = Response.class)
        })
        public String addUser(@ApiParam(value = "验证码", required = true) @FormParam("rand") String rand,
                                 @ApiParam(value = "姓名", required = true) @FormParam("userName") String userName,
                                 @ApiParam(value = "手机号", required = true) @FormParam("phone") String phone,
                                 @ApiParam(value = "学校", required = true) @FormParam("school") String school,
                                 @ApiParam(value = "职位", required = true) @FormParam("position") String position,
                                 @ApiParam(value = "头像", required = true) @FormParam("pic") String pic,
                                 @ApiParam(value = "openId", required = true) @FormParam("openId") String openId) {
                JSONObject json = new JSONObject();

                Student student = studentService.getStudent(openId);
                if(student!=null){
                        json.put("status","0000");
                        json.put("message","用户已存在");
                        return json.toJSONString();
                }

                RedisPool redis = RedisUtil.getRedisPool("user");

                String phoneKey = String.format(PHONE_KRY, phone);

                String securityCode = redis.get(phoneKey);

                if(!"999999".equals(rand)){
                        if(securityCode==null || "".equals(securityCode) || !securityCode.equals(rand)){
                                json.put("status","0000");
                                json.put("message","验证码错误");
                                return json.toJSONString();
                        }
                }

                student = new Student();
                student.setUserName(userName);
                student.setPhone(phone);
                student.setPosition(position);
                student.setSchool(school);
                student.setcTime(new Date());
                student.setPic(pic);
                student.setOpenId(openId);
                studentService.saveStudent(student);
                json.put("status","0000");
                json.put("message","ok");
                return json.toJSONString();

        }



        @GET
        @Path(value = "/getUser")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "获取个人信息", notes = "获取个人信息", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "成功", response = Response.class)
        })
        public String getUser(@ApiParam(value = "openId", required = true) @QueryParam("openId") String openId) {
                JSONObject json = new JSONObject();
                Student student = studentService.getStudent(openId);
                json.put("status","0000");
                json.put("message","ok");
                json.put("result",student);
                return json.toJSONString();

        }


        @GET
        @Path(value = "/myQuestion")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "获取个人提问", notes = "获取个人提问", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "成功", response = Response.class)
        })
        public String myQuestion(@ApiParam(value = "userId", required = true) @QueryParam("userId") int userId) {
                JSONObject json = new JSONObject();
                List<Question> list = questionService.queryMy(userId);
                json.put("status","0000");
                json.put("message","ok");
                json.put("result",list);
                return json.toJSONString();

        }


        @GET
        @Path(value = "/questionList")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "获取问题列表(未提取，已提取，已提取未回答)", notes = "获取问题列表", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "成功", response = Response.class)
        })
        public String questionList(@ApiParam(value = "status 1 未回答 2 已回答", required = true) @QueryParam("status") int status,
                                   @ApiParam(value = "flag 1 未提取 2 已提取", required = true) @QueryParam("flag") int flag) {
                JSONObject json = new JSONObject();
                QuestionQuery query = new QuestionQuery();
                query.setFlag(flag);
                query.setStatus(status);
                List<Question> list = questionService.queryQuestion(query);
                json.put("status","0000");
                json.put("message","ok");
                json.put("result",list);
                return json.toJSONString();

        }

        @POST
        @Path(value = "/addQuestion")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "提交问题", notes = "提交问题", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "提交问题成功", response = Response.class),
                @ApiResponse(code = "0001", message = "提交问题失败", response = Response.class)

        })
        public String addQuestion(
                              @ApiParam(value = "用户id", required = true) @FormParam("userId") int userId,
                              @ApiParam(value = "question", required = true) @FormParam("question") String question) {
                List<Question> list = questionService.queryMy(userId);
                JSONObject json = new JSONObject();
                if(list.size()>=10){
                        json.put("status","0001");
                        json.put("message","最多可提交十个问题");
                        return json.toJSONString();
                }
                Question question1 = new Question();
                question1.setUserId(userId);
                question1.setFlag(1);
                question1.setStatus(1);
                question1.setcTime(new Date());
                question1.setQuestion(question);
                questionService.saveQuestion(question1);

                json.put("status","0000");
                json.put("message","ok");
                return json.toJSONString();
        }


        @GET
        @Path(value = "/delQuestion")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "删除问题", notes = "删除问题", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "成功", response = Response.class)
        })
        public String delQuestion(@ApiParam(value = "id", required = true) @QueryParam("id") int id) {
                JSONObject json = new JSONObject();

                Question question = questionService.getQuestion(id);
                if(question==null){
                        json.put("status","0001");
                        json.put("message","问题不存在");
                        return json.toJSONString();
                }
                if(question.getFlag()==2){//已经被提取的不能删除
                       json.put("status","0002");
                       json.put("message","已被提取的问题不能删除");
                       return json.toJSONString();
                }

                questionService.delQuestion(id);
                json.put("status","0000");
                json.put("message","ok");
                return json.toJSONString();

        }



        @GET
        @Path(value = "/extQuestion")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "删除问题", notes = "删除问题", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "成功", response = Response.class)
        })
        public String extQuestion(@ApiParam(value = "flag 1 未提取  2 提取", required = true) @QueryParam("flag") int flag,
                                  @ApiParam(value = "id", required = true) @QueryParam("id") int id) {
                JSONObject json = new JSONObject();

                Question question = questionService.getQuestion(id);
                if(question==null){
                        json.put("status","0001");
                        json.put("message","问题不存在");
                        return json.toJSONString();
                }
                if(question.getStatus()==2){//已经回答的问题不能操作
                        json.put("status","0000");
                        json.put("message","问题已经被回答，不能操作");
                        return json.toJSONString();
                }

                questionService.updateFlag(id,flag);
                json.put("status","0000");
                json.put("message","ok");
                return json.toJSONString();

        }


        @GET
        @Path(value = "/answerQuestion")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "删除问题", notes = "删除问题", author = "更新于 2018-07-16")
        @ApiResponses(value = {
                @ApiResponse(code = "0000", message = "成功", response = Response.class)
        })
        public String answerQuestion(@ApiParam(value = "status 1 未回答  2 已回答", required = true) @QueryParam("status") int status,
                                  @ApiParam(value = "id", required = true) @QueryParam("id") int id) {
                JSONObject json = new JSONObject();

                Question question = questionService.getQuestion(id);
                if(question==null){
                        json.put("status","0001");
                        json.put("message","问题不存在");
                        return json.toJSONString();
                }
                if(question.getStatus()==2){//已经回答的问题不能操作
                        json.put("status","0000");
                        json.put("message","不能重复回答");
                        return json.toJSONString();
                }

                questionService.updateStatus(id, status);
                json.put("status","0000");
                json.put("message","ok");
                return json.toJSONString();

        }

}
